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

import java.time.{LocalDate, LocalDateTime, ZonedDateTime}
import java.util

import io.guanaco.events.messages.LogEvent
import io.guanaco.events.dispatcher.EventDispatcher
import org.ops4j.pax.logging.spi.{PaxAppender, PaxLoggingEvent}

import scala.collection.mutable

/**
  * Created by gertv on 5/13/17.
  */
class GuanacoPaxAppender extends PaxAppender {

  val buffer = mutable.Buffer.empty[EventDispatcher]


  override def doAppend(paxLoggingEvent: PaxLoggingEvent): Unit = {

    val event = LogEvent(ZonedDateTime.now(), paxLoggingEvent.getLoggerName, paxLoggingEvent.getLevel.toString, paxLoggingEvent.getMessage, toMDC(paxLoggingEvent.getProperties), Option(paxLoggingEvent.getThrowableStrRep) map { _.toSeq})
    buffer foreach { dispatcher => dispatcher.send(event)}
  }

  def toMDC(properties: util.Map[_, _]): LogEvent.MDC = {
    import scala.collection.JavaConversions._
    properties.toMap flatMap { (tuple: (Any, Any)) => tuple match {
      case (key: String, value: String) => Some(key -> value)
      case (key: String, value: Boolean) => Some(key -> value)
      case _ => None
    }}
  }
}
