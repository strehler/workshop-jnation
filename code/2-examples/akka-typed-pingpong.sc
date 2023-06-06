#!/usr/bin/env -S scala-cli shebang
//> using lib "com.typesafe.akka::akka-actor-typed:2.6.19"

import akka.NotUsed
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ ActorRef, ActorSystem, Behavior }

case class Ping(count: Int, replyTo: ActorRef[Ping])

object Pingponger {
  def apply(): Behavior[Ping] = Behaviors.receive { (context, ping) =>
    println(s"${context.self.path} received ping, count down ${ping.count}")

    if (ping.count > 0) {
      ping.replyTo ! Ping(ping.count - 1, context.self)
    } else {
      System.exit(0)
    }

    Behaviors.same
  }
}

object Main {
  def apply(): Behavior[NotUsed] =
    Behaviors.setup { context =>
      val pinger = context.spawn(Pingponger(), "pinger")
      val ponger = context.spawn(Pingponger(), "ponger")
      pinger ! Ping(10, ponger)
      
      Behaviors.empty
    }
}

ActorSystem(Main(), "PingPongDemo")
