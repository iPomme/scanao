package io.nao.scanao

import akka.actor.{Props, ActorSystem}
import com.typesafe.config.ConfigFactory
import io.nao.scanao.msg._


/**
 * @author ${user.name}
 */
object App {


  def main(args: Array[String]) {

    println("getting actor system ...")
    val system = ActorSystem("NaoApplication", ConfigFactory.load.getConfig("naoClient"))
    println("getting text to speach actor ...")

    val textToSpeechActor = system.actorFor("akka://NaoApplication@sonny.local:2552/user/nao/text")
    println("About to say hello ...")
    textToSpeechActor ! txt.Say("*******")
    println("bye")
    system.shutdown()

  }

}
