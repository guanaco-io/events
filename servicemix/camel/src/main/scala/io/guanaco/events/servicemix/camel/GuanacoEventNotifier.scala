package io.guanaco.events.servicemix.camel

import java.time.{LocalDateTime, ZonedDateTime}
import java.util.EventObject

import io.guanaco.events.messages.MessageEvent._
import io.guanaco.events.messages.{MessageCompletedEvent, MessageEvent, MessageFailedEvent, MessageProcessingEvent}
import io.guanaco.events.dispatcher.EventDispatcher
import org.apache.camel.Exchange
import org.apache.camel.management.event._
import org.apache.camel.spi.EventNotifier
import org.apache.camel.support.EventNotifierSupport
import org.apache.camel.Exchange.BREADCRUMB_ID

/**
  * Created by gertv on 5/18/17.
  */
class GuanacoEventNotifier(dispatcher: EventDispatcher) extends EventNotifierSupport {

  override def isEnabled(event: EventObject): Boolean = event.isInstanceOf[AbstractExchangeEvent]

  override def notify(event: EventObject): Unit =
    map(event) foreach dispatcher.send

  def map(event: EventObject): Option[MessageEvent] = {
    def id(exchange: Exchange) =
      Identification(exchange.getExchangeId, Option(exchange.getIn.getHeader(BREADCRUMB_ID, classOf[String])))

    def body(exchange: Exchange) =
      Option(exchange.getIn.getBody) map { body => Body(body.getClass.getName) } orElse (Some(Body("null")))

    def headers(exchange: Exchange) = {
      import scala.collection.JavaConversions._
      Map(exchange.getIn.getHeaders.entrySet flatMap { entry =>
        (entry.getKey, entry.getValue) match {
          case (key, value: String) => Some(key -> value)
          case (key, value: java.lang.Boolean) => Some(key -> value.booleanValue())
          case (key, value: java.lang.Integer) => Some(key -> value.intValue())
          case _ => None
        }
      } toSeq : _*)
    }

    def exception(exchange: Exchange): Option[ExceptionInfo] = {
      Option(exchange.getException()) map { exception =>
        val stacktrace = exception.getStackTrace.map { element => element.toString }
        ExceptionInfo(exception.getClass.getName, exception.getMessage)
      }
    }

    def toRouting(camelEvent: AbstractExchangeEvent, exchange: Exchange): Routing = {
      val to = camelEvent match {
        case ese: ExchangeSendingEvent => Some(ese.getEndpoint.toString)
        case ese: ExchangeSentEvent => Some(ese.getEndpoint.toString)
        case _ => None
      }
      Routing(exchange.getContext.getName, Option(exchange.getFromRouteId), Option(exchange.getFromEndpoint.toString), to)
    }

    def toMessageEvent(camelEvent: AbstractExchangeEvent, apply: (ZonedDateTime, Identification, Routing, Headers, Option[Body], Option[ExceptionInfo]) => MessageEvent): MessageEvent = {
      val exchange = camelEvent.getExchange
      apply(ZonedDateTime.now(), id(exchange), toRouting(camelEvent, exchange), headers(exchange), body(exchange), exception(exchange))
    }

    event match {
      case cse: ExchangeSendingEvent => Some(toMessageEvent(cse, MessageProcessingEvent))
      case cse: ExchangeSentEvent => Some(toMessageEvent(cse, MessageProcessingEvent))
      case cec: ExchangeCompletedEvent => Some(toMessageEvent(cec, MessageCompletedEvent))
      case efe: ExchangeFailedEvent => Some(toMessageEvent(efe, MessageFailedEvent))
      case _ => None
    }
  }

}
