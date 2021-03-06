package io.guanaco.events.messages

import org.junit.Test
import org.apache.commons.io.IOUtils

import org.junit.Assert._
import spray.json._

/**
  * Created by gertv on 5/12/17.
  */
class LogEventTest {

  import Events._

  @Test
  def readAndWriteLogEvent(): Unit = {
    val input = IOUtils.toString(getClass.getClassLoader.getResource("events/logevent.json"), "UTF-8").trim
    val event = input.parseJson.convertTo[LogEvent]
    val output = event.toJson.prettyPrint
    assertEquals(input, output)
  }

}
