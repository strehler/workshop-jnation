import static java.lang.System.out;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

interface Actor {

    interface Address { Address tell(Object msg); }
    interface Behavior { Effect receive(Object msg); }
    interface Effect { Behavior transition(Behavior behavior); }

    static Effect Become(Behavior next) { return old -> next; }
    Effect Stay = current -> current;
    Effect Die = Become(msg -> {
        out.println("Dropping msg [" + msg + "] due to severe case of death.");
        return Stay;
    });

    record System(ExecutorService executorService) {
        public Address actorOf(Function<Address, Behavior> initial) {
            var addr = new Address() {
                AtomicInteger on = new AtomicInteger();
                ConcurrentLinkedQueue<Object> mb = new ConcurrentLinkedQueue<>();
                Behavior behavior = msg -> switch(msg) {
                    case Address a -> Become(initial.apply(a));
                    default -> Stay;
                };
                public Address tell(Object msg) { mb.offer(msg); async(); return this; }
                void async() {
                    if (!mb.isEmpty() && on.compareAndSet(0, 1)) {
                        try { executorService.execute(this::run); }
                        catch (Throwable t) { on.set(0); throw t; }}}
                public void run() {
                    try { if (on.get() == 1) { var m = mb.poll(); if (m!=null) { 
                        behavior = behavior.receive(m).transition(behavior); } }}
                    finally { on.set(0); async(); }}
            };
            return addr.tell(addr);
        }
    }

}

