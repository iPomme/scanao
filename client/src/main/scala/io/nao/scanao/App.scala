package io.nao.scanao

import akka.actor.{ActorSelection, Props, ActorSystem}
import com.typesafe.config.ConfigFactory
import io.nao.scanao.msg._
import akka.util.Timeout
import scala.concurrent.duration._
import akka.pattern.ask
import scala.concurrent.{Await, ExecutionContext, Future}
import ExecutionContext.Implicits.global
import scala.util.{Failure, Success}


/**
 * @author Nicolas Jorand
 */
object App {

  implicit val timeout = Timeout(15 seconds)

  println("getting actor system ...")

  val customConf = ConfigFactory.parseString("""

                 |akka {
                 |
                 |  actor {
                 |    provider = "akka.remote.RemoteActorRefProvider"
                 |  }
                 |
                 |  remote {
                 |    netty {
                 |      hostname = "0.0.0.0"
                 |      remote.netty.port = 0
                 |
                 |    }
                 |  }
                 |
                 |  # Event handlers to register at boot time (Logging$DefaultLogger logs to STDOUT)
                 |    event-handlers = ["akka.event.slf4j.Slf4jLogger"]
                 |
                 |    # Log level used by the configured loggers (see "event-handlers") as soon
                 |    # as they have been started; before that, see "stdout-loglevel"
                 |    # Options: ERROR, WARNING, INFO, DEBUG
                 |    loglevel = DEBUG
                 |
                 |    # Log level for the very basic logger activated during AkkaApplication startup
                 |    # Options: ERROR, WARNING, INFO, DEBUG
                 |    stdout-loglevel = DEBUG
                 |}
                                             """.stripMargin)

  val system = ActorSystem("NaoClientApplication", ConfigFactory.load(customConf))

  def main(args: Array[String]) {
//    testEvents
    testSpeech
    testPosition
//    testBehavior
//    testMemory
    //system.shutdown()

  }
  def testEvents = {
      val eventManager = system.actorFor("akka.tcp://NaoApplication@127.0.0.1:2552/user/nao/evt")
//      eventManager ! tech.SubscribeEvent("ALTextToSpeech/TextStarted", "SNEvents", "event")
      while (true) Thread.sleep(100)
  }

  def testBehavior = {
    val behaviorActor = system.actorFor("akka.tcp://NaoApplication@sonny.local:2552/user/nao/behavior")
    // Create the future having the list of the behaviors
    val resultsFuture: Future[List[String]] = (behaviorActor ? behavior.BehaviorNames).mapTo[List[String]]

    // Defines a callBack fonction for the future
    resultsFuture.onComplete {
      case Success(values) => println("From Callback: instance is " + values.hashCode());println(values.foreach(behavior => println(s"From Callback: $behavior")))
      case Failure(t) => println(s"An error occured: $t.getMessage")
    }

    // Await the response to not exit without the result
    val results = Await.result(resultsFuture, 10 second)
    // The result is the same as in the callback function
    println("Instance is "+results.hashCode())
    results.foreach(println)

  }

  def testPosition = {
    val motionActor = system.actorSelection("akka.tcp://NaoApplication@192.168.1.72:2552/user/nao/cmd/motion").resolveOne(1 second)

    motionActor onComplete {
      case Success(ref) => {
        ref ! motion.Stiffness(LeftArm(stiffness = 1.0f))
        ref ! motion.OpenHand(Hand.Left)
        ref ! motion.CloseHand(Hand.Left)
        ref ! motion.Stiffness(LeftArm(stiffness = 0.0f))
      }
      case Failure(failure) => println(failure)
    }

  }

  def testSpeech = {
    println("getting text to speech actor ...")
    val textToSpeechActor = system.actorSelection("akka.tcp://NaoApplication@192.168.1.72:2552/user/nao/cmd/text").resolveOne(1 second)
    println("About to something ...")
    textToSpeechActor onComplete {
      case Success(ref) => {
        ref ! txt.Say("Bon, maintenant je sais communiquer avec Akka, c'est pas sorcier!")
        ref ! txt.Say("1")
        ref ! txt.Say("2")
      }
      case Failure(failure) => println(failure)
    }


    //    textToSpeechActor ! "Blah"
    //    textToSpeechActor ! txt.Say("Bon, maintenant je sais communiquer avec utilisant Akka, c'est pas sorcier!")
  }

  def testMemory = {
    println("getting memory actor ...")
    val memoryActor = system.actorFor("akka.tcp://NaoApplication@sonny.local:2552/user/nao/memory")
    val memoryKey = "myTest"
    val badMemFuture: Future[String] = ask(memoryActor, memory.DataInMemoryAsString(memoryKey)).mapTo[String]
    badMemFuture onComplete {
      case Success(value) => println(s"Before setting the memory: $value")
      case Failure(t) => println(s"An error occured: $t.getMessage")
    }

    memoryActor ! memory.Insert(memoryKey, this.hashCode().toString)

    val memFuture: Future[String] = ask(memoryActor, memory.DataInMemoryAsString(memoryKey)).mapTo[String]
    memFuture onComplete  {
      case Success(x) => x
      case Failure(t) => "ERROR"
    }
    val theValue = Await.result(memFuture, 10 second)
    println(s"After setting the memory: $theValue")

  }

}
