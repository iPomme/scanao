/*
 * -----------------------------------------------------------------------------
 *  - ScaNao is an open-source enabling Nao's control from Scala code.            -
 *  - At the low level jNaoqi is used to bridge the C++ code with the JVM.        -
 *  -                                                                             -
 *  -  CreatedBy: Nicolas Jorand                                                  -
 *  -       Date: 3 Feb 2013                                                      -
 *  -                                                                            	-
 *  -       _______.  ______      ___      .__   __.      ___       ______       	-
 *  -      /       | /      |    /   \     |  \ |  |     /   \     /  __  \      	-
 *  -     |   (----`|  ,----'   /  ^  \    |   \|  |    /  ^  \   |  |  |  |     	-
 *  -      \   \    |  |       /  /_\  \   |  . `  |   /  /_\  \  |  |  |  |     	-
 *  -  .----)   |   |  `----. /  _____  \  |  |\   |  /  _____  \ |  `--'  |     	-
 *  -  |_______/     \______|/__/     \__\ |__| \__| /__/     \__\ \______/      	-
 *  -----------------------------------------------------------------------------
 */

package io.nao.scanao.srv

import java.util.concurrent.TimeUnit

import akka.actor.{DeadLetter, _}
import akka.kernel.Bootable
import com.aldebaran.qimessaging.{Application => AldeApplication, DynamicObjectBuilder, Future => AldeFuture, Object => AldeObject, QimessagingService, Session}
import com.typesafe.config.ConfigFactory
import io.nao.scanao.msg.tech._

import scala.collection.mutable.{HashMap, MultiMap, Set}
import scala.concurrent.duration._


class NaoSupervisor extends Actor with ActorLogging {

  override def preStart() {
    log.debug("NaoSupervisor preStart.")
    // Create the children
    context.actorOf(SNCmdManagementActor.props(), name = "cmd")
    context.actorOf(SNEvtManagementActor.props(), name = "evt")
    // Initialize a DeadLeter Logging Actor and subscirbe it to the eventStream
    val listener = context.system.actorOf(DeadLetterActor.props())
    context.system.eventStream.subscribe(listener, classOf[DeadLetter])
    // Print all the paths registered out
    self ! PrintDebug
    log.info("NaoSupervisor Initalized.")
  }

  def receive = {
    case msg@PrintDebug =>
      val children = context.children.foldLeft("")((b, ref) => s"$b,$ref")
      log.debug(s"About to send 'printDebug' to $children")
      context.actorSelection("*").forward(msg)
    case msg@PrintMap =>
      val children = context.children.foldLeft("")((b, ref) => s"$b,$ref")
      log.debug(s"About to send 'printMap' to all the $children")
      context.actorSelection("*").forward(msg)
    case _@msg =>
      log.info(s"${this.getClass.getName} received: $msg")
  }


}

object NaoSupervisor {
  /**
   * Used by the Java API
   */
  val app: AldeApplication = new AldeApplication(null)
  val address = "tcp://0.0.0.0:9559"

  /**
   * Create the Props for this actor
   * @return a Props for creating this actor
   */
  def props(): Props = Props(new NaoSupervisor)
}

class SNCmdManagementActor extends Actor with ActorLogging {


  override def preStart() {
    // Create an actor for each driver
    context.actorOf(SNTextToSpeechActor.props(), name = "text")
    context.actorOf(SNMemoryActor.props(), name = "memory")
    //    context.actorOf(SNAudioDeviceActor.props()), name = "audio")
    context.actorOf(SNBehaviorManagerActor.props(), name = "behavior")
    context.actorOf(SNMotionActor.props(), name = "motion")
    //    context.actorOf(SNRobotPoseActor.props), name = "pose")
    //    context.actorOf(SNSoundDetectionActor.props), name = "sound")
    self ! PrintDebug
    log.info("Command manager Initalized.")
  }


  def receive = {
    case PrintDebug =>
      log.info(context.children.foldLeft("Command Manager registered these paths :\n")((b, ref) => s"$b\t$ref\n"))
    case _@msg =>
      log.info(s"${this.getClass.getName} received: $msg")
  }

}

object SNCmdManagementActor {
  /**
   * Create the Props for this actor
   * @return a Props for creating this actor
   */
  def props(): Props = Props(new SNCmdManagementActor)
}

class SNEvtManagementActor extends Actor with ActorLogging {

  import io.nao.scanao.srv.NaoServer._

  var subscriber = new HashMap[String, Set[ActorRef]] with MultiMap[String, ActorRef]


  override def preStart() {
    val session = new Session()

    val eventService: QimessagingService = new EventService(this)

    log.info("EventService Created")

    val objectBuilder: DynamicObjectBuilder = new DynamicObjectBuilder()
    objectBuilder.advertiseMethod("event::m(mmm)", eventService, "events callback")
    val theObject: AldeObject = objectBuilder.`object`()
    eventService.init(theObject)
    val fut = session.connect(NaoSupervisor.address)
    fut.synchronized(fut.wait(TIMEOUT_IN_MILLIS))
    session.registerService("SNEvents", theObject)
    log.info(s"Service ${eventService.getClass.getName} registered")
    log.info(s"connected to: $NaoSupervisor.address")
    log.info("Event manager Initalized.")
    log.debug(context.children.foldLeft("Event Manager registered these paths :\n")((b, ref) => s"$b\t$ref\n"))
  }

  def receive = {
    case SubscribeEvent(event, module, method, callback) =>
      // Register call back function
      try {
        val sessionReg = new Session()
        log.info("Get a new Session and try to connect to the local server...")

        val futp = sessionReg.connect(NaoSupervisor.address)
        futp.synchronized(futp.wait(TIMEOUT_IN_MILLIS))
        log.info("Connected to the QI local server")

        val memory = sessionReg.service("ALMemory")
        log.info("Got memory services")

        memory.call("subscribeToEvent", event, module, method)
        log.info(s"Subscribed to $event , event redirected to the module: $module method: $method")
        sessionReg.close()
        subscriber addBinding(event, callback)
        context.watch(callback)
        log.info(s"I'm watching ${sender()} ...")
        sender ! EventSubscribed(event, module, method)
      } catch {
        case ex: Exception => ex.printStackTrace()
      }
    case UnsubscribeEvent(event, module) =>
      try {
        val sessionReg = new Session()
        log.info("Get a new Session and try to connect to the local server...")

        val futp = sessionReg.connect(NaoSupervisor.address)
        futp.synchronized(futp.wait(TIMEOUT_IN_MILLIS))
        log.info("Connected to the QI local server")

        val memory = sessionReg.service("ALMemory")
        log.info("Got memory services")

        memory.call("unsubscribeToEvent", event, module)
        log.info(s"unsubscribed to $event , event redirected to the module: $module")
        sessionReg.close()
        subscriber removeBinding(event, sender())

      } catch {
        case ex: Exception => ex.printStackTrace()
      }
    case Terminated(who) =>
      log.info(s"The actor $who has been terminated")
      // Remove the ActorRef from all the list of ActorRef. First get all the keys and then remove it
      subscriber.filter(t => t._2.contains(who)).foreach(t => subscriber.removeBinding(t._1, who))
    case PrintMap =>
      log.info(s"The internal map is $subscriber")
    case PrintDebug =>
      log.info(context.children.foldLeft("Event Manager registered these paths :\n")((b, ref) => s"$b\t$ref\n"))
    case "kill" =>
      log.info(s" ${sender()} killed")
    case _@msg =>
      log.info(s"${this.getClass.getName} received: $msg")
  }

  def event(key: java.lang.Object, values: java.lang.Object, msg: java.lang.Object): java.lang.Object = {
    try {
      log.debug(s"event: $key / values: ${values.toString} / message: ${msg.toString}")
      subscriber.getOrElse(key.toString, Set.empty).foreach(_ ! NaoEvent(key.toString, values, msg.toString))
    } catch {
      case e: Exception => e.printStackTrace()
    }
    key
  }
}

object SNEvtManagementActor {
  /**
   * Create the Props for this actor
   * @return a Props for creating this actor
   */
  def props(): Props = Props(new SNEvtManagementActor)
}

class NaoServer extends Bootable {
  // Create the system actor
  val system = ActorSystem("NaoApplication", ConfigFactory.load.getConfig("nao"))


  def startup() {
    //Use the system's dispatcher as ExecutionContext
    import system.dispatcher
    val nao = system.actorOf(NaoSupervisor.props(), name = "nao")
    // Used to get the debug information about the actors variables
    scala.util.Properties.envOrNone("NAO_DEBUG") match {
      case Some(_) => system.scheduler.schedule(2 seconds, 2 seconds, nao, PrintMap)
      case None => system.log.info("scanao not running in debug (use NAO_DEBUG=1 to enable it)")
    }
  }

  def shutdown() {
    system.shutdown()
  }
}

object NaoServer {
  val TIMEOUT = 1
  val TIMEOUT_UNIT = TimeUnit.HOURS
  val TIMEOUT_IN_MILLIS = 10000

}


