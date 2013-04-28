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

trait SNMemory extends SNProxy {

  /* TODO: Move to traits and Change API if needed */
  def declareEvent(eventName: String): Unit = {
    var veventName: Variant = null
    veventName = new Variant(eventName)
    var result: Variant = call("declareEvent", veventName)
  }

  /* TODO: Move to traits and Change API if needed */
  def declareEvent(eventName: String, extractorName: String): Unit = {
    var veventName: Variant = null
    veventName = new Variant(eventName)
    var vextractorName: Variant = null
    vextractorName = new Variant(extractorName)
    var result: Variant = call("declareEvent", veventName, vextractorName)
  }

  /**
  <summary>
  Gets the value of a key-value pair stored in memory
   </summary>
  <param name="key"> Name of the value. </param>
  <returns> The data as an ALValue. This can often be cast transparently into the original type. </returns>
    */
  def getData(key: String): Variant = {
    var vkey: Variant = null
    vkey = new Variant(key)
    call("getData", vkey)
  }

  /* TODO: Move to traits and Change API if needed */
  def getData(key: String, deprecatedParameter: Int): Variant = {
    var vkey: Variant = null
    vkey = new Variant(key)
    var vdeprecatedParameter: Variant = null
    vdeprecatedParameter = new Variant(deprecatedParameter)
    var result: Variant = call("getData", vkey, vdeprecatedParameter)
    return result
  }

  /* TODO: Move to traits and Change API if needed */
  def getDataList(filter: String): Array[String] = {
    var vfilter: Variant = null
    vfilter = new Variant(filter)
    var result: Variant = call("getDataList", vfilter)
    return result.toStringArray.asInstanceOf[Array[String]]
  }

  /* TODO: Move to traits and Change API if needed */
  def getDataListName: Array[String] = {
    var result: Variant = call("getDataListName")
    return result.toStringArray.asInstanceOf[Array[String]]
  }

  /* TODO: Move to traits and Change API if needed */
  def getDataOnChange(key: String, deprecatedParameter: Int): Variant = {
    var vkey: Variant = null
    vkey = new Variant(key)
    var vdeprecatedParameter: Variant = null
    vdeprecatedParameter = new Variant(deprecatedParameter)
    var result: Variant = call("getDataOnChange", vkey, vdeprecatedParameter)
    return result
  }

  /* TODO: Move to traits and Change API if needed */
  def getDataPtr(key: String): Unit = {
    var vkey: Variant = null
    vkey = new Variant(key)
    var result: Variant = call("getDataPtr", vkey)
  }

  /* TODO: Move to traits and Change API if needed */
  def getEventList: Array[String] = {
    var result: Variant = call("getEventList")
    return result.toStringArray.asInstanceOf[Array[String]]
  }

  /* TODO: Move to traits and Change API if needed */
  def getExtractorEvent(extractorName: String): Array[String] = {
    var vextractorName: Variant = null
    vextractorName = new Variant(extractorName)
    var result: Variant = call("getExtractorEvent", vextractorName)
    return result.toStringArray.asInstanceOf[Array[String]]
  }

  /* TODO: Move to traits and Change API if needed */
  def getListData(keyList: Variant): Variant = {
    var vkeyList: Variant = null
    vkeyList = new Variant(keyList)
    var result: Variant = call("getListData", vkeyList)
    return result
  }

  /* TODO: Move to traits and Change API if needed */
  def getMicroEventList: Array[String] = {
    var result: Variant = call("getMicroEventList")
    return result.toStringArray.asInstanceOf[Array[String]]
  }

  /* TODO: Move to traits and Change API if needed */
  def getSubscribers(name: String): Array[String] = {
    var vname: Variant = null
    vname = new Variant(name)
    var result: Variant = call("getSubscribers", vname)
    return result.toStringArray.asInstanceOf[Array[String]]
  }

  /* TODO: Move to traits and Change API if needed */
  def getType(key: String): String = {
    var vkey: Variant = null
    vkey = new Variant(key)
    var result: Variant = call("getType", vkey)
    return result.toString
  }

  /* TODO: Move to traits and Change API if needed */
  def insertData(key: String, value: Int): Unit = {
    var vkey: Variant = null
    vkey = new Variant(key)
    var vvalue: Variant = null
    vvalue = new Variant(value)
    var result: Variant = call("insertData", vkey, vvalue)
  }

  /* TODO: Move to traits and Change API if needed */
  def insertData(key: String, value: Float): Unit = {
    var vkey: Variant = null
    vkey = new Variant(key)
    var vvalue: Variant = null
    vvalue = new Variant(value)
    var result: Variant = call("insertData", vkey, vvalue)
  }

  /**
  <summary>
   Inserts a key-value pair into memory, where value is an int
   </summary>
   <param name="key"> Name of the value to be inserted. </param>
   <param name="value"> The int to be inserted </param>
    */
  def insertData(key: String)(value: String): Unit = {
    var vkey: Variant = null
    vkey = new Variant(key)
    var vvalue: Variant = null
    vvalue = new Variant(value)
    vvalue.setVariantType(Variant.typeV.VSTRING)
    var result: Variant = call("insertData", vkey, vvalue)
  }

  /* TODO: Move to traits and Change API if needed */
  def insertData(key: String, data: Variant): Unit = {
    var vkey: Variant = null
    vkey = new Variant(key)
    var vdata: Variant = null
    vdata = new Variant(data)
    var result: Variant = call("insertData", vkey, vdata)
  }

  /* TODO: Move to traits and Change API if needed */
  def insertListData(list: Variant): Unit = {
    var vlist: Variant = null
    vlist = new Variant(list)
    var result: Variant = call("insertListData", vlist)
  }

  /* TODO: Move to traits and Change API if needed */
  def raiseEvent(name: String, value: Variant): Unit = {
    var vname: Variant = null
    vname = new Variant(name)
    var vvalue: Variant = null
    vvalue = new Variant(value)
    var result: Variant = call("raiseEvent", vname, vvalue)
  }

  /* TODO: Move to traits and Change API if needed */
  def raiseMicroEvent(name: String, value: Variant): Unit = {
    var vname: Variant = null
    vname = new Variant(name)
    var vvalue: Variant = null
    vvalue = new Variant(value)
    var result: Variant = call("raiseMicroEvent", vname, vvalue)
  }

  /* TODO: Move to traits and Change API if needed */
  def removeData(key: String): Unit = {
    var vkey: Variant = null
    vkey = new Variant(key)
    var result: Variant = call("removeData", vkey)
  }

  /* TODO: Move to traits and Change API if needed */
  def removeMicroEvent(name: String): Unit = {
    var vname: Variant = null
    vname = new Variant(name)
    var result: Variant = call("removeMicroEvent", vname)
  }

  /* TODO: Move to traits and Change API if needed */
  def subscribeToEvent(name: String, callbackModule: String, callbackMethod: String): Unit = {
    var vname: Variant = null
    vname = new Variant(name)
    var vcallbackModule: Variant = null
    vcallbackModule = new Variant(callbackModule)
    var vcallbackMethod: Variant = null
    vcallbackMethod = new Variant(callbackMethod)
    var result: Variant = call("subscribeToEvent", vname, vcallbackModule, vcallbackMethod)
  }

  /* TODO: Move to traits and Change API if needed */
  def subscribeToEvent(name: String, callbackModule: String, callbackMessage: String, callbacMethod: String): Unit = {
    var vname: Variant = null
    vname = new Variant(name)
    var vcallbackModule: Variant = null
    vcallbackModule = new Variant(callbackModule)
    var vcallbackMessage: Variant = null
    vcallbackMessage = new Variant(callbackMessage)
    var vcallbacMethod: Variant = null
    vcallbacMethod = new Variant(callbacMethod)
    var result: Variant = call("subscribeToEvent", vname, vcallbackModule, vcallbackMessage, vcallbacMethod)
  }

  /* TODO: Move to traits and Change API if needed */
  def subscribeToMicroEvent(name: String, callbackModule: String, callbackMessage: String, callbackMethod: String): Unit = {
    var vname: Variant = null
    vname = new Variant(name)
    var vcallbackModule: Variant = null
    vcallbackModule = new Variant(callbackModule)
    var vcallbackMessage: Variant = null
    vcallbackMessage = new Variant(callbackMessage)
    var vcallbackMethod: Variant = null
    vcallbackMethod = new Variant(callbackMethod)
    var result: Variant = call("subscribeToMicroEvent", vname, vcallbackModule, vcallbackMessage, vcallbackMethod)
  }

  /* TODO: Move to traits and Change API if needed */
  def unregisterModuleReference(moduleName: String): Unit = {
    var vmoduleName: Variant = null
    vmoduleName = new Variant(moduleName)
    var result: Variant = call("unregisterModuleReference", vmoduleName)
  }

  /* TODO: Move to traits and Change API if needed */
  def unsubscribeToEvent(name: String, callbackModule: String): Unit = {
    var vname: Variant = null
    vname = new Variant(name)
    var vcallbackModule: Variant = null
    vcallbackModule = new Variant(callbackModule)
    var result: Variant = call("unsubscribeToEvent", vname, vcallbackModule)
  }

  /* TODO: Move to traits and Change API if needed */
  def unsubscribeToMicroEvent(name: String, callbackModule: String): Unit = {
    var vname: Variant = null
    vname = new Variant(name)
    var vcallbackModule: Variant = null
    vcallbackModule = new Variant(callbackModule)
    var result: Variant = call("unsubscribeToMicroEvent", vname, vcallbackModule)
  }

}
