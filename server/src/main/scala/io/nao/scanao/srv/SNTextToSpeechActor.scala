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

import io.nao.scanao.msg._
import com.aldebaran.proxy.{ALTextToSpeechProxy}
import akka.actor.{ActorLogging, Actor}

//class SNTextToSpeechActor(ip: String = "127.0.0.1", port: Int = 9559) extends ALTextToSpeechProxy(ip, port) with Actor with ActorLogging {
class SNTextToSpeechActor(ip: String = "127.0.0.1", port: Int = 9559) extends Actor with ActorLogging {

  log.info("Creating instance of SNTextToSpeechActor")

  def receive = {
//    case txt.AvailableLanguages => {
//      val languages = getAvailableLanguages.toList
//      sender ! languages
//    }
//    case txt: txt.Say => {
//      say(txt.string)
//      sender ! Done
//    }
//    case txt: txt.SayToFileAndPlay => {
//      val taskId = sayToFileAndPlay(txt.string)
//      sender ! taskId
//    }

    case x@_ => {
      log.error("Unknown Message " + x)
    }
  }
}
