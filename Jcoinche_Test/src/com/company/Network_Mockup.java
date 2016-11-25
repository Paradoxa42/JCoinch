package com.company;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Gabriel on 24/11/2016.
 */
public class Network_Mockup implements Runnable
{
    ArrayList<Player>   waiting_players;
    boolean             stop_waiting;
    Game                Game;

    Network_Mockup(Game n_Game)
    {
        waiting_players = new ArrayList<Player>();
        stop_waiting = false;
        Game = n_Game;
    }

    public void run()
    {
        int i = 0;
        Scanner scanner = new Scanner(System.in);
        while (waiting_players.size() < 4 && stop_waiting == false && i < 4)
        {
            System.out.print("Enter your name : \n");
            String name = scanner.next();
            waiting_players.add(new Player(name));
            Game.waiting_player = true;
            i++;
        }
        System.out.print("Stopping looking for players.\n");
    }

    public String Take_Next_Input()
    {
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        return (input);
    }
}
