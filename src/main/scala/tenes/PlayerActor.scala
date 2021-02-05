package tenes
package actors

import tenes.Converters._

import org.jsoup._

import com.typesafe.config.ConfigFactory

import akka.actor.ActorRef

import akka.actor.{Actor, ActorLogging, Props}

import org.mongodb.scala._
import org.mongodb.scala.bson.collection.immutable.Document

object PlayerActor {
  val conf = ConfigFactory.load()
  val playerUrl = conf.getString("playerUrl")

  def props(database: MongoDatabase, o: ActorRef): Props =
    Props(new PlayerActor(database, o))
}

class PlayerActor(database: MongoDatabase, o: ActorRef) extends Actor {
  import PlayerActor._
  import PersisterActor._

  def receive: Actor.Receive = {
    case s: String =>
      val doc = Jsoup.connect(s"${playerUrl}${s}").get()
      val e = doc.select(".product_wrapper.rac > a > img")
      val ex = e.attr("alt")
      println(s"$s => $ex")
      o ! PlayerR(s, ex)
  }
}
