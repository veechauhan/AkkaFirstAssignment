package edu.Knoldus

import akka.actor._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.util.Try


case object Messages

case class End(receivePings: Int)

case class GetPongSum(sum: Option[Int])

case class ThrowException()

object PartTwo extends App {

  class Ping extends Actor {
    var sum = 0
    val pong: ActorRef = context.actorOf(Props[Pong], name = "pong")

    def receive: Receive = {
      case Messages => for (x <- 1 to 10000) pong ! "Ping"
      case message: String => sum += 1
      case End(s) => println("Sum:" + s)


      case GetPongSum(s) => println(s)
        pong ! GetPongSum(None)
        sender ! End(sum)
        pong ! ThrowException()
        pong ! GetPongSum(None)
        pong ! GetPongSum(Some(sum))


    }
  }

  class Pong extends Actor {
    var sum = 0

    def doWork(): Int = {

      1
    }

    def receive: Receive = {
      case ThrowException() => println(Try(throw new Exception()))
      case End(counter) => println("Counter:" + counter)
      case GetPongSum(value) => println(value)
      case message: String => val future: Future[Int] = Future {
        sum += doWork()
        sum
      }
        Await.result(future, Duration.Inf)
        if (sum < 10000)
          sender ! "Pong"
        else if (sum == 10000) {
          sender ! End(sum)

          sender ! GetPongSum(Some(sum))
        }

    }
  }

  val system = ActorSystem()
  val ping = system.actorOf(Props[Ping], name = "ping")

  ping ! Messages
}