package com.pluralsight.MazeNavigation.agent;

import com.pluralsight.MazeNavigation.enums.Action;

interface Memory {
    //realization of following method required later
     double readMem(Pos2d s, Action a);   //read from memory at postion sa
     void clearMem();
}
