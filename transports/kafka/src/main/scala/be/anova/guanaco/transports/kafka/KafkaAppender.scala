package be.anova.guanaco.transports.kafka

import java.util.Properties

import be.anova.guanaco.api.LogEventListener
import be.anova.guanaco.events.LogEvent
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.kafka.common.serialization.StringSerializer
import spray.json._

/**
  * Created by gertv on 4/27/17.
  */
class KafkaAppender extends LogEventListener {

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
    if (event.logger.startsWith("org.apache.kafka") || event.logger.startsWith("be.anova.guanaco.logging.kafka")) {
      return
    }

    import be.anova.guanaco.events.Events._
    producer.send(new ProducerRecord[String, String]("logging", event.toJson.compactPrint))
  }

}
