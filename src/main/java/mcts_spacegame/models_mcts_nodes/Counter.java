package mcts_spacegame.models_mcts_nodes;

public class Counter {
    int myCount;
    public Counter( ) {
        myCount = 0;
    }
    public void increment ( ) {
        myCount++;
    }
    public void reset ( ) {
        myCount = 0;
    }

    public void setMyCount(int count) {
        myCount=count;
    }

    public int value ( ) {
        return myCount;
    }
}
