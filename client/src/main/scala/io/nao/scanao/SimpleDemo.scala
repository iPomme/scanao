/*
 * -----------------------------------------------------------------------------
 *  - ScaNao is an open-source enabling Nao's control from Scala code.            -
 *  - At the low level jNaoqi is used to bridge the C++ code with the JVM.        -
 *  -                                                                             -
 *  -  CreatedBy: Nicolas Jorand                                                  -
 *  -       Date: 13 Oct 2013                                                      -
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
import akka.pattern.{ask, pipe}
import akka.kernel.Bootable
import scala.concurrent.duration._
import com.typesafe.config.ConfigFactory
import io.nao.scanao.msg._
import io.nao.scanao.msg.tech.{EventSubscribed, NaoEvent}
import io.nao.scanao.msg.txt.Say
import scala.util._
import akka.util.Timeout
import scala.concurrent.{Future, Await, Promise}
import io.nao.scanao.msg.tech.NaoEvent
import io.nao.scanao.msg.tech.EventSubscribed
import io.nao.scanao.msg.txt.Say
import io.nao.scanao.msg.tech.NaoEvent
import scala.util.Failure
import io.nao.scanao.msg.tech.EventSubscribed
import scala.util.Success
import io.nao.scanao.msg.txt.Say
import concurrent.ExecutionContext.Implicits.global
import io.nao.scanao.SimpleApp._
import java.util.concurrent.TimeUnit

class DemoActor(subscription: Promise[String]) extends Actor with ActorLogging {

  implicit val timeout = Timeout(TIMEOUT_DEFAULT)

  val robotIP = "sonny.local"
  val naoEvt = s"akka://NaoApplication@$robotIP:2552/user/nao/evt"
  val naoCmd = s"akka://NaoApplication@$robotIP:2552/user/nao/cmd"
  val naoText = s"akka://NaoApplication@$robotIP:2552/user/nao/cmd/text"
  val naoMemory = s"akka://NaoApplication@$robotIP:2552/user/nao/cmd/memory"
  val naoBehavior = s"akka://NaoApplication@$robotIP:2552/user/nao/cmd/behavior"

  val naoEvtActor = context.actorFor(naoEvt)
  val naoCmdActor = context.actorFor(naoCmd)
  val naoTxtActor = context.actorFor(naoText)
  val naoMemoryActor = context.actorFor(naoMemory)
  val naoBehaviorActor = context.actorFor(naoBehavior)


  def receive = {
    case "register" => {
      log.info("registering events ...")
//      naoEvtActor ! tech.SubscribeEvent("ALTextToSpeech/TextStarted", "SNEvents", "event", self)
      val future: Future[EventSubscribed] = (naoEvtActor ? tech.SubscribeEvent("ALTextToSpeech/TextStarted", "SNEvents", "event", self))(Timeout(10, TimeUnit.SECONDS)).mapTo[EventSubscribed]
//      val returned = Await.result(future, scala.concurrent.duration.Duration.Inf)
//      log.info(returned.toString)
      log.info("Return back the future....")
      sender ! future
//      naoEvtActor ! tech.SubscribeEvent("DarknessDetection/DarknessDetected", "SNEvents", "event", self)
//      naoEvtActor ! tech.SubscribeEvent("BacklightingDetection/BacklightingDetected", "SNEvents", "event")
    }
    case EventSubscribed(eventName, values, message) => {
      if (eventName.startsWith("DarknessDetection"))
        subscription.complete(Success(eventName))
    }
    case NaoEvent(eventName, values, message) => {
      log.info(s"received NaoEvent name: $eventName values: $values message: $message")
      if (eventName.startsWith("DarknessDetection"))
        naoTxtActor ! Say("Nuit!")
    }
    case Say(msg) => {
      naoTxtActor ! txt.Say(msg)
    }
    case memory.DataInMemoryAsInt(key) => {
      naoMemoryActor ? memory.DataInMemoryAsInt(key) pipeTo sender
    }
    case memory.DataInMemoryAsString(key) => {
      naoMemoryActor ? memory.DataInMemoryAsString(key) pipeTo sender
    }
    case memory.Insert(key, value) => {
      log.info(s"about to set in memory $value for the key $key")
      naoMemoryActor ! memory.Insert(key, value)
    }
    case memory.DataKeysList => {
      log.info(s"about to get in memory the data keys")
      naoMemoryActor ? memory.DataKeysList pipeTo sender
    }
    case behavior.BehaviorNames => {
      log.info(s"about to get the list of all behaviors")
      naoBehaviorActor ? behavior.BehaviorNames pipeTo sender
    }
    case msg@behavior.RunBehavior => {
      log.info(s"about to get the list of all behaviors")
      naoBehaviorActor ! msg
    }
    case _@msg => {
      log.error(s"Received $msg but don't know what to do with")
    }
  }
}

class SimpleDemo extends Bootable {

  implicit val timeout = Timeout(TIMEOUT_DEFAULT)

  val system = ActorSystem("SimpleDemo", ConfigFactory.load.getConfig("simpleDemo"))

  val promise = Promise[String]()
  val future = promise.future
  val naoProxy = system.actorOf(Props(new DemoActor(promise)), name = "simpleDemo")

  def registerEvent() : Future[tech.EventSubscribed] = {
    val fut = (naoProxy ? "register") (Timeout(20, TimeUnit.SECONDS))
    system.log.info("GOT the future from the nao Proxy !!!")
    fut.mapTo[tech.EventSubscribed]
//    import concurrent.ExecutionContext.Implicits.global
//
//    future.onComplete {
//      case Success(text) =>
//        text
//      case Failure(ex) =>
//        throw ex
//    }
//    val result = Await.result(future, TIMEOUT_DEFAULT)
//    system.log.info(s" registerEvent exit with $result registered")
  }

  def regEvent(){
//    implicit val executor = system.dispatcher
    val ringFuture = registerEvent()
    ringFuture map { event =>
      println(s" ********** $event")
    }
  }

  def writeToMemory(key: String, value: Int) {
    naoProxy ! memory.Insert(key, value.toString)
  }

  def getValueInMemory(key: String) = {
    Await.result(naoProxy.ask(memory.DataInMemoryAsString(key)), TIMEOUT_DEFAULT)
  }

  def getValueInMemoryAsString(key: String) = {
    Await.result(naoProxy.ask(memory.DataInMemoryAsString(key)), TIMEOUT_DEFAULT)
  }

  def getListKeysInMemory() = {
    Await.result(naoProxy.ask(memory.DataKeysList), TIMEOUT_DEFAULT)
  }

  def getBehaviorList() = {
    Await.result(naoProxy.ask(behavior.BehaviorNames), TIMEOUT_DEFAULT)
  }

  def runBehavior(name: String) {
    naoProxy ! behavior.RunBehavior("User/say Hello")
  }

  def say(something: String) {
    naoProxy ! txt.Say(something)
  }

  def startup() = {}

  def shutdown() = {
    system.shutdown()
  }
}

object SimpleApp {

  val TIMEOUT_DEFAULT = 15 seconds

  def main(args: Array[String]) {
    val demo = new SimpleDemo
//    println("Started Simple Demo")
//    demo.regEvent()
//    demo.say("bobo")
//    waitFor(2000)
//    val listOfKey = demo.getListKeysInMemory()
//    println(s"list of memory keys is :\n$listOfKey ")
//    val key = "thisIs"
//    val valueBefore = demo.getValueInMemory(key)
//    println(s"Got in memory before to write for the key '$key' the value $valueBefore")
//    val (writeTime, dummy) = time {
//      demo.writeToMemory(key, Random.nextInt(100))
//    }
//    println(s"took $writeTime to write in memory")
//    waitFor(1000)
//    val (readTime, valueAfter) = time {
//      demo.getValueInMemory(key)
//    }
//    println(s"Got in memory after write for the key '$key' the value $valueAfter")
//    println(s"took $readTime to read from memory")
//    val keyArray = "ALTextToSpeech/Status" // VisualSpaceHistory/VisualGrid/Data"
//    val mixedArray = demo.getValueInMemoryAsString(keyArray)
//    println(s"The memory with key $keyArray is ${mixedArray}")

    println(s"List of all behavior is: /n ${demo.getBehaviorList()}")

    waitFor(100000)
    demo.shutdown()
  }

  def waitFor(howLong: Int) {
    var i = howLong / 10
    while (i != 0) {
      Thread.sleep(10)
      i -= 1
    }
  }

  def time(body: => Any): (Long, Any) = {
    val start = System.currentTimeMillis()
    val ret = body
    (System.currentTimeMillis() - start, ret)
  }
}
