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
