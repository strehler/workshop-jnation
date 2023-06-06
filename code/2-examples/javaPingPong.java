///usr/bin/env jbang "$0" "$@" ; exit $?
//JAVA 20
//JAVAC_OPTIONS --enable-preview --release 20
//JAVA_OPTIONS  --enable-preview
//SOURCES ../actor/TypedActor.java

import io.github.evacchi.TypedActor;

import java.util.concurrent.Executors;

import static io.github.evacchi.TypedActor.*;
import static java.lang.System.out;

interface PingPong {

    sealed interface Pong {}
    record SimplePong(Address<Ping> sender, int count) implements Pong {}
    record DeadlyPong(Address<Ping> sender) implements Pong {}

    record Ping(Address<Pong> sender, int count) {}

    static void main(String... args) {
        var actorSystem = new TypedActor.System(Executors.newCachedThreadPool());
        Address<Ping> ponger = actorSystem.actorOf(self -> msg -> pongerBehavior(self, msg));
        Address<Pong> pinger = actorSystem.actorOf(self -> msg -> pingerBehavior(self, msg));
        ponger.tell(new Ping(pinger, 10));
    }
    static Effect<Ping> pongerBehavior(Address<Ping> self, Ping msg) {
        if (msg.count() > 0) {
            out.println("ping! 👉");
            msg.sender().tell(new SimplePong(self, msg.count() - 1));
            return Stay();
        } else {
            out.println("ping! 💀");
            msg.sender().tell(new DeadlyPong(self));
            return Die();
        }
    }
    static Effect<Pong> pingerBehavior(Address<Pong> self, Pong msg) {
        return switch (msg) {
            case SimplePong(var sender, var count) -> {
                out.println("pong! 👈");
                sender.tell(new Ping(self, count - 1));
                yield Stay();
            }
            case DeadlyPong p -> {
                out.println("pong! 😵");
                yield Die();
            }
        };
    }
}
