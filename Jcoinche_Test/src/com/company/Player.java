package com.company;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Gabriel on 24/11/2016.
 */
public class Player
{
    CardSet hand;
    short team;
    String name;
    int score;

    public Player(String n_name)
    {
        hand = new CardSet();
        name = n_name;
        team = -1;
        score = 0;
    }
}
