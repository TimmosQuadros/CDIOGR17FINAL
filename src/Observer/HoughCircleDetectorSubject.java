package Observer;

import Interface.HoughCircleDetector;
import Singleton.VideoCaptureSingleton;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import java.util.ArrayList;
import java.util.List;

public class HoughCircleDetectorSubject implements HoughCircleDetector {


    private final int FRAMECOUNT = 10;
    private final double MAXDIFF = 2.0;
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
                        System.out.println("White circle detected!");
                        boolean shouldAddBall = false;
                        for (int i = 0; i < circles.cols(); i++) {
                            double[] circle = circles.get(0, i);
                            Point center = new Point(circle[0], circle[1]);

                            if(!listCircles.isEmpty() && listCircles.size()>1){
                                double xDiff = 0.0;
                                double yDiff = 0.0;
                                for(Point point : listCircles){
                                    for(Point pointNest : listCircles){
                                        xDiff = Math.abs(point.x-pointNest.x);
                                        yDiff = Math.abs(point.x-pointNest.x);
                                    }
                                }
                                if(xDiff<=MAXDIFF && yDiff<=MAXDIFF){
                                    listCircles.add(center);
                                }
                            }else{
                                listCircles.add(center);
                            }

                            int radius = (int) Math.round(circle[2]);
                            Imgproc.circle(frame1, center, radius, new Scalar(0, 0, 255), 2);
                        }
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            System.out.println("Failed to open camera!");
        }

        return listCircles;
    }
}
