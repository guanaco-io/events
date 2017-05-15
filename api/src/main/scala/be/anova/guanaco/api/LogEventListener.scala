package be.anova.guanaco.api

import be.anova.guanaco.events.LogEvent

/**
  * Created by gertv on 5/10/17.
  */
trait LogEventListener extends EventListener {

  def onLogEvent(event: LogEvent): Unit

}
