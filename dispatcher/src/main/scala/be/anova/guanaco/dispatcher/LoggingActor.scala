package be.anova.guanaco.dispatcher

import akka.actor.{Actor, ActorLogging, Props}
import be.anova.guanaco.api.LogEventListener
import be.anova.guanaco.dispatcher.DispatcherActor.{AddListener, RemoveListener}
import be.anova.guanaco.events.LogEvent

/**
  * Created by gertv on 5/15/17.
  */
class LoggingActor extends Actor with ActorLogging {

  override def receive: Receive = listen(Nil)

  def listen(listeners: List[LogEventListener]): Receive = {
    case AddListener(listener: LogEventListener) => {
      log.info(s"Adding listener ${listener}")
      context become listen(listener :: listeners)
    }
    case RemoveListener(listener: LogEventListener) => {
      log.info(s"Removing listener ${listener}")
      val remaining = listeners filter { item => item == listener }
      context become listen(remaining)
    }
    case event: LogEvent =>
      for (listener <- listeners) listener.onLogEvent(event)
    case value =>
      log.warning(s"Unexpected message ${value}")
  }

}

object LoggingActor {

  def props() = Props(classOf[LoggingActor])

}

