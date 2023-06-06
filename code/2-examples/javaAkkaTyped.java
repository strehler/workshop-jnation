///usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS com.typesafe.akka:akka-actor-typed_2.13:2.6.19

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;


import static java.lang.System.*;

public class javaAkkaTyped {

    static class Ping {
        final int count;
        ActorRef<Ping> replyTo;

        Ping(int count, ActorRef<Ping> replyTo) {
            this.count = count;
            this.replyTo = replyTo;
        }

        int getCount() {
            return count;
        }

        ActorRef<Ping> getReplyTo() {
            return replyTo;
        }
    }

    static class Pingponger extends AbstractBehavior<Ping> {

        public static Behavior<Ping> create() {
            return Behaviors.setup(Pingponger::new);
        }

        private Pingponger(ActorContext<Ping> context) {
            super(context);
        }

        @Override
        public Receive<Ping> createReceive() {
            return newReceiveBuilder().onMessage(Ping.class, this::onPing).build();
        }

        private Behavior<Ping> onPing(Ping ping) {
            out.println(getContext().getSelf().path() + " received ping, count down " + ping.getCount());
            if (ping.getCount() > 0) {
                ping.getReplyTo().tell(new Ping(ping.getCount() - 1, getContext().getSelf()));
            } else {
                System.exit(0);
            }
            return this;
        }

    }

    static class Main extends AbstractBehavior<Void> {

        public static Behavior<Void> create() {
            return Behaviors.setup(Main::new);
        }

        private Main(ActorContext<Void> context) {
            super(context);
            ActorRef pinger = context.spawn(Pingponger.create(), "pinger");
            ActorRef ponger = context.spawn(Pingponger.create(), "ponger");
            pinger.tell(new Ping(10, ponger));
        }

        @Override
        public Receive<Void> createReceive() {
            return newReceiveBuilder().build();
        }

    }

    public static void main(String... args) {
        ActorSystem.create(Main.create(), "main");
    }
}
