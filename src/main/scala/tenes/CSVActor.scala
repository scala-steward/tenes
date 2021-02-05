package tenes
package actors

import java.io.File

import akka.actor.{Actor, ActorRef, Props}
import com.github.tototoshi.csv._

object CSVActor {
  def props(o: ActorRef): Props =
    Props(new CSVActor(o))
}

class CSVActor(o: ActorRef) extends Actor {
  import PersisterActor._
  val brands = new File("brands.csv")

  val racquets = new File("racquets.csv")

  val racquets100 = new File("racquets100.csv")

  val racquets98 = new File("racquets98.csv")

  val racquets95 = new File("racquets95.csv")

  val little = new File("little.csv")

  val playersRacquets = new File("players-racquets.csv")

  import java.nio.charset.StandardCharsets
  import java.util.Base64

  def receive: Actor.Receive = {
    case brand: Brand =>
      println(s"brand: $brand")
      val writer = CSVWriter.open(brands, append = true)
      writer.writeRow(List(brand.name, brand.href))
      writer.close()
      sender() ! brand.name

      case class Racquet(
          name: String,
          headSize: Int,
          weight: Float,
          stiffness: Int,
          length: Double,
          beam: Int,
          balance: String,
          balanceP1: Int,
          balanceP2: String,
          url: String = "",
          desc: String
      )

    case racquet: Racquet =>
      println(s"racquet: $racquet")
      val writer = CSVWriter.open(racquets, append = true)
      writer.writeRow(
        List(
          Base64.getEncoder
            .encodeToString(racquet.name.getBytes(StandardCharsets.UTF_8)),
          racquet.headSize,
          racquet.weight,
          racquet.stiffness,
          racquet.length,
          racquet.beam
        )
      )
      writer.close()
      sender() ! racquet.name

    case r100: R100 =>
      println(s"R100: $r100")
      val writer = CSVWriter.open(racquets100, append = true)
      val racquet = r100.r
      // val name = Base64.getEncoder.encodeToString(racquet.name.getBytes(StandardCharsets.UTF_8))
      val name = racquet.name
      writer.writeRow(
        List(
          name,
          racquet.weight,
          racquet.stiffness,
          racquet.beam
        )
      )
      writer.close()
    // sender() ! r100.r.name

    case _r: R98 =>
      def calcB(r: Racquet) = {
        if (r.balanceP2 == "HL") {
          r.balanceP1 * -1
        } else if (r.balanceP2 == "HH") {
          r.balanceP1
        } else {
          0
        }
      }
      println(
        s"\tR98: ${_r.r.name}\tbalanceP1:${_r.r.balanceP1}\tbalanceP2:${_r.r.balanceP2}"
      )
      val writer = CSVWriter.open(racquets98, append = true)
      val racquet = _r.r
      // val name = Base64.getEncoder.encodeToString(racquet.name.getBytes(StandardCharsets.UTF_8))
      val name = racquet.name
      writer.writeRow(
        List(
          name,
          racquet.weight,
          racquet.stiffness,
          racquet.beam,
          calcB(racquet)
        )
      )
      writer.close()
    // sender() ! _r.r.name

    case _r: R95 =>
      println(s"\t\tR95: ${_r}")
      sender() ! _r.r.name

    case _r: Little =>
      println(s"\tLittle: ${_r}")
      sender() ! _r.r.name

    case r: tenes.actors.OverallActor.Review =>
      o ! r

    case r: tenes.actors.PersisterActor.Review =>
      if (r.overall >= 86)
        println(s"${r.name}: ${r.overall}")

    case r: PlayerR =>
      println(s"PlayerR: $r")
      sender() ! r.code

    case x =>
      println(s"oh no\t $x")

  }
}
