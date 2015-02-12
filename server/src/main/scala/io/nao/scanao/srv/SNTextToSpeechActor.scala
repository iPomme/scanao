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

import akka.actor.{Props, ActorLogging, Actor}
import com.aldebaran.qimessaging.Session
import com.github.levkhomich.akka.tracing.ActorTracing
import io.nao.scanao.msg._
import akka.event.LoggingAdapter
import io.nao.scanao.srv.NaoServer._

class SNTextToSpeechActor extends Actor with ActorLogging with SNQIMessage {
  //with ActorTracing {

  log.info("Creating instance of SNTextToSpeechActor")
  val moduleName = "ALTextToSpeech"

  def receive = {
    case txt.AvailableLanguages => {
      val languages = srv.call("getAvailableLanguages").get()
      sender ! languages
    }
    case txt: txt.Say => {
      //      trace.sample(txt, "NaoServer")
      //      trace.record(txt, "Nao Say...")
      srv.call("say", txt.string)
      //      trace.finish(txt)
    }
    case txt: txt.SayToFileAndPlay => {
      val taskId = srv.call("sayToFileAndPlay", txt.string).get()
      sender ! taskId
    }

    case x@_ => {
      log.error("Unknown Message " + x)
    }
  }
}

object SNTextToSpeechActor {
  /**
   * Create the Props for this actor
   * @return a Props for creating this actor
   */
  def props(): Props = Props(new SNTextToSpeechActor)
}