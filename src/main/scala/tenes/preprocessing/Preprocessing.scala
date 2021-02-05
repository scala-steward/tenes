package tenes.preprocessing

import tenes._

import org.mongodb.scala._
import org.bson.{BsonString, BsonInt32, BsonDouble, Document}
import com.typesafe.config.ConfigFactory

object Preprocessing {

  case class Norm(
      headSize: Int,
      weight: Float,
      stiffness: Int,
      length: Double,
      beam: Int
  )

  import scala.util.{Failure, Success}
  val conf = ConfigFactory.load()
  val databaseName = conf.getString("database")

  def preprocess() = {
    val mongoClient: MongoClient = MongoClient()

    val database: MongoDatabase = mongoClient.getDatabase(databaseName)

    val l = database
      .getCollection("racquets")
      .find()
      .map(doc2Racquet(_))
      .collect()
      .map(x => norm(x.toList))
      .foreach(println)
  }

  private[this] def norm(racquets: List[Racquet]) = {
    val h1 = racquets.map(_.headSize).max
    val h2 = racquets.filter(_.headSize > 0).map(_.headSize).min
    val havg = racquets.filter(_.headSize > 0).map(_.headSize).sum / racquets
      .filter(_.headSize > 0)
      .map(_.headSize)
      .length
    println(s"$h1 ~ $h2 ~~ $havg")

    val w1 = racquets.map(_.weight).max
    val w2 = racquets.map(_.weight).min
    val wavg = racquets.filter(_.weight > 0).map(_.weight).sum / racquets
      .filter(_.weight > 0)
      .map(_.weight)
      .length
    println(s"$w1 ~ $w2 ~~ $wavg")

    val s1 = racquets.map(_.stiffness).max
    val s2 = racquets.map(_.stiffness).min
    val savg = racquets.filter(_.stiffness > 0).map(_.stiffness).sum / racquets
      .filter(_.stiffness > 0)
      .map(_.stiffness)
      .length
    println(s"$s1 ~ $s2 ~~ $savg")

    val l1 = racquets.map(_.length).max
    val l2 = racquets.map(_.length).min
    println(s"$l1 ~ $l2")

    val b1 = racquets.map(_.beam).max
    val b2 = racquets.filter(_.beam > 0).map(_.beam).min
    println(s"$b1 ~ $b2")

    Norm(h1 - h2, w1 - w2, s1 - s2, l1 - l2, b1 - b2)
  }

  import org.mongodb.scala.bson.BsonTransformer._

  private[this] def doc2Racquet(doc: Document): Racquet = {
    val n = doc.getString("name")
    val h = doc.getInteger("headSize")
    val w = doc.getDouble("weight")
    val s = doc.getInteger("stiffness")
    val l = doc.getDouble("length")
    val b = doc.getInteger("beam")
    val bb = doc.getString("balance")
    val bb1 = doc.getInteger("balanceP1")
    val bb2 = doc.getString("balanceP2")
    val u = doc.getString("url")
    val d = doc.getString("desc")

    Racquet(n, h, w.toFloat, s, l, b, bb, bb1, bb2, u, d)
  }
}
