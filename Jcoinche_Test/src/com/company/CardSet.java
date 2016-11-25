package com.company;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Gabriel on 24/11/2016.
 */
public class CardSet
{
    ArrayList<Card>    set;
    int             length;

    CardSet()
    {
        set = new ArrayList<Card>();
        length = 0;
    }

    public void Fill_Set_32()
    {
        int suit = 0;
        int value = 5;
        Card temp;

        for (int i = 0; i < 32; i++)
        {
            temp = new Card(suit, value);
            set.add(temp);
            value++;
            length++;
            if ((i + 1) % 8 == 0)
            {
                suit++;
                value = 5;
            }
        }
    }

    public void Empty_Set()
    {
        int size = set.size();

        for (int i = (size - 1); i >= 0; i--)
        {
            set.remove(i);
            length--;
        }
    }

    public void Shuffle_Set(int shuffling_rows)
    {
        int swap_value;
        Card temp;

        for (int i = 1; i <= (length * shuffling_rows); i++)
        {
            swap_value = ThreadLocalRandom.current().nextInt(0, length - 2) + 1;
            temp = set.get((i - 1) % length);
            set.set(((i - 1) % length), set.get(swap_value));
            set.set((swap_value), temp);
        }
    }

    public void Add_Card(Card card)
    {
        set.add(card);
        length++;
    }

    public void Take_Card(CardSet destination, int index)
    {
        destination.Add_Card(set.get(index));
        set.remove(index);
        length--;
    }

    public void Take_Card(CardSet destination)
    {
        destination.Add_Card(set.get(set.size() - 1));
        set.remove(set.size() - 1);
        length--;
    }

    public Card Take_Card(int index)
    {
        Card temp = set.get(index);

        set.remove(index);
        length--;

        return (temp);
    }

    public Card Take_Card()
    {
        Card temp = set.get(set.size() - 1);

        set.remove(set.size() - 1);
        length--;

        return (temp);
    }
}
