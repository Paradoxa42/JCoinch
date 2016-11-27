//package com.company;

import sun.nio.ch.Net;
import io.netty.channel.Channel;

import java.util.ArrayList;

/**
 * Created by Gabriel on 24/11/2016.
 */

public class Game implements Runnable
{
    Network_Mockup Network;

    ArrayList<Channel>  channels = new ArrayList<Channel>();
    int                 donne;
    int                 trick;
    int                 reshuffle;
    CardSet             deck;
    ArrayList<Player>   players_list;
    CARD_SUIT           trump;
    CARD_SUIT           leading_suit;
    int                 contracts[];
    int                 scores[];
    CardSet             tricks[];
    int                 highest_contract;
    boolean             waiting_player;
    short               taking_team;
    boolean             capot;
    boolean             coinche;
    boolean             surcoinche;

    public void Write_to_all(String msg) {
        for (Channel c: channels) {
            c.writeAndFlush(msg + "\r");
        }
    }

    public Game()
    {
        Network = new Network_Mockup(this);

        donne = 0;
        deck = new CardSet();
//        deck.Shuffle_Set(0);
        players_list = new ArrayList<Player>();
        contracts = new int[2];
        scores = new int[2];
        scores[0] = 0;
        scores[1] = 0;
        tricks = new CardSet[8];
        highest_contract = 0;
        waiting_player = false;
        taking_team = -1;
    }

    public void run()
    {
        Wait_Players();
        Write_to_all("OWPEN DA GAME!\n");
        while(donne < 8 && reshuffle < 10)
        {
            reshuffle = 0;
            Run_Row();
        }
        Write_to_all("CLOSE DA GAME!\n");
    }

    private void Empty_All()
    {
        deck.Empty_Set();
        for (int j = 0; j < 4; j++)
            players_list.get(j).hand.Empty_Set();
    }

    private void Run_Row()
    {
        int i = 0;
        int pass_per_row = 0;
        boolean last_bidding = false;
        boolean bidding = false;

        trick = 0;

        if (reshuffle >= 10)
            return;

        Write_to_all("Starting donne " + (donne + 1) + " reshuffled " + reshuffle + " times.\n\n");
        deck.Fill_Set_32();
        deck.Shuffle_Set(2);
        Distribute_Cards();

       // trump = deck.Take_Card();

        while ((pass_per_row < 3 || bidding == false) && coinche == false)
        {
            if (pass_per_row == 4 && reshuffle < 10)
            {
                Empty_All();
                reshuffle++;
                Run_Row();
                return;
            }

            last_bidding = Handle_Bidding(players_list.get(i), bidding);

            if (last_bidding == true)
            {
                bidding = true;
                pass_per_row = 0;
            }
            else
                pass_per_row++;
            i = (i + 1) % 4;
            if (coinche == true)
                Handle_Bidding(players_list.get(i), bidding);
        }

        if (contracts[0] == highest_contract)
            taking_team = 0;
        else
            taking_team = 1;

        Write_to_all("Starting the game.\n");
        Write_to_all("Taking Team : " + taking_team + "\n");
        Write_to_all("Contract : " + highest_contract + " points.\n");
        Write_to_all("Trump : " + trump.toString() + ".\n");

        Main_Game();

        Empty_All();
        donne++;
    }

    private void Main_Game()
    {
        leading_suit = null;
        int starting_player = 0;

        while (trick < 2)
        {
            tricks[trick] = new CardSet();
            for (int i = 0; i < 4; i++)
            {
                Write_to_all("Leading suit : " + leading_suit + "\n");
                Handle_Card(players_list.get((i + starting_player) % 4));
            }
            starting_player = players_list.indexOf(tricks[trick].Get_Strongest_Card(trump).last_owner);
            Write_to_all(players_list.get(starting_player).name + " won the last trick. He will chose the next suit.\n");
            leading_suit = null;
            trick++;
        }
        Write_to_all("Game ended, counting the scores.\n");
        Calculate_Scores();
    }

    private void Calculate_Scores()
    {
        int[] round_score = new int[2];
        int temp = 0;
        round_score[0] = 0;
        round_score[1] = 0;

        for (int i = 0; i < trick; i++)
        {
            round_score[tricks[i].Get_Strongest_Card(trump).last_owner.team] += tricks[i].Sum_Score(trump);
        }
        if ((round_score[taking_team] > contracts[taking_team]) && round_score[taking_team] > round_score[1 - taking_team])
        {
            temp = ((scores[taking_team] += (contracts[taking_team] + round_score[taking_team]) * (((coinche) ? 1 : 0) + 1)) * (((surcoinche) ? 1 : 0) + 1));
            Write_to_all("Team " + taking_team + " has fullfilled its contract!\n");
            Write_to_all("Team " + taking_team + " has won " + temp + " points!\n");
            temp = scores[1 - taking_team] += round_score[1 - taking_team];
            Write_to_all("Team " + (1 - taking_team) + " has won " + temp + " points!\n");
        }
        else
        {
            temp = ((scores[1 - taking_team] += (contracts[taking_team] + round_score[1 - taking_team]) * (((coinche) ? 1 : 0) + 1)) * (((surcoinche) ? 1 : 0) + 1));
            Write_to_all("Team " + taking_team + " has failed its contract!\n");
            Write_to_all("Team " + (1 - taking_team) + " has won " + temp + " points!\n");
        }
    }

    private void Handle_Card(Player current_player)
    {
        String input = "";
        int int_input = 0;
        boolean first_loop = true;

        Write_to_all("\n" + current_player.name + "'s turn to play!\n");
        Write_to_all("Your hand : \n");
        for (int i = 0; i < current_player.hand.length; i++)
        {
            System.out.print((i + 1) + " : " + current_player.hand.set.get(i).value.toString() + " of " + current_player.hand.set.get(i).suit.toString() + "\n");
        }
        while (input.isEmpty() || !isValid(input, current_player.hand.length, current_player))
        {
            if (first_loop == false)
                Write_to_all("Invalid input.\n");

            input = Network.Take_Next_Input();
            if (first_loop == true)
                first_loop = false;
        }
        int_input = Integer.parseInt(input) - 1;
        current_player.hand.set.get(int_input).last_owner = current_player;
        current_player.hand.Take_Card(tricks[trick], int_input);
        if (leading_suit == null)
            leading_suit = tricks[trick].set.get(0).suit;
    }

    private boolean Handle_Bidding(Player current_player, boolean bidding) {
        String  input = "";
        String  suit = "";
        int     int_input = 0;
        boolean first_loop = true;

        Write_to_all(current_player.name + "'s turn to bid!\n");


        while (input.isEmpty() || suit.isEmpty() || !isValid(input, suit))
        {
            if (first_loop == false)
                Write_to_all("Invalid input.\n");
            input = Network.Take_Next_Input();

            if (input.equalsIgnoreCase("Pass"))
                return (false);
            else if (input.equalsIgnoreCase("Capot"))
            {
                capot = true;
                return (false);
            }
            else if (input.equalsIgnoreCase("Coinche"))
            {
                if (bidding == true)
                {
                    if (coinche = true)
                        surcoinche = true;
                    else
                        coinche = true;
                    return (false);
                }
            }
            else if (input.contains(" "))
            {
                suit = input.substring(input.indexOf(" ") + 1);
                input = input.substring(0, input.indexOf(" "));
            } else if (isValid(input))
                input = Network.Take_Next_Input();
/*            input = Network.Take_Next_Input();
            if (input.contains(" ")) {
                suit = input.substring(input.indexOf(" ") + 1);
                input = input.substring(0, input.indexOf(" "));
            } else
                suit = Network.Take_Next_Input();*/
 //           System.out.print("Input : " + input + "\n");
//            System.out.print("Suit : " + suit + "\n");
            if (first_loop == true)
                first_loop = false;
        }

        int_input = Integer.parseInt(input);
        int_input = (int)(10 * Math.ceil(int_input / 10));

        highest_contract = int_input;
        trump = String_To_Suit(suit);
        contracts[current_player.team] = int_input;

        // Notifies the player and takes the input

        // Returns false if the player passes, true otherwise

        return (true);
    }

    private void Distribute_Cards()
    {
        for (int i = 0; i < 3; i++)
        {
            for (int j = 0; j < 4; j++)
            {
                deck.Take_Card(players_list.get(j).hand);
            }
        }
        for (int i = 0; i < 2; i++)
        {
            for (int j = 0; j < 4; j++)
            {
                deck.Take_Card(players_list.get(j).hand);
            }
        }
        for (int i = 0; i < 3; i++)
        {
            for (int j = 0; j < 4; j++)
            {
                deck.Take_Card(players_list.get(j).hand);
            }
        }
        for (int i = 0; i < 4; i++)
        {
            Write_to_all(players_list.get(i).name + "'s hand : \n");
            for (int j = 0; j < players_list.get(i).hand.set.size(); j++)
            {
                Write_to_all((j + 1) + " : " + players_list.get(i).hand.set.get(j).value + " of " +  players_list.get(i).hand.set.get(j).suit + "\n");
            }
            Write_to_all("\n");
        }
    }

    private void Wait_Players()
    {
        new Thread(Network).start();
        while (players_list.size() < 4)
        {
            try {
                Thread.sleep(1000);
            }
            catch (InterruptedException error) {
                System.out.println("Interrupted Exception");
            }
            if (waiting_player)
            {
                players_list.add(Network.waiting_players.get(Network.waiting_players.size() - 1));
                Network.waiting_players.remove(Network.waiting_players.size() - 1);
                waiting_player = false;
                Write_to_all("Adding new player : " + players_list.get(players_list.size() - 1).name + "\n");
            }
        }
        Network.stop_waiting = true;
        players_list.get(0).team = 1;
        players_list.get(1).team = 0;
        players_list.get(2).team = 1;
        players_list.get(3).team = 0;
        Write_to_all("Room complete!\n");
    }

    boolean isNumeric(String str)
    {
        for (int i = 0; i < str.length(); i++)
        {
            if (!Character.isDigit(str.charAt(i)))
                return (false);
        }
        return (true);
    }

    boolean isValid(String input)
    {
        int int_input;
        if (isNumeric(input))
        {
            int_input = Integer.parseInt(input);
            int_input = (int) Math.floor((int_input + 10/2) / 10) * 10;
            if ((int_input >= 80 && int_input <= 650) && int_input > highest_contract)
                return (true);
        }
        return (false);
    }

    boolean isValid(String input, int length, Player current_player)
    {
        int int_input;

        if (isNumeric(input))
        {
            int_input = Integer.parseInt(input) - 1;
            if (int_input > 0 || int_input <= length)
                if (leading_suit != null)
                {
                    if (current_player.hand.set.get(int_input).suit == leading_suit)
                        return (true);
                    else if (!current_player.hand.Has_Suit(leading_suit))
                    {
                         if (current_player.team == tricks[trick].Get_Strongest_Card(trump).last_owner.team)
                             return (true);
                         if (current_player.hand.set.get(int_input).suit == trump)
                         {
                             if (current_player.hand.set.get(int_input).Get_Score(trump) > tricks[trick].Get_Strongest_Card(trump, trump).Get_Score(trump))
                                 return (true);
                             else if (current_player.hand.Get_Strongest_Card(trump, trump).Get_Score(trump) < tricks[trick].Get_Strongest_Card(trump, trump).Get_Score(trump))
                                 return (true);
                         }
                         else if (!current_player.hand.Has_Suit(trump))
                             return (true);
                    }
                }
                else
                    return (true);
        }
        return (false);
    }

    boolean isValid(String input, String suit)
    {
        int int_input;
        if (!isValid(input))
            return (false);
        if (!isNumeric(suit))
        {
            if (!suit.equalsIgnoreCase("Spade") && !suit.equalsIgnoreCase("Club") && !suit.equalsIgnoreCase("Heart") && !suit.equalsIgnoreCase("Diamond"))
                return (false);
        }
        return (true);
    }

    CARD_SUIT String_To_Suit(String suit)
    {
        if (suit.equalsIgnoreCase("Spade"))
            return CARD_SUIT.SPADE;
        if (suit.equalsIgnoreCase("Club"))
            return CARD_SUIT.CLUB;
        if (suit.equalsIgnoreCase("Heart"))
            return CARD_SUIT.HEART;
        if (suit.equalsIgnoreCase("Diamond"))
            return CARD_SUIT.DIAMOND;
        return (null);
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