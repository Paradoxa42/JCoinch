package com.util;
import java.util.ArrayList;

public class WaitRoom {
    static final ArrayList Players = new ArrayList();

    public boolean AddPlayer(int ID) {
        Players.add(ID);
        if (Players.size() == 4) {
            System.out.println("La game peut commencer YAY !");
            return true;
        }
        return false;
    }
}