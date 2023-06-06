///usr/bin/env jbang "$0" "$@" ; exit $?
//JAVA 20
//JAVAC_OPTIONS --enable-preview --release 20
//JAVA_OPTIONS  --enable-preview

import static java.lang.System.*;

public class hello {
    sealed interface ToGreet {};
    record User(String name) implements ToGreet {};
    record World() implements ToGreet {};

    public static void main(String... args) {
        switch (args.length > 0 ? new User(args[0]) : new World()) {
            case User(var name) -> out.println("Hello " + name);
            case World w -> out.println("Hello World!");
        }
    }
}
