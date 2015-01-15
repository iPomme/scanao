package io.nao.scanao

import akka.actor.ActorSystem

import com.typesafe.config.ConfigFactory
import akka.agent.Agent

object STMDemo {
  val system = ActorSystem("NaoApplication", ConfigFactory.load.getConfig("naoClient"))

  def main(args: Array[String]) {

//    // Mutable references to values
//    val result = Ref[Double]
//
//    // Get the Proxy
//    val ttsProxy = system.actorFor("akka.tcp://NaoApplication@sonny.local:2552/user/nao/text")
//
//    // Wrap the proxy into an Agent to execute side effect action along with STM transaction
//    val tts = Agent(ttsProxy)
//
//    def atomicSay(a: Agent[SNTextToSpeechProxy]) = atomic {
//      a.send {
//        w => w.say("Nice, Finished to process and result is %s".format(result.get)); w
//      }
//    }
//    def atomicPerformMath(dividend: Int, divisor: Int)= atomic {
//      result.swap(dividend / divisor)
//    }
//    // Define the atomic behavior
//    def atomicExecution() = atomic {
//      atomicSay(tts)
//      atomicPerformMath(30, 2)
//      atomicPerformMath(30, 3)
//      //      atomicPerformMath(30, 0) // div per 0 ! Uncomment this line to try the STM with an error
//    }
//    // run the behavior
//    try {
//      println("result before = " + result)
//      atomicExecution()
//    } catch {
//      case e: Throwable => println("Got the Error !")
//    } finally {
//      println("result after = " + result)
//      tts.close
//    }
  }
}
