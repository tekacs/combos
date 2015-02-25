package controllers

import actors.TopicActor
import actors.TopicActor.{TakeFacts, NewFact}
import misc.AkkaInterop._
import misc.{Fact, Topic}
import play.api.libs.concurrent.Akka
import play.api.libs.json.{JsArray, JsObject, Json}
import play.api.mvc._

import scala.util.{Failure, Success, Try}

object TopicController extends Controller {
  import play.api.Play.current

  def topics =
    Action { _ =>
      Ok(JsArray(Topic.topics.map(_.json).toSeq))
    }

  def facts(topic: Topic, drop: Option[Int] = None, limit: Option[Int] = None, after_id: Option[String] = None) =
    Action { _ =>
      after_id match {
        case None =>
          TopicActor.get(topic, Akka.system).callBlocking[List[Fact]](
            TakeFacts(drop.getOrElse(0), limit.getOrElse(10))
          ) match {
            case Success(facts) => Ok(JsArray(facts.map(_.json).toSeq))
            case Failure(_) => RequestTimeout
          }
        case Some(after) => NotImplemented
      }
    }

  def publishFact(topic: Topic) =
    Action { request =>
      Try(Json.parse(request.body.asText.get)).toOption match {
        case Some(json@JsObject(_)) =>
          TopicActor.get(topic, Akka.system) ! NewFact(new Fact(topic, json))
          Accepted
        case _ => BadRequest
      }
    }
}
