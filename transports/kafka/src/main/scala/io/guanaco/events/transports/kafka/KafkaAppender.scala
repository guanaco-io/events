/*
 * Copyright 2018 - anova r&d bvba
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.guanaco.events.transports.kafka

import java.util.Properties

import io.guanaco.events.api.LogEventListener
import io.guanaco.events.messages.{LogEvent, MessageEvent}
import io.guanaco.events.api.{LogEventListener, MessageEventListener}
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.kafka.common.serialization.StringSerializer
import spray.json._

/**
  * Created by gertv on 4/27/17.
  */
class KafkaAppender extends LogEventListener with MessageEventListener {

  def getProperties() = {
    val props = new Properties()
    props.put("bootstrap.servers", "localhost:9092");
    props.put("acks", "all");
    props.put("retries", Int.box(0));
    props.put("batch.size", Int.box(16384));
    props.put("linger.ms", Int.box(1));
    props.put("buffer.memory", Int.box(33554432));
    props.put("key.serializer", classOf[StringSerializer]);
    props.put("value.serializer", classOf[StringSerializer]);
    props
  }

  lazy val producer: KafkaProducer[String, String] =
    new KafkaProducer[String, String](getProperties())

  override def onLogEvent(event: LogEvent): Unit = {
    if (event.logger.startsWith("org.apache.kafka") || event.logger.startsWith("io.guanaco.events.transports.kafka")) {
      return
    }

    import io.guanaco.events.messages.Events._
    producer.send(new ProducerRecord[String, String]("logging", event.toJson.compactPrint))
  }

  override def onMessageEvent(event: MessageEvent): Unit = {
    import io.guanaco.events.messages.Events._
    producer.send(new ProducerRecord[String, String]("messages", event.toJson.compactPrint))
  }

}
