//JAVA 20
//JAVAC_OPTIONS --enable-preview --release 20
//JAVA_OPTIONS  --enable-preview
//SOURCES TypedActor.java

import io.github.evacchi.TypedActor;

import java.util.concurrent.Executors;

import static io.github.evacchi.TypedActor.*;
import static java.lang.System.out;

public interface PingPong {

    sealed interface Pong {}
    record SimplePong(Address<Ping> sender) implements Pong {}
    record DeadlyPong(Address<Ping> sender) implements Pong {}

    record Ping(Address<Pong> sender) {}

    static void main(String... args) {
        var actorSystem = new TypedActor.System(Executors.newCachedThreadPool());
        Address<Ping> ponger = actorSystem.actorOf(self -> msg -> pongerBehavior(self, msg, 0));
        Address<Pong> pinger = actorSystem.actorOf(self -> msg -> pingerBehavior(self, msg));
        ponger.tell(new Ping(pinger));
    }
    static Effect<Ping> pongerBehavior(Address<Ping> self, Ping msg, int counter) {
        return Stay();
    }
    static Effect<Pong> pingerBehavior(Address<Pong> self, Pong msg) {
        return Stay();
    }
}
