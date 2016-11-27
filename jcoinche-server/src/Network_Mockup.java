//package com.company;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Gabriel on 24/11/2016.
 */
public class Network_Mockup implements Runnable
{
    String              message;
    ArrayList<Player>   waiting_players;
    boolean             stop_waiting;
    Game                game;
    Object              condition = new Object();

    Network_Mockup(Game n_Game)
    {
        waiting_players = new ArrayList<Player>();
        stop_waiting = false;
        game = n_Game;
        message = "";
    }

    public void run()
    {
        int i = 0;
        Scanner scanner = new Scanner(System.in);
        while (waiting_players.size() < 4 && stop_waiting == false && i < 4)
        {
            game.Write_to_all("Enter your name : \n");
            waiting_players.add(new Player(Take_Next_Input()));
            game.waiting_player = true;
            i++;
        }
        stop_waiting = true;
        game.Write_to_all("Stopping looking for players.\n");
    }

    public String Take_Next_Input()
    {
        message = "";
        try {
            while (message.isEmpty()) Thread.sleep(1000);
        }
        catch (InterruptedException error) {
            System.out.println("Interrupted Exception");
        }
        System.out.println(message);
        return (message);
    }
}
