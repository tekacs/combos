package misc

import akka.actor.ActorRef
import akka.pattern.ask

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.language.implicitConversions
import scala.reflect.ClassTag
import scala.util.Try

class AkkaInterop(val ref: ActorRef) extends AnyVal {
  def call[T: ClassTag](message: Any, actorTimeout: FiniteDuration = 30.seconds): Future[T] = {
    ask(ref, message)(actorTimeout + 1.second).mapTo[T]
  }
  def callBlocking[T](message: Any, timeout: FiniteDuration = 30.seconds): Try[T] = {
    Try(Await.result(ask(ref, message)(timeout + 1.second), timeout).asInstanceOf[T])
  }
}
object AkkaInterop {
  implicit def interopFromRef(ref: ActorRef): AkkaInterop = new AkkaInterop(ref)
}
