package be.anova.guanaco.events

import java.time.{LocalDateTime, ZonedDateTime}

import be.anova.guanaco.events.LogEvent.MDC
import be.anova.guanaco.events.MessageEvent.ExceptionInfo

/**
  * Created by gertv on 5/12/17.
  */
case class LogEvent(timestamp: ZonedDateTime, logger: String, level: String, message: String, mdc: MDC, exception: Option[Seq[String]] = None) extends Event

object LogEvent {

  type MDC = Map[String, Any]

}