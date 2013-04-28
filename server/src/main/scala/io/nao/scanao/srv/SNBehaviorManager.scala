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
import com.aldebaran.proxy.Variant


trait SNBehaviorManager extends SNProxy {

  def addDefaultBehavior(prefixedBehavior: String) {
    val vprefixedBehavior: Variant = new Variant(prefixedBehavior)
    call("addDefaultBehavior", vprefixedBehavior)
  }

  def getBehaviorNames: Array[String] = {
    val result: Variant = call("getBehaviorNames")
    result.toStringArray.asInstanceOf[Array[String]]
  }

  def getDefaultBehaviors: Array[String] = {
    val result: Variant = call("getDefaultBehaviors")
    result.toStringArray.asInstanceOf[Array[String]]
  }

  def getRunningBehaviors: Array[String] = {
    val result: Variant = call("getRunningBehaviors")
    result.toStringArray.asInstanceOf[Array[String]]
  }

  def getSystemBehaviorNames: Array[String] = {
    val result: Variant = call("getSystemBehaviorNames")
    result.toStringArray.asInstanceOf[Array[String]]
  }

  def getUserBehaviorNames: Array[String] = {
    val result: Variant = call("getUserBehaviorNames")
    result.toStringArray.asInstanceOf[Array[String]]
  }

  def isBehaviorPresent(prefixedBehavior: String): Boolean = {
    val vprefixedBehavior: Variant = new Variant(prefixedBehavior)
    val result: Variant = call("isBehaviorPresent", vprefixedBehavior)
    result.toBoolean
  }

  def isBehaviorRunning(prefixedBehavior: String): Boolean = {
    val vprefixedBehavior: Variant = new Variant(prefixedBehavior)
    val result: Variant = call("isBehaviorRunning", vprefixedBehavior)
    result.toBoolean
  }

  def playDefaultProject() {
    call("playDefaultProject")
  }

  def preloadBehavior(prefixedBehavior: String): Boolean = {
    val vprefixedBehavior: Variant = new Variant(prefixedBehavior)
    val result: Variant = call("preloadBehavior", vprefixedBehavior)
    result.toBoolean
  }

  def removeBehavior(prefixedBehavior: String): Boolean = {
    val vprefixedBehavior: Variant = new Variant(prefixedBehavior)
    val result: Variant = call("removeBehavior", vprefixedBehavior)
    result.toBoolean
  }

  def removeDefaultBehavior(prefixedBehavior: String) {
    val vprefixedBehavior: Variant = new Variant(prefixedBehavior)
    call("removeDefaultBehavior", vprefixedBehavior)
  }

  def runBehavior(prefixedBehavior: String): Option[Int] = {
    val vprefixedBehavior: Variant = new Variant(prefixedBehavior)
    Option(call("runBehavior", vprefixedBehavior).toInt)
  }

  def stopAllBehaviors() {
    call("stopAllBehaviors")
  }

  def stopBehavior(prefixedBehavior: String) {
    val vprefixedBehavior: Variant = new Variant(prefixedBehavior)
    call("stopBehavior", vprefixedBehavior)
  }

}
