/*
 * Copyright 2018 - anova r&d bvba
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.guanaco.events.dispatcher

import java.util
import java.util.Properties

import akka.actor.{ActorRef, ActorSystem}
import akka.osgi.ActorSystemActivator
import io.guanaco.events.api.LogEventListener
import DispatcherActor.{AddListener, RemoveListener}
import io.guanaco.events.messages.Event
import io.guanaco.events.api.EventListener
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
