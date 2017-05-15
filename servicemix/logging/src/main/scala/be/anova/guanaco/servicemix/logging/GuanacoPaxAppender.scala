package be.anova.guanaco.servicemix.logging

import java.time.{LocalDate, LocalDateTime}
import java.util

import be.anova.guanaco.dispatcher.EventDispatcher
import be.anova.guanaco.events.LogEvent
import org.ops4j.pax.logging.spi.{PaxAppender, PaxLoggingEvent}

import scala.collection.mutable

/**
  * Created by gertv on 5/13/17.
  */
class GuanacoPaxAppender extends PaxAppender {

  val buffer = mutable.Buffer.empty[EventDispatcher]


  override def doAppend(paxLoggingEvent: PaxLoggingEvent): Unit = {

    val event = LogEvent(LocalDateTime.now(), paxLoggingEvent.getLoggerName, paxLoggingEvent.getLevel.toString, paxLoggingEvent.getMessage, toMDC(paxLoggingEvent.getProperties))
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
