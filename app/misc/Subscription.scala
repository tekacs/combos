package misc

import akka.actor.ActorRef

case class Subscription(id: String, topic: Topic, ref: ActorRef) {
  def json = implicitly[JsonFormatter[Subscription]].json(this)
}
