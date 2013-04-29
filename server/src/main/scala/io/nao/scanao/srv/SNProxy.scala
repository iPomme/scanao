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

trait SNProxy {

//  /**
//  <summary>
//  /// Retrieves a method's description.
//  /// </summary>
//  /// @param methodName The name of the method. </param>
//  /// @return A structure containing the method's description.
//    */
//  def getMethodHelp(methodName: String): SNStructMethodDescription = {
//    val vmethodName = new Variant(methodName)
//    val result: Variant = call("getMethodHelp", vmethodName)
//    val size = result.getSize
//    new SNStructMethodDescription(
//      methodName = result.getElement(0).toString,
//      description = result.getElement(1).toString,
//      returnName = result.getElement(size - 2).toString,
//      returnDescription = result.getElement(size - 1).toString)
//  }
//
//  /**
//  <summary>
//  /// Gets the name of the parent broker.
//  /// </summary>
//  /// @return The name of the parent broker.
//    */
  def getBrokerName: String = {
    val result: Variant = call("getBrokerName")
    result.toString
  }
//
//
//  /**
//  <summary>
//  /// Retrieves the module's method list.
//  /// </summary>
//  /// @return An array of method names.
//    */
//  def getMethodList: Array[String] = {
//    val result: Variant = call("getMethodList")
//    result.toStringArray.asInstanceOf[Array[String]]
//  }
//
//  /**
//  <summary>
//  /// Retrieves the module's description.
//  /// </summary>
//  /// @return A structure describing the module.
//    */
//  def getModuleHelp: SNStructModuleDescription = {
//    val result: Variant = call("getModuleHelp")
//    if (result.getSize > 2) {
//      //We have examples
//      // TODO: implements modules example
//    }
//    new SNStructModuleDescription(description = result.getElement(0).toString)
//  }
//
//  /**
//  <summary>
//  /// Gets the method usage string. This summarise how to use the method.
//  /// </summary>
//  /// @param name The name of the method. </param>
//  /// @return A string that summarises the usage of the method.
//    */
//  def getUsage(name: String): String = {
//    val vname = new Variant(name)
//    val result: Variant = call("getUsage", vname)
//    result.toString
//  }
//
//  /**
//  <summary>
//  /// Exits and unregisters the module.
//  </summary>
//    */
//  def exit() {
//    call("exit")
//  }
//
//  /**
//  <summary>
//  /// Returns true if the method is currently running.
//  /// </summary>
//  /// @param id The ID of the method that was returned when calling the method using 'post' </param>
//  /// @return True if the method is currently running
//    */
//  def isRunning(id: Int): Boolean = {
//    val vid = new Variant(id)
//    val result: Variant = call("isRunning", vid)
//    result.toBoolean
//  }
//
//  /**
//  <summary>
//  /// Just a ping. Always returns true
//  /// </summary>
//  /// @return returns true
//    */
//  def ping: Boolean = {
//    val result: Variant = call("ping")
//    result.toBoolean
//  }
//
//  /**
//  <summary>
//  /// returns true if the method is currently running
//  /// </summary>
//  /// @param id the ID of the method to wait for
//    */
//  def stop(id: Int) {
//    val vid = new Variant(id)
//    call("stop", vid)
//  }
//
//  /**
//  <summary>
//  /// Returns the version of the module.
//  /// </summary>
//  /// @return A string containing the version of the module.
//    */
//  def version: String = {
//    val result: Variant = call("version")
//    result.toString
//  }
//
//  /**
//  <summary>
//  /// Wait for the end of a long running method that was called using 'post'
//  /// </summary>
//  /// @param id The ID of the method that was returned when calling the method using 'post' definitely, use a timeoutPeriod of zero. </param>
//  /// @return True if the timeout period terminated. False if the method returned.
//    */
//  def wait(id: Int, timeoutPeriod: Int): Boolean = {
//    val vid = new Variant(id)
//    val vtimeoutPeriod = new Variant(timeoutPeriod)
//    val result: Variant = call("wait", vid, vtimeoutPeriod)
//    result.toBoolean
//  }
//
//  //Abstract Methods
//
  def call(method: String): Variant
//
//  def call(method: String, val0: Variant): Variant
//
//  def call(method: String, val1: Variant, val2: Variant): Variant
//
//  def call(method: String, val1: Variant, val2: Variant, val3: Variant): Variant
//
//  def call(method: String, val1: Variant, val2: Variant, val3: Variant, val4: Variant): Variant
//
//  def call(method: String, val1: Variant, val2: Variant, val3: Variant, val4: Variant, val5: Variant): Variant
//
//  def call(method: String, val1: Variant, val2: Variant, val3: Variant, val4: Variant, val5: Variant, val6: Variant): Variant

}
