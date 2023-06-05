package Observer;

import Interface.FindAreaOfInterest;
import ObjectDetection.MrRobotDetection;
import ObjectDetection.RedCrossDetection;
import ObjectDetection.RedRectangleDetection;
import Singleton.VideoCaptureSingleton;
import org.opencv.core.Point;
import org.opencv.videoio.VideoCapture;

import java.util.List;

public class FindAreaOfInterestSubject extends Subject implements FindAreaOfInterest {

    private final RedRectangleDetection fieldDetection;

    public FindAreaOfInterestSubject(){
        fieldDetection = new RedRectangleDetection();
        fieldDetection.detectField();
        fieldDetection.determineGoalCenters();
    }

    @Override
    public List<Point> getGoalPos() {
        return fieldDetection.getGoals();
    }

    @Override
    public List<Point> getCorners() {
        return fieldDetection.getFloorCorners();
    }

    @Override
    public List<Point> getCross() {
        return fieldDetection.getRedCross().getCoordinates();
    }

    public void detectRobot(){
        MrRobotDetection mrRobotDetection = new MrRobotDetection();
        mrRobotDetection.findPoints(fieldDetection);
    }

    public void newDetection(){
        this.fieldDetection.detectField();
        this.fieldDetection.determineGoalCenters();
    }


}
