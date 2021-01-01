package org.chis.sim.userclasses.pathplanning;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.chis.sim.Util.Vector2D;
import org.chis.sim.Util.Vector2D.Type;


public class PurePursuit {
    
    /**
     * Returns the sign of the input number n. Note that the function returns 1 for n = 0 to satisfy the requirements
     * set forth by the line-circle intersection formula.
     *
     * @param n The number to return the sign of.
     * @return A double value of the sign of the number (-1.0f for n < 0, else 1.0f).
     */
    private static double signum(double n) {
        if (n == 0) return 1;
        else return Math.signum(n);
    }

    public static double getCurvature(ArrayList<Vector2D> path, double r){
        double curvature = 0;
        Vector2D lookahead = getLookaheadPoint(path, 0, 0, r);
        if(lookahead != null){
            curvature = 2 * lookahead.y / Math.pow(lookahead.getMagnitude(), 2);
        }
        return curvature;
    }


    /**
     * Generate the furthest lookahead point on the path that is distance r from the point (x, y).
     *
     * @param path The set of Vector2D coordinate pairs that form the path
     * @param x The x of the origin.
     * @param y The y of the origin.
     * @param r The lookahead distance.
     * @return A Vector2D coordinate pair if the lookahead point exists, or null.
     * @see <a href="http://mathworld.wolfram.com/Circle-LineIntersection.html">Circle-Line Intersection</a>
     */
    public static Vector2D getLookaheadPoint(ArrayList<Vector2D> path, double x, double y, double r) {
        Vector2D lookahead = null;

        // iterate through all pairs of points
        for (int i = 0; i < path.size() - 1; i++) {
            // form a segment from each two adjacent points
            Vector2D segmentStart = path.get(i);
            Vector2D segmentEnd = path.get(i + 1);


            // translate the segment to the origin
            Vector2D p1 = new Vector2D(segmentStart.x - x, segmentStart.y - y, Type.CARTESIAN);
            Vector2D p2 = new Vector2D(segmentEnd.x - x, segmentEnd.y - y, Type.CARTESIAN);


            // calculate an intersection of a segment and a circle with radius r (lookahead) and origin (0, 0)
            double dx = p2.x - p1.x;
            double dy = p2.y - p1.y;
            double d = (double) Math.sqrt(dx * dx + dy * dy);
            double D = p1.x * p2.y - p2.x * p1.y;


            // if the discriminant is zero or the points are equal, there is no intersection
            double discriminant = r * r * d * d - D * D;

            if (discriminant < 0 || p1.equals(p2)) continue;



            // the x components of the intersecting points
            double x1 = (double) (D * dy + signum(dy) * dx * Math.sqrt(discriminant)) / (d * d);
            double x2 = (double) (D * dy - signum(dy) * dx * Math.sqrt(discriminant)) / (d * d);


            // the y components of the intersecting points
            double y1 = (double) (-D * dx + Math.abs(dy) * Math.sqrt(discriminant)) / (d * d);
            double y2 = (double) (-D * dx - Math.abs(dy) * Math.sqrt(discriminant)) / (d * d);


            // whether each of the intersections are within the segment (and not the entire line)
            boolean validIntersection1 = Math.min(p1.x, p2.x) < x1 && x1 < Math.max(p1.x, p2.x)
                    || Math.min(p1.y, p2.y) < y1 && y1 < Math.max(p1.y, p2.y);
            boolean validIntersection2 = Math.min(p1.x, p2.x) < x2 && x2 < Math.max(p1.x, p2.x)
                    || Math.min(p1.y, p2.y) < y2 && y2 < Math.max(p1.y, p2.y);


            // remove the old lookahead if either of the points will be selected as the lookahead
            if (validIntersection1 || validIntersection2) lookahead = null;


            // select the first one if it's valid
            if (validIntersection1) {
                lookahead = new Vector2D(x1 + x, y1 + y, Type.CARTESIAN);
            }


            // select the second one if it's valid and either lookahead is none,
            // or it's closer to the end of the segment than the first intersection
            if (validIntersection2) {
                if (lookahead == null || Math.abs(x1 - p2.x) > Math.abs(x2 - p2.x) || Math.abs(y1 - p2.y) > Math.abs(y2 - p2.y)) {
                    lookahead = new Vector2D(x2 + x, y2 + y, Type.CARTESIAN);
                }
            }
        }


        // special case for the very last point on the path
        if (path.size() > 0) {
            Vector2D lastPoint = path.get(path.size() - 1);


            double endX = lastPoint.x;
            double endY = lastPoint.y;


            // if we are closer than lookahead distance to the end, set it as the lookahead
            if (Math.sqrt((endX - x) * (endX - x) + (endY - y) * (endY - y)) <= r) {
                return new Vector2D(endX, endY, Type.CARTESIAN);
            }
        }


        return lookahead;
    }

    public static void main(String[] args) {
        ArrayList<Vector2D> path = new ArrayList<Vector2D>();
        path.add(new Vector2D(-6, 1, Type.CARTESIAN));
        path.add(new Vector2D(4, 1, Type.CARTESIAN));
        path.add(new Vector2D(5, -3, Type.CARTESIAN));
        System.out.println(getLookaheadPoint(path, 0, 0, 3));
    }

}
