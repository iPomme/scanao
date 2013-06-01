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

import scala.Enumeration
import com.aldebaran.proxy.ALProxy
import io.nao.scanao.msg.motion._
import io.nao.scanao.msg.Done
import akka.actor.{ActorLogging, Actor}

/**
 * This package is used for syntactic sugar when sending a message to thi Actor
 */

class SNMotionActor(ip: String = "127.0.0.1", port: Int = 9559) extends ALProxy("ALMotion", ip, port) with SNMotion with Actor with ActorLogging {

  log.info("Creating instance of SNMotionActor")

  def receive = {
    case data: Stiffness => {
      setStiffnesses(data.joint)
      sender ! Done
    }
    case data: AngleInterpolation => {
      angleInterpolation(data.joints, data.absolute)
      sender ! Done
    }
    case data: AngleInterpolationBezier => {
      angleInterpolationBezier(data.joints)
      sender ! Done
    }
    case data: AngleInterpolationWithSpeed => {
      angleInterpolationWithSpeed(data.joints, data.maxSpeed)
      sender ! Done
    }
    case data: ChangeAngles => {
      changeAngles(data.joints, data.maxSpeed)
      sender ! Done
    }
    case data: ChangePosition => {
      changePosition(data.effectorName, data.space, data.fractionMaxSpeed)
      sender ! Done
    }
    case data: CloseHand => {
      closeHand(data.hand)
      sender ! Done
    }
    case data: OpenHand => {
      openHand(data.hand)
      sender ! Done
    }
    case data: AreResourcesAvailable => {
      sender ! areResourcesAvailable(data.resourceNames)
    }

    case x@_ => {
      log.error("Unknown Message " + x)
    }
  }
}

