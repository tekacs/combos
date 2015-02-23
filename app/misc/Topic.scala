package misc

import actors.TopicActor

import scala.language.implicitConversions

case class Topic(segments: String*) {
  def name = segments.mkString("/")
  override def toString = name
  def unapply(topic: String) = this == Topic.fromString(topic)
  def json = implicitly[JsonFormatter[Topic]].json(this)
}

object Topic {
  implicit def fromString(topic: String): Topic = Topic(topic.split('/'): _*)

  def topics: Iterable[Topic] = TopicActor.topicMap.keys.seq

  val wildcard = Topic("#")
}
