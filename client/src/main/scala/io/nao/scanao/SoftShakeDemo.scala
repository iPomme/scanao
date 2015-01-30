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
import akka.pattern.ask
import akka.util.Timeout
import com.github.levkhomich.akka.tracing.ActorTracing
import com.typesafe.config.ConfigFactory
import io.nao.scanao.msg._
import io.nao.scanao.msg.tech.NaoEvent

import scala.collection.immutable.HashMap
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.io.StdIn


class SoftshakeDemo {

  val system = ActorSystem("softshake", ConfigFactory.load.getConfig("softshake_client"))


  // create the actors for the demo
  val mediator = system.actorOf(Props[SoftshakeMediator], "mediator")

  def shutdown() = {
    system.shutdown()
  }
}

sealed trait State

object Day extends State

object Night extends State

object CheckLight extends State


case class LightSwitchedOff(level: Int, naoActors: HashMap[String, Option[ActorRef]])

case class LightEvt(level: Int)

class DayNight(val naoRefs: HashMap[String, Option[ActorRef]]) extends Actor with FSM[State, Unit] with ActorLogging {

  import io.nao.scanao.SoftshakeApp._

  implicit val timeout = Timeout(5 seconds)


  // Set the initial state not initialized with the current light value
  startWith(Day, Unit)

  when(Day) {
    case Event(l: LightSwitchedOff, _) =>
      log.info(s"When Day received the event: $l")
      goto(Night)
    case Event(e, d) =>
      log.debug(s"--Day----------> $e , $d")
      stay()
  }

  when(Night, stateTimeout = 1 seconds) {
    case Event(StateTimeout, _) =>
      goto(CheckLight)
    case Event(l: LightEvt, _) if l.level < 60 =>
      log.info(s"When Night received the event $l")
      goto(Day)
    case Event(e, d) =>
      log.debug(s"--Night--------> $e , $d")
      stay()
  }

  when(CheckLight, stateTimeout = 100 milliseconds) {
    case Event(StateTimeout, _) =>
      goto(Night)
    case Event(l: LightEvt, _) if l.level < 60 =>
      log.info(s"When CheckLight received the event $l")
      goto(Day)
    case Event(e, d) =>
      log.debug(s"--CheckLight--------> $e , $d")
      stay()
  }

  onTransition {
    case Day -> Night =>
      naoRefs(naoText).map(_ ! txt.Say("Hee, il fait nuit !"))
    case Night -> Day | CheckLight -> Day =>
      naoRefs(naoText).map(_ ! txt.Say("aaaa! encore un gag a Nicolas!"))
    case Night -> CheckLight | CheckLight -> Night =>
      val future = ask(naoRefs(naoMemory).get, memory.DataInMemoryAsString("DarknessDetection/DarknessValue")).mapTo[Int]
      val newLight = Await.result(future, 10 seconds)
      log.info(s"Got a new Light value of $newLight")
      self ! LightEvt(newLight)

  }

  initialize()
}

sealed trait InitState

object Initializing extends InitState

object Initialized extends InitState

object Ready extends InitState

case class References(queue: scala.collection.immutable.HashMap[String, Option[ActorRef]])

/**
 * Actor used to do mediation between Nao and the client.
 * The initialization is based on a state machine,
 * this is due to the fact that the server needs to initialize a JNI connection with the robot and this initialization is taking time.
 * Notice that all the messages received before initialisation would be stach and replay once the robot is ready.
 */
class SoftshakeMediator extends Actor with FSM[InitState, References] with Stash with ActorLogging with ActorTracing {

  import io.nao.scanao.SoftshakeApp._

  var fsmDayNightActor: ActorRef = _

  def listActorRef = {
    // Get the reference to the Nao actors
    HashMap.empty[String, Option[ActorRef]] + ((naoEvt, None)) + ((naoCmd, None)) + ((naoText, None)) + ((naoMemory, None)) + ((naoBehavior, None))
  }

  def identifyActors(id: String): Unit = {
    log.info(s"Send the identify message to $id")
    context.actorSelection(id) ! Identify(id)
  }

  // Send the Identity to all the references needed
  listActorRef.foreach { case (id, ref) => identifyActors(id)}

  // Set the initial state with the list of refs needed, note that at this point all the ActorRef should be set to None
  startWith(Initializing, References(listActorRef))

  when(Initializing) {
    case Event(ActorIdentity(id, ref@Some(_)), a@References(q)) =>
      log.info(s"Got the reference to $id !!")
      log.debug(s"The current missing remote reference is ${q.filter(_._2 == None)}")
      val uptQueue = q + ((id.toString, ref))
      if (uptQueue.values.exists(_ == None))
      // Some remote references are missing, stay in this state till everything initialized
        stay using a.copy(uptQueue)
      else
      // All the remote references has been resolved, move to the initialized state.
        goto(Initialized) using a.copy(uptQueue)
    case Event(ActorIdentity(id, None), a) =>
      log.error(s"Impossible to get the reference to $id")
      context.stop(self)
      stay()

    case Event(m@_, References(h)) =>
      stash()
      log.info(s"Message $m stached as still initializing")
      stay()
    //TODO: Manage to watch all the remote references.
  }

  def traceSay(msg: txt.Say)(send: => Unit) {
    trace.sample(msg, "NaoClient")
    trace.record(msg, "Send Saying event")
    send
    trace.finish(msg)
  }

  def sendSay(msg: txt.Say, ref: HashMap[String, Option[ActorRef]]) {
    log.info(s"Got the message $msg to send to ${ref(naoText)}")
    traceSay(msg) {
      ref(naoText).get ! msg
    }
  }

  when(Initialized) {
    case Event(m: txt.Say, References(h)) =>
      sendSay(m, h)
      stay()
    case Event(m: tech.SubscribeEvent, References(h)) =>
      log.info(s"Got the message $m to send to ${h(naoText)}")
      h(naoEvt).map(_ ! m)
      stay()
    case Event(m@tech.EventSubscribed(name, module, method), References(h)) =>
      log.info(s"Subscribed to $m")
      h(naoText).map(_ ! txt.Say("Je suis pret"))
      goto(Ready)

  }

  when(Ready) {
    case Event(m: txt.Say, References(h)) =>
      sendSay(m, h)
      stay()
    case Event(NaoEvent(eventName, values, message), References(h)) =>
      log.info(s"received NaoEvent name: $eventName values: $values message: $message")
      // Send the LightSwitchedOff to the State Machine
      if (eventName.startsWith("DarknessDetection"))
        fsmDayNightActor ! LightSwitchedOff(values.toString.toInt, h)
      stay()
    case Event(m@_, References(h)) =>
      log.info(s"UNKNOWN MESSAGE: $m")
      stay()
  }

  onTransition {
    case Initializing -> Initialized =>
      log.info("Transition to Initialized, unstash the messages ...")
      fsmDayNightActor = context.actorOf(Props(classOf[DayNight], nextStateData.queue), "daynight")
      unstashAll()
    case Initialized -> Ready =>
      log.info("Transition to Ready")


  }
}

/**
 * Entry point of the demonstration
 */
object SoftshakeApp {

  val robotIP = "sonny.local"
  val robotPort = "2552"
  val remoteAkkaContext = s"akka.tcp://NaoApplication@$robotIP:$robotPort"
  val naoEvt = s"$remoteAkkaContext/user/nao/evt"
  val naoCmd = s"$remoteAkkaContext/user/nao/cmd"
  val naoText = s"$remoteAkkaContext/user/nao/cmd/text"
  val naoMemory = s"$remoteAkkaContext/user/nao/cmd/memory"
  val naoBehavior = s"$remoteAkkaContext/user/nao/cmd/behavior"

  def main(args: Array[String]) {
    /**
     * Create the demo instance and initialize all the local actors
     * Return event if the remote actors are not finished to be initialized
     */
    val softApp = new SoftshakeDemo

    /**
     * Thanks to the initialization, all the messages would be stached if the robot is not ready
     */

    /**
     * Inform that the demo is ready to start
     */
    softApp.mediator ! txt.Say(s"C'est partit pour la démo, pourvu que ca marche ! Je te dis des que je suis pret ...")

    /**
     * Subscribe to event and use a state Machine to handle it
     */
    softApp.mediator ! tech.SubscribeEvent("DarknessDetection/DarknessDetected", "SNEvents", "event", softApp.mediator) // Subscribe to an event


    /**
     * Sample call
     */
    def count(nb: Int) {
      (1 to nb).foreach(x => {
        println(x)
        softApp.mediator ! txt.Say(x.toString)
      })
    }

    /**
     * Console
     */
    println("Type 'exit' to finish")

    def demoCases(cmd: List[String]) = cmd.map(_.toLowerCase) match {
      case "count" :: Nil => count(10)
      case "count" :: nb :: Nil => count(Integer.parseInt(nb))
      case "help" :: Nil | "?" :: Nil => println(
        """Help:
          | count [nb] -> Nao will count (from 1 to 10 if no number passed)
          | help  -> Print this help
        """.stripMargin)
      case txt@_ => println(s"you wrote: $txt")
    }

    Iterator.continually(StdIn.readLine()).takeWhile(_ != "exit").foreach(cmd => demoCases(cmd.split(" ").toList))

    softApp.shutdown()

  }

}

