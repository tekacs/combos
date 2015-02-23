package actors

import misc.Util.generateId
import misc.{JsonFormatter, RenderedJSON, Topic}
import org.joda.time.DateTime
import play.api.libs.json.JsObject

case class Fact(id: String, timestamp: DateTime, topic: Topic, data: JsObject) {
  def this(topic: Topic, data: JsObject) = this(generateId, DateTime.now, topic, data)
  val rendered = new RenderedJSON(implicitly[JsonFormatter[Fact]].json(this))
  override def toString = rendered.rendered
  def json = implicitly[JsonFormatter[Fact]].json(this)
}

