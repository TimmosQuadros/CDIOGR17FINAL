package Singleton;

import org.opencv.videoio.VideoCapture;

public class VideoCaptureSingleton {
    private static VideoCaptureSingleton instance;
    private VideoCapture videoCapture;

    private VideoCaptureSingleton() {
        // Private constructor to prevent direct instantiation
        videoCapture = new VideoCapture(0);
    }

    public static VideoCaptureSingleton getInstance() {
        if (instance == null) {
            synchronized (VideoCaptureSingleton.class) {
                if (instance == null) {
                    instance = new VideoCaptureSingleton();
                }
            }
        }
        return instance;
    }

    public VideoCapture getVideoCapture() {
        return videoCapture;
    }
}

//VideoCaptureSingleton videoCaptureSingleton = VideoCaptureSingleton.getInstance();
//VideoCapture videoCapture = videoCaptureSingleton.getVideoCapture();