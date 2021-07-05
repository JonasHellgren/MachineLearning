package com.pluralsight.MazeNavigation.enums;

public enum Action {
    N(0), E(1), S(2), W(3);
    public final int val;

    Action(int i) {
        this.val = i;
    }

}