import actors.TopicActor
import be.cafeba.cors.filters.CorsFilter
import play.api.libs.concurrent.Akka
import play.api.mvc.WithFilters
import play.api.{Application, GlobalSettings}

object Global
  extends WithFilters(new CorsFilter)
  with GlobalSettings {

  override def onStart(app: Application): Unit = {
    implicit val _ = app
    TopicActor.initWildcard(Akka.system)
  }
}
