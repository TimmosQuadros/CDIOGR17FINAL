package Observer;

import Interface.RobotPosition;
import Singleton.VideoCaptureSingleton;
import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import java.util.ArrayList;
import java.util.List;

public class RobotPositionSubject implements RobotPosition {

    private final VideoCapture videoCapture;

    public RobotPositionSubject(){
        this.videoCapture = VideoCaptureSingleton.getInstance().getVideoCapture();
    }


    @Override
    public List<Point> getPos() {
        Point center = null;
        Mat frame1 = new Mat();
        int frames = 1;
        List<Point> circlePoints = new ArrayList<>();

        if (videoCapture.isOpened()) {
            for (int j = 0; j<frames;j++) {
                videoCapture.read(frame1);
                if (!frame1.empty()) {
                    Mat grayFrame = new Mat();
                    Imgproc.cvtColor(frame1, grayFrame, Imgproc.COLOR_BGR2GRAY);

                    Mat blurredFrame = new Mat();
                    Imgproc.GaussianBlur(grayFrame, blurredFrame, new Size(9, 9), 2, 2);

                    Mat circles = new Mat();
                    Imgproc.HoughCircles(blurredFrame, circles, Imgproc.HOUGH_GRADIENT, 1, 20, 200, 30, 40, 50);

                    if (circles.cols() > 0) {
                        for (int i = 0; i < circles.cols(); i++) {
                            double[] circle = circles.get(0, i);
                            center = new Point(circle[0], circle[1]);
                            circlePoints.add(center);
                        }
                    }else{
                        //just keep trying until you get a result
                        frames++;
                    }
                }
            }
        } else {
            System.out.println("Failed to open camera!");
        }
        return circlePoints;
    }
}
