#! /usr/bin/env elixir

defmodule Main do
  def pingponger(name) do
    receive do
      {:ping, count, sender} ->
        IO.puts "#{name} received ping, count down #{count}"
        if count > 0 do
          send sender, {:ping, count - 1, self()}
          pingponger(name)
        else
          :ok
        end
    end
  end

  def wait(pid) do
    wait(pid, true)
  end

  def wait(pid, alive) do
    case alive do
      false ->
        :ok
      true ->
        wait(pid, Process.alive?(pid))
    end
  end
end

pid1 = spawn fn -> Main.pingponger("pinger") end
pid2 = spawn fn -> Main.pingponger("ponger") end

send pid1, {:ping, 10, pid2}

Main.wait(pid1)
