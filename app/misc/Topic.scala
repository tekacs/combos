package misc

import actors.TopicActor

import scala.annotation.switch
import scala.language.implicitConversions

case class Topic(segments: String*) {
  def name = segments.mkString("/")
  override def toString = name
  def unapply(topic: String) = this == Topic.fromString(topic)
  def json = implicitly[JsonFormatter[Topic]].json(this)
}

object Topic {
  implicit def fromString(topic: String): Topic = (topic: @switch) match {
    case "#" | "%23" => wildcard
    case _ => Topic(topic.split('/'): _*)
  }

  def topics: Iterable[Topic] = TopicActor.topicMap.keys.seq

  val wildcard = Topic("#")
}
