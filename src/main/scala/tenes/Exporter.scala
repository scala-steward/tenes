package tenes

import com.github.tototoshi.csv._
import com.typesafe.config.ConfigFactory
import org.mongodb.scala._

object Exporter {
  val conf = ConfigFactory.load()
  val databaseName = conf.getString("database")

  val mongoClient: MongoClient = MongoClient()
  val database: MongoDatabase = mongoClient.getDatabase(databaseName)

  val f = new java.io.File(
    s"out-${System.currentTimeMillis.toString.substring(8, 12)}"
  )
  // val writer = CSVWriter.open(f, append = true)
  // writer.writeRow(List("a", "b", "c"))

  implicit object MyFormat extends DefaultCSVFormat {
    override val delimiter = ' '
  }

  database.getCollection("racquets").find().foreach { b =>
    var s = 0d
    var l = 0d
    var be = 0d
    var w = 0d
    var bi = 0d
    val writer = CSVWriter.open(f, append = true)

    b.get("stiffness").foreach { x =>
      s = x.asInt32().getValue().toDouble
    }
    b.get("length").foreach { x =>
      l = x.asInt32().getValue().toDouble
    }
    b.get("beam").foreach { x =>
      be = x.asInt32().getValue().toDouble
    }
    b.get("weight").foreach { x =>
      w = BigDecimal(x.asDouble().getValue())
        .setScale(2, BigDecimal.RoundingMode.HALF_UP)
        .toDouble
    }
    b.get("balanceP1").foreach { x =>
      bi = x.asInt32().getValue().toDouble
    }

    if (s > 0 && l == 27d && be > 0) {
      writer.writeRow(List(s, l, be, w, bi))
      writer.close()
    }
  }

}
