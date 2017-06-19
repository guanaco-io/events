package be.anova.guanaco.events

import java.time.{LocalDateTime, ZonedDateTime}

import be.anova.guanaco.events.LogEvent.MDC
import be.anova.guanaco.events.MessageEvent.Body
import spray.json.{DefaultJsonProtocol, JsBoolean, JsNull, JsObject, JsString, JsValue, RootJsonFormat}
import spray.json._

/**
  * Created by gertv on 5/12/17.
  */
object Events extends DefaultJsonProtocol {

  implicit object ZonedDateTimeFormat extends RootJsonFormat[ZonedDateTime] {

    import java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME

    override def read(json: JsValue): ZonedDateTime = json match {
      case JsString(value) => ZonedDateTime.parse(value, ISO_OFFSET_DATE_TIME)
      case _ => ???
    }

    override def write(value: ZonedDateTime): JsValue = Option(value) match {
      case Some(datetime) => JsString(datetime.format(ISO_OFFSET_DATE_TIME))
      case None => JsNull
    }
  }

  implicit object MDCFormat extends RootJsonFormat[MDC] {

    override def read(json: JsValue): MDC = json match {
      case JsObject(values) => read(values)
      case _ => ???
     }

    def read(values: Map[String, JsValue]): MDC = {
      def mapTuple(tuple: (String, JsValue)): (String, Any) = tuple match {
        case (key, JsString(string)) => key -> string
        case (key, JsBoolean(bool)) => key -> bool
        case (key, JsNumber(number)) => key -> number
        case _ => ???
      }

      values map mapTuple
    }

    override def write(obj: MDC): JsValue = {
      def mapTuple(tuple: (String, Any)): Option[(String, JsValue)] = tuple match {
        case (key, bool: Boolean) => Some(key -> JsBoolean(bool))
        case (key, value: String) => Some(key -> JsString(value))
        case (key, value: BigDecimal) => Some(key -> JsNumber(value))
        case (key, value: Int) =>  Some(key -> JsNumber(value))
        case (key, value: Integer) => Some(key -> JsNumber(value))
        case value => {
          println(s"How about ${value}?")
          None
        }
      }

      JsObject(obj flatMap mapTuple)
    }
  }

  implicit object BodyFormat extends RootJsonFormat[Body] {

    override def read(json: JsValue): Body = json match {
      case JsObject(fields) => fields.get("javaType") match {
        case Some(JsString(value)) => Body(value)
        case _ => ???
      }
      case _ => ???
    }

    override def write(obj: Body): JsValue = JsObject("javaType" -> JsString(obj.javaType))
  }


  implicit object MessageEventFormat extends RootJsonFormat[MessageEvent] {

    import MessageEvent._

    implicit val identification = jsonFormat2(Identification)
    implicit val exceptionInfo = jsonFormat3(ExceptionInfo)
    implicit val routing = jsonFormat4(Routing)
    implicit val messageCompletedEvent = jsonFormat6(MessageCompletedEvent)
    implicit val messageProcessingEvent = jsonFormat6(MessageProcessingEvent)
    implicit val messageFailedEvent = jsonFormat6(MessageFailedEvent)

    override def read(json: JsValue): MessageEvent = json match {
      case JsObject(values) if values.size == 1 => values.head match {
        case (Processing, obj) => obj.convertTo[MessageProcessingEvent]
        case (Completed, obj) => obj.convertTo[MessageCompletedEvent]
        case (Failed, obj) => obj.convertTo[MessageFailedEvent]
      }
      case _ => ???
    }

    override def write(obj: MessageEvent): JsValue = obj match {
      case e: MessageProcessingEvent => JsObject(Processing -> e.toJson)
      case e: MessageFailedEvent => JsObject(Failed -> e.toJson)
      case e: MessageCompletedEvent => JsObject(Completed -> e.toJson)
      case _ => ???
    }
  }

  implicit val logEventFormat = jsonFormat6(LogEvent.apply)

}
