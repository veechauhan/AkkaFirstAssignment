package edu.Knoldus

import akka.actor._
import akka.event.{Logging, LoggingAdapter}

case object StartMessage

object PartOne extends App {
  class Ping extends Actor {
    val log: LoggingAdapter = Logging(context.system, this)
    val pong: ActorRef = context.actorOf(Props[Pong], name = "pong")

    def receive: Receive = {
      case StartMessage => pong ! "Ping"
      case message: String => log.info(message)

    }
  }

  class Pong extends Actor {
    val log: LoggingAdapter = Logging(context.system, this)

    def receive: Receive = {
      case message: String => log.info(message)
        sender ! "Pong"

    }
  }

  val system = ActorSystem()
  val ping = system.actorOf(Props[Ping], name = "ping")
  ping ! StartMessage
}