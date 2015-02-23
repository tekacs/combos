package controllers

import play.api.mvc.{Action, Controller}

object IndexController extends Controller {
  def index = Action { _ =>
    Ok(
      views.html.index(
        routes.IndexController.index(),
        routes.TopicController.topics(),
        routes.TopicController.facts("TOPIC", None),
        routes.SubscriptionController.newSubscription("TOPIC"),
        routes.SubscriptionController.next("TOPIC", "SUB_ID")
      )
    )
  }
}
