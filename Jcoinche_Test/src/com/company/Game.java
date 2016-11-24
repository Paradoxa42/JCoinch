package com.company;
import sun.nio.ch.Net;

import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by Gabriel on 24/11/2016.
 */

public class Game
{
    Network_Mockup Network;

    int row;
    CardSet deck;
    ArrayList<Player> players_list;
    boolean waiting_player;

    public Game()
    {
        Network = new Network_Mockup(this);

        row = 0;
        deck = new CardSet(32);
        deck.Fill_Set_32();
        deck.Shuffle_Set(0);
        players_list = new ArrayList<Player>();
        waiting_player = false;
    }

    public void run()
    {
        Wait_Players();
        System.out.print("OPEN DA GAME!\n");
        while(true)
        {
        }
    }

    private void Wait_Players()
    {
        new Thread(Network).start();
        while (players_list.size() < 4)
        {
            if (waiting_player)
            {
                players_list.add(Network.waiting_players.get(Network.waiting_players.size() - 1));
                Network.waiting_players.remove(Network.waiting_players.size() - 1);
                waiting_player = false;
                System.out.print("Adding new player : " + players_list.get(players_list.size() - 1).name + "\n");
            }
        }
        Network.stop_waiting = true;
        System.out.print("Room complete!\n");
    }
}

enum CARD_SUIT
{
    SPADE,
    CLUB,
    HEART,
    DIAMOND
}

enum CARD_VALUE
{
    TWO,
    THREE,
    FOUR,
    FIVE,
    SIX,
    SEVEN,
    EIGHT,
    NINE,
    TEN,
    JACK,
    QUEEN,
    KING,
    ACE
}