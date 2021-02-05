package tenes
package actors

import tenes.Converters._

import akka.actor.ActorRef

import akka.actor.{Actor, ActorLogging, Props}

import org.mongodb.scala._
import org.mongodb.scala.bson.collection.immutable.Document

object PersisterActor {
  def props(database: MongoDatabase, o: ActorRef): Props =
    Props(new PersisterActor(database, o))

  case class R100(r: Racquet, n: Int = 100)
  case class R98(r: Racquet, n: Int = 98)
  case class R95(r: Racquet, n: Int = 95)
  case class Little(r: Racquet, n: Int)

  case class Review(name: String, overall: Int = 0)

  case class PlayerR(code: String, racquet: String)

}

class PersisterActor(database: MongoDatabase, o: ActorRef) extends Actor {
  import PersisterActor._
  val brands: MongoCollection[Document] =
    database.getCollection("brands")

  val racquets: MongoCollection[Document] =
    database.getCollection("racquets")

  val racquets100: MongoCollection[Document] =
    database.getCollection("racquets100")

  val racquets98: MongoCollection[Document] =
    database.getCollection("racquets98")

  val racquets95: MongoCollection[Document] =
    database.getCollection("racquets95")

  val little: MongoCollection[Document] =
    database.getCollection("little")

  val playersRacquets: MongoCollection[Document] =
    database.getCollection("players_racquets")

  def receive: Actor.Receive = {
    case brand: Brand =>
      println(s"brand: $brand")
      brands
        .insertOne(brand2Document(brand))
        .foreach(_ => ())
      sender() ! brand.name

    case racquet: Racquet =>
      println(s"racquet: $racquet")
      racquets
        .insertOne(racquet2Document(racquet))
        .foreach(_ => ())
      sender() ! racquet.name

    case r100: R100 =>
      println(s"R100: $r100")
      racquets100
        .insertOne(racquet2Document(r100.r))
        .foreach(_ => ())
      sender() ! r100.r.name

    case _r: R98 =>
      println(s"\tR98: ${_r}")
      racquets98
        .insertOne(racquet2Document(_r.r))
        .foreach(_ => ())
      sender() ! _r.r.name

    case _r: R95 =>
      println(s"\t\tR95: ${_r}")
      racquets95
        .insertOne(racquet2Document(_r.r))
        .foreach(_ => ())
      sender() ! _r.r.name

    case _r: Little =>
      println(s"\tLittle: ${_r}")
      little
        .insertOne(racquet2Document(_r.r))
        .foreach(_ => ())
      sender() ! _r.r.name

    case doc: Document =>
      println(doc)

    case r: tenes.actors.OverallActor.Review =>
      o ! r

    case r: tenes.actors.PersisterActor.Review =>
      if (r.overall >= 86)
        println(s"${r.name}: ${r.overall}")

    case r: PlayerR =>
      println(s"PlayerR: $r")
      playersRacquets
        .insertOne(playersRacquet(r))
        .foreach(_ => ())
      sender() ! r.code
  }
}
