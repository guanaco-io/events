package be.anova.guanaco.api

import be.anova.guanaco.events.MessageEvent

/**
  * Created by gertv on 5/18/17.
  */
trait MessageEventListener extends EventListener {

  def onMessageEvent(event: MessageEvent): Unit

}
