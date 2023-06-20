package Observer;

import Interface.FindAreaOfInterest;
import LineCreation.Circle;
import ObjectDetection.RedCrossDetection;
import ObjectDetection.RedRectangleDetection;
import Singleton.VideoCaptureSingleton;
import org.opencv.core.Point;
import org.opencv.videoio.VideoCapture;

import java.util.List;

public class FindAreaOfInterestSubject extends Subject implements FindAreaOfInterest {

    public final RedRectangleDetection fieldDetection;

    public FindAreaOfInterestSubject(){
        fieldDetection = new RedRectangleDetection();
        fieldDetection.detectField();
    }

    @Override
    public List<Point> getGoalPos() {
        return fieldDetection.getGoals();
    }

    @Override
    public Point[] getCorners() {
        return fieldDetection.getCourseCoordinates();
    }

    public Point[] getFloorCorners(){return fieldDetection.getFloorCorners();}

    @Override
    public Circle getCross() {
        return fieldDetection.getRedCross().getCrossArea();
    }

    @Override
    public Circle getScaleAdjustedCross() {
        return fieldDetection.getRedCross().getScalefactorAdjustedCrossArea();
    }

    @Override
    public double getScaleFactor() {
        return fieldDetection.getScaleFactor();
    }

    public void newDetection(){
        this.fieldDetection.detectField();
    }


}
