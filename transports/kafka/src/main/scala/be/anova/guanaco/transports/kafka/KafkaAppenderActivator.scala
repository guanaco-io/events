package be.anova.guanaco.transports.kafka

import java.util
import java.util.Properties

import be.anova.guanaco.api.{EventListener, LogEventListener, MessageEventListener}
import org.ops4j.pax.logging.PaxLoggingService
import org.osgi.framework.{BundleActivator, BundleContext, ServiceRegistration}
import org.slf4j.LoggerFactory

import scala.collection.mutable

/**
  * Created by gertv on 4/27/17.
  */
class KafkaAppenderActivator extends BundleActivator {

  import KafkaAppenderActivator.LOGGER

  val registrations = mutable.Buffer.empty[ServiceRegistration[_]]

  override def start(context: BundleContext): Unit = {
    LOGGER.info("Starting Guanaco Kafka Pax Logging appender")
    val props = new Properties()
    props.put(PaxLoggingService.APPENDER_NAME_PROPERTY, "guanaco-kafka")

    val appender = new KafkaAppender()

    registrations += context.registerService[MessageEventListener](classOf[MessageEventListener], appender, new java.util.Hashtable())
    registrations += context.registerService[LogEventListener](classOf[LogEventListener], appender, new java.util.Hashtable())
    registrations += context.registerService[EventListener](classOf[EventListener], appender, new java.util.Hashtable())
  }

  override def stop(context: BundleContext): Unit =
    for (registration <- registrations) registration.unregister()

}

object KafkaAppenderActivator {

  val LOGGER = LoggerFactory.getLogger(classOf[KafkaAppender])

}
