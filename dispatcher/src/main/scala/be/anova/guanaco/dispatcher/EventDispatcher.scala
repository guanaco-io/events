package be.anova.guanaco.dispatcher

import be.anova.guanaco.events.Event

/**
  * Created by gertv on 5/13/17.
  */
trait EventDispatcher {

  def send(event: Event): Unit

}
