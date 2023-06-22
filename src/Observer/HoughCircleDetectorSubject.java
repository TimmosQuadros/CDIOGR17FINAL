package Observer;

import Interface.HoughCircleDetector;
import Singleton.VideoCaptureSingleton;
import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import java.util.ArrayList;
import java.util.List;

/*
 * Author Mohammed Irout
 */

public class HoughCircleDetectorSubject implements HoughCircleDetector {


    private final int FRAMECOUNT = 30;
    private final double MINDIFF = 10;
    private final VideoCapture videoCapture;
    private final VideoCaptureSingleton videoCaptureSingleton;

    public HoughCircleDetectorSubject(){
        this.videoCaptureSingleton = VideoCaptureSingleton.getInstance();
        this.videoCapture = videoCaptureSingleton.getVideoCapture();
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    @Override
    public List<Point> getBalls() {
        Mat frame1 = new Mat();
        List<Point> listCircles = new ArrayList<>();
        if (videoCapture.isOpened()) {
            for (int j = 0; j<FRAMECOUNT; j++) {
                videoCapture.read(frame1);
                Mat frame = videoCaptureSingleton.mask(frame1);
                if (!frame.empty()) {
                    Mat grayFrame = new Mat();
                    Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);

                    Mat blurredFrame = new Mat();
                    Imgproc.GaussianBlur(grayFrame, blurredFrame, new Size(9, 9), 2, 2);

                    Mat circles = new Mat();
                    Imgproc.HoughCircles(grayFrame, circles, Imgproc.HOUGH_GRADIENT, 1, 30, 300, 15, 12, 16);

                    if (circles.cols() > 0) {
                        for (int i = 0; i < circles.cols(); i++) {
                            double[] circle = circles.get(0, i);
                            Point center = new Point(circle[0], circle[1]);

                            if(!listCircles.isEmpty()){
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
                        }
                    }
                    /*HighGui.imshow("test",frame);
                    int key = HighGui.waitKey(10);
                    if (key == 27) {
                        break;
                    }*/
                }
            }
            //HighGui.destroyAllWindows();
        } else {
            System.out.println("Failed to open camera!");
        }

        return listCircles;
    }
}
