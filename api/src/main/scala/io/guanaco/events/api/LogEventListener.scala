package io.guanaco.events.api

import io.guanaco.events.messages.LogEvent

/**
  * Created by gertv on 5/10/17.
  */
trait LogEventListener extends EventListener {

  def onLogEvent(event: LogEvent): Unit

}
