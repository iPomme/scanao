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
import io.nao.scanao.msg.behavior.{BehaviorNames, IsBehaviorRunning, StopBehavior, RunBehavior}
import com.aldebaran.qimessaging.Future
import io.nao.scanao.srv.NaoServer._
import io.nao.scanao.msg._
import io.nao.scanao.msg.behavior.RunBehavior
import io.nao.scanao.msg.behavior.IsBehaviorRunning
import io.nao.scanao.msg.behavior.StopBehavior
import java.util.concurrent.TimeUnit

class SNBehaviorManagerActor extends Actor with ActorLogging with SNQIMessage {

  log.info("Creating instance of SNBehaviorManagerActor")
  val moduleName = "ALBehaviorManager"

  def receive = {
    case msg: RunBehavior => {
      log.debug(s"About to run the behavior '${msg.name}'")
      srv.call("runBehavior", msg.name)
    }
    case msg: StopBehavior => {
      srv.call("stopBehavior", msg.name)
    }
    case msg: IsBehaviorRunning => {
      log.debug(s"About to run the isBehaviorRunning for '${msg.name}'")
      val fut: Future[Boolean] = srv.call[Boolean]("isBehaviorRunning", msg.name)
      sender ! fut.get(TIMEOUT, TIMEOUT_UNIT)
    }
    case BehaviorNames => {
      val fut = srv.call("getBehaviorNames")
      sender ! fut.get(TIMEOUT, TIMEOUT_UNIT)
    }
    case x@_ => {
      log.error("Unknown Message " + x)
    }
  }

}