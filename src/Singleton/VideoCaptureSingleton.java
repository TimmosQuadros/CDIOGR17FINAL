package Singleton;

import org.opencv.videoio.VideoCapture;

public class VideoCaptureSingleton {
    private static VideoCaptureSingleton instance;
    private final VideoCapture videoCapture;

    private VideoCaptureSingleton() {
        // Private constructor to prevent direct instantiation
        videoCapture = new VideoCapture(0);
        setMaxResolution(instance);
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

    private void setMaxResolution(VideoCaptureSingleton videoCapture) {
        // Set the property for maximum resolution to a high value
        int maxResolutionProperty = -1;
        // Find the property representing the maximum resolution
        double maxResolution = 0.0;
        ;
        for (int prop = 0; prop < 20; prop++) { // Iterate over the properties
            double value = videoCapture.getVideoCapture().get(prop);
            if (value > maxResolution) {
                maxResolution = value;
                maxResolutionProperty = prop;
            }
        }

        videoCapture.getVideoCapture().set(maxResolutionProperty, 9999);

        // Get the resolution of the video capture
        int width = (int) videoCapture.getVideoCapture().get(3);
        int height = (int) videoCapture.getVideoCapture().get(4);

        // Print the resolution
        System.out.println("Resolution: " + width + "x" + height);
    }
}

//VideoCaptureSingleton videoCaptureSingleton = VideoCaptureSingleton.getInstance();
//VideoCapture videoCapture = videoCaptureSingleton.getVideoCapture();