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

import akka.actor.{ActorLogging, Actor}
import com.aldebaran.qimessaging.Session
import io.nao.scanao.msg._
import akka.event.LoggingAdapter
import io.nao.scanao.srv.NaoServer._

class SNTextToSpeechActor extends Actor with ActorLogging with SNQIMessage {

  log.info("Creating instance of SNTextToSpeechActor")
  val moduleName = "ALTextToSpeech"

  def receive = {
    case txt.AvailableLanguages => {
      val languages = srv.call("getAvailableLanguages").get()
      sender ! languages
    }
    case txt: txt.Say => {
      srv.call("say", txt.string)
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
