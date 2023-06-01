package Observer;

import Interface.QRCodeDetector;
import Singleton.VideoCaptureSingleton;
import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class QRCodeDetectorSubject extends Subject implements QRCodeDetector {

    private final VideoCapture videoCapture;

    public QRCodeDetectorSubject(VideoCaptureSingleton videoCaptureSingleton){
        this.videoCapture = VideoCaptureSingleton.getInstance().getVideoCapture();
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }


    @Override
    public Point getPos() {

        List<Point> points = new ArrayList<>();

        // Create a QR code detector
        org.opencv.objdetect.QRCodeDetector qrCodeDetector = new org.opencv.objdetect.QRCodeDetector();

        // Create a previous frame for motion detection
        Mat prevFrame = new Mat();

        Point point1 = new Point(1,1);
        Point point2 = new Point(1,1);
        Point point3 = new Point(1,1);
        Point point4 = new Point(1,1);
        Point point5 = new Point(1,1);
        Point point6 = new Point(1,1);
        Point point7 = new Point(1,1);

        final int FRAMECOUNT = 100;
        // Iterate through the video frames
        for(int i = 0; i<FRAMECOUNT; i++){
            Mat frame = new Mat();
            videoCapture.read(frame);

            if (frame.empty()) {
                System.out.println("End of video stream");
                break;
            }

            // Convert the frame to grayscale
            Mat grayFrame = new Mat();
            Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);

            // Motion detection
            if (prevFrame.empty()) {
                grayFrame.copyTo(prevFrame);
            }

            Mat frameDelta = new Mat();
            Core.absdiff(grayFrame, prevFrame, frameDelta);

            Imgproc.threshold(frameDelta, frameDelta, 25, 255, Imgproc.THRESH_BINARY);
            Imgproc.blur(frameDelta, frameDelta, new Size(10, 10));
            Imgproc.threshold(frameDelta, frameDelta, 25, 255, Imgproc.THRESH_BINARY);

            // Update the previous frame
            grayFrame.copyTo(prevFrame);

            // Detect and decode QR codes
            Mat qrCodePoints = new Mat();
            qrCodeDetector.detect(grayFrame, qrCodePoints);

            // Highlight and track the QR codes
            point1 = new Point(qrCodePoints.get(0, 0));
            point2 = new Point(qrCodePoints.get(0, 1));
            point3 = new Point(qrCodePoints.get(0, 2));
            point4 = new Point(qrCodePoints.get(0, 3));
            point5 = new Point((point1.x+point2.x)/2,(point1.y+point2.y)/2);
            point6 = new Point((point3.x+point4.x)/2,(point3.y+point4.y)/2);
            point7 = new Point((point5.x+point6.x)/2,(point5.y+point6.y)/2);

            points.add(point7);
        }

        double threshold = 2.0; // Set the threshold for outlier filtering

        Point average = calculateFilteredAveragePoint(points, threshold);

        return null;
    }

    public Point calculateFilteredAveragePoint(List<Point> points, double threshold) {
        List<Point> validPoints = points.stream()
                .filter(point -> {
                    double diffX = Math.abs(point.x);
                    double diffY = Math.abs(point.y);
                    return diffX <= threshold && diffY <= threshold;
                }).toList();

        double sumX = validPoints.stream().mapToDouble(point -> point.x).sum();
        double sumY = validPoints.stream().mapToDouble(point -> point.y).sum();
        int count = validPoints.size();

        if (count == 0) {
            return null; // Return null if no valid points exist
        }

        double averageX = sumX / count;
        double averageY = sumY / count;

        return new Point(averageX, averageY);
    }

}