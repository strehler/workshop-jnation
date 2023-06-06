///usr/bin/env jbang "$0" "$@" ; exit $?
//JAVA 20
//COMPILE_OPTIONS --enable-preview --release 19
//RUNTIME_OPTIONS --enable-preview 
//SOURCES Actor.java
import static java.lang.System.out;

interface Playground {
    static void main(String... args) {
        out.println("Hello JFOKUS!");
    }
}
