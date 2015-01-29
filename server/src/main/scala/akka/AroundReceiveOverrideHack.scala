package akka

import akka.actor.Actor
import akka.actor.Actor.Receive
import com.github.levkhomich.akka.tracing.ActorTracing

trait AroundReceiveOverrideHack {
  self: Actor with ActorTracing =>
  override protected[akka] def aroundReceive(receive: Receive, msg: Any): Unit = {
    aroundReceiveImpl(receive, msg)
    super.aroundReceive(receive, msg)
  }

  protected def aroundReceiveImpl(receive: Receive, msg: Any): Unit
}
