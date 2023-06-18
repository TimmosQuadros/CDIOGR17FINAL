package run;

import IncodeMessage.Incoder;
import IncodeMessage.MessageStrings;
import Interface.RobotPosition;
import LineCreation.AlignRobot;
import LineCreation.LineSegment;
import Navigation.BallTracker;
import Observer.FindAreaOfInterestSubject;
import Observer.HoughCircleDetectorSubject;
import Observer.QRCodeDetectorSubject;
import Observer.RobotPositionSubject;
import Server.Server;
import Singleton.VideoCaptureSingleton;
import Vectors.VectorCalculations;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import java.util.ArrayList;
import java.util.List;

public class RobotAI {

    //Holds the corner in clock direction from top left to bottom left.
    private List<Point> corners;
    private List<Point> goals;
    private AlignRobot alignRobot;
    private Incoder incoder = new Incoder();
    private Server server;
    private boolean robotFacingTop;
    private boolean robotFacingLeft;


    public RobotAI(Server server){
        //FindAreaOfInterestSubject areaOfInterestSubject = new FindAreaOfInterestSubject();
        //corners = areaOfInterestSubject.getCorners();
        //goals = areaOfInterestSubject.getGoalPos();
        //areaOfInterestSubject.detectRobot();
        //alignRobot = new AlignRobot(corners);
        this.server = server;

    }


    public void run() {
        VideoCapture videoCapture = VideoCaptureSingleton.getInstance().getVideoCapture();
        Mat frame = new Mat();
        Math1.LineCreation lineCreation = new Math1.LineCreation();
        BallTracker ballTracker = new BallTracker();
        boolean targetingBall = false;
        RobotPositionSubject robotPositionSubject = new RobotPositionSubject();
        HoughCircleDetectorSubject houghCircleDetectorSubject = new HoughCircleDetectorSubject();
        List<Point> ballPositions = houghCircleDetectorSubject.getBalls();
        boolean blue = false;
        boolean isLookingForBallAndTurning = false;
        while(true){
            Point redCircle = null;
            Point blueCircle = null;
            List<Point> redCircleList = robotPositionSubject.getPos(false);
            List<Point> blueCircleList = new ArrayList<>();
            if(redCircleList.size()==0){
                blueCircleList = robotPositionSubject.getPos(true);
            }

            List<Point> bigcirclePoints = robotPositionSubject.getPos();

            if(blueCircleList.size()>0){
                blueCircle = blueCircleList.get(0);
                blue = true;
            }else if(redCircleList.size()>0){
                redCircle = redCircleList.get(0);
                blue = false;
            }

            for(Point p : bigcirclePoints){
                if(!blue){
                    if(Math.abs(p.x-redCircle.x)>10 || Math.abs(p.y-redCircle.y)>10){
                        blueCircle = p;
                    }
                }else{
                    if(Math.abs(p.x-blueCircle.x)>10 || Math.abs(p.y-blueCircle.y)>10){
                        redCircle = p;
                    }
                }
            }

            if(redCircle!=null && blueCircle!=null){
                robotFacingTop = facingDirection(redCircle.y, blueCircle.y);
                robotFacingLeft = facingDirection(redCircle.x, blueCircle.x);
            }

            double[] line = null;
            if(bigcirclePoints.size()>1)
                line = lineCreation.getSlopeAndBegin(bigcirclePoints.get(0),bigcirclePoints.get(1));
            if(line!=null){
                Point targetBall = ballTracker.checkIfBallIsOnLine(line,ballPositions,10.0,redCircle,blueCircle);
                if(targetBall!=null && !targetingBall){
                    server.writeMessage(incoder.lineAB(line));
                    server.writeMessage(incoder.targetBall(targetBall));
                    System.out.println("ballFound");
                    server.writeMessage("Stop");
                    System.out.println(server.receiveMessage());
                    targetingBall = true;
                }else{
                    if(!isLookingForBallAndTurning){
                        server.writeMessage("Turn");
                        System.out.println(server.receiveMessage());
                        isLookingForBallAndTurning = true;
                    }
                }
            }
            if(targetingBall){
                boolean finnish = false;
                Imgproc.line(frame,new Point(0,line[0]*0+line[1]),new Point(1920,line[0]*1920+line[1]),new Scalar(0, 0, 255),2);
                while(true){
                    boolean isReading = videoCapture.read(frame);
                    String mes = null;
                    if(!finnish)
                        mes = server.receiveMessage();
                    if(mes!=null){
                        if(mes.equalsIgnoreCase(MessageStrings.GETRobotPos.toString())){
                            List<Point> robPos = robotPositionSubject.getPos();
                            if(robPos.size()>0)
                                server.writeMessage(robPos.get(0).x+","+robPos.get(0).y+";"+robPos.get(1).x+","+robPos.get(1).y+";"+robotFacingLeft);
                        }else{
                            finnish = true;
                        }
                    }
                    if(isReading){
                        Imgproc.line(frame,new Point(0,line[0]*0+line[1]),new Point(1920,line[0]*1920+line[1]),new Scalar(0, 0, 255),2);
                        HighGui.imshow("Video Capture", frame);

                        if (HighGui.waitKey(10) == 27) {
                            break; // stop capturing when Esc key is pressed
                        }
                    }
                }
                HighGui.destroyAllWindows();
            }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
        }
    }


    /**
     * @param firstCoordinate the coordinate for the front facing circle
     * @param secondCoordinate the coordinate for the back facing circle
     * @return the true or false if the robot is facing towards the direction in question
     */
    private boolean facingDirection(double firstCoordinate, double secondCoordinate){
        return firstCoordinate-secondCoordinate < 0;
    }
}
