package actors

import actors.TopicActor.NewFact
import akka.actor.{PoisonPill, ActorRef, ActorLogging, Actor}
import misc.Fact

import scala.collection.mutable
import scala.concurrent.duration.FiniteDuration
import scala.util.Try

class SubscriptionReplyActor(queue: mutable.Queue[Fact], replyTo: ActorRef, timeout: FiniteDuration)
  extends Actor with ActorLogging {
  import scala.concurrent.ExecutionContext.Implicits.global
  import SubscriptionReplyActor._

  def receive = {
    case Init =>
      if (replyDequeue()) self ! Expire
      else context.system.scheduler.scheduleOnce(timeout, self, Expire)
    case NewFact(fact) =>
      replyDequeue()
    case Expire =>
      replyTo ! None
      self ! PoisonPill
  }

  def dequeue: Option[Fact] = Try(queue.dequeue()).toOption

  def replyDequeue(): Boolean = dequeue match {
    case Some(fact) =>
      replyTo ! Some(fact)
      true
    case None => false
  }
}
object SubscriptionReplyActor {
  case object Init
  case object Expire
  case object Stop
}
