/*
 * -----------------------------------------------------------------------------
 *  - ScaNao is an open-source enabling Nao's control from Scala code.            -
 *  - At the low level jNaoqi is used to bridge the C++ code with the JVM.        -
 *  -                                                                             -
 *  -  CreatedBy: Nicolas Jorand                                                  -
 *  -       Date: 15 Oct 2013                                                      -
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

import io.nao.scanao.srv.NaoServer._
import com.aldebaran.qimessaging.{Future, Session}
import akka.event.LoggingAdapter
import akka.actor.{ActorLogging, Actor}


trait SNQIMessage {
  this: Actor with ActorLogging =>
  val moduleName: String

  lazy val session = new Session()
  lazy val srv: com.aldebaran.qimessaging.Object = session.service(moduleName)


  override def preStart() {
    log.info(s"$moduleName is initializing against QiMessage")
    val fut = session.connect(NaoSupervisor.address)
    fut.synchronized(fut.wait(TIMEOUT_IN_MILLIS))

    val ping: Boolean = srv.call("ping").get()

    if (!ping) {
      log.error(s"Could not ping $moduleName");
    } else {
      log.info(s"$moduleName initialized");
    }
  }

}
