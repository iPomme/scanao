package io.nao.scanao

import javax.script.{ScriptEngineManager, ScriptEngine}
import com.typesafe.config.ConfigFactory
import akka.pattern.ask
import scala.concurrent.duration._
import scala.concurrent.{Future, Promise, Await}
import akka.util.Timeout
import akka.actor.{Actor, ActorSystem, Props}
import io.nao.scanao.msg._
import concurrent.ExecutionContext.Implicits.global


object Launcher {
  val robotIP = "sonny.local"
  val robotPort = "2552"
  val remoteAkkaContext = s"akka.tcp://NaoApplication@$robotIP:$robotPort"
  val naoEvt = s"$remoteAkkaContext/user/nao/evt"
  val naoCmd = s"$remoteAkkaContext/user/nao/cmd"
  val naoText = s"$remoteAkkaContext/user/nao/cmd/text"
  val naoMotion = s"$remoteAkkaContext/user/nao/cmd/motion"
  val naoMemory = s"$remoteAkkaContext/user/nao/cmd/memory"
  val naoBehavior = s"$remoteAkkaContext/user/nao/cmd/behavior"


  def main(args: Array[String]) {
    new Control()
  }
}

class Control {

  import Launcher._

  val NaoLocation = "sonny.local"
  // Actor setup
  val system = ActorSystem("SoftshakePresentation", ConfigFactory.load.getConfig("softshake_client"))

  implicit val timeout = Timeout(150 seconds)
  // The robots Handles
  val behave = system.actorFor(naoBehavior)
  val text = system.actorFor(naoText)
  val mot = system.actorFor(naoMotion)

  // The keynote
  val keyNote = system.actorOf(Props(new PreziCommand), "keynote")

  def init = {
    //        mot ! motion.Stiffness(Head(stiffness = 1.0f))

  }

  def release = {
    try {
      behave ! behavior.RunBehavior("User/sitdown")
      waitFor(2)
      waitOn("User/sitdown")
      mot ! motion.Stiffness(Head(stiffness = 0.3f))
      waitFor(2)
    } finally {
      system.shutdown()
    }
  }


  play(init) {

//    keyNote ! "Start"
//
//    behave ! behavior.RunBehavior("User/softshake_p1")
//    waitFor(5)
//    waitOn("User/softshake_p1")
//    system.log.info("****** Premiere partie finie *********")
//
//    text ! txt.Say("Je crois que je serai mieux debout.")
//    behave ! behavior.RunBehavior("User/standup")
//    waitFor(5)
//    waitOn("User/standup")
//    system.log.info("******  Lever partie finie *********")
//    text ! txt.Say("Voila, sa fait du bien.")
//
//    keyNote ! "NextSlide"

//    behave ! behavior.RunBehavior("User/softshake_p2")
//    waitFor(10)
//    waitOn("User/softshake_p2")
//    system.log.info("****** Deuxieme partie finie *********")

    keyNote ! "NextSlide"
    text ! txt.Say("Au fait, avez-vous remarqué que c'est moi qui change les diapo. C'est coule non! ")

    behave ! behavior.RunBehavior("User/softshake_p3")
    waitFor(10)
    waitOn("User/softshake_p3")
    system.log.info("****** Troisieme partie finie *********")

    keyNote ! "NextSlide"

    text ! txt.Say("Voila, je laisse la parole a Nicolas qui va vous expliquer les details d'implémentation.")


  }(release)

  /**
   * This is the pattern used for the whole process
   * @param init
   * @param job
   * @param post
   * @return
   */
  def play(init: => Unit)(job: => Unit)(post: => Unit) = {
    init
    try {
      job
    } catch {
      case e: Exception => println(e)
    }
    post
  }

  /**
   * Wait till the action has been executed.
   */
  def waitOn(behaviorName: String, sec: Int = 3) = {
    while (Await.result(behave ? behavior.IsBehaviorRunning(behaviorName), sec seconds).asInstanceOf[Boolean]) {
      Thread.sleep(300)
    }
  }

  /**
   * Wait till the action has been executed.
   */
  def waitFor(sec: Int = 3) = {
    var i = sec * 10
    while (i > 0) {
      Thread.sleep(100)
      i -= 1
    }
  }


}

class PreziCommand extends Actor {
  val scriptEngine: ScriptEngine = new ScriptEngineManager().getEngineByName("AppleScript")


  override def receive = {
    case "Start" => {
      start
    }
    case "NextSlide" => {
      nextSlide
    }
  }

  def start {
    try {
      val script: String =
        """
        tell application "PreziDesktop"
          activate
        end tell
        """
      scriptEngine.eval(script)
    } catch {
      case e: Exception => e.printStackTrace()
    }
  }

  def nextSlide {
    try {
      val script: String = """
                        tell application "System Events"
                          keystroke (ASCII character 29) -- Right
                        end tell
                           """
      scriptEngine.eval(script)
    } catch {
      case e: Exception => e.printStackTrace()
    }
  }
}