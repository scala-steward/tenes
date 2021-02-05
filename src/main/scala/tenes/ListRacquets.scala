package tenes

import akka.actor.ActorRef
import org.jsoup._
import org.mongodb.scala._

import scala.collection.JavaConverters._

class ListRacquets(p: ActorRef, database: MongoDatabase) {

  database.getCollection("brands").find().foreach { b =>
    b.get("href").foreach { url =>
      val u = url.asString().getValue()
      val doc = Jsoup.connect(u).get()
      val els = doc.select(".product_wrapper.cf.rac > div.text_wrap > a")
      els.asScala.foreach { el =>
        new RacquetDetails(p, el.attr("href"))
      }
    }
  }

  // p ! PoisonPill

}
