//JAVA 20
//JAVAC_OPTIONS --enable-preview --release 20
//JAVA_OPTIONS  --enable-preview

package io.github.evacchi;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Function;

import static java.lang.System.out;

public interface TypedLoomActor {
    interface Effect<T> { Behavior<T> transition(Behavior<T> next); }
    interface Behavior<T> { Effect<T> receive(T o); }
    interface Address<T> { Address<T> tell(T msg); }

    static <T> Effect<T> Become(Behavior<T> next) { return current -> next; }
    static <T> Effect<T> Stay() { return current -> current; }
    static <T> Effect<T> Die() { return Become(msg -> { out.println("Dropping msg [" + msg + "] due to severe case of death."); return Stay(); }); }

    record System() {
        public <T> Address<T> actorOf(Function<Address<T>, Behavior<T>> initial) {
            return null;
        }
    }
}
