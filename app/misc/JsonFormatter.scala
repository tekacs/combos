package misc

import actors.Fact
import controllers.routes
import play.api.libs.json.{JsNumber, JsObject, JsString}

trait JsonFormatter[T] {
  def json(obj: T): JsObject
}
object JsonFormatter {
  implicit object FactFormatter extends JsonFormatter[Fact] {
    def json(fact: Fact) = {
      val internals = JsObject(Seq(
        "combo_id" -> JsString(fact.id),
        "combo_timestamp" -> JsNumber(fact.timestamp.getMillis / 1000),
        "combo_topic" -> JsString(fact.topic.name)
      ))
      fact.data ++ internals
    }
  }

  implicit object TopicFormatter extends JsonFormatter[Topic] {
    def json(topic: Topic) = JsObject(Seq(
      "topic_name" -> JsString(topic.name),
      "subscription_url" -> JsString(routes.SubscriptionController.newSubscription(topic.name).absoluteURL()),
      "facts_url" -> JsString(routes.TopicController.facts(topic.name, None).absoluteURL())
    ))
  }

  implicit object SubscriptionFormatter extends JsonFormatter[Subscription] {
    def json(sub: Subscription) = JsObject(Seq(
      "subscription_id" -> JsString(sub.id),
      "retrieval_url" -> JsString(routes.SubscriptionController.next(sub.topic.name, sub.id).absoluteURL())
    ))
  }
}
