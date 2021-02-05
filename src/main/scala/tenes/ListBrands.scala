package tenes

import scala.collection.JavaConverters._
import scala.util.{Failure, Success, Try}

import org.jsoup._
import org.jsoup.select.Elements

import akka.pattern.{ask, pipe}
import akka.actor.ActorRef

import scala.concurrent.duration._

import com.typesafe.config.ConfigFactory

class ListBrands(persisterActor: ActorRef) {
  val conf = ConfigFactory.load()

  val baseUrl = conf.getString("baseUrl")
  val listUrl = "TennisRacquets.html"

  def execute() = {
    import scala.concurrent.ExecutionContext.Implicits.global
    implicit val timeout = akka.util.Timeout(5 seconds) // needed for `?` below
    val doc = Jsoup.connect(s"${baseUrl}${listUrl}").get();

    val elements: Elements = doc.select(".category_brands-sub > a")
    // doc.select("ul.brandlist_icons.cf > li > a.name")

    val x = elements.asScala.map { el =>
      val brand = Brand(
        name = el.text(),
        href = el.attr("href")
      )
      println(brand)
      // persisterActor ! brand
      (persisterActor ? brand).mapTo[String]
    }

    x.map(
      _.map { o =>
        Success(o)
      }.recover { case t => Failure(t) }
    )
  }

}
