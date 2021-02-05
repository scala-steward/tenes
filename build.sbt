ThisBuild / version      := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "2.12.13"

Compile / run / fork := true

cancelable in Global := true

scalafmtOnCompile := true

lazy val CatsEffectVersion = "1.0.0"
lazy val Fs2Version        = "1.0.0-RC1"
lazy val Http4sVersion     = "0.20.7"
lazy val CirceVersion      = "0.10.0"
lazy val LogbackVersion    = "1.2.3"

lazy val root = (project in file("."))
  .settings(
    name                 := "tenes",
    libraryDependencies ++= Seq(
      "org.jsoup"              % "jsoup"              % "1.12.1",
      "com.github.tototoshi"  %% "scala-csv"          % "1.3.6",
      "org.mongodb.scala"     %% "mongo-scala-driver" % "2.6.0",
      "com.typesafe.akka"     %% "akka-actor"         % "2.5.23"
    )
  )

