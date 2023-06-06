#!/usr/bin/env escript

pingponger(Name, Main) ->
    receive
        {ping, Count, Ping_PID} ->
            io:format("~s received ping, count down ~w~n", [Name, Count]),
            if Count > 0 ->
              Ping_PID ! {ping, Count - 1, self()},
              pingponger(Name, Main);
            true ->
              exit ("done")
            end
    end.

main(_) ->
    Ping1_PID = spawn(fun() -> pingponger("pinger", self()) end),
    Ping2_PID = spawn(fun() -> pingponger("ponger", self()) end),
    Ping1_PID ! {ping, 10, Ping2_PID},
    wait(Ping1_PID, true).

wait(Pid, true) ->
  wait(Pid, is_process_alive(Pid));
wait(_, false) ->
  ok.
