package io.guanaco.events.messages

import java.time.{LocalDateTime, ZonedDateTime}

import io.guanaco.events.messages.LogEvent.MDC
import io.guanaco.events.messages.MessageEvent.ExceptionInfo

/**
  * Created by gertv on 5/12/17.
  */
case class LogEvent(timestamp: ZonedDateTime, logger: String, level: String, message: String, mdc: MDC, exception: Option[Seq[String]] = None) extends Event

object LogEvent {

  type MDC = Map[String, Any]

}