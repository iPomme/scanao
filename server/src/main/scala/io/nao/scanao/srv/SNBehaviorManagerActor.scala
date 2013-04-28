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

import java.lang.String
import com.aldebaran.proxy.ALProxy
import io.nao.scanao.msg.behavior._
import io.nao.scanao.msg.Done
import akka.actor.{ActorLogging, Actor}

class SNBehaviorManagerActor(ip: String = "127.0.0.1", port: Int = 9559) extends ALProxy("ALBehaviorManager", ip, port) with SNBehaviorManager with Actor with ActorLogging{

  log.info("Creating instance of SNBehaviorManagerActor")

  def receive = {
    case msg: RunBehavior => {
      sender ! runBehavior(msg.name)
    }
    case msg: StopBehavior => {
      stopBehavior(msg.name)
      sender ! Done
    }
    case msg: IsBehaviorRunning => {
      sender ! isBehaviorRunning(msg.name)
    }
    case BehaviorNames => {
      sender ! getBehaviorNames.toList
    }
    case x@_ => {
      log.error("Unknown Message " + x)
    }
  }
}

object SNBehaviorManagerActor {
  System.loadLibrary("JNaoQi")
}
