package io.guanaco.events.messages

import org.apache.commons.io.IOUtils
import org.junit.Assert._
import org.junit.Test
import spray.json._

/**
  * Created by gertv on 5/12/17.
  */
class MessageEventTest {

  import Events._

  @Test
  def readAndWriteMessageProcessingEvent(): Unit =
    testReadAndWriteMessageEvent("events/messageprocessingevent.json")

  @Test
  def readAndWriteMessageCompletedEvent(): Unit =
    testReadAndWriteMessageEvent("events/messagecompletedevent.json")

  @Test
  def readAndWriteMessageFailedEvent(): Unit =
    testReadAndWriteMessageEvent("events/messagefailedevent.json")

  def testReadAndWriteMessageEvent(name: String): Unit = {
    val input = IOUtils.toString(getClass.getClassLoader.getResource(name), "UTF-8")
    val event = input.parseJson.convertTo[MessageEvent]
    val output = event.toJson.prettyPrint
    assertEquals(input, output)
  }

}
