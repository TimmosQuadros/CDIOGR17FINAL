package IncodeMessage;

import org.opencv.core.Point;

import java.util.List;

public class Incoder
{
    public String ballPositions(List<Point> balls){
        String res = "";
        int size = balls.size()-1;
        Point lastBall = balls.get(size);
        res+=MessageStrings.BallsPos.toString()+":";
        for(Point p : balls){
            if(p.equals(lastBall)){
                res+=p.x+","+p.y;
            }else{
                res+=p.x+","+p.y+";";
            }
        }
        return res;
    }

    public String starPos(Point startPos){
        return MessageStrings.Position+":"+startPos.x+","+startPos.y;
    }

    public String startAngle(double startAngle){
        return MessageStrings.StartAngle+":"+startAngle;
    }
}
