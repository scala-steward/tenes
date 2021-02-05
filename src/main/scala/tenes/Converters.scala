package tenes

import org.mongodb.scala._

import actors.PersisterActor._

object Converters {

  def brand2Document(brand: Brand): Document = {
    Document(
      "_id" -> slugify(brand.name),
      "name" -> brand.name,
      "href" -> brand.href
    )
  }

  def racquet2Document(raq: Racquet): Document = {
    Document(
      "_id" -> slugify(raq.name),
      "name" -> raq.name,
      "headSize" -> raq.headSize,
      "stiffness" -> raq.stiffness,
      "length" -> raq.length.toDouble,
      "beam" -> raq.beam,
      "weight" -> raq.weight.toDouble,
      "balance" -> raq.balance,
      "balanceP1" -> raq.balanceP1,
      "balanceP2" -> raq.balanceP2,
      "url" -> raq.url,
      "desc" -> raq.desc
    )
  }

  def playersRacquet(p: PlayerR): Document = {
    val tmp = p.code.split("=").last
    Document(
      "_id" -> s"${slugify(p.racquet)}-${tmp}",
      "code" -> p.code,
      "racquet" -> p.racquet
    )
  }
}
