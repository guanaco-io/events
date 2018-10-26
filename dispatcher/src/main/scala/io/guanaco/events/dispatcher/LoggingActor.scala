package io.guanaco.events.dispatcher

import akka.actor.{Actor, ActorLogging, Props}
import DispatcherActor.{AddListener, RemoveListener}
import io.guanaco.events.messages.LogEvent
import io.guanaco.events.api.LogEventListener

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
      for (listener <- listeners)
        try {
          listener.onLogEvent(event)
        } catch {
          case e: Exception => println(s"${e.getMessage} on ${event}")
        }
    case value =>
      log.warning(s"Unexpected message ${value}")
  }

}

object LoggingActor {

  def props() = Props(classOf[LoggingActor])

}

