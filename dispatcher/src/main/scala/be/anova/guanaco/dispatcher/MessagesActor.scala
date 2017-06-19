package be.anova.guanaco.dispatcher

import akka.actor.{Actor, ActorLogging, Props}
import be.anova.guanaco.api.MessageEventListener
import be.anova.guanaco.dispatcher.DispatcherActor.{AddListener, RemoveListener}
import be.anova.guanaco.events.MessageEvent

/**
  * Created by gertv on 5/18/17.
  */
class MessagesActor extends Actor with ActorLogging {

  override def receive: Receive = listen(Nil)

  def listen(listeners: List[MessageEventListener]): Receive = {
    case AddListener(listener: MessageEventListener) => {
      log.info(s"Adding listener ${listener}")
      context become listen(listener :: listeners)
    }
    case RemoveListener(listener: MessageEventListener) => {
      log.info(s"Removing listener ${listener}")
      val remaining = listeners filter { item => item == listener }
      context become listen(remaining)
    }
    case event: MessageEvent =>
      for (listener <- listeners) {
        try {
          listener.onMessageEvent(event)
        } catch {
          case e: Exception =>
            log.error(e, s"Error while calling ${listener} on ${event}")
        }
      }
    case value =>
      log.warning(s"Unexpected message ${value}")
  }
}

object MessagesActor {

  def props(): Props = Props(classOf[MessagesActor])

}
