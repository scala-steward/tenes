package tenes
package actors

import akka.actor.{Actor, Props}
import org.jsoup._

import scala.collection.JavaConverters._

object OverallActor {
  def props(): Props =
    Props(new OverallActor())

  case class Review(name: String, fullUrl: String)
}

class OverallActor() extends Actor {
  import OverallActor._

  def receive: Actor.Receive = { case r: Review =>
    val doc = Jsoup.connect(r.fullUrl).get()

    val els = doc.select(".total_score.fr")

    els.asScala.foreach { el =>
      sender() ! tenes.actors.PersisterActor.Review(r.name, el.text().toInt)
    }
  }
}
