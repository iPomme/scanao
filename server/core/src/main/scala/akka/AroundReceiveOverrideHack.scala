package akka

//import akka.actor.Actor
//import com.github.levkhomich.akka.tracing.ActorTracing
//
//trait AroundReceiveOverrideHack extends Actor with ActorTracing {
//  override protected[akka] def aroundReceive(receive: Receive, msg: Any): Unit = {
//    aroundReceiveInt(receive, msg)
//    super.aroundReceive(receive, msg)
//  }
//
//  protected def aroundReceiveInt(receive: Receive, msg: Any): Unit
//}
