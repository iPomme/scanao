package io.nao.scanao.msg

/**
 * Object used to synchronized the Nao calls
 */
object Done extends Serializable

/*********************************************
 * Audio
 *********************************************/
package audioDevice {

object DisableEnergyComputation extends Serializable

object EnableEnergyComputation extends Serializable

object FrontMicEnergy extends Serializable

object RearMicEnergy extends Serializable

object LeftMicEnergy extends Serializable

object RightMicEnergy extends Serializable

case class MicEnergyResult(value: Float)

}

/*********************************************
 * Behavior
 *********************************************/
package behavior {

case class RunBehavior(name: String)

case class StopBehavior(name: String)

object BehaviorNames extends Serializable

case class IsBehaviorRunning(name: String)

}

/*********************************************
 * Memory
 *********************************************/
package memory {

object DataKeysList extends Serializable

case class Insert(key: String, value: String)

case class DataInMemoryAsInt(key: String)

case class DataInMemoryAsString(key: String)

case class DataInMemoryAsBoolean(key: String)

case class DataInMemoryAsFloat(key: String)

case class DataInMemoryAsByteList(key: String)

case class DataInMemoryAsFloatList(key: String)

case class DataInMemoryAsIntList(key: String)

case class DataList(filter: String)

}


/*********************************************
 * Motion
 *********************************************/
package motion {

import io.nao.scanao.msg.Hand.Hand
import io.nao.scanao.msg.Space.Space

case class Stiffness(joint: H25)

case class AngleInterpolation(joints: List[Joint], absolute: Boolean)

case class AngleInterpolationBezier(joints: List[Joint])

case class AngleInterpolationWithSpeed(joints: List[Joint], maxSpeed: Float)

case class AreResourcesAvailable(resourceNames: List[Joint])

case class ChangeAngles(joints: List[Joint], maxSpeed: Float)

case class ChangePosition(effectorName: Effector, space: Space, fractionMaxSpeed: Float)

case class CloseHand(hand: Hand)

case class OpenHand(hand: Hand)

}

/*********************************************
 * Pose
 *********************************************/
package robotPose {

object ActualPoseAndTime extends Serializable

case class Pose(name: String, since: Float)

object PosesName extends Serializable

}

/*********************************************
 * Speech
 *********************************************/
package txt {

object AvailableLanguages extends Serializable

case class Say(string: String)

case class SayToFileAndPlay(string: String)

}
