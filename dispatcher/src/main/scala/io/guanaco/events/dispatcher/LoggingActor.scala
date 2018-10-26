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
import DispatcherActor.{AddListener, RemoveListener}
import io.guanaco.events.messages.LogEvent
import io.guanaco.events.api.LogEventListener

/**
  * Created by gertv on 5/15/17.
  */
class LoggingActor extends Actor with ActorLogging {

  override def receive: Receive = listen(Nil)

  def listen(listeners: List[LogEventListener]): Receive = {
    case AddListener(listener: LogEventListener) => {
      log.info(s"Adding listener ${listener}")
      context become listen(listener :: listeners)
    }
    case RemoveListener(listener: LogEventListener) => {
      log.info(s"Removing listener ${listener}")
      val remaining = listeners filter { item => item == listener }
      context become listen(remaining)
    }
    case event: LogEvent =>
      for (listener <- listeners)
        try {
          listener.onLogEvent(event)
        } catch {
          case e: Exception => println(s"${e.getMessage} on ${event}")
        }
    case value =>
      log.warning(s"Unexpected message ${value}")
  }

}

object LoggingActor {

  def props() = Props(classOf[LoggingActor])

}

