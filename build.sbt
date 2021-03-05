ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "2.13.4"

Compile / run / fork := true

cancelable in Global := true

scalafmtOnCompile := true

lazy val root = (project in file("."))
  .settings(
    name := "tenes",
    libraryDependencies ++= Seq(
      "org.jsoup" % "jsoup" % "1.13.1",
      "com.github.tototoshi" %% "scala-csv" % "1.3.7",
      "org.mongodb.scala" %% "mongo-scala-driver" % "4.1.1",
      "com.typesafe.akka" %% "akka-actor" % "2.6.12"
    )
  )
