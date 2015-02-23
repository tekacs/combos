package actors

import akka.actor._
import misc.AkkaInterop._
import misc.Util.generateId
import misc.{Fact, Subscription, Topic}

import scala.collection.{mutable, parallel}
import scala.concurrent.duration.FiniteDuration

class SubscriptionActor extends Actor with ActorLogging {
  import actors.SubscriptionActor._
  import actors.TopicActor._

  val queue = mutable.Queue[Fact]()
  var replyActor: Option[ActorRef] = None

  override def receive = {
    case Init(replyTo, topic) =>
      val sub_id = generateId
      context.become(subscribed(sub_id), discardOld = true)
      val sub = Subscription(sub_id, topic, self)
      subscriptionMap.put(sub_id, sub)
      replyTo ! sub
  }

  def subscribed(sub: String): Receive = {
    case newFact@NewFact(fact) =>
      queue.enqueue(fact)
      replyActor match {
        case Some(ref) => ref ! newFact
        case None =>
      }
    case Next(timeout: FiniteDuration) =>
      replyActor = Some(context.actorOf(Props.create(classOf[SubscriptionReplyActor], queue, sender(), timeout)))
      replyActor.get.call(SubscriptionReplyActor.Init, timeout)
    case Flush =>
      queue.clear()
    case Stop =>
      self ! PoisonPill
  }
}
object SubscriptionActor {
  case class Init(replyTo: ActorRef, topic: Topic)
  case class Next(timeout: FiniteDuration)
  case object Flush
  case object Stop

  val subscriptionMap = parallel.mutable.ParMap[String, Subscription]()
  def get(sub_id: String) = subscriptionMap.get(sub_id)
}
