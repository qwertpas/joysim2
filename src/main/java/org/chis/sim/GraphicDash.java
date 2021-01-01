package org.chis.sim;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.chis.sim.GraphicDash.Serie.Point;
import org.chis.sim.Util.Vector2D;

public class GraphicDash extends JPanel{

    // static functions and variables, affects all window graphs
    public static ArrayList<GraphicDash> graphicDashs = new ArrayList<GraphicDash>();

    public static void paintAll(){
        for(GraphicDash graphicDash : graphicDashs){
            graphicDash.repaint();
        }
    }

    public static void resetAll(){
        for(GraphicDash graphicDash : graphicDashs){
            graphicDash.reset();
        }
    }


    // Instance functions and variables, for each window graph separately
    JFrame frame;
    Dimension frameSize = new Dimension(300, 300);
    boolean isTracking;

    ArrayList<Serie> series = new ArrayList<Serie>();

    public GraphicDash(String name, int maxPoints, boolean tracking){
        isTracking = tracking;

        frame = new JFrame(name);
		frame.add(this);
        frame.setSize(frameSize);
        frame.setLocation((int) (GraphicSim.screenWidth - Util.posModulo((frameSize.getWidth() * (graphicDashs.size() + 2)), GraphicSim.screenWidth)), 0);
		frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        calcScales();

        for(Serie serie : series){
            serie.maxLength = maxPoints;
        }
        
        graphicDashs.add(this);

        System.out.println("New GraphicDash: " + name);
    }

    int leftMargin = 20;
    int rightMargin = 20;
    int bottomMargin = 20;
    int topMargin = 20;

    double xMin, xMax, yMin, yMax;

    double plotWidth, plotHeight;
    double xAxis, yAxis;
    double xScale, yScale;

    int xMinPixel, xMaxPixel, yMinPixel, yMaxPixel, xAxisPixel, yAxisPixel; //actual pixel locations, after applying margins

    @Override
    public void paint(Graphics g) { //run by each instance of GraphicDebug (each window graph)
        super.paint(g);
        
        if(isTracking){
            xMin = Integer.MAX_VALUE;
            xMax = Integer.MIN_VALUE;
            yMin = Integer.MAX_VALUE;
            yMax = Integer.MIN_VALUE;
        }

        for(Serie serie : series){
            if(serie.on){
                synchronized(serie.points){ //synchonized to avoid concurrent exceptions with usercode thread
                    for(Point point : serie.points){
                        if(point.x < xMin) {
                            xMin = point.x;
                        }else if(point.x > xMax){
                            xMax = point.x;
                        }

                        if(point.y < yMin) {
                            yMin = point.y;
                        }else if(point.y > yMax){
                            yMax = point.y;
                        }
                    }
                }

            }
        }

        if(isTracking){
            xMin -= 0.1;
            xMax += 0.1;
            yMin -= 0.1;
            yMax += 0.1;
        }

        calcScales();

        //draw axes
        g.drawLine(xMinPixel, xAxisPixel, xMaxPixel, xAxisPixel);
        g.drawLine(yAxisPixel, yMinPixel, yAxisPixel, yMaxPixel);

        //label axes
        g.setColor(Color.BLACK);
        NumberFormat numFormat = new DecimalFormat("#.###E0");
        g.drawString(numFormat.format(xMin), xMinPixel - 20, frame.getContentPane().getHeight()/2);
        g.drawString(numFormat.format(xMax), xMaxPixel - 40, frame.getContentPane().getHeight()/2);
        g.drawString(numFormat.format(yMin), frame.getContentPane().getWidth()/2 - 20, yMinPixel);
        g.drawString(numFormat.format(yMax), frame.getContentPane().getWidth()/2 - 20, yMaxPixel);

        for(Serie serie : series){ //draw each scatterplot series in the graph
            if(serie.on){
                g.setColor(serie.color);
                synchronized(serie.points){
                    int displayX = 0;
                    int displayY = 0;
                    for(Point point : serie.points){ //draw all the points in the serie so far
                        displayX = (int) (point.x * xScale + yAxis + leftMargin);
                        displayY = (int) (frame.getContentPane().getHeight() - (point.y * yScale + xAxis + bottomMargin));

                        g.fillOval(displayX, displayY, serie.lineWidth, serie.lineWidth);
                    }
                    g.drawString(serie.name, displayX, displayY);
                }
                
            }
        }


    }

    /**
     * Prints the number and graphs it with respect to elapsed time. Run this every time you want to update the number.
     * @param label What the number means.
     * @param number The number you want to print
     * @param graph Which graph you want to put it on
     * @param color what color you want the point to be
     */
    public void putNumber(String label, double number, Color color){
        Printouts.put(label, number);

        for(Serie serie : series){
            if(serie.name == label){
                serie.addPoint(Main.elaspedTime, number);
                return;
            }
        }

        Serie newSerie = new Serie(label, color);
        newSerie.addPoint(Main.elaspedTime, number);
        series.add(newSerie);
    }

    /**
     * Prints the point and graphs it. Run this every time you want to update the number.
     * @param label What the number means.
     * @param number The number you want to print
     * @param graph Which graph you want to put it on
     * @param color what color you want the point to be
     */
    public void putPoint(String label, Vector2D point, Color color){
        Printouts.put(label, point);

        for(Serie serie : series){
            if(serie.name == label){
                serie.addPoint(point);
                return;
            }
        }

        Serie newSerie = new Serie(label, color);
        newSerie.addPoint(point);
        series.add(newSerie);
    }

    

    void calcScales(){
        plotWidth = frame.getContentPane().getWidth() - leftMargin - rightMargin;
        plotHeight = frame.getContentPane().getHeight() - bottomMargin - topMargin;
        
        yAxis = plotWidth * (-xMin / (xMax - xMin));
        xScale = Math.abs(yAxis / xMin);

        xAxis = plotHeight * (-yMin / (yMax - yMin));
        yScale = Math.abs(xAxis / yMin);

        xMinPixel = leftMargin;
        xMaxPixel = frame.getContentPane().getWidth() - rightMargin;
        yMinPixel = frame.getContentPane().getHeight() - bottomMargin;
        yMaxPixel = topMargin;
        yAxisPixel = (int) (leftMargin + yAxis); //an x coordinate
        xAxisPixel = (int)(frame.getContentPane().getHeight() - (bottomMargin + xAxis)); //a y coordinate

    }

    public void reset(){
        for(Serie serie: series){
            serie.points.clear();
        }
        xMin = Integer.MAX_VALUE;
        xMax = Integer.MIN_VALUE;
        yMin = Integer.MAX_VALUE;
        yMax = Integer.MIN_VALUE;
    }


    public static class Serie{ //series but singular :/
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


    //idk why this is needed
    private static final long serialVersionUID = 1L;

}