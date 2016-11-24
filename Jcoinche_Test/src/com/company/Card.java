package com.company;

/**
 * Created by Gabriel on 24/11/2016.
 */

public class Card
{
    CARD_SUIT suit;
    CARD_VALUE value;

    public Card(int n_suit, int n_value)
    {
        suit = CARD_SUIT.values()[n_suit];
        value = CARD_VALUE.values()[n_value];
    }
}
