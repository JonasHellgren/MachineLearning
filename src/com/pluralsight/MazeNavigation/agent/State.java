package com.pluralsight.MazeNavigation.agent;

public class State {
    public byte x;  //x position
    public byte y;  //y position


    public  State(State s) {   //constructor
        this.x=s.x; this.y=s.y;
    }

    public  State(int x, int y) {   //constructor
        this.x = (byte)x; this.y = (byte)y;
    }

    public void setXY(int x, int y) {
        this.x = (byte)x; this.y = (byte)y;
    }


    public void setS(State s) {
        this.x = s.x; this.y = s.y;
    }


    public void add(State s) {
        this.x= (byte) (this.x+s.x);    this.y= (byte) (this.y+s.y);
    }


    public void rot(Double theta) {
        this.x= (byte) (this.x*Math.cos(theta)-this.y*Math.sin(theta));
        this.y= (byte) (this.x*Math.sin(theta)+this.y*Math.cos(theta));
    }


    public boolean eq(State s) {
        return (this.x==s.x) && (this.y==s.y);
    }

    @Override
    public String toString() {
    StringBuilder sb = new StringBuilder(20);
    sb.append(this.x); sb.append(", ");  sb.append(this.y);
    return sb.toString();
    }

}
