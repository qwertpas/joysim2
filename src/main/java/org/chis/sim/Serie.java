package org.chis.sim;

import java.util.ArrayList;

import org.chis.sim.Util.Vector2D;

import java.awt.Color;

public class Serie{ //series but singular :/
    String name = "defaultname";
    Color color = Color.BLACK;
    int lineWidth = 3;
    int maxLength = 300;
    volatile ArrayList<Point> points = new ArrayList<Point>();
    volatile Boolean on = true;

    /** 
     * An object that contains a set of points. Feed into a GraphicDebug to graph it.
     * @param name_input label what the dots mean
     * @param color_input color you want the dots to be
     */
    public Serie(String name_input, Color color_input){
        name = name_input;
        color = color_input;
    }

    public Serie(String name_input, Color color_input, ArrayList<Vector2D> path){
        name = name_input;
        color = color_input;
        synchronized(points){
            for(Vector2D point : path){
                points.add(new Point(point.x, point.y));
            }
        }
    }

    /**
     * Adds a point into the serie. If the serie is already at max length, it removes the oldest point.
     * @param x x-coordinate of the point
     * @param y y-coordinate of the point
     */
    public void addPoint(double x, double y){
        synchronized(points){ //synchronized so usercode thread can call this while painting and avoid concurrentModificationException
            points.add(new Point(x, y));
            if(points.size() > maxLength){
                points.remove(0);
            }
        }
        
    }

    public void addPoint(Vector2D vector2d){
        synchronized(points){ //synchronized so usercode thread can call this while painting and avoid concurrentModificationException
            points.add(new Point(vector2d.x, vector2d.y));
            if(points.size() > maxLength){
                points.remove(0);
            }
        }
        
    }


    public class Point{ // quick alternative to java.awt.Point which can only do ints
        double x, y;
        public Point(double x, double y){
            this.x = x;
            this.y = y;
        }
    }
}