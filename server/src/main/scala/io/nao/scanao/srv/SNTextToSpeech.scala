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

trait SNTextToSpeech extends SNProxy {
//  def disableNotifications() {
//    call("disableNotifications")
//  }
//
//  def enableNotifications() {
//    call("enableNotifications")
//  }
//
//  def getAvailableLanguages: Array[String] = {
//    val result: Variant = call("getAvailableLanguages")
//    result.toStringArray.asInstanceOf[Array[String]]
//  }
//
//  def getAvailableVoices: Array[String] = {
//    val result: Variant = call("getAvailableVoices")
//    result.toStringArray.asInstanceOf[Array[String]]
//  }
//
//  def getLanguage: String = {
//    val result: Variant = call("getLanguage")
//    result.toString
//  }
//
//  def getLanguageEncoding(pLanguage: String): String = {
//    val vpLanguage: Variant = new Variant(pLanguage)
//    val result: Variant = call("getLanguageEncoding", vpLanguage)
//    result.toString
//  }
//
//  def getParameter(pParameterName: String): Float = {
//    val vpParameterName: Variant = new Variant(pParameterName)
//    val result: Variant = call("getParameter", vpParameterName)
//    result.toFloat
//  }
//
//  def getVoice: String = {
//    val result: Variant = call("getVoice")
//    result.toString
//  }
//
//  def getVolume: Float = {
//    val result: Variant = call("getVolume")
//    result.toFloat
//  }
//
//  def loadVoicePreference(pPreferenceName: String) {
//    val vpPreferenceName: Variant = new Variant(pPreferenceName)
//    call("loadVoicePreference", vpPreferenceName)
//  }
//
//  def say(stringToSay: String) {
//    val vstringToSay: Variant = new Variant(stringToSay)
//    call("say", vstringToSay)
//  }
//
//  def sayToFile(pStringToSay: String, pFileName: String) {
//    val vpStringToSay: Variant = new Variant(pStringToSay)
//    val vpFileName: Variant = new Variant(pFileName)
//    call("sayToFile", vpStringToSay, vpFileName)
//  }
//
//  def sayToFileAndPlay(pStringToSay: String): Int = {
//    val vpStringToSay: Variant = new Variant(pStringToSay)
//    val result: Variant = call("sayToFileAndPlay", vpStringToSay)
//    result.toInt
//  }
//
//  def setLanguage(pLanguage: String) {
//    val vpLanguage: Variant = new Variant(pLanguage)
//    call("setLanguage", vpLanguage)
//  }
//
//  // TODO: Create Enum for the effects
//  /**
//  <summary>
//   Changes the parameters of the voice. The available parameters are:
//
//    	 pitchShift: applies a pitch shifting to the voice. The value indicates the ratio between the new fundamental frequencies and the old ones (examples: 2.0: an octave above, 1.5: a quint above). Correct range is (1.0 -- 4), or 0 to disable effect.
//
//    	 doubleVoice: adds a second voice to the first one. The value indicates the ratio between the second voice fundamental frequency and the first one. Correct range is (1.0 -- 4), or 0 to disable effect
//
//    	 doubleVoiceLevel: the corresponding value is the level of the double voice (1.0: equal to the main voice one). Correct range is (0 -- 4).
//
//    	 doubleVoiceTimeShift: the corresponding value is the delay between the double voice and the main one. Correct range is (0 -- 0.5)
//
//    If the effect value is not available, the effect parameter remains unchanged.
//   </summary>
//   @param pEffectName Name of the parameter.
//  @param pEffectValue Value of the parameter.
//    */
//  def setParameter(pEffectName: String, pEffectValue: Float) {
//    val vpEffectName: Variant = new Variant(pEffectName)
//    val vpEffectValue: Variant = new Variant(pEffectValue)
//    call("setParameter", vpEffectName, vpEffectValue)
//  }
//
//  def setVoice(pVoiceID: String) {
//    val vpVoiceID: Variant = new Variant(pVoiceID)
//    call("setVoice", vpVoiceID)
//  }
//
//  def setVolume(volume: Float) {
//    val vvolume: Variant = new Variant(volume)
//    call("setVolume", vvolume)
//  }
//
//  def stopAll() {
//    call("stopAll")
//  }
}
