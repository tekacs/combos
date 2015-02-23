package actors

import akka.actor._
import misc.{Fact, Topic}

import scala.collection.{mutable, parallel}
import scala.language.implicitConversions

class TopicActor(val topic: Topic) extends Actor with ActorLogging {
  import actors.TopicActor._

  var subscribers = mutable.MutableList[ActorRef]()
  val facts = mutable.MutableList[Fact]()

  def receive = {
    case Subscribe =>
      val subscriptionActor = context.actorOf(Props[SubscriptionActor])
      subscribers += subscriptionActor
      subscriptionActor ! SubscriptionActor.Init(sender(), topic)
    case newFact @ NewFact(fact) =>
      facts += fact
      for (subscriber <- subscribers) subscriber ! newFact
    case AllFacts =>
      sender ! facts.toList

  }
}
object TopicActor {
  var topicMap = parallel.ParMap[Topic, ActorRef]()
  def get(topic: Topic, system: ActorSystem): ActorRef = topicMap.synchronized {
    topicMap.getOrElse(topic, {
      val actor = system.actorOf(Props.create(classOf[TopicActor], topic))
      topicMap = topicMap + (topic, actor)
      actor
    })
  }

  case object Subscribe
  case object AllFacts
  case class NewFact(fact: Fact)
}
