package be.anova.guanaco.dispatcher

import java.util
import java.util.Properties

import akka.actor.{ActorRef, ActorSystem}
import akka.osgi.ActorSystemActivator
import be.anova.guanaco.api.{EventListener, LogEventListener}
import be.anova.guanaco.dispatcher.DispatcherActor.{AddListener, RemoveListener}
import be.anova.guanaco.events.Event
import org.osgi.framework.{BundleActivator, BundleContext, ServiceReference, ServiceRegistration}
import org.osgi.util.tracker.{ServiceTracker, ServiceTrackerCustomizer}

import scala.collection.mutable

/**
  * Created by gertv on 5/13/17.
  */
class DispatcherActivator extends ActorSystemActivator {


  var tracker: Option[ServiceTracker[EventListener, EventListener]] = None
  val buffer: mutable.Buffer[ServiceRegistration[_]] = mutable.Buffer.empty[ServiceRegistration[_]]

  override def stop(context: BundleContext): Unit = {
    tracker foreach { value => value.close() }

    super.stop(context)
  }

  override def configure(context: BundleContext, system: ActorSystem): Unit = {
    val dispatcher = system.actorOf(DispatcherActor.props, "dispatcher")

    val customizer = new EventListenerServiceTrackerCustomer(context, dispatcher)
    val tracker = new ServiceTracker[EventListener, EventListener](context, classOf[EventListener], customizer)
    tracker.open()
    this.tracker = Some(tracker)

    buffer += context.registerService(classOf[EventDispatcher], new EventDispatcherImpl(dispatcher), new java.util.Hashtable[String, AnyRef]())

  }

  override def getActorSystemName(context: BundleContext): String = "guanaco-dispatcher"

  class EventListenerServiceTrackerCustomer(context: BundleContext, dispatcher: ActorRef) extends ServiceTrackerCustomizer[EventListener, EventListener] {

    override def addingService(serviceReference: ServiceReference[EventListener]): EventListener = {
      val listener = context.getService(serviceReference)
      dispatcher ! AddListener(listener)
      listener
    }

    override def removedService(serviceReference: ServiceReference[EventListener], listener: EventListener): Unit = {
      dispatcher ! RemoveListener(listener)
      context.ungetService(serviceReference)
    }

    override def modifiedService(serviceReference: ServiceReference[EventListener], t: EventListener): Unit = {
      // let's just ignore this
    }
  }

  class EventDispatcherImpl(dispatcher: ActorRef) extends EventDispatcher {
    override def send(event: Event): Unit = dispatcher ! event
  }

}
