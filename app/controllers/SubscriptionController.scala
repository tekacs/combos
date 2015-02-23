package controllers

import actors.{SubscriptionActor, TopicActor}
import misc.AkkaInterop._
import misc.{Fact, Subscription, Topic}
import play.api.libs.concurrent.Akka
import play.api.mvc._

import scala.concurrent.duration._
import scala.util.{Try, Failure, Success}

object SubscriptionController extends Controller {
  import actors.SubscriptionActor._
  import actors.TopicActor._
  import play.api.Play.current

  def newSubscription(topic: Topic) = Action { _ =>
    val actor = TopicActor.get(topic, Akka.system)
    actor.callBlocking[Subscription](Subscribe) match {
      case Success(v) => Ok(v.json)
      case Failure(_) => RequestTimeout
    }
  }

  def next(topic: Topic, sub_id: String) = Action { request =>
    val patience = Try(request.headers("patience").toInt).toOption match {
      case Some(t) => t.seconds
      case None => 2.seconds
    }
    SubscriptionActor.get(sub_id) match {
      case None => NotFound
      case Some(sub) => sub.ref.callBlocking[Option[Fact]](Next(patience)) match {
        case Success(v) => v match {
          case Some(fact) => Ok(fact.json)
          case None => NoContent
        }
        case Failure(_) => RequestTimeout
      }
    }
  }

  def flush(topic: Topic, sub_id: String) = Action { _ =>
    SubscriptionActor.get(sub_id) match {
      case None => NotFound
      case Some(sub) =>
        sub.ref.call(Flush)
        Accepted
    }
  }
}
