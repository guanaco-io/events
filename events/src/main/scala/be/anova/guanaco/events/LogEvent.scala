package be.anova.guanaco.events

import java.time.LocalDateTime

import be.anova.guanaco.events.LogEvent.MDC

/**
  * Created by gertv on 5/12/17.
  */
case class LogEvent(timestamp: LocalDateTime, logger: String, level: String, message: String, mdc: MDC) extends Event

object LogEvent {

  type MDC = Map[String, Any]

}