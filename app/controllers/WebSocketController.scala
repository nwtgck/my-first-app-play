package controllers

import javax.inject._

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, KillSwitches, UniqueKillSwitch}
import akka.stream.scaladsl.{BroadcastHub, Flow, Keep, MergeHub, Sink}
import play.api.mvc._

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class WebSocketController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def connect(): WebSocket = WebSocket.accept{ req =>
    Flow.fromFunction[String, String](txt => s"flow passed! '$txt'")
  }

  def dynamic(): WebSocket = WebSocket.accept[String, String]{ req=>
    DynamicStream.publishSubscribeFlow
  }
}

// (from: https://speakerdeck.com/wadayusuke/implement-stream-server-with-akka-streams-and-websocket)
object DynamicStream{
  implicit val as  = ActorSystem()
  implicit val mat = ActorMaterializer()

  import scala.concurrent.duration._

  lazy val publishSubscribeFlow: Flow[String, String, UniqueKillSwitch] = {
    val (sink, source) =
      MergeHub.source[String](perProducerBufferSize = 16)
        .toMat(BroadcastHub.sink(bufferSize = 256))(Keep.both)
        .run()

    source.runWith(Sink.ignore)

    val busFlow: Flow[String, String, UniqueKillSwitch] =
      Flow.fromSinkAndSource(sink, source)
          .joinMat(KillSwitches.singleBidi[String, String])(Keep.right)
          .backpressureTimeout(3.seconds)

    busFlow
  }
}