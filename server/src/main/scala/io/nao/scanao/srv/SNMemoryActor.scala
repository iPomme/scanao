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

import com.aldebaran.proxy.ALProxy
import io.nao.scanao.msg.Done
import io.nao.scanao.msg.memory._
import akka.actor.{ActorLogging, Actor}

class SNMemoryActor(ip: String = "127.0.0.1", port: Int = 9559) extends ALProxy("ALMemory", ip, port) with SNMemory with Actor with ActorLogging {

  log.info("Creating instance of SNMemoryActor")

  def receive = {
    case insert: Insert => {
      insertData(insert.key)(insert.value)
      sender ! Done
    }
    case data: DataInMemoryAsInt => {
      sender ! getData(data.key).toInt
    }
    case data: DataInMemoryAsString => {
      sender ! getData(data.key).toString
    }
    case data: DataInMemoryAsBoolean => {
      sender ! getData(data.key).toBoolean
    }
    case data: DataInMemoryAsFloat => {
      sender ! getData(data.key).toFloat
    }
    case data: DataInMemoryAsByteList => {
      sender ! getData(data.key).toBinary.toList
    }
    case data: DataInMemoryAsFloatList => {
      sender ! getData(data.key).toFloatArray.toList
    }
    case data: DataInMemoryAsIntList => {
      sender ! getData(data.key).toIntArray.toList
    }
    case data: DataList => {
      var result: Array[String] = getDataList(data.filter)
      sender ! result.toList
    }
    case DataKeysList => {
      var result: Array[String] = getDataListName
      sender ! result.toList
    }
    case x@_ => {
      log.error("Unknown Message " + x)
    }

  }
}
