package tenes

import akka.actor.ActorRef
import org.jsoup._

class RacquetDetails(p: ActorRef, racquetUrl: String) {

  val doc = Jsoup.connect(racquetUrl).get();

  val name = doc.select("h1.name").text()

  var weight: Float = 0
  var stiffness = 0
  var length = 0d
  var beam = 0
  var balance = ""
  var balanceP1 = 0
  var balanceP2 = ""
  var headSize = 0

  var desc = ""
  var overall = 0

  doc.select(".SpecsDk").forEach { e =>
    if (e.text().startsWith("Stiffness")) {
      try {
        stiffness = e.text().split(":")(1).trim.toInt
      } catch {
        case e: Throwable => println(name)
      }
    }
    if (e.text().startsWith("Length")) {
      length = e
        .text()
        .split(":")(1)
        .trim
        .split(" / ")(0)
        .trim
        .split("\\D+")
        .mkString(".")
        .toDouble
    }
    if (e.text().startsWith("Balance")) {
      balance = e.text().split(":")(1).trim
      if (balance.split(" / ").length > 1) {
        balanceP1 = balance.split(" / ")(2).split(" ")(0).toInt
        balanceP2 = balance.split(" / ")(2).split(" ")(2)
      } else {
        try {
          balanceP1 = balance.split(" ")(0).toInt
          balanceP2 = balance.split(" ")(2)
        } catch {
          case e: Throwable => println(name)
        }
      }
    }
  }

  doc.select(".SpecsLt").forEach { e =>
    if (e.text().startsWith("Head Size")) {
      val s = e.text().trim.split(":")(1).trim
      try {
        val n = s.trim.split(" / ")(0).splitAt(3)._1.trim.toInt
        headSize = n
      } catch {
        case e: Throwable => println(name)
      }
    }
    if (e.text().startsWith("Strung Weight")) {
      if (e.text().trim.split(":")(1).trim.split(" / ")(0).contains("oz")) {
        weight = java.lang.Float.valueOf(
          e.text().trim.split(":")(1).trim.split(" / ")(0).replace("oz", "")
        )
      } else {
        weight = java.lang.Float.valueOf(
          e.text().trim.split(":")(1).trim.split(" / ")(1).replace("oz", "")
        )
      }
    }
    if (e.text().startsWith("Beam Width")) {
      if (e.text().split(":")(1).trim.split(" / ").length > 1) {
        beam =
          e.text().split(":")(1).trim.split(" / ")(1).split("\\D+")(0).toInt
      }
    }
  }

  doc
    .select("#desc_tab > div.desc_column > span:nth-child(3) > p:nth-child(2)")
    .forEach { e =>
      val s_x = e.text().trim
    // desc = s_x
    }

  if (stiffness > 0 && length > 0) {
    val r = Racquet(
      name = name,
      headSize = headSize,
      stiffness = stiffness,
      length = length,
      beam = beam,
      weight = weight,
      balance = balance,
      balanceP1 = balanceP1,
      balanceP2 = balanceP2,
      url = racquetUrl,
      desc = desc
    )

    // p ! r
    if (headSize == 100 && length == 27 && beam > 0) {
      p ! tenes.actors.PersisterActor.R100(r)
    }
    if (headSize == 98 && length == 27 && beam > 0) {
      p ! tenes.actors.PersisterActor.R98(r)
    }
    // if (headSize == 95 && length == 27) {
    //   p ! tenes.actors.PersisterActor.R95(r)
    // }
    // if (headSize < 95 && length == 27) {
    //   p ! tenes.actors.PersisterActor.Little(r, r.length.toInt)
    // }
  } else {
    println(s"oh no! $name")
  }
}
