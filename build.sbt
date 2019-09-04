import Dependencies._

ThisBuild / scalaVersion     := "2.12.8"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.goibibo"
ThisBuild / organizationName := "goibibo"

enablePlugins(JavaAppPackaging)

lazy val root = (project in file("."))
  .settings(
    name := "kafkaconsumer",
    libraryDependencies ++= Seq(scalaTest % Test,
                                "org.apache.kafka" % "kafka-clients" % "2.3.0",
                                "com.typesafe.akka" %% "akka-stream-kafka" % "1.0.5",
                                "com.softwaremill.sttp" %% "akka-http-backend" % "1.6.3",
                                "com.typesafe.akka" %% "akka-stream" % "2.5.11",
                                "com.softwaremill.sttp" %% "core" % "1.6.3",
                                "io.circe" %% "circe-core" % "0.11.1",
                                "io.circe" %% "circe-generic" % "0.11.1",
                                "io.circe" %% "circe-parser" % "0.11.1",
                                "com.typesafe" % "config" % "1.3.4"
                               )
  )


// Uncomment the following for publishing to Sonatype.
// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for more detail.

// ThisBuild / description := "Some descripiton about your project."
// ThisBuild / licenses    := List("Apache 2" -> new URL("http://www.apache.org/licenses/LICENSE-2.0.txt"))
// ThisBuild / homepage    := Some(url("https://github.com/example/project"))
// ThisBuild / scmInfo := Some(
//   ScmInfo(
//     url("https://github.com/your-account/your-project"),
//     "scm:git@github.com:your-account/your-project.git"
//   )
// )
// ThisBuild / developers := List(
//   Developer(
//     id    = "Your identifier",
//     name  = "Your Name",
//     email = "your@email",
//     url   = url("http://your.url")
//   )
// )
// ThisBuild / pomIncludeRepository := { _ => false }
// ThisBuild / publishTo := {
//   val nexus = "https://oss.sonatype.org/"
//   if (isSnapshot.value) Some("snapshots" at nexus + "content/repositories/snapshots")
//   else Some("releases" at nexus + "service/local/staging/deploy/maven2")
// }
// ThisBuild / publishMavenStyle := true
