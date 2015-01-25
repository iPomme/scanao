/*
 * -----------------------------------------------------------------------------
 *  - ScaNao is an open-source enabling Nao's control from Scala code.            -
 *  - At the low level jNaoqi is used to bridge the C++ code with the JVM.        -
 *  -                                                                             -
 *  -  CreatedBy: Nicolas Jorand                                                  -
 *  -       Date: 21 Oct 2013                                                      -
 *  -                                                                            	-
 *  -       _______.  ______      ___      .__   __.      ___       ______       	-
 *  -      /       | /      |    /   \     |  \ |  |     /   \     /  __  \      	-
 *  -     |   (----`|  ,----'   /  ^  \    |   \|  |    /  ^  \   |  |  |  |     	-
 *  -      \   \    |  |       /  /_\  \   |  . `  |   /  /_\  \  |  |  |  |     	-
 *  -  .----)   |   |  `----. /  _____  \  |  |\   |  /  _____  \ |  `--'  |     	-
 *  -  |_______/     \______|/__/     \__\ |__| \__| /__/     \__\ \______/      	-
 *  -----------------------------------------------------------------------------
 */

package io.nao.scanao

import akka.actor._
import akka.kernel.Bootable
import akka.pattern.ask
import akka.pattern.pipe
import akka.util.Timeout
import scala.concurrent.{Future, Promise, Await}
import scala.concurrent.duration._
import com.typesafe.config.ConfigFactory
import io.nao.scanao.msg._
import io.nao.scanao.msg.tech.{NaoEvent, EventSubscribed}
import scala.util.Success
import io.nao.scanao.msg.txt.Say
import io.nao.scanao.msg.tech.EventSubscribed
import scala.util.Success
import io.nao.scanao.msg.tech.NaoEvent
import io.nao.scanao.msg.txt.Say
import scala.collection.mutable.{MultiMap, Set, HashMap}
import java.util.concurrent.TimeUnit
import concurrent.ExecutionContext.Implicits.global


class SoftshakeDemo {

  import SoftshakeApp._

  val system = ActorSystem("softshake", ConfigFactory.load.getConfig("softshake_client"))


  val mediator = system.actorOf(Props[SoftshakeMediator], "mediator")
  val fsmDayNightActor = system.actorOf(Props[DayNight], "daynight")
  val naoTxtActor = system.actorFor(naoText)
  val naoEvtActor = system.actorFor(naoEvt)
  val naoCmdActor = system.actorFor(naoCmd)
  val naoMemoryActor = system.actorFor(naoMemory)
  val naoBehaviorActor = system.actorFor(naoBehavior)

  def startup() = {

  }

  def shutdown() = {
    system.shutdown()
  }
}

sealed trait State

object Day extends State
object Night extends State
object CheckLight extends State

case class LightSwitchedOff(level: Int)

case class LightEvt(level: Int)

class DayNight() extends Actor with FSM[State, Unit] with ActorLogging {

  import context._
  import SoftshakeApp._

  implicit val timeout = Timeout(5 seconds)


  // Set the initial state not initialized with the current light value
  startWith(Day, Unit)

  when(Day) {
    case Event(l: LightSwitchedOff, _) => {
      log.info(s"When Day received the event: $l")
      goto(Night)
    }
    case Event(e, d) =>
      log.debug(s"--Day----------> $e , $d")
      stay
  }

  when(Night, stateTimeout = 1 seconds) {
    case Event(StateTimeout, _) => {
      goto(CheckLight)
    }
    case Event(l: LightEvt, _) if l.level < 60 => {
      log.info(s"When Night received the event $l")
      goto(Day)
    }
    case Event(e, d) =>
      log.debug(s"--Night--------> $e , $d")
      stay
  }

  when(CheckLight, stateTimeout = 100 milliseconds) {
    case Event(StateTimeout, _) => {
      goto(Night)
    }
    case Event(l: LightEvt, _) if l.level < 60 => {
      log.info(s"When CheckLight received the event $l")
      goto(Day)
    }
    case Event(e, d) =>
      log.debug(s"--CheckLight--------> $e , $d")
      stay
  }

  onTransition {
    case Day -> Night => {
      context.actorFor(naoText) ! txt.Say("Hee, il fait nuit !")
    }
    case Night -> Day | CheckLight -> Day => {
      context.actorFor(naoText) ! txt.Say("aaaa! encore un gag a Nicolas!")
    }
    case Night -> CheckLight | CheckLight -> Night => {
      val future = ask(context.actorFor(naoMemory), memory.DataInMemoryAsString("DarknessDetection/DarknessValue"))(5 seconds).mapTo[Int]
      val newLight = Await.result(future, 10 seconds)
      log.info(s"Got a new Light value of $newLight")
      self ! LightEvt(newLight)
    }

  }

  initialize
}

/**
 * Actor used to reveive event from Nao
 */
class SoftshakeMediator extends Actor with ActorLogging {

  import SoftshakeApp._

  implicit val timeout = Timeout(10 seconds)

  def receive = {
    case NaoEvent(eventName, values, message) => {
      log.info(s"received NaoEvent name: $eventName values: $values message: $message")
      // Send the LightSwitchedOff to the State Machine
      if (eventName.startsWith("DarknessDetection"))
        context.actorFor("/user/daynight") ! LightSwitchedOff(values.toString.toInt)
    }
    case msg@_ => {
      log.info(s"UNKNOWN MESSAGE: $msg")
    }
  }
}

/**
 * Entry point of the demonstration
 */
object SoftshakeApp {
  implicit val timeout = Timeout(10 seconds)

  val robotIP = "sonny.local"
  val robotPort = "2552"
  val remoteAkkaContext = s"akka.tcp://NaoApplication@$robotIP:$robotPort"
  val naoEvt = s"$remoteAkkaContext/user/nao/evt"
  val naoCmd = s"$remoteAkkaContext/user/nao/cmd"
  val naoText = s"$remoteAkkaContext/user/nao/cmd/text"
  val naoMemory = s"$remoteAkkaContext/user/nao/cmd/memory"
  val naoBehavior = s"$remoteAkkaContext/user/nao/cmd/behavior"

  def main(args: Array[String]) {
    val softApp = new SoftshakeDemo

    softApp.naoTxtActor ! txt.Say(s"C'est partit pour la démo, pourvu que ca marche !")

    /**
     * Subscribe to event and use a state Machine to handle it
     */
    softApp.naoEvtActor ? tech.SubscribeEvent("DarknessDetection/DarknessDetected", "SNEvents", "event", softApp.mediator) // Subscribe to an event



    /**
     * Sample call
     */
    def count() {
      (1 to 10).foreach(x => {
        println(x)
        softApp.naoTxtActor ! txt.Say(x.toString)
      })
    }

    /**
     * Console
     */
    println("Type 'exit' to finish")

    def demoCases(cmd: String) = cmd match {
      case "count" => count()
      case "help" | "?" => println(
        """Help:
          | count -> Nao will count from 1 to 10
          | help  -> Print this help
        """.stripMargin)
      case txt@_ => println(s"you wrote: $txt")
    }
    Iterator.continually(Console.readLine).takeWhile(_ != "exit").foreach(cmd => demoCases(cmd))


    softApp.shutdown()

  }
}

