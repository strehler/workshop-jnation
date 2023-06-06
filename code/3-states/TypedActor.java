
public interface TypedActor {
    interface Behavior<T> { Effect<T> receive(T o); }
    interface Effect<T> { Behavior<T> transition(Behavior<T> next); }
    interface Address<T> { Address<T> tell(T msg); }
}
