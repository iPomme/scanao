package io.nao.scanao.msg

//import com.github.levkhomich.akka.tracing.TracingSupport

trait NaoMessage extends Serializable

//with TracingSupport

/**
 * Object used to synchronized the Nao calls
 */
object Done extends NaoMessage

/** *******************************************
  * Technical messages
  * ********************************************/

package tech {

import akka.actor.ActorRef

trait TechMessage extends NaoMessage

case class SubscribeEvent(eventName: String, moduleName: String, methodName: String, callback: ActorRef) extends TechMessage

case class EventSubscribed(eventName: String, moduleName: String, methodName: String) extends TechMessage

case class UnsubscribeEvent(eventName: String, moduleName: String) extends TechMessage

case class NaoEvent(eventName: String, values: Any, message: String) extends TechMessage

case object PrintDebug extends TechMessage

case object PrintMap extends TechMessage


}

/** *******************************************
  * Audio
  * ********************************************/
package audioDevice {

trait AudioMessage extends NaoMessage

object DisableEnergyComputation extends AudioMessage

object EnableEnergyComputation extends AudioMessage

object FrontMicEnergy extends AudioMessage

object RearMicEnergy extends AudioMessage

object LeftMicEnergy extends AudioMessage

object RightMicEnergy extends AudioMessage

case class MicEnergyResult(value: Float) extends AudioMessage

}

/** *******************************************
  * Behavior
  * ********************************************/
package behavior {

trait BehaviorMessage extends NaoMessage

case class RunBehavior(name: String) extends BehaviorMessage

case class StopBehavior(name: String) extends BehaviorMessage

object BehaviorNames extends BehaviorMessage

case class IsBehaviorRunning(name: String) extends BehaviorMessage

}

/** *******************************************
  * Memory
  * ********************************************/
package memory {

trait MemoryMessage extends NaoMessage

object DataKeysList extends MemoryMessage

case class Insert(key: String, value: String) extends MemoryMessage

case class DataInMemory(key: String) extends MemoryMessage

case class DataInMemoryAsInt(key: String) extends MemoryMessage

case class DataInMemoryAsString(key: String) extends MemoryMessage

case class DataInMemoryAsBoolean(key: String) extends MemoryMessage

case class DataInMemoryAsFloat(key: String) extends MemoryMessage

case class DataInMemoryAsByteList(key: String) extends MemoryMessage

case class DataInMemoryAsFloatList(key: String) extends MemoryMessage

case class DataInMemoryAsIntList(key: String) extends MemoryMessage

case class DataList(filter: String) extends MemoryMessage

}


/** *******************************************
  * Motion
  * ********************************************/
package motion {

import io.nao.scanao.msg.Hand.Hand
import io.nao.scanao.msg.Space.Space

trait MotionMessage extends NaoMessage

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

trait PoseMessage extends NaoMessage

object ActualPoseAndTime extends PoseMessage

case class Pose(name: String, since: Float) extends PoseMessage

object PosesName extends PoseMessage

}

/** *******************************************
  * Speech
  * ********************************************/
package txt {

trait SpeechMessage extends NaoMessage

object AvailableLanguages extends SpeechMessage

case class Say(string: String) extends SpeechMessage

case class SayToFileAndPlay(string: String) extends SpeechMessage

}
