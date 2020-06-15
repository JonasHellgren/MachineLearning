package com.pluralsight.MazeNavigation.agent;

import com.pluralsight.MazeNavigation.enums.Action;

import java.util.TreeMap;

public class TabularMemory implements Memory {

    TreeMap<Integer, Double> Qsa;  // HashMap of type TreeMap

    public TabularMemory() {  //constructor
        Qsa = new TreeMap<>();  // Creating TreeMap
    }

    @Override
    public void saveMem(Pos2d s, Action a, Double value) {
        Qsa.put(getKey(s,a),value);
    }

    @Override
    public double readMem(Pos2d s, Action a) {
        Integer key=getKey(s,a);
        if (Qsa.containsKey(key))
            return Qsa.get(key);
         else
            return 0;
    }

    public Integer getKey(Pos2d s, Action a) {
        int x=s.getX(); int y=s.getY(); int ai=a.val;
        int nofa=Action.values().length;
        int nofy=10;  //TODO, hardcoded replace later
        return x*nofy*nofa+y*nofa+ai;
    }

    public void clearMem()  { Qsa.clear(); };
}
