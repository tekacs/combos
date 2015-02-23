package misc

import play.api.libs.json.{JsObject, JsValue, Json}

import scala.language.implicitConversions

case class RenderedJSON(data: JsObject, rendered: String) {
  def this(data: JsObject) = this(data, Json.stringify(data))
  override def toString = rendered
}

object RenderedJSON {
  def fromString(json: String): Either[JsValue, RenderedJSON] = Json.parse(json) match {
    case obj@JsObject(_) => Right(new RenderedJSON(obj))
    case bareValue => Left(bareValue)
  }

  implicit def fromObject(obj: JsObject): RenderedJSON = new RenderedJSON(obj)
}
