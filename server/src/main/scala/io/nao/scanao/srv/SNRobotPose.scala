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

import com.aldebaran.proxy.Variant

trait SNRobotPose extends SNProxy {

  /**
  <summary>
    Get the actual robot pose and the time since this pose was activate.
  </summary>
  @return A tuple array of size 2. With first a string of the robot pose and then a float with the time in second since this pose is activated.
    */
  def getActualPoseAndTime: (String, Float) = {
    val result: Variant = call("getActualPoseAndTime")
    (result.getElement(0).toString, result.getElement(1).toFloat)
  }

  /**
  <summary>
   Get the full list of pose possibly  by this module.
   </summary>
   @return A ALValue array of string containing the possible Poses.
    */
  def getPoseNames: Array[String] = {
    val result: Variant = call("getPoseNames")
    result.toStringArray.asInstanceOf[Array[String]]
  }

}
