package com.goibibo.kafka

import org.apache.kafka.clients.consumer.KafkaConsumer
import java.util.{Properties, Collections}
import scala.collection.JavaConverters._
import com.softwaremill.sttp._
import com.softwaremill.sttp.akkahttp._
import io.circe.syntax._
import io.circe.parser._

import akka.kafka.scaladsl.Consumer
import org.apache.kafka.common.serialization._


import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.kafka.ConsumerMessage.{CommittableMessage, CommittableOffsetBatch}
import akka.kafka._
import akka.kafka.scaladsl.Consumer.DrainingControl
import akka.kafka.scaladsl._
import akka.stream.{ActorMaterializer, Materializer}
import akka.stream.scaladsl.{Flow, Keep, RestartSource, Sink, Source}
import akka.{Done, NotUsed}
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord}
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.{
  ByteArrayDeserializer,
  ByteArraySerializer,
  StringDeserializer,
  StringSerializer
}
import org.apache.kafka.common.TopicPartition
import com.softwaremill.sttp.Uri


object Hello extends App {

  implicit val system = ActorSystem("demo")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val akkaConfig = system.settings.config.getConfig("akka.kafka.consumer")
  val bootstrapServers = akkaConfig.getString("bootstrap-servers")
  val topics = akkaConfig.getStringList("topics").asScala
  val webhookURL= akkaConfig.getString("webhook-url")
  val maxPartitions = akkaConfig.getInt("max-partitions")

  val committerSettings = CommitterSettings(system)
  val consumerSettings =
    ConsumerSettings(akkaConfig, new StringDeserializer, new StringDeserializer)
      .withBootstrapServers(bootstrapServers)
      .withGroupId("group1")
      .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

  val subscription = Subscriptions.topics(topics: _*)

  def webhookFlow[T] = Flow[akka.kafka.ConsumerMessage.CommittableMessage[String,String]].map(
    record => {
      println(record.record)
      val jsonString = record.record.value.asJson.toString
      val req = sttp
        .contentType("application/json")
        .body(jsonString)
        .post(Uri(webhookURL))
      implicit val backend = AkkaHttpBackend()
      val response = req.send()
      record
    }
  )

  val control = Consumer
    .committablePartitionedSource(consumerSettings, Subscriptions.topics(topics: _*))
    .mapAsyncUnordered(maxPartitions) {
      case (topicPartition, source) =>
        source
          .via(webhookFlow)
          .map(_.committableOffset)
          .runWith(Committer.sink(committerSettings))
    }
    .toMat(Sink.ignore)(Keep.both)
    .mapMaterializedValue(DrainingControl.apply)
    .run()
}
