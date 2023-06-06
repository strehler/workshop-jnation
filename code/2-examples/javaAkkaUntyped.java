///usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS com.typesafe.akka:akka-actor_2.13:2.6.19

import akka.actor.AbstractActor;
import akka.actor.ActorSystem;
import akka.actor.ActorRef;
import akka.actor.Props;

import static java.lang.System.*;

public class javaAkkaUntyped {

    static class Ping {
        final int count;

        Ping(int count) {
            this.count = count;
        }

        int getCount() {
            return count;
        }
    }

    static class Pingponger extends AbstractActor {

        @Override
        public Receive createReceive() {
            return receiveBuilder()
                .match(
                    Ping.class,
                    ping -> {
                        out.println(getSelf().path() + " received ping, count down " + ping.getCount());
                        if (ping.getCount() > 0) {
                            getSender().tell(new Ping(ping.getCount() - 1), getSelf());
                        } else {
                            System.exit(0);
                        }
                    })
                .build();
        }
    }

    public static void main(String... args) {
        ActorSystem system = ActorSystem.apply("pingpong");

        ActorRef pinger = system.actorOf(Props.create(Pingponger.class), "pinger");
        ActorRef ponger = system.actorOf(Props.create(Pingponger.class), "ponger");

        pinger.tell(new Ping(10), ponger);
    }
}
