/*
 *    Copyright 2021 Andrea Peruffo
 *    Copyright 2021 Edoardo Vacchi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */
//SOURCES lib/Channels.java

import io.github.evacchi.channels.Channels;

import java.nio.charset.StandardCharsets;
import java.util.function.Function;

import static io.github.evacchi.TypedActor.*;

class ChannelActor<T> {
    sealed interface ChannelProtocol {};
    static record WriteLine(String payload) implements ChannelProtocol {}
    static record ReadBuffer(String content) implements ChannelProtocol {}

    private final Address<T> parent;
    private final Function<String, T> buildMsg;
    private final Channels.Socket channel;

    ChannelActor(Address<T> parent, Function<String, T> buildMsg, Channels.Socket channel) {
        this.parent = parent;
        this.buildMsg = buildMsg;
        this.channel = channel;
    }

    <T> Behavior<ChannelProtocol> socketHandler(Address<ChannelProtocol> self) {
        return socketHandler(self,"");
    }

    private <T> Behavior<ChannelProtocol> socketHandler(Address<ChannelProtocol> self, String partial) {
        channel.read()
                .thenAccept(s -> self.tell(new ReadBuffer(s)))
                .exceptionally(err -> { err.printStackTrace(); return null; });

        return msg -> socketHandler(self, msg, partial);
    }

    private <T> Effect<ChannelProtocol> socketHandler(Address<ChannelProtocol> self, ChannelProtocol msg, String partial) {
        return switch (msg) {
            case ReadBuffer incoming -> {
                var acc = (partial + incoming.content());
                var eol = acc.indexOf('\n');
                if (eol >= 0) {
                    var line = acc.substring(0, eol);
                    parent.tell(buildMsg.apply(line));
                    var rest = incoming.content().substring(Math.min(eol + 2, incoming.content().length()));
                    yield Become(socketHandler(self, rest));
                } else {
                    var rest = partial + incoming.content();
                    yield Become(socketHandler(self, rest));
                }
            }
            case WriteLine line -> {
                channel.write((line.payload() + '\n').getBytes(StandardCharsets.UTF_8));
                yield Stay();
            }
        };
    }
}
