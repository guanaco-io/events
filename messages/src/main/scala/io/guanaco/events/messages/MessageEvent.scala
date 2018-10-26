package io.guanaco.events.messages

import java.time.ZonedDateTime

import io.guanaco.events.messages.MessageEvent._

/**
  * Created by gertv on 5/15/17.
  */
sealed trait MessageEvent extends Event {

  // always there
  val timestamp: ZonedDateTime
  val id: Identification
  val routing: Routing
  val headers: Headers

  // nice to have
  val body: Option[Body]
  val exception: Option[ExceptionInfo]


}

import MessageEvent.emptyHeaders

case class MessageProcessingEvent(timestamp: ZonedDateTime,
                                  id: Identification,
                                  routing: Routing,
                                  headers: Headers = emptyHeaders(),
                                  body: Option[Body] = None,
                                  exception: Option[ExceptionInfo] = None) extends MessageEvent

case class MessageCompletedEvent(timestamp: ZonedDateTime,
                                 id: Identification,
                                 routing: Routing,
                                 headers: Headers = emptyHeaders(),
                                 body: Option[Body] = None,
                                 exception: Option[ExceptionInfo] = None) extends MessageEvent

case class MessageFailedEvent(timestamp: ZonedDateTime,
                              id: Identification,
                              routing: Routing,
                              headers: Headers = emptyHeaders(),
                              body: Option[Body] = None,
                              exception: Option[ExceptionInfo] = None) extends MessageEvent

object MessageEvent {

  type EventType = String

  val Processing: EventType = "processing"
  val Completed: EventType = "completed"
  val Failed: EventType = "failed"

  type Headers = Map[String, Any]
  def emptyHeaders(): Headers = Map.empty[String, Any]

  case class Identification(id: String, breadcrumb: Option[String])
  case class Body(javaType: String)
  case class ExceptionInfo(javaType: String, message: String, stackTrace: Option[Seq[String]] = None)
  case class Routing(context: String, route: Option[String], from: Option[String], to: Option[String])


}
