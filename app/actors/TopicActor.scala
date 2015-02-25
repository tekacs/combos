package actors

import akka.actor._
import com.typesafe.config.Config
import misc.config.TopicConfig
import misc.{Fact, Topic}
import net.ceedubs.ficus.Ficus._

import scala.collection.{mutable, parallel}
import scala.language.implicitConversions

class TopicActor(val topic: Topic) extends Actor with ActorLogging {

  import actors.TopicActor._
  import net.ceedubs.ficus.readers.ArbitraryTypeReader._
  import play.api.Play.current

  private val appConfig = current.configuration.underlying
  val topicKey = if (topic == Topic.wildcard) "_wildcard" else topic.name
  val configPath = s"combo.topic.$topicKey"
  val configBase = appConfig.withFallback(appConfig.as[Config]("combo.topic._default"))
  val config = if (configBase.hasPath(configPath))
    configBase.as[TopicConfig](configPath)
  else
    configBase.as[TopicConfig]("combo.topic._default")
  val facts = new breeze.collection.mutable.RingBuffer[Fact](config.buffer_size)
  var subscribers = mutable.MutableList[ActorRef]()

  def receive = {
    case Subscribe =>
      val subscriptionActor = context.actorOf(Props[SubscriptionActor])
      subscribers += subscriptionActor
      subscriptionActor ! SubscriptionActor.Init(sender(), topic)
    case newFact@NewFact(fact) =>
      facts += fact
      if (wildcardActor != self) wildcardActor ! newFact
      for (subscriber <- subscribers) subscriber ! newFact
    case TakeFacts(drop, take) =>
      sender ! facts.reverseIterator.drop(drop).take(take).toList
  }
}

object TopicActor {
  var wildcardActor: ActorRef = null
  var topicMap = parallel.ParMap[Topic, ActorRef]()

  def initWildcard(system: ActorSystem): Unit = {
    wildcardActor = system.actorOf(Props.create(classOf[TopicActor], Topic.wildcard))
    topicMap.synchronized {
      topicMap = topicMap +(Topic.wildcard, wildcardActor)
    }
  }

  def get(topic: Topic, system: ActorSystem): ActorRef = topicMap.getOrElse(topic, {
    topicMap.synchronized {
      val actor = system.actorOf(Props.create(classOf[TopicActor], topic))
      topicMap = topicMap +(topic, actor)
      actor
    }
  })

  case class TakeFacts(drop: Int, take: Int)
  case class NewFact(fact: Fact)
  case object Subscribe
}
