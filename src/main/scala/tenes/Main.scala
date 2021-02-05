package tenes

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import org.mongodb.scala._
import tenes.actors.{CSVActor, OverallActor, PersisterActor, PlayerActor}

object Main extends App {
  if (args.length == 0) {
    System.err.println("PLEASE CHOOSE AN OPTION BETWEEN B, R OR P ")
    System.exit(1)
  }

  val conf = ConfigFactory.load()
  val databaseName = conf.getString("database")
  val system = ActorSystem("tenes")
  val mongoClient: MongoClient = MongoClient()
  val database: MongoDatabase = mongoClient.getDatabase(databaseName)

  val overallA = system.actorOf(OverallActor.props())
  val persisterA = system.actorOf(PersisterActor.props(database, overallA))
  val playerA = system.actorOf(PlayerActor.props(database, persisterA))
  val csvA = system.actorOf(CSVActor.props(overallA))

  if (args(0) == "B") {
    new ListBrands(persisterA).execute().map { _ =>
      System.exit(0)
    }
  } else if (args(0) == "R") {
    new ListRacquets(csvA, database)
    System.exit(0)
  } else if (args(0) == "P") {
    new Players(playerA)
    System.exit(0)
  } else {
    System.err.println("PLEASE CHOOSE AN OPTION BETWEEN B, R OR P ")
    System.exit(1)
  }

}
