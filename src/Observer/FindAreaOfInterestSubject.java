package Observer;

import Interface.FindAreaOfInterest;
import ObjectDetection.RedCrossDetection;
import ObjectDetection.RedRectangleDetection;
import Singleton.VideoCaptureSingleton;
import org.opencv.core.Point;
import org.opencv.videoio.VideoCapture;

import java.util.List;

public class FindAreaOfInterestSubject extends Subject implements FindAreaOfInterest {

    private RedRectangleDetection fieldDetection;
    private final VideoCapture videoCapture;

    public FindAreaOfInterestSubject(VideoCaptureSingleton videoCaptureSingleton){
        this.videoCapture = VideoCaptureSingleton.getInstance().getVideoCapture();
        fieldDetection = new RedRectangleDetection(videoCapture);

    }

    @Override
    public List<Point> getGoalPos() {
        return fieldDetection.detectField(this.videoCapture);
    }

    @Override
    public List<Point> getCorners() {
        return fieldDetection.determineGoalCenters();
    }

    @Override
    public List<Point> getCross() {
        RedCrossDetection redCrossDetection = new RedCrossDetection(videoCapture, fieldDetection.getAoiMask());
        redCrossDetection.detectCross();
        return redCrossDetection.getCross();
    }


}
