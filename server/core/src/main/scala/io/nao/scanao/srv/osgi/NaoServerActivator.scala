package io.nao.scanao.srv.osgi

import java.io.File

import akka.actor.ActorSystem
import akka.osgi.ActorSystemActivator
import com.typesafe.config.ConfigFactory
import io.nao.scanao.msg.tech.PrintMap
import io.nao.scanao.srv.NaoSupervisor
import org.osgi.framework.BundleContext
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global


/**
 * Created by nicolasjorand on 13/02/15.
 */
class NaoServerActivator extends ActorSystemActivator {
  override def configure(context: BundleContext, system: ActorSystem): Unit = {
    val nao = system.actorOf(NaoSupervisor.props(), name = "nao")
    // Used to get the debug information about the actors variables
    scala.util.Properties.envOrNone("NAO_DEBUG") match {
      case Some(_) => system.scheduler.schedule(2 seconds, 2 seconds, nao, PrintMap)
      case None => system.log.info("scanao not running in debug (use NAO_DEBUG=1 to enable it)")
    }

    // Register the system Actor as a service
    registerService(context, system)
  }

  override def start(context: BundleContext) = {
    super.start(context)
  }

  override def stop(context: BundleContext) = {
    super.stop(context)
  }

  override def getActorSystemName(bundle: BundleContext) = "naoSystem"

  override def getActorSystemConfiguration(context: BundleContext) = ConfigFactory.parseFile(new File("etc/nao.conf"))
}
