package run;

import IncodeMessage.Incoder;
import IncodeMessage.MessageStrings;
import LineCreation.AlignRobot;
import LineCreation.LineSegment;
import Math1.LineCreation;
import Navigation.BallTracker;
import Navigation.PathAdjustment;
import Observer.HoughCircleDetectorSubject;
import Observer.RobotPositionSubject;
import Observer.FindAreaOfInterestSubject;
import Server.Server;
import Singleton.VideoCaptureSingleton;
import Vectors.VectorCalculations;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import java.util.*;

public class RobotAI {

    //Holds the corner in clock direction from top left to bottom left.
    private List<Point> corners;
    private List<Point> goals;
    private Point robotPos;
    private AlignRobot alignRobot;
    private Incoder incoder = new Incoder();
    private Server server;
    private PathAdjustment pather;
    private boolean robotFacingTop;
    private boolean robotFacingLeft;
    private FindAreaOfInterestSubject findAreaOfInterestSubject;


    public RobotAI(Server server){
        findAreaOfInterestSubject = new FindAreaOfInterestSubject();
        pather = new PathAdjustment(findAreaOfInterestSubject.fieldDetection);
        //corners = areaOfInterestSubject.getCorners();
        //goals = areaOfInterestSubject.getGoalPos();
        //areaOfInterestSubject.detectRobot();
        //alignRobot = new AlignRobot(corners);
        this.server = server;
    }


    public void run() {
        VideoCaptureSingleton videoCaptureSingleton = VideoCaptureSingleton.getInstance();
        VideoCapture videoCapture = videoCaptureSingleton.getVideoCapture();
        videoCaptureSingleton.setPoint(findAreaOfInterestSubject);

        Mat frame = new Mat();
        Math1.LineCreation lineCreation = new Math1.LineCreation();
        BallTracker ballTracker = new BallTracker();
        boolean targetingBall = false;
        RobotPositionSubject robotPositionSubject = new RobotPositionSubject();
        HoughCircleDetectorSubject houghCircleDetectorSubject = new HoughCircleDetectorSubject();
        List<Point> ballPositions = houghCircleDetectorSubject.getBalls();
        boolean blue = false;
        boolean isLookingForBallAndTurning = false;
        int i = 0;

        List<Point> blueCircle = robotPositionSubject.getPos(false);
        List<Point> redCircle = robotPositionSubject.getPos(true);
        if(blueCircle.size()>0 && redCircle.size()>0){
            robotPos = getMidPoint(blueCircle.get(0),redCircle.get(0));

            List<Ball> balls = new ArrayList<>();

            LinkedList<Point> ballPositionsQueue = new LinkedList<Point>(); //Adding a variable to store the list of balls
            for (Point ball: ballPositions){                                //Looping through all balls
                if (pather.isEasy(ball)){                                   //Checking if the ball is considered easy
                    ballPositionsQueue.add(ball);                           //Adding the easy ball to the variable for the remaining balls
                    balls.add(new Ball(ball));
                }
            }

            // Repeat until all balls are picked
            while (balls.stream().anyMatch(ball -> !ball.picked)) {
                Ball nearestBall = findNearestBall(robotPos, balls);
                if(pather.pathIntersects(nearestBall.location, robotPos) ||
                        pather.isAroundCross(nearestBall.location) || pather.isNearSide(nearestBall.location)){
                    List<Point> path = pather.adjustPath(nearestBall.location, robotPos);
                    for (Point waypoint : path){
                        testRun(waypoint, lineCreation, robotPos, robotPositionSubject);
                        robotPos = robotPositionSubject.getPos(true).get(0);
                    }
                }else
                    testRun(nearestBall.location, lineCreation, robotPos, robotPositionSubject);
                if (nearestBall != null) {
                    nearestBall.picked = true;
                    blueCircle = robotPositionSubject.getPos(false);
                    redCircle = robotPositionSubject.getPos(true);
                    if(blueCircle.size()>0 && redCircle.size()>0) {
                        robotPos = getMidPoint(blueCircle.get(0), redCircle.get(0));
                    }else{
                        robotPos = nearestBall.location;
                    }
                }
            }

            /*if(ballPositionsQueue.isEmpty()){                               //Checking if the list of ball positions is empty
                ballPositionsQueue = listOfPointsToQueue(ballPositions);    //If empty, all the difficult balls are added to the list with the Iver-pathfinding
            }else{
                ballPositionsQueue = listOfPointsToQueue(ballPositionsQueue);   //Else all the easy balls are added to the list with waypoints underway
            }

            if(ballPositionsQueue.size()>0){
                testRun(ballPositionsQueue,lineCreation,robotPos,robotPositionSubject);     //Run is started with the ballWaypoint queue and other required variables
            }*/
        }




        /*while(true){
            Point redCircle = null;
            Point blueCircle = null;
            List<Point> redCircleList = robotPositionSubject.getPos(true);
            List<Point> blueCircleList = robotPositionSubject.getPos(false);
            redCircle = redCircleList.get(0);
            blueCircle = blueCircleList.get(0);

            if(redCircle!=null && blueCircle!=null){
                robotFacingTop = facingDirection(redCircle.y, blueCircle.y);
                robotFacingLeft = facingDirection(redCircle.x, blueCircle.x);
            }

            double[] line = null;
            line = lineCreation.getSlopeAndBegin(redCircle,blueCircle);
            VectorCalculations vectorCalculations;
            double angle = 0;
            if(redCircle!=null && blueCircle!=null){
                vectorCalculations = new VectorCalculations(new LineSegment(blueCircle,redCircle),ballPositions.get(0));
                angle = vectorCalculations.getAngle();
            }
            if(line!=null){

                Point targetBall = ballPositions.get(0);//ballTracker.checkIfBallIsOnLine(line,ballPositions,10.0,redCircle,blueCircle);
                if(targetBall!=null && !targetingBall){
                    server.writeMessage(incoder.lineAB(line));
                    server.receiveMessage();
                    server.writeMessage(incoder.targetBall(targetBall));
                    server.receiveMessage();
                    System.out.println("ballFound");
                    server.writeMessage("Stop");
                    System.out.println(server.receiveMessage());
                    targetingBall = true;
                }else{
                    if(!isLookingForBallAndTurning){
                        server.writeMessage("Turn"+";"+angle);
                        server.receiveMessage();
                        System.out.println(server.receiveMessage());
                        if(i<2) {
                            isLookingForBallAndTurning = true;
                        }
                        i++;
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
                            List<Point> robPos = robotPositionSubject.getPos(true);
                            List<Point> robPos1 = robotPositionSubject.getPos(false);
                            if(robPos.size()>0)
                                server.writeMessage(robPos.get(0).x+","+robPos.get(0).y+";"+robPos1.get(0).x+","+robPos1.get(0).y+";"+robotFacingLeft);
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
        }*/
    }

    private LinkedList<Point> listOfPointsToQueue(List<Point> ballPositions) {
        LinkedList<Point> queue = new LinkedList<Point>();
        ballPositions.sort(new Comparator<Point>() {
            @Override
            public int compare(Point o1, Point o2) {
                return Double.compare(Math.sqrt(Math.pow(o1.x-robotPos.x, 2)+Math.pow(o1.y-robotPos.y, 2)), Math.sqrt(Math.pow(o2.x-robotPos.x, 2)+Math.pow(o2.y-robotPos.y, 2)));
            }
        });
        for(Point p : ballPositions){

            queue.addAll(pather.adjustPath(p, robotPos));
        }
        return queue;
    }

    /**
     * @param firstCoordinate the coordinate for the front facing circle
     * @param secondCoordinate the coordinate for the back facing circle
     * @return the true or false if the robot is facing towards the direction in question
     */
    private boolean facingDirection(double firstCoordinate, double secondCoordinate){
        return firstCoordinate-secondCoordinate < 0;
    }

    private void testRun(Point wayPoint, LineCreation lineCreation, Point robotPosition, RobotPositionSubject robotPositionSubject){
        VectorCalculations vectorCalculations;
        //Point targetWayPoint = wayPoints.poll();
        if(wayPoint==null){
            return;
        }

        //Turn the robot to the desired angle
        turn(robotPositionSubject, wayPoint);

        double[] lineToWaypoint = lineCreation.getSlopeAndBegin(robotPosition, wayPoint);
        if(lineToWaypoint==null){
            return;
        }

        //Navigate to First wayPoint
        server.writeMessage(MessageStrings.WayPoints.toString()+":"+lineToWaypoint[0]+","+lineToWaypoint[1]+";"+wayPoint.x+","+wayPoint.y);
        server.receiveMessage();
        robotPosition = startFollowingLine(robotPositionSubject,robotPosition, wayPoint);

        /*while (!wayPoints.isEmpty()){
            //Turn the robot to the desired angle
            targetWayPoint = wayPoints.poll();
            turn(robotPositionSubject, targetWayPoint);
            lineToWaypoint = lineCreation.getSlopeAndBegin(robotPosition, targetWayPoint);
            server.writeMessage(MessageStrings.WayPoints.toString()+":"+lineToWaypoint[0]+","+lineToWaypoint[1]+";"+targetWayPoint.x+","+targetWayPoint.y);
            server.receiveMessage();
            robotPosition = startFollowingLine(robotPositionSubject,robotPosition, targetWayPoint);
        }*/
    }

    private void turn(RobotPositionSubject robotPositionSubject, Point targetWayPoint) {
        VectorCalculations vectorCalculations;
        for(int i = 0; i<2; i++){
            List<Point> redCirc = robotPositionSubject.getPos(true);
            List<Point> blueCirc = robotPositionSubject.getPos(false);
            if(redCirc.size()>0 && blueCirc.size()>0){
                vectorCalculations = new VectorCalculations(new LineSegment(blueCirc.get(0),redCirc.get(0)), targetWayPoint);
            }else {
                return;
            }
            //Turn to face firstWayPoint
            server.writeMessage(MessageStrings.Turn+":"+vectorCalculations.getAngle());
            //TODO maybe do something
            String res = server.receiveMessage();
        }
    }

    private Point startFollowingLine(RobotPositionSubject robotPositionSubject, Point robocupPos, Point targetWayPoint) {
        String res = server.receiveMessage();
        Point lastPosition = null;

        //Test Code
        VideoCapture videoCapture = VideoCaptureSingleton.getInstance().getVideoCapture();
        Mat frame = new Mat();
        boolean small = true;
        while(true){
            List<Point> smallCircles = robotPositionSubject.getPos(true);
            List<Point> bigCircles = robotPositionSubject.getPos(false);
            if(bigCircles.size()>0 && smallCircles.size()>0){
                Point p1 = bigCircles.get(0);
                Point p2 = smallCircles.get(0);
                robotFacingTop = facingDirection(p2.y, p1.y);
                robotFacingLeft = facingDirection(p2.x, p1.x);
                lastPosition = getMidPoint(p1,p2);
                /*if(small){
                    server.writeMessage(p1.x+","+p1.y+";"+p2.x+","+p2.y+";"+robotFacingLeft);
                    small = false;
                }else{
                    server.writeMessage(p2.x+","+p2.y+";"+p1.x+","+p1.y+";"+robotFacingLeft);
                    small = true;
                }*/
                server.writeMessage(p1.x+","+p1.y+";"+p2.x+","+p2.y+";"+robotFacingLeft);

            }
            res = server.receiveMessage();
            if(res.contains("finnish")){
                break;
            }
            videoCapture.read(frame);
            HighGui.imshow("abe",frame);
            if(robotFacingLeft){
                Imgproc.line(frame,robocupPos,targetWayPoint,new Scalar(255,0,0),6);
            }else{
                Imgproc.line(frame,robocupPos,targetWayPoint,new Scalar(255,0,0),6);
            }

            int key = HighGui.waitKey(10);
            if(key ==27){
                break;
            }
        }
        HighGui.destroyAllWindows();
        return lastPosition;
    }

    private Point getMidPoint(Point p1, Point p2) {
        return new Point((p1.x+p2.x)/2,(p1.y+p2.y)/2);
    }

    private Ball findNearestBall(Point vehiclePosition, List<Ball> balls) {
        Ball nearestBall = null;
        double minDistance = Double.MAX_VALUE;

        for (Ball ball : balls) {
            if (ball.picked) continue;

            double distance = euclideanDistance(vehiclePosition, ball.location);
            if (distance < minDistance) {
                minDistance = distance;
                nearestBall = ball;
            }
        }

        return nearestBall;
    }

    private double euclideanDistance(Point p1, Point p2) {
        return Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));
    }

    public class Ball {
        Point location;
        boolean picked = false;

        Ball(Point ball) {
            location = ball;
        }
    }
}


