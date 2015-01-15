package io.nao.scanao.srv

import akka.actor.{Props, ActorLogging, Actor, DeadLetter}

/**
 * Created by nicolasjorand on 15/01/15.
 */
class DeadLetterActor extends Actor with ActorLogging {
  def receive = {
    case d: DeadLetter ⇒ log.error(s" Received Dead message: $d")
    case m => log.debug(s" Received <<unknown>> message: $m")
  }

}

object DeadLetterActor {
  /**
   * Create the Props for this actor
   * @return a Props for creating this actor
   */
  def props(): Props = Props[DeadLetterActor]
}