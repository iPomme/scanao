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

import akka.actor.{ActorLogging, Actor, Props, ActorSystem}
import akka.kernel.Bootable
import com.typesafe.config.ConfigFactory

class NaoSupervisor extends Actor with ActorLogging {

  val text = context.actorOf(Props(new SNTextToSpeechActor()), name = "text")
  val memory = context.actorOf(Props(new SNMemoryActor()), name = "memory")
  val audio = context.actorOf(Props(new SNAudioDeviceActor()), name = "audio")
//  val behavior = context.actorOf(Props(new SNBehaviorManagerActor()), name = "behavior")
//  val motion = context.actorOf(Props(new SNMotionActor()), name = "motion")
//  val pose = context.actorOf(Props(new SNRobotPoseActor()), name = "pose")
//  val sound = context.actorOf(Props(new SNSoundDetectionActor()), name = "sound")

  log.info("NaoSupervisor Initalized.")
  // Print all the paths registered out
  log.debug(context.children.foldLeft("Path registered :\n")((b, ref) => s"$b\t$ref\n"))

  def receive = {
    case _@msg => {
      log.info(s"NaoSuperVisor received: $msg")
    }
  }

}

case object Start

class NaoServer extends Bootable {
  // Create the system actor
  val system = ActorSystem("NaoApplication", ConfigFactory.load.getConfig("nao"))


  def startup() {
    // Create and send the Start message to the nao supervisor
    system.actorOf(Props(new NaoSupervisor), name = "nao") ! Start
  }

  def shutdown() {
    system.shutdown()
  }
}