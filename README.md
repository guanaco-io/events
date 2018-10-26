# guanaco-events
If you like Camel and Alpakka, a guanaco might be just the thing for you!
This subproject provides the event messages and some bundles to collect the events in Karaf/ServiceMix

## Requirements

* Apache ServiceMix 7.0.1
* Apache Kafka 1.1.1

## Installation

### `config.properties`
Before you start, add the `sun.net` package to the `org.osgi.framework.system.packages.extra` setting in `etc/config.properties`.

```diff
org.osgi.framework.system.packages.extra = \
    org.apache.karaf.branding, \
    org.apache.karaf.jaas.boot.principal, \
    org.apache.karaf.jaas.boot, \
    sun.misc, \
+     sun.net, \
    javax.xml.stream;uses:=\"javax.xml.namespace,javax.xml.stream.events,javax.xml.stream.util,javax.xml.transform\";version=1.2, \
    javax.xml.stream.events;uses:=\"javax.xml.namespace,javax.xml.stream\";version=1.2, \
    javax.xml.stream.util;uses:=\"javax.xml.namespace,javax.xml.stream,javax.xml.stream.events\";version=1.2, \

```

### `org.ops4j.pax.logging.cfg`

Update the `etc/org.ops4j.pax.logging.cfg` file to send log events to all appenders

```diff
- log4j.rootLogger = INFO, out, osgi:VmLogAppender
+ log4j.rootLogger = INFO, out, osgi:*
```

### Feature

Afterwards, just install the features (business as usual)

   feature:repo-add mvn:io.guanaco.events/features/1.0-SNAPSHOT/xml/features
   feature:install guanaco-camel-events guanaco-log-appender guanaco-kafka
   
## Events

If everything is working properly, events will be published to two Kafka topics:

* `messages` will contain a stream of Camel exchange events
* `logging` will contain a stream of logging events
