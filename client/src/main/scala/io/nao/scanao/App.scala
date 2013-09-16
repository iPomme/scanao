package io.nao.scanao

import akka.actor.{Props, ActorSystem}
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

  val system = ActorSystem("NaoApplication", ConfigFactory.load.getConfig("naoClient"))

  def main(args: Array[String]) {
    testSpeech
//    testPosition
//    testBehavior
//    testMemory
    system.shutdown()

  }
  def testBehavior = {
    val behaviorActor = system.actorFor("akka://NaoApplication@sonny.local:2552/user/nao/behavior")
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
    val motionActor = system.actorFor("akka://NaoApplication@sonny.local:2552/user/nao/motion")
    motionActor ! motion.Stiffness(LeftArm(stiffness = 1.0f))
    motionActor ! motion.OpenHand(Hand.Left)
    motionActor ! motion.CloseHand(Hand.Left)
    motionActor ! motion.Stiffness(LeftArm(stiffness = 0.0f))
  }

  def testSpeech = {
    println("getting text to speech actor ...")
    val textToSpeechActor = system.actorFor("akka://NaoApplication@sonny.local:2552/user/nao/text")
    println("About to say hello ...")
    textToSpeechActor ! txt.Say("Bon, maintenant je sais communiquer avec utilisant Akka, c'est pas sorcier!")
  }

  def testMemory = {
    println("getting memory actor ...")
    val memoryActor = system.actorFor("akka://NaoApplication@sonny.local:2552/user/nao/memory")
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
