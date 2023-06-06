package io.github.evacchi;

import java.util.function.Function;
import java.util.concurrent.Executor;
import static java.lang.System.out;

public interface TypedActor {
    interface Effect<T> { Behavior<T> transition(Behavior<T> next); }
    interface Behavior<T> { Effect<T> receive(T o); }
    interface Address<T> { Address<T> tell(T msg); }

    static <T> Effect<T> Become(Behavior<T> next) { return current -> next; }
    static <T> Effect<T> Stay() { return current -> current; }
    static <T> Effect<T> Die() { return Become(msg -> { out.println("Dropping msg [" + msg + "] due to severe case of death."); return Stay(); }); }

    record System(Executor executor) {
        public <T> Address<T> actorOf(Function<Address<T>, Behavior<T>> initial) {
            return null;
        }
    }
}
