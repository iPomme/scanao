package io.nao.scanao

import javax.script.{ScriptEngineManager, ScriptEngine}
import com.typesafe.config.ConfigFactory
import akka.pattern.ask
import scala.concurrent.duration._
import scala.concurrent.{Future, Promise, Await}
import akka.util.Timeout
import akka.actor.{Actor, ActorSystem, Props}
import io.nao.scanao.msg.Head
import io.nao.scanao.msg._


object Launcher {
  val robotIP = "sonny.local"
  val robotPort = "2552"
  val remoteAkkaContext = s"akka://NaoApplication@$robotIP:$robotPort"
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
  val keyNote = system.actorOf(Props(new KeynoteCommand), "keynote")

  def init = {
    mot ! motion.Stiffness(Head(stiffness = 1.0f))

  }

  def release = {
    try {
      waitOn(ask(behave, behavior.RunBehavior("WebControl_SitDown")), 150)
      mot ! motion.Stiffness(Head(stiffness = 0.3f))
    } finally {
      system.shutdown()
    }
  }


  play(init) {

//    keyNote ! "Start"
//    val standFuture = ask(behave, behavior.RunBehavior("User/standup"))
    text ! txt.Say("Bonjour, Je crois que je serai mieux debout.")
//    waitOn(standFuture)
//    val presFuture = ask(behave, behavior.RunBehavior("User/PresentationNicolas"))
    text ! txt.Say("Voila, sa fait du bien.")
//    waitOn(presFuture)

//    behave ! behavior.RunBehavior("User/Disappointed")
    text ! txt.Say("Malheureusement, je suis encore un débutant, je ne ferais donc pas trop long.")


//    waitOn(ask(behave, behavior.RunBehavior("PresentationP1_general")))
//    waitOn(ask(behave, behavior.RunBehavior("ScratchLeg")))
//    waitOn(ask(behave, behavior.RunBehavior("PresentationP2_Simplification")))
//
//    val binoFuture = ask(behave, behavior.RunBehavior("Binoculars"))
//    val monitorFuture = ask(text, txt.Say("Comme vous avez pu le voir ce matin, la solution peut être monitorée et fournir de précieuses informations pour les personnes en charge de l'exploitation mais aussi pour les gens du business."))
//    waitOn(monitorFuture)

//    behave ! behavior.RunBehavior("User/Kiss")
    keyNote ! "NextSlide"
    text ! txt.Say("Je vous remercie de votre attention et je vais laisser la parole à Nicolas qui va vous presenter l'agenda.")


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
   * @param action The action to be performed
   * @return
   */
  def waitOn(action: => Future[Any], sec: Int = 180) = {
    Await.result(action, sec seconds)
  }


}

class KeynoteCommand extends Actor {
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
        tell application "Keynote"
          activate
          try
           tell slideshow 1
             start
           end tell
          on error
           activate
          end try
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