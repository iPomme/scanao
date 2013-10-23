package io.nao.scanao.srv

import com.aldebaran.qimessaging.{Future, Session, QimessagingService}
import org.slf4j.LoggerFactory

class TestService() extends QimessagingService {

  val log =  LoggerFactory.getLogger(this.getClass)

  def TestService(){

  }

  def greet (name : String): String = {
    println("Method greet called")
    log.info(s"Method greet called with $name")
    "Hello, " + name
  }

  def event(key : java.lang.Object, values : java.lang.Object, msg: java.lang.Object) : java.lang.Object = {
    try{
      log.debug(s"event: $key / values: ${values.toString} / message: ${msg.toString}")
      if(values.toString.equalsIgnoreCase("1")){
        log.info("OUCH !!!")
      }
    }catch{
      case e : Exception => e.printStackTrace()
    }
    key
  }
}
