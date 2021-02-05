package tenes
package actors

import akka.actor.{Actor, ActorRef, Props}
import com.typesafe.config.ConfigFactory
import org.jsoup._
import org.mongodb.scala._

object PlayerActor {
  val conf = ConfigFactory.load()
  val playerUrl = conf.getString("playerUrl")

  def props(database: MongoDatabase, o: ActorRef): Props =
    Props(new PlayerActor(database, o))
}

class PlayerActor(database: MongoDatabase, o: ActorRef) extends Actor {
  import PersisterActor._
  import PlayerActor._

  def receive: Actor.Receive = { case s: String =>
    val doc = Jsoup.connect(s"${playerUrl}${s}").get()
    val e = doc.select(".product_wrapper.rac > a > img")
    val ex = e.attr("alt")
    println(s"$s => $ex")
    o ! PlayerR(s, ex)
  }
}
