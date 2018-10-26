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

import akka.actor.{Actor, ActorLogging, Props}
import io.guanaco.events.api.{EventListener, LogEventListener}
import DispatcherActor.{AddListener, RemoveListener}
import io.guanaco.events.messages.{LogEvent, MessageEvent}
import io.guanaco.events.api.{EventListener, LogEventListener, MessageEventListener}
import org.osgi.service.log.LogListener

/**
  * Created by gertv on 5/13/17.
  */
class DispatcherActor extends Actor with ActorLogging {

  val logging = context.actorOf(LoggingActor.props(), "logging")
  val messages = context.actorOf(MessagesActor.props(), "messages")

  override def receive: Receive = {
    case add @ AddListener(listener: EventListener) =>
      if (listener.isInstanceOf[LogEventListener]) logging ! AddListener(listener)
      if (listener.isInstanceOf[MessageEventListener]) messages ! AddListener(listener)
    case remove @ RemoveListener(listener: LogEventListener) =>
      if (listener.isInstanceOf[LogEventListener]) logging ! RemoveListener(listener)
      if (listener.isInstanceOf[MessageEventListener]) messages ! RemoveListener(listener)
    case event: LogEvent =>
      logging ! event
    case event: MessageEvent =>
      messages ! event
    case msg =>
      log.warning(s"Not sure why we get ${msg} here")
  }

}

object DispatcherActor {

  def props = Props(classOf[DispatcherActor])

  case class AddListener(listener: EventListener)

  case class RemoveListener(listener: EventListener)

}
