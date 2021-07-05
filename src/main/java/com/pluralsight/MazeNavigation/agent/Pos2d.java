package com.pluralsight.MazeNavigation.agent;

public class Pos2d {
    private byte x;  //x position
    private byte y;  //y position


    public Pos2d(Pos2d s) {   //constructor
        this.x=s.x; this.y=s.y;
    }

    public Pos2d(int x, int y) {   //constructor
        this.x = (byte)x; this.y = (byte)y;
    }

    public void setXY(int x, int y) {
        this.x = (byte)x; this.y = (byte)y;
    }


    public void setS(Pos2d s) {
        this.x = s.x; this.y = s.y;
    }

    public byte getX() {
        return x;
    }

    public byte getY() {
        return y;
    }

    public void add(Pos2d s) {
        this.x= (byte) (this.x+s.x);    this.y= (byte) (this.y+s.y);
    }


    public void rot(Double theta) {
        byte xtemp= (byte) (this.x*Math.cos(theta)-this.y*Math.sin(theta));  //store in temp to not corrupt this.y calc
        this.y= (byte) (this.x*Math.sin(theta)+this.y*Math.cos(theta));
        this.x=xtemp;
    }


    public boolean eq(Pos2d s) {
        return (this.x==s.x) && (this.y==s.y);
    }

    @Override
    public String toString() {
    StringBuilder sb = new StringBuilder(20);
    sb.append(this.x); sb.append(", ");  sb.append(this.y);
    return sb.toString();
    }

}
