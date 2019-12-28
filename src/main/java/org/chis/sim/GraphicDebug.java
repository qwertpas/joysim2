package org.chis.sim;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.chis.sim.GraphicDebug.Serie.Point;
import org.chis.sim.Util.Vector2D;

public class GraphicDebug extends JPanel{
    private static final long serialVersionUID = -3303992246381800667L;

    // static functions and variables, affects all window graphs

    public static ArrayList<GraphicDebug> graphicDebugs = new ArrayList<GraphicDebug>();

    public static void paintAll(){
        for(GraphicDebug graphicDebug : graphicDebugs){
            graphicDebug.repaint();
        }
    }

    public static void turnOnAll(){
        for(GraphicDebug graphicDebug : graphicDebugs){
            for(Serie serie : graphicDebug.series){
                serie.on = true;
            }
        }
    }

    



    // Instance functions and variables, for each window graph separately
    JFrame frame;
    Dimension frameSize = new Dimension(300, 300);

    ArrayList<Serie> series = new ArrayList<Serie>();

    public GraphicDebug(String name){ // call this from init() above. This makes a new window for graphs. Requires user to use addSerie() after
        frame = new JFrame(name);
		frame.add(this);
        frame.setSize(frameSize);
        frame.setLocation((int) (GraphicSim.screenWidth - Util.posModulo((frameSize.getWidth() * graphicDebugs.size()), GraphicSim.screenWidth)), 0);
		frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        calcScales();
        graphicDebugs.add(this);

        System.out.println("New GraphicDebug: " + name);
    }

    public GraphicDebug(String name, Serie[] series_input, int maxPoints_input){ // same but allows creating series outside and no need to call addSerie()
        for(Serie serie_input : series_input){
            series.add(serie_input);
        }
        frame = new JFrame(name);
		frame.add(this);
        frame.setSize(frameSize);
        frame.setLocation((int) (GraphicSim.screenWidth - Util.posModulo((frameSize.getWidth() * (graphicDebugs.size() + 1)), GraphicSim.screenWidth)), 0);
		frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        calcScales();

        for(Serie serie : series){
            serie.maxLength = maxPoints_input;
        }
        
        graphicDebugs.add(this);

        System.out.println("New GraphicDebug: " + name);
    }

    public void addSerie(){
        series.add(new Serie());
    }

    public void addSerie(Color color){
        series.add(new Serie(color));
    }

    public void addSerie(int lineWidth){
        series.add(new Serie(lineWidth));
    }

    public void addSerie(Color color, int lineWidth){
        series.add(new Serie(color, lineWidth));
    }

    
    
    int leftMargin = 20;
    int rightMargin = 20;
    int bottomMargin = 20;
    int topMargin = 20;

    double xMin = -1;
    double xMax = 1;
    double yMin = -1;
    double yMax = 1;

    double plotWidth, plotHeight;
    double xAxis, yAxis;
    double xScale, yScale;

    int xMinPixel, xMaxPixel, yMinPixel, yMaxPixel, xAxisPixel, yAxisPixel; //actual pixel locations, after applying margins

    @Override
    public void paint(Graphics g) { //run by each instance of GraphicDebug (each window graph)
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.drawString(xMin+"", xMinPixel, xAxisPixel);
        g.drawString(xMax+"", xMaxPixel, xAxisPixel);
        g.drawString(yMin+"", yAxisPixel, yMinPixel);
        g.drawString(yMax+"", yAxisPixel, yMaxPixel);
        
        // draw x-axis
        g.drawLine(xMinPixel, xAxisPixel, xMaxPixel, xAxisPixel);
        // draw y-axis
        g.drawLine(yAxisPixel, yMinPixel, yAxisPixel, yMaxPixel);

        for(Serie serie : series){ //draw each scatterplot series in the graph
            if(serie.on){
                g.setColor(serie.color);
                synchronized(serie.points){ //synchonized to avoid concurrent exceptions with usercode thread
                    for(Point point : serie.points){ //draw all the points in the serie so far

                        if(point.x < xMin) {
                            xMin = point.x;
                            calcScales();
                        }else if(point.x > xMax){
                            xMax = point.x;
                            calcScales();
                        }

                        if(point.y < yMin) {
                            yMin = point.y;
                            calcScales();
                        }else if(point.y > yMax){
                            yMax = point.y;
                            calcScales();
                        }

                        int displayX = (int) (point.x * xScale + yAxis + leftMargin);
                        int displayY = (int) (frame.getContentPane().getHeight() - (point.y * yScale + xAxis + bottomMargin));

                        g.fillOval(displayX, displayY, serie.lineWidth, serie.lineWidth);
                    }
                }
                
            }
        }
    }

    void calcScales(){
        plotWidth = frame.getContentPane().getWidth() - leftMargin - rightMargin;
        plotHeight = frame.getContentPane().getHeight() - bottomMargin - topMargin;
        
        yAxis = plotWidth * (Math.abs(xMin) / (xMax - xMin));
        xScale = -yAxis / xMin;

        xAxis = plotHeight * (Math.abs(yMin) / (yMax - yMin));
        yScale = -xAxis / yMin;

        xMinPixel = leftMargin;
        xMaxPixel = frame.getContentPane().getWidth() - rightMargin;
        yMinPixel = frame.getContentPane().getHeight() - bottomMargin;
        yMaxPixel = topMargin;
        yAxisPixel = (int) (leftMargin + yAxis); //an x coordinate
        xAxisPixel = (int)(frame.getContentPane().getHeight() - (bottomMargin + xAxis)); //a y coordinate

    }


    public static class Serie{ //series but singular :/
        Color color = Color.BLACK;
        int lineWidth = 1;
        int maxLength = 300;
        volatile ArrayList<Point> points = new ArrayList<Point>();
        volatile Boolean on = false; //set to true once UserCode initializes

        public Serie(){
        }

        public Serie(Color color_input){
            color = color_input;
        }

        public Serie(int lineWidth_input){
            lineWidth = lineWidth_input;
        }

        public Serie(Color color_input, int lineWidth_input){
            color = color_input;
            lineWidth = lineWidth_input;
        }

        public void addPoint(double x, double y){
            synchronized(points){ //synchronized so usercode thread can call this while painting and avoid concurrentModificationException
                points.add(new Point(x, y));
            }
            if(points.size() > maxLength){
                points.remove(0);
            }
        }

        public void addPoint(Vector2D vector2d){
            synchronized(points){ //synchronized so usercode thread can call this while painting and avoid concurrentModificationException
                points.add(new Point(vector2d.x, vector2d.y));
            }
            if(points.size() > maxLength){
                points.remove(0);
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
}