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

import com.aldebaran.proxy.{ALAudioDeviceProxy, ALProxy}
import io.nao.scanao.msg.audioDevice._
import akka.actor.{ActorLogging, Actor}

class SNAudioDeviceActor(ip: String = "127.0.0.1", port: Int = 9559) extends ALAudioDeviceProxy(ip, port) with Actor with ActorLogging {

  log.info("Creating instance of SNAudioDeviceActor")


  def receive = {
    case DisableEnergyComputation => disableEnergyComputation
    case EnableEnergyComputation => enableEnergyComputation
    case FrontMicEnergy => {
      sender ! MicEnergyResult(getFrontMicEnergy)
    }
    case RearMicEnergy => {
      sender ! MicEnergyResult(getRearMicEnergy)
    }
    case LeftMicEnergy => {
      sender ! MicEnergyResult(getLeftMicEnergy)
    }
    case RightMicEnergy => {
      sender ! MicEnergyResult(getRightMicEnergy)
    }
    case x@_ => {
      log.error("Unknown Message " + x)
    }
  }
}

//object SNAudioDeviceActor {
//  System.loadLibrary("jnaoqi")
//}

