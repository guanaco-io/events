package be.anova.guanaco.dispatcher

import akka.actor.{Actor, ActorLogging, Props}
import be.anova.guanaco.api.{EventListener, LogEventListener, MessageEventListener}
import be.anova.guanaco.dispatcher.DispatcherActor.{AddListener, RemoveListener}
import be.anova.guanaco.events.{LogEvent, MessageEvent}
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
