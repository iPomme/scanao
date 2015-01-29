package io.nao.scanao.msg


trait NaoMessage extends Serializable

import com.github.levkhomich.akka.tracing.TracingSupport


/**
 * Object used to synchronized the Nao calls
 */
object Done extends NaoMessage

/** *******************************************
  * Technical messages
  * ********************************************/

package tech {

import akka.actor.ActorRef

case class SubscribeEvent(eventName: String, moduleName: String, methodName: String, callback: ActorRef) extends NaoMessage

case class EventSubscribed(eventName: String, moduleName: String, methodName: String) extends NaoMessage

case class UnsubscribeEvent(eventName: String, moduleName: String) extends NaoMessage

case class NaoEvent(eventName: String, values: Any, message: String) extends NaoMessage

case object PrintDebug extends NaoMessage

case object PrintMap extends NaoMessage


}

/** *******************************************
  * Audio
  * ********************************************/
package audioDevice {

object DisableEnergyComputation extends NaoMessage

object EnableEnergyComputation extends NaoMessage

object FrontMicEnergy extends NaoMessage

object RearMicEnergy extends NaoMessage

object LeftMicEnergy extends NaoMessage

object RightMicEnergy extends NaoMessage

case class MicEnergyResult(value: Float)

}

/** *******************************************
  * Behavior
  * ********************************************/
package behavior {

case class RunBehavior(name: String)

case class StopBehavior(name: String)

object BehaviorNames extends NaoMessage

case class IsBehaviorRunning(name: String)

}

/** *******************************************
  * Memory
  * ********************************************/
package memory {

object DataKeysList extends NaoMessage

case class Insert(key: String, value: String)

case class DataInMemory(key: String)

case class DataInMemoryAsInt(key: String)

case class DataInMemoryAsString(key: String)

case class DataInMemoryAsBoolean(key: String)

case class DataInMemoryAsFloat(key: String)

case class DataInMemoryAsByteList(key: String)

case class DataInMemoryAsFloatList(key: String)

case class DataInMemoryAsIntList(key: String)

case class DataList(filter: String)

}


/** *******************************************
  * Motion
  * ********************************************/
package motion {

import io.nao.scanao.msg.Hand.Hand
import io.nao.scanao.msg.Space.Space

trait MotionMessage extends TracingSupport

case class Stiffness(joint: H25) extends MotionMessage

case class AngleInterpolation(joints: List[Joint], absolute: Boolean) extends MotionMessage

case class AngleInterpolationBezier(joints: List[Joint]) extends MotionMessage

case class AngleInterpolationWithSpeed(joints: List[Joint], maxSpeed: Float) extends MotionMessage

case class AreResourcesAvailable(resourceNames: List[Joint]) extends MotionMessage

case class ChangeAngles(joints: List[Joint], maxSpeed: Float) extends MotionMessage

case class ChangePosition(effectorName: Effector, space: Space, fractionMaxSpeed: Float) extends MotionMessage

case class CloseHand(hand: Hand) extends MotionMessage

case class OpenHand(hand: Hand) extends MotionMessage

}

/** *******************************************
  * Pose
  * ********************************************/
package robotPose {

object ActualPoseAndTime extends NaoMessage

case class Pose(name: String, since: Float)

object PosesName extends NaoMessage

}

/** *******************************************
  * Speech
  * ********************************************/
package txt {

object AvailableLanguages extends NaoMessage

case class Say(string: String) extends NaoMessage

case class SayToFileAndPlay(string: String) extends NaoMessage

}
