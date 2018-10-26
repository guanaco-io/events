package io.guanaco.events.dispatcher

import io.guanaco.events.messages.Event

/**
  * Created by gertv on 5/13/17.
  */
trait EventDispatcher {

  def send(event: Event): Unit

}
