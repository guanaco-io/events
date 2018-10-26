package io.guanaco.events.api

import io.guanaco.events.messages.MessageEvent

/**
  * Created by gertv on 5/18/17.
  */
trait MessageEventListener extends EventListener {

  def onMessageEvent(event: MessageEvent): Unit

}
