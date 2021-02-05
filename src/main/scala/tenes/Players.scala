package tenes

import scala.collection.JavaConverters._

import org.jsoup._
import org.jsoup.select.Elements

import akka.actor.ActorRef
import com.typesafe.config.ConfigFactory

class Players(p: ActorRef) {
  val conf = ConfigFactory.load()
  val playersATPUrl = conf.getString("playersATPUrl")

  val doc = Jsoup.connect(s"${playersATPUrl}").get()

  val elsP: Elements =
    doc.select(".cat_item.player_icon > div > a:nth-child(1)")
  val elsOP: Elements = doc.select("li.cat_item > div > a")

  elsP.asScala.distinct.foreach { el =>
    val x = el.attr("href").split("\\?").tail.head
    // println(s"${url}?${x}")
    // val doc = Jsoup.connect(s"${url}?${x}").get()
    p ! x
  }
  elsOP.asScala.distinct.foreach { el =>
    if (el.attr("href").contains("player.html")) {
      val x = el.attr("href").split("\\?").tail.head
      // println(s"${url}?${x}")
      // val doc = Jsoup.connect(s"${url}?${x}").get()
      p ! x
    }
  }

}
