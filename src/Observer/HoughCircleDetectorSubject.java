package Observer;

import Interface.HoughCircleDetector;
import Singleton.VideoCaptureSingleton;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import java.util.ArrayList;
import java.util.List;

public class HoughCircleDetectorSubject implements HoughCircleDetector {


    private final int FRAMECOUNT = 30;
    private final double MINDIFF = 10;
    private final VideoCapture videoCapture;
    public HoughCircleDetectorSubject(){
        this.videoCapture = VideoCaptureSingleton.getInstance().getVideoCapture();
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    @Override
    public List<Point> getBalls() {
        Mat frame1 = new Mat();
        List<Point> listCircles = new ArrayList<>();
        if (videoCapture.isOpened()) {
            for (int j = 0; j<FRAMECOUNT; j++) {
                videoCapture.read(frame1);
                if (!frame1.empty()) {
                    Mat grayFrame = new Mat();
                    Imgproc.cvtColor(frame1, grayFrame, Imgproc.COLOR_BGR2GRAY);

                    Mat blurredFrame = new Mat();
                    Imgproc.GaussianBlur(grayFrame, blurredFrame, new Size(9, 9), 2, 2);

                    Mat circles = new Mat();
                    Imgproc.HoughCircles(blurredFrame, circles, Imgproc.HOUGH_GRADIENT, 1, 20, 200, 30, 1, 15);

                    if (circles.cols() > 0) {
                        boolean shouldAddBall = false;
                        for (int i = 0; i < circles.cols(); i++) {
                            double[] circle = circles.get(0, i);
                            Point center = new Point(circle[0], circle[1]);

                            if(!listCircles.isEmpty() && listCircles.size()>1){
                                double xDiff = 999999999;
                                double yDiff = 999999999;
                                for(Point pointNest : listCircles){
                                        if(Math.abs(center.x-pointNest.x)<xDiff)
                                            xDiff = Math.abs(center.x-pointNest.x);
                                        if(Math.abs(center.y-pointNest.y)<yDiff)
                                            yDiff = Math.abs(center.y-pointNest.y);
                                }
                                if(xDiff>= MINDIFF || yDiff>= MINDIFF){
                                    listCircles.add(center);
                                }
                            }else{
                                listCircles.add(center);
                            }

                            int radius = (int) Math.round(circle[2]);
                            Imgproc.circle(frame1, center, radius, new Scalar(0, 0, 255), 2);
                        }
                    }
                }
            }
        } else {
            System.out.println("Failed to open camera!");
        }

        return listCircles;
    }
}
