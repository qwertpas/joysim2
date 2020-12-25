package org.chis.sim.userclasses.pathplanning;

import java.util.Arrays;
import java.util.List;


public class PurePursuit {
    // list of the points of the path
    private List<float[]> path;

    
    /**
     * Returns the sign of the input number n. Note that the function returns 1 for n = 0 to satisfy the requirements
     * set forth by the line-circle intersection formula.
     *
     * @param n The number to return the sign of.
     * @return A float value of the sign of the number (-1.0f for n < 0, else 1.0f).
     */
    private float signum(float n) {
        if (n == 0) return 1;
        else return Math.signum(n);
    }


    /**
     * Generate the furthest lookahead point on the path that is distance r from the point (x, y).
     *
     * @param x The x of the origin.
     * @param y The y of the origin.
     * @param r The lookahead distance.
     * @return A float[] coordinate pair if the lookahead point exists, or null.
     * @see <a href="http://mathworld.wolfram.com/Circle-LineIntersection.html">Circle-Line Intersection</a>
     */
    public float[] getLookaheadPoint(float x, float y, float r) {
        float[] lookahead = null;



        // iterate through all pairs of points
        for (int i = 0; i < path.size() - 1; i++) {
            // form a segment from each two adjacent points
            float[] segmentStart = path.get(i);
            float[] segmentEnd = path.get(i + 1);


            // translate the segment to the origin
            float[] p1 = new float[]{segmentStart[0] - x, segmentStart[1] - y};
            float[] p2 = new float[]{segmentEnd[0] - x, segmentEnd[1] - y};


            // calculate an intersection of a segment and a circle with radius r (lookahead) and origin (0, 0)
            float dx = p2[0] - p1[0];
            float dy = p2[1] - p1[1];
            float d = (float) Math.sqrt(dx * dx + dy * dy);
            float D = p1[0] * p2[1] - p2[0] * p1[1];


            // if the discriminant is zero or the points are equal, there is no intersection
            float discriminant = r * r * d * d - D * D;
            if (discriminant < 0 || Arrays.equals(p1, p2)) continue;


            // the x components of the intersecting points
            float x1 = (float) (D * dy + signum(dy) * dx * Math.sqrt(discriminant)) / (d * d);
            float x2 = (float) (D * dy - signum(dy) * dx * Math.sqrt(discriminant)) / (d * d);


            // the y components of the intersecting points
            float y1 = (float) (-D * dx + Math.abs(dy) * Math.sqrt(discriminant)) / (d * d);
            float y2 = (float) (-D * dx - Math.abs(dy) * Math.sqrt(discriminant)) / (d * d);


            // whether each of the intersections are within the segment (and not the entire line)
            boolean validIntersection1 = Math.min(p1[0], p2[0]) < x1 && x1 < Math.max(p1[0], p2[0])
                    || Math.min(p1[1], p2[1]) < y1 && y1 < Math.max(p1[1], p2[1]);
            boolean validIntersection2 = Math.min(p1[0], p2[0]) < x2 && x2 < Math.max(p1[0], p2[0])
                    || Math.min(p1[1], p2[1]) < y2 && y2 < Math.max(p1[1], p2[1]);


            // remove the old lookahead if either of the points will be selected as the lookahead
            if (validIntersection1 || validIntersection2) lookahead = null;


            // select the first one if it's valid
            if (validIntersection1) {
                lookahead = new float[]{x1 + x, y1 + y};
            }


            // select the second one if it's valid and either lookahead is none,
            // or it's closer to the end of the segment than the first intersection
            if (validIntersection2) {
                if (lookahead == null || Math.abs(x1 - p2[0]) > Math.abs(x2 - p2[0]) || Math.abs(y1 - p2[1]) > Math.abs(y2 - p2[1])) {
                    lookahead = new float[]{x2 + x, y2 + y};
                }
            }
        }


        // special case for the very last point on the path
        if (path.size() > 0) {
            float[] lastPoint = path.get(path.size() - 1);


            float endX = lastPoint[0];
            float endY = lastPoint[1];


            // if we are closer than lookahead distance to the end, set it as the lookahead
            if (Math.sqrt((endX - x) * (endX - x) + (endY - y) * (endY - y)) <= r) {
                return new float[]{endX, endY};
            }
        }


        return lookahead;
    }

}
