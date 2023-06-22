package Math1;

import org.opencv.core.Point;

/*
 * Author Timm Daniel Rasmussen.
 */

public class LineCreation {

    public double[] getSlopeAndBegin(Point p1, Point p2){
        double[] result = new double[2];
        double slope = (p2.y-p1.y)/(p2.x-p1.x);
        double begin = p1.y-(slope*p1.x);
        result[0] = slope;
        result[1] = begin;
        return result;
    }

}
