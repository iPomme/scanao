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
import io.nao.scanao.msg.behavior.RunBehavior
import io.nao.scanao.msg.behavior.IsBehaviorRunning
import io.nao.scanao.msg.behavior.StopBehavior

class SNBehaviorManagerActor extends Actor with ActorLogging with SNQIMessage {

  log.info("Creating instance of SNBehaviorManagerActor")
  val moduleName = "ALBehaviorManager"

  def receive = {
    case msg: RunBehavior => {
      log.debug(s"About to run the behavior '${msg.name}'")
      val fut: Future[Int] = srv.call[Int]("runBehavior", msg.name)
      sender ! fut.get(TIMEOUT, TIMEOUT_UNIT)
    }
    case msg: StopBehavior => {
      srv.call("stopBehavior", msg.name)
    }
    case msg: IsBehaviorRunning => {
      sender ! srv.call("isBehaviorRunning", msg.name)
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