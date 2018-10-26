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

package io.guanaco.events.servicemix.logging

import java.util

import io.guanaco.events.dispatcher.EventDispatcher
import org.ops4j.pax.logging.spi.PaxAppender
import org.osgi.framework.{BundleActivator, BundleContext, ServiceReference, ServiceRegistration}
import org.osgi.util.tracker.{ServiceTracker, ServiceTrackerCustomizer}

import scala.collection.mutable

/**
  * Created by gertv on 5/13/17.
  */
class AppenderActivator extends BundleActivator {

  var tracker = Option.empty[ServiceTracker[EventDispatcher, EventDispatcher]]
  val registrations = mutable.Buffer.empty[ServiceRegistration[PaxAppender]]

  override def start(bundleContext: BundleContext): Unit = {
    val appender = new GuanacoPaxAppender()
    val customer = new EventDispatcherTrackerCustomizer(bundleContext, appender)

    val tracker = new ServiceTracker[EventDispatcher, EventDispatcher](bundleContext, classOf[EventDispatcher], customer)
    tracker.open()
    this.tracker = Some(tracker)

    val properties = new java.util.Hashtable[String, Any]()
    properties.put("org.ops4j.pax.logging.appender.name", classOf[GuanacoPaxAppender].getSimpleName)
    registrations += bundleContext.registerService(classOf[PaxAppender], appender, properties)
  }

  override def stop(bundleContext: BundleContext): Unit = {
    tracker foreach { t => t.close()}
    registrations foreach { registration => registration.unregister() }
  }

  class EventDispatcherTrackerCustomizer(context: BundleContext, appender: GuanacoPaxAppender) extends ServiceTrackerCustomizer[EventDispatcher, EventDispatcher] {
    override def addingService(serviceReference: ServiceReference[EventDispatcher]): EventDispatcher = {
      val dispatcher = context.getService(serviceReference)
      appender.buffer += dispatcher
      dispatcher
    }

    override def removedService(serviceReference: ServiceReference[EventDispatcher], t: EventDispatcher): Unit = {
      appender.buffer -= t
    }

    override def modifiedService(serviceReference: ServiceReference[EventDispatcher], t: EventDispatcher): Unit = ???
  }

}
