package tenes

import akka.actor.{Actor, ActorSystem, Props}
import tenes.actors.{OverallActor, PersisterActor, CSVActor, PlayerActor}

import org.mongodb.scala._
import org.mongodb.scala.bson.collection.immutable.Document

import com.typesafe.config.ConfigFactory

object Main extends App {
  val conf = ConfigFactory.load()
  val databaseName = conf.getString("database")

  val system = ActorSystem("tenes")

  val mongoClient: MongoClient = MongoClient()

  val database: MongoDatabase = mongoClient.getDatabase(databaseName)

  val overallA = system.actorOf(OverallActor.props())
  val persisterA = system.actorOf(PersisterActor.props(database, overallA))
  val playerA = system.actorOf(PlayerActor.props(database, persisterA))
  val csvA = system.actorOf(CSVActor.props(overallA))

  // new ListBrands(persisterA).execute().map { _ =>
  //   System.exit(0)
  // }

  new ListRacquets(csvA, database)

  // new Players(playerA)

}
