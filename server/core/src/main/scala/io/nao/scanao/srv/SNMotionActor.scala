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
import io.nao.scanao.msg.motion.{Stiffness, OpenHand, CloseHand}

//import com.github.levkhomich.akka.tracing.ActorTracing

/**
 * This package is used for syntactic sugar when sending a message to thi Actor
 */

class SNMotionActor extends Actor with ActorLogging with SNQIMessage {
  //} with ActorTracing {

  log.info("Creating instance of SNMemoryActor")

  val moduleName = "ALMotion"


  def receive = {
    case data: Stiffness => {
      srv.call("setStiffnesses", data.joint.name, data.joint.stiffness.asInstanceOf[java.lang.Float])
    }
    //    case data: AngleInterpolation => {
    //      angleInterpolation(data.joints, data.absolute)
    //      sender ! Done
    //    }
    //    case data: AngleInterpolationBezier => {
    //      angleInterpolationBezier(data.joints)
    //      sender ! Done
    //    }
    //    case data: AngleInterpolationWithSpeed => {
    //      angleInterpolationWithSpeed(data.joints, data.maxSpeed)
    //      sender ! Done
    //    }
    //    case data: ChangeAngles => {
    //      changeAngles(data.joints, data.maxSpeed)
    //      sender ! Done
    //    }
    //    case data: ChangePosition => {
    //      changePosition(data.effectorName, data.space, data.fractionMaxSpeed)
    //      sender ! Done
    //    }
    case data: CloseHand => {
      srv.call("closeHand", data.hand.name)
    }
    case data: OpenHand => {
      srv.call("openHand", data.hand.name)
    }
    //    case data: AreResourcesAvailable => {
    //      sender ! areResourcesAvailable(data.resourceNames)
    //    }

    case x@_ => {
      log.error("Unknown Message " + x)
    }
  }
}

object SNMotionActor {
  /**
   * Create the Props for this actor
   * @return a Props for creating this actor
   */
  def props(): Props = Props(new SNMotionActor)
}

