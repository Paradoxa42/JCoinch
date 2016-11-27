//package com.company;

/**
 * Created by Gabriel on 24/11/2016.
 */

public class Card
{
    CARD_SUIT suit;
    CARD_VALUE value;

    Player  last_owner;

    public Card(int n_suit, int n_value)
    {
        suit = CARD_SUIT.values()[n_suit];
        value = CARD_VALUE.values()[n_value];
    }

    public Card(CARD_SUIT n_suit, CARD_VALUE n_value)
    {
        suit = n_suit;
        value = n_value;
    }

    public int Get_Score(CARD_SUIT trump)
    {
        if (value == CARD_VALUE.NINE)
            if (suit == trump)
                return (14);
            else
                return (0);
        if (value == CARD_VALUE.TEN)
                return (10);
        if (value == CARD_VALUE.JACK)
            if (suit == trump)
                return (20);
            else
                return (2);
        if (value == CARD_VALUE.QUEEN)
            return (3);
        if (value == CARD_VALUE.KING)
            return (4);
        if (value == CARD_VALUE.ACE)
            return (11);
        return (0);
    }
}
