package be.anova.guanaco.dispatcher

import akka.actor.{Actor, ActorLogging, Props}
import be.anova.guanaco.api.{EventListener, LogEventListener}
import be.anova.guanaco.dispatcher.DispatcherActor.{AddListener, RemoveListener}
import be.anova.guanaco.events.LogEvent
import org.osgi.service.log.LogListener

/**
  * Created by gertv on 5/13/17.
  */
class DispatcherActor extends Actor with ActorLogging {

  val logging = context.actorOf(LoggingActor.props(), "logging")

  override def receive: Receive = {
    case add @ AddListener(listener: LogEventListener) =>
      logging ! add
    case remove @ RemoveListener(listener: LogEventListener) =>
      logging ! remove
    case event: LogEvent =>
      logging ! event
    case msg =>
      log.warning(s"Not sure why we get ${msg} here")
  }

}

object DispatcherActor {

  def props = Props(classOf[DispatcherActor])

  case class AddListener(listener: EventListener)

  case class RemoveListener(listener: EventListener)

}
