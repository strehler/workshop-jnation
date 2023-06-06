//JAVA 20
//JAVAC_OPTIONS --enable-preview --release 20
//JAVA_OPTIONS  --enable-preview
//SOURCES ../../actor/TypedActor.java
package io.github.evacchi.typed.examples;

import io.github.evacchi.TypedActor;

import java.util.concurrent.Executors;

import static io.github.evacchi.TypedActor.*;
import static java.lang.System.out;

public interface VendingMachine {
    sealed interface Vend {}
    static record Coin(int amount) implements Vend {
        public Coin {
            if (amount < 1 && amount > 100)
                throw new AssertionError("1 <= amount < 100");
        }
    }
    static record Choice(String product) implements Vend {}

    static void main(String... args) {
        var actorSystem = new TypedActor.System(Executors.newCachedThreadPool());
        Address<Vend> vendingMachine = actorSystem.actorOf(VendingMachine::initial);
        vendingMachine
                .tell(new Coin(50))
                .tell(new Coin(40))
                .tell(new Coin(30))
                .tell(new Choice("Chocolate"));
    }
    static Behavior<Vend> initial(Address<Vend> self) {
        return message -> switch (message) {
            case Choice c -> Stay();
            case Coin c -> {
                onFirstCoin(c.amount());
                yield Become(waitCoin(self, c.amount()));
            }
        };
    }

    static Behavior<Vend> waitCoin(Address<Vend> self, int counter) {
        return message -> switch(message) {
            case Coin c when counter + c.amount() < 100 -> {
                var count = counter + c.amount();
                onCoin(count);
                yield Become(waitCoin(self, count));
            }
            case Coin c -> {
                var count = counter + c.amount();
                var change = counter + c.amount() - 100;
                onLastCoin(count);
                yield Become(vend(self, change));
            }
            case Choice c -> Stay(); // ignore message, stay in this state
        };
    }

    static Behavior<Vend> vend(Address<Vend> self, int change) {
        return message -> switch(message) {
            case Choice c -> {
                vendProduct(c.product());
                releaseChange(change);
                yield Become(initial(self));
            }
            case Coin c -> Stay(); // ignore message, stay in this state
        };
    }

    static void onFirstCoin(int amount) {
        out.println("Received first coin: " + amount);
    }

    static void onCoin(int count) {
        out.println("Received coin: " + count + " of 100");
    }

    static void onLastCoin(int count) {
        out.println("Received last coin: " + count + " of 100");
    }

    static void vendProduct(String product) {
        out.println("VENDING: " + product);
    }

    static  void releaseChange(int change) {
        out.println("CHANGE: " + change);
    }

}