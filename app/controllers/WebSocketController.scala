package controllers

import javax.inject._

import akka.stream.scaladsl.Flow
import play.api.mvc._

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class WebSocketController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def connect(): WebSocket = WebSocket.accept{req =>
    Flow.fromFunction[String, String](txt => s"flow passed! '$txt'")
  }
}
