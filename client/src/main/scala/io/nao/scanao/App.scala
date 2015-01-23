package io.nao.scanao

import akka.actor.ActorSystem
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import io.nao.scanao.msg._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Success}


/**
 * @author Nicolas Jorand
 */
object App {

  implicit val timeout = Timeout(15 seconds)

  println("getting actor system ...")

  val customConf = ConfigFactory.parseString( """

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

    Future.sequence(List(testSpeech, testPosition)).onComplete {
      case Success(_) => system.shutdown()
      case Failure(fail) => println(fail)
    }


  }


  def testPosition: Future[Unit] = {
    val done = Promise[Unit]()
    val motionActor = system.actorSelection("akka.tcp://NaoApplication@sonny.local:2552/user/nao/cmd/motion").resolveOne()

    motionActor onComplete {
      case Success(ref) => {
        ref ! motion.Stiffness(LeftArm(stiffness = 1.0f))
        ref ! motion.OpenHand(Hand.Left)
        ref ! motion.CloseHand(Hand.Left)
        ref ! motion.Stiffness(LeftArm(stiffness = 0.0f))
        done.success()
      }
      case Failure(failure) => {
        println(failure)
        done.failure(failure)
      }
    }
    done.future
  }

  def testSpeech: Future[Unit] = {
    val done: Promise[Unit] = Promise[Unit]()
    println("getting text to speech actor ...")
    val textToSpeechActor = system.actorSelection("akka.tcp://NaoApplication@sonny.local:2552/user/nao/cmd/text").resolveOne()
    println("About to something ...")
    textToSpeechActor onComplete {
      case Success(ref) => {
        ref ! txt.Say("Bon, maintenant je sais communiquer avec Akka, c'est pas sorcier!")
        ref ! txt.Say("1")
        ref ! txt.Say("2")
        done.success()
      }
      case Failure(failure) => {
        println(failure)
        done.failure(failure)
      }
    }
    done.future
  }


}
