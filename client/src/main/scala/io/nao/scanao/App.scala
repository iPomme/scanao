package io.nao.scanao

import akka.actor.{Props, ActorSystem}
import com.typesafe.config.ConfigFactory
import io.nao.scanao.msg._
import akka.util.Timeout
import scala.concurrent.duration._
import akka.pattern.ask
import scala.concurrent.{ExecutionContext, Future}
import ExecutionContext.Implicits.global
import scala.util.{Failure, Success}


/**
 * @author ${user.name}
 */
object App {


  def main(args: Array[String]) {

    println("getting actor system ...")
    val system = ActorSystem("NaoApplication", ConfigFactory.load.getConfig("naoClient"))
    println("getting text to speech actor ...")

    val memoryActor = system.actorFor("akka://NaoApplication@sonny.local:2552/user/nao/memory")
    implicit val timeout = Timeout(15 seconds)
    val memoryKey = "myTest"
    val badMemFutur: Future[String] = ask(memoryActor, memory.DataInMemoryAsString(memoryKey)).mapTo[String]
    badMemFutur onComplete {
      case Success(value) => println(s"Before setting the memory: $value")
      case Failure(t) => println(s"An error occured: $t.getMessage")
    }

    memoryActor ! memory.Insert(memoryKey, "My Value is Valuable")
    val memFutur: Future[String] = ask(memoryActor, memory.DataInMemoryAsString(memoryKey)).mapTo[String]


    val theValue = memFutur onSuccess {
      case v => v
    }
    println(s"After setting the memory: $theValue")

    val textToSpeechActor = system.actorFor("akka://NaoApplication@sonny.local:2552/user/nao/text")
    println("About to say hello ...")
    textToSpeechActor ! txt.Say(".")
    system.shutdown()

  }

}
