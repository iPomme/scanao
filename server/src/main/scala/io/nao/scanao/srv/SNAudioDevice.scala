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

trait SNAudioDevice extends SNProxy {

  /* TODO: Move to traits and Change API if needed */
  def closeAudioInputs: Unit = {
    var result: Variant = call("closeAudioInputs")
    ()
  }

  /* TODO: Move to traits and Change API if needed */
  def closeAudioOutputs: Unit = {
    var result: Variant = call("closeAudioOutputs")
  }

  def disableEnergyComputation: Unit = {
    var result: Variant = call("disableEnergyComputation")
  }

  def enableEnergyComputation: Unit = {
    var result: Variant = call("enableEnergyComputation")
  }

  /* TODO: Move to traits and Change API if needed */
  def flushAudioOutputs: Unit = {
    var result: Variant = call("flushAudioOutputs")
  }

  def getFrontMicEnergy: Float = {
    var result: Variant = call("getFrontMicEnergy")
    return result.toFloat
  }

  def getLeftMicEnergy: Float = {
    var result: Variant = call("getLeftMicEnergy")
    return result.toFloat
  }

  /* TODO: Move to traits and Change API if needed */
  def getOutputVolume: Int = {
    var result: Variant = call("getOutputVolume")
    return result.toInt
  }

  /* TODO: Move to traits and Change API if needed */
  def getParameter(pParamName: String): Int = {
    var vpParamName: Variant = null
    vpParamName = new Variant(pParamName)
    var result: Variant = call("getParameter", vpParamName)
    return result.toInt
  }

  /* TODO: Move to traits and Change API if needed */
  def getRearMicEnergy: Float = {
    var result: Variant = call("getRearMicEnergy")
    return result.toFloat
  }

  def getRightMicEnergy: Float = {
    var result: Variant = call("getRightMicEnergy")
    return result.toFloat
  }

  /* TODO: Move to traits and Change API if needed */
  def openAudioInputs: Unit = {
    var result: Variant = call("openAudioInputs")
  }

  /* TODO: Move to traits and Change API if needed */
  def openAudioOutputs: Unit = {
    var result: Variant = call("openAudioOutputs")
  }

  /* TODO: Move to traits and Change API if needed */
  def playSine(frequence: Int, gain: Int, pan: Int, duration: Float): Unit = {
    var vfrequence: Variant = null
    vfrequence = new Variant(frequence)
    var vgain: Variant = null
    vgain = new Variant(gain)
    var vpan: Variant = null
    vpan = new Variant(pan)
    var vduration: Variant = null
    vduration = new Variant(duration)
    var result: Variant = call("playSine", vfrequence, vgain, vpan, vduration)
  }

  /* TODO: Move to traits and Change API if needed */
  def resetAudio: Unit = {
    var result: Variant = call("resetAudio")
  }

  /* TODO: Move to traits and Change API if needed */
  def sendAudioBuffer(arg1: Int, arg2: Int, arg3: Int, arg4: Int): Unit = {
    var varg1: Variant = null
    varg1 = new Variant(arg1)
    var varg2: Variant = null
    varg2 = new Variant(arg2)
    var varg3: Variant = null
    varg3 = new Variant(arg3)
    var varg4: Variant = null
    varg4 = new Variant(arg4)
    var result: Variant = call("sendAudioBuffer", varg1, varg2, varg3, varg4)
  }

  /* TODO: Move to traits and Change API if needed */
  def sendLocalBufferToOutput(nbOfFrames: Int, pBuffer: Int): Boolean = {
    var vnbOfFrames: Variant = null
    vnbOfFrames = new Variant(nbOfFrames)
    var vpBuffer: Variant = null
    vpBuffer = new Variant(pBuffer)
    var result: Variant = call("sendLocalBufferToOutput", vnbOfFrames, vpBuffer)
    return result.toBoolean
  }

  /* TODO: Move to traits and Change API if needed */
  def sendRemoteBufferToOutput(nbOfFrames: Int, pBuffer: Variant): Boolean = {
    var vnbOfFrames: Variant = null
    vnbOfFrames = new Variant(nbOfFrames)
    var vpBuffer: Variant = null
    vpBuffer = new Variant(pBuffer)
    var result: Variant = call("sendRemoteBufferToOutput", vnbOfFrames, vpBuffer)
    return result.toBoolean
  }

  /* TODO: Move to traits and Change API if needed */
  def setFileAsInput(pFileName: String): Unit = {
    var vpFileName: Variant = null
    vpFileName = new Variant(pFileName)
    var result: Variant = call("setFileAsInput", vpFileName)
  }

  /* TODO: Move to traits and Change API if needed */
  def setFileAsOutput(pFileName: String): Unit = {
    var vpFileName: Variant = null
    vpFileName = new Variant(pFileName)
    var result: Variant = call("setFileAsOutput", vpFileName)
  }

  /* TODO: Move to traits and Change API if needed */
  def setOutputVolume(volume: Int): Unit = {
    var vvolume: Variant = null
    vvolume = new Variant(volume)
    var result: Variant = call("setOutputVolume", vvolume)
  }

  /* TODO: Move to traits and Change API if needed */
  def setParameter(pParamName: String, pParamValue: Int): Unit = {
    var vpParamName: Variant = null
    vpParamName = new Variant(pParamName)
    var vpParamValue: Variant = null
    vpParamValue = new Variant(pParamValue)
    var result: Variant = call("setParameter", vpParamName, vpParamValue)
  }

  /* TODO: Move to traits and Change API if needed */
  def startMicrophonesRecording(pFileName: String): Unit = {
    var vpFileName: Variant = null
    vpFileName = new Variant(pFileName)
    var result: Variant = call("startMicrophonesRecording", vpFileName)
  }

  /* TODO: Move to traits and Change API if needed */
  def stopAudioOut: Unit = {
    var result: Variant = call("stopAudioOut")
  }

  /* TODO: Move to traits and Change API if needed */
  def stopMicrophonesRecording: Unit = {
    var result: Variant = call("stopMicrophonesRecording")
  }

  /* TODO: Move to traits and Change API if needed */
  def subscribe(pModule: String, arg2: String, arg3: String): Unit = {
    var vpModule: Variant = null
    vpModule = new Variant(pModule)
    var varg2: Variant = null
    varg2 = new Variant(arg2)
    var varg3: Variant = null
    varg3 = new Variant(arg3)
    var result: Variant = call("subscribe", vpModule, varg2, varg3)
  }

  /* TODO: Move to traits and Change API if needed */
  def subscribeLocalModule(pModule: String): Boolean = {
    var vpModule: Variant = null
    vpModule = new Variant(pModule)
    var result: Variant = call("subscribeLocalModule", vpModule)
    return result.toBoolean
  }

  /* TODO: Move to traits and Change API if needed */
  def subscribeRemoteModule(pModule: String): Boolean = {
    var vpModule: Variant = null
    vpModule = new Variant(pModule)
    var result: Variant = call("subscribeRemoteModule", vpModule)
    return result.toBoolean
  }

  /* TODO: Move to traits and Change API if needed */
  def unSubscribeLocalModule(pModule: String): Boolean = {
    var vpModule: Variant = null
    vpModule = new Variant(pModule)
    var result: Variant = call("unSubscribeLocalModule", vpModule)
    return result.toBoolean
  }

  /* TODO: Move to traits and Change API if needed */
  def unSubscribeRemoteModule(pModule: String): Boolean = {
    var vpModule: Variant = null
    vpModule = new Variant(pModule)
    var result: Variant = call("unSubscribeRemoteModule", vpModule)
    return result.toBoolean
  }

  /* TODO: Move to traits and Change API if needed */
  def unsubscribe(pModule: String): Unit = {
    var vpModule: Variant = null
    vpModule = new Variant(pModule)
    var result: Variant = call("unsubscribe", vpModule)
  }


}
