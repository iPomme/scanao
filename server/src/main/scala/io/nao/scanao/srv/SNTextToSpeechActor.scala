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

class SNTextToSpeechActor extends Actor with ActorLogging {

  log.info("Creating instance of SNTextToSpeechActor")

  var srv: com.aldebaran.qimessaging.Object = _



  val moduleName = "ALTextToSpeech"
  override def preStart() {
    val session = new Session()
    log.info(s"$moduleName is initializing against QiMessage...")
    val fut = session.connect(NaoSupervisor.address)
    fut.synchronized(fut.wait(TIMEOUT_IN_MILLIS))
    log.info(s"$moduleName about to create the service...")
    srv = session.service(moduleName)
    log.info(s"$moduleName about to call ping.")
    val ping: Boolean = srv.call("ping").get()

    if (!ping) {
      log.error(s"Could not ping $moduleName");
    } else {
      log.info(s"$moduleName initialized");
    }
  }

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
