#!/usr/bin/env -S scala-cli shebang
//> using lib "com.typesafe.akka::akka-actor:2.6.19"

import akka.actor._

case class Ping(count: Int)

class Pingponger extends Actor {
  def receive = { // Any => Unit
    case Ping(count) =>
      println(s"${self.path} received ping, count down $count")

      if (count > 0) {
        sender() ! Ping(count - 1)
      } else {
        System.exit(0)
      }
  }
}

val system = ActorSystem("pingpong")

val pinger = system.actorOf(Props[Pingponger](), "pinger")
val ponger = system.actorOf(Props[Pingponger](), "ponger")

pinger.tell(Ping(10), ponger)
