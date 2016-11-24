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

    CardSet(int n_length)
    {
        set = new ArrayList<Card>();
        length = n_length;
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
            if ((i + 1) % 8 == 0)
            {
                suit++;
                value = 5;
            }
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
       /* for (int i = 0; i < length; i++)
        {
            System.out.print(i + " : " + set.get(i).value + " of " + set.get(i).suit + "S\n");
            if ((i + 1) % 8 == 0 && shuffling_rows == 0)
                System.out.print('\n');
        }*/
    }
}
