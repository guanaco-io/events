package be.anova.guanaco.events

import java.time.LocalDateTime

import be.anova.guanaco.events.LogEvent.MDC
import spray.json.{DefaultJsonProtocol, JsBoolean, JsNull, JsObject, JsString, JsValue, RootJsonFormat}

/**
  * Created by gertv on 5/12/17.
  */
object Events extends DefaultJsonProtocol {

  implicit object LocalDateTimeFormat extends RootJsonFormat[LocalDateTime] {

    import java.time.format.DateTimeFormatter.ISO_DATE_TIME

    override def read(json: JsValue): LocalDateTime = json match {
      case JsString(value) => LocalDateTime.parse(value, ISO_DATE_TIME)
      case _ => ???
    }

    override def write(value: LocalDateTime): JsValue = Option(value) match {
      case Some(datetime) => JsString(datetime.format(ISO_DATE_TIME))
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
        case _ => ???
      }

      values map mapTuple
    }

    override def write(obj: MDC): JsValue = {
      def mapTuple(tuple: (String, Any)): (String, JsValue) = tuple match {
        case (key, bool: Boolean) => key -> JsBoolean(bool)
        case (key, value: String) => key -> JsString(value)
        case _ => ???
      }

      JsObject(obj map mapTuple)
    }
  }

  implicit val logEventFormat = jsonFormat5(LogEvent.apply)

}
