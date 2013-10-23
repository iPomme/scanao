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


class SNRobotPoseActor(ip: String = "127.0.0.1", port: Int = 9559) extends Actor with ActorLogging{

  log.info("Creating instance of SNRobotPoseActor")

  def receive = {
//    case ActualPoseAndTime => {
    //      val pose = Pose.tupled(getActualPoseAndTime)
    //      sender ! pose
    //    }
    //    case PosesName => {
    //      val poses = getPoseNames.toList
    //      sender ! poses
    //    }

    case x@_ => {
      log.error("Unknown Message " + x)
    }
  }
}
