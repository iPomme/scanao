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

import akka.actor.{ActorLogging, Actor}
import com.aldebaran.qimessaging.{Future, Session}
import scala.collection.JavaConverters._
import java.util.{ArrayList => JArrayList}
import java.lang.{Object => JObject}
import io.nao.scanao.msg.memory._
import io.nao.scanao.msg.memory.DataInMemoryAsBoolean
import io.nao.scanao.msg.memory.DataInMemoryAsString
import io.nao.scanao.msg.memory.DataInMemoryAsInt
import io.nao.scanao.msg.memory.Insert
import io.nao.scanao.srv.NaoServer._

class SNMemoryActor extends Actor with ActorLogging with SNQIMessage {

  log.info("Creating instance of SNMemoryActor")

  val moduleName = "ALMemory"

  def receive = {
    case insert: Insert => {
      srv.call("insertData", insert.key, insert.value)
    }
    case data: DataInMemoryAsInt => {
      val fut: Future[Int] = srv.call[Int]("getData", data.key)
      sender ! fut.get(TIMEOUT, TIMEOUT_UNIT)
    }
    case data: DataInMemory => {
      val fut: Future[JObject] = srv.call[JObject]("getData", data.key)
      val resp = try {
        fut.get(TIMEOUT, TIMEOUT_UNIT)
      } catch {
        case ex:Exception => None
      }
      sender ! resp

    }
    case data: DataInMemoryAsString => {
      val fut: Future[String] = srv.call[String]("getData", data.key)
      val resp = try {
        fut.get(TIMEOUT, TIMEOUT_UNIT)
      } catch {
        case ex:Exception => None
      }
      sender ! resp

    }
    case data: DataInMemoryAsBoolean => {
      val fut: Future[Boolean] = srv.call[Boolean]("getData", data.key)
      sender ! fut.get(TIMEOUT, TIMEOUT_UNIT)
    }
    case data: DataInMemoryAsFloat => {
      val fut: Future[Float] = srv.call[Float]("getData", data.key)
      sender ! fut.get(TIMEOUT, TIMEOUT_UNIT)
    }
    case data: DataInMemoryAsByteList => {
      val fut: Future[JArrayList[JObject]] = srv.call[JArrayList[JObject]]("getData", data.key)
      sender ! fut.get(TIMEOUT, TIMEOUT_UNIT).asScala.toList.map(t => t.asInstanceOf[Byte])
    }
    case data: DataInMemoryAsFloatList => {
      val fut: Future[JArrayList[JObject]] = srv.call[JArrayList[JObject]]("getData", data.key)
      sender ! fut.get(TIMEOUT, TIMEOUT_UNIT).asScala.toList.map(t => t.asInstanceOf[Float])
    }
    case data: DataInMemoryAsIntList => {
      val fut: Future[JArrayList[JObject]] = srv.call[JArrayList[JObject]]("getData", data.key)
      sender ! fut.get(TIMEOUT, TIMEOUT_UNIT).asScala.toList.map(t => t.asInstanceOf[Int])
    }
    case data: DataList => {
      val fut: Future[JArrayList[JObject]] = srv.call[JArrayList[JObject]]("getDataList", data.filter)
      sender ! fut.get(TIMEOUT, TIMEOUT_UNIT).asScala.toList.map(t => t.asInstanceOf[String])
    }
    case DataKeysList => {
      val fut: Future[JArrayList[JObject]] = srv.call[JArrayList[JObject]]("getDataListName")
      sender ! fut.get(TIMEOUT, TIMEOUT_UNIT).asScala.toList.map(t => t.asInstanceOf[String])
    }
    case x@_ => {
      log.error("Unknown Message " + x)
    }
  }
}
