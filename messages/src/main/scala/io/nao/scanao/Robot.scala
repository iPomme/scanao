package io.nao.scanao

import io.nao.scanao.AxisMask.Mask

object JointName extends Enumeration {
  type JointName = Value
  val Head,
  HeadYaw,
  HeadPitch,
  ShoulderPitch,
  ShoulderRoll,
  ElbowYaw,
  ElbowRoll,
  WristYaw,
  Hand,
  HipYawPitch,
  HipRoll,
  HipPitch,
  KneePitch,
  AnklePitch,
  AnkleRoll = Value
}

/**
 * Enumeration definition for motion
 */


object AxisMask extends Enumeration {

  case class Mask(override val id: Int) extends Val(id) {
    def +(x: Mask): Mask = {
      Mask(this.id + x.id)
    }
  }

  val X = Mask(1)
  val Y = Mask(2)
  val Z = Mask(4)
  val WX = Mask(8)
  val WY = Mask(16)
  val WZ = Mask(32)

  val AllPosition = Mask(7)
  val AllRotation = Mask(56)
  val All = Mask(63)

}

object Hand extends Enumeration {

  case class Hand(val name: String) extends Val(name)


  val Left = Hand("LHand")
  val Right = Hand("RHand")
}

object EffectorName extends Enumeration {
  type EffectorName = Value
  val Head,
  LArm,
  RArm,
  LLeg,
  RLeg,
  Torso = Value
}

object Space extends Enumeration {
  type Space = Value
  val Torso,
  World,
  Nao = Value
}

// TODO: does it make sense to have specific H25 defined why not just robot ?
/**
 * Robot structure definition
 */
trait H25 {
  val name: String
  val stiffness: Float
}

trait Joint extends H25 {
  val name: String
  val angle: Float = 0
  val stiffness: Float = 0
  val time: Float = 0
  val controlPoint: Option[ControlPoint] = None
}

trait Effector extends H25 {
  val name: String
  val position6d: Option[Position6D] = None
  val stiffness: Float = 0
  val mask: Mask
}

case class BezierHandle(val interpolationType: Int = 0, val angle: Float = 0, val time: Float = 0)

case class ControlPoint(val angle: Float = 0, val handle1: BezierHandle, val handle2: BezierHandle)

case class Position6D(val x: Float = 0.0f, val y: Float = 0.0f, val z: Float = 0.0f, val wx: Float = 0.0f, val wy: Float = 0.0f, val wz: Float = 0.0f) {

  def toArray(): Array[Float] = Array(x, y, z, wx, wy, wz)

}


case class HeadYaw(name: String = JointName.HeadYaw.toString, override val angle: Float = 0, override val stiffness: Float = 0, override val time: Float = 0, override val controlPoint: Option[ControlPoint] = None) extends Joint

case class HeadPitch(name: String = JointName.HeadPitch.toString, override val angle: Float = 0, override val stiffness: Float = 0, override val time: Float = 0, override val controlPoint: Option[ControlPoint] = None) extends Joint

//TODO: Add all the effectors
/*******
  * Defines the Effectors
  */

//case class ShoulderPitch(angle:Float = 80) extends Joint
//case class ShoulderRoll(angle:Float = 20) extends Joint
//case class ElbowYaw(angle:Float = -80) extends Joint
//case class ElbowRoll(angle:Float = -60) extends Joint
//case class WristYaw(angle:Float = 0) extends Joint
//case class Hand(angle:Float = 0) extends Joint
//case class Arm(joints: Map[String,Joint])

case class Body(name: String = EffectorName.Torso.toString, override val position6d: Option[Position6D] = None, override val stiffness: Float = 0, override val mask: Mask = AxisMask.All) extends Effector

case class Head(name: String = EffectorName.Head.toString, override val position6d: Option[Position6D] = None, override val stiffness: Float = 0, override val mask: Mask = AxisMask.All) extends Effector with Joint

case class LeftArm(name: String = EffectorName.LArm.toString, override val position6d: Option[Position6D] = None, override val stiffness: Float = 0, override val mask: Mask = AxisMask.All) extends Effector

case class RightArm(name: String = EffectorName.RArm.toString, override val position6d: Option[Position6D] = None, override val stiffness: Float = 0, override val mask: Mask = AxisMask.All) extends Effector


//
//case class HipYawPitch(angle:Float = 0) extends Joint
//case class HipRoll(angle:Float = 0) extends Joint
//case class HipPitch(angle:Float = -25) extends Joint
//case class KneePitch(angle:Float = 40) extends Joint
//case class AnklePitch(angle:Float = -20) extends Joint
//case class AnkleRoll(angle:Float = 0) extends Joint
//case class LeftLeg(hipYawPitch:HipYawPitch,hipRoll:HipRoll,hipPitch:HipPitch,kneePitch:KneePitch,anklePitch:AnklePitch,ankleRoll:AnkleRoll)
//case class RightLeg(hipYawPitch:HipYawPitch,hipRoll:HipRoll,hipPitch:HipPitch,kneePitch:KneePitch,anklePitch:AnklePitch,ankleRoll:AnkleRoll)


