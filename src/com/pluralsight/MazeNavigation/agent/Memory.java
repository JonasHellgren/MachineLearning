package com.pluralsight.MazeNavigation.agent;

import com.pluralsight.MazeNavigation.enums.Action;

interface Memory {
    //realization of following methods required later
     void saveMem(Pos2d s, Action a,Double value);  //set value at memory position sa
     double readMem(Pos2d s, Action a);   //read from memory at postion sa
}
