package run;

import IncodeMessage.Incoder;
import LineCreation.AlignRobot;
import Navigation.BallTracker;
import Observer.FindAreaOfInterestSubject;
import Observer.HoughCircleDetectorSubject;
import Observer.RobotPositionSubject;
import Server.Server;
import org.opencv.core.Core;
import org.opencv.core.Point;
import org.opencv.highgui.HighGui;
import org.opencv.videoio.VideoCapture;

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


    public RobotAI(Server server) {
        FindAreaOfInterestSubject areaOfInterestSubject = new FindAreaOfInterestSubject();
        //corners = areaOfInterestSubject.getCorners();
        goals = areaOfInterestSubject.getGoalPos();
        //areaOfInterestSubject.detectRobot();
        //alignRobot = new AlignRobot(corners);
        this.server = server;

    }


    public void run() {
        Math1.LineCreation lineCreation = new Math1.LineCreation();
        BallTracker ballTracker = new BallTracker();
        boolean targetingBall = false;
        while (true) {
            RobotPositionSubject robotPositionSubject = new RobotPositionSubject();
            HoughCircleDetectorSubject houghCircleDetectorSubject = new HoughCircleDetectorSubject();

            boolean isBlueCircle = true; // Set the desired circle color preference here

            List<Point> ballPositions = houghCircleDetectorSubject.getBalls();


            List<Point> robotPosBlue = robotPositionSubject.getPos(isBlueCircle);
            List<Point> robotPosRed = robotPositionSubject.getPos(!isBlueCircle);

            Point redCircleRobot = robotPosRed.get(0);
            Point blueCircleRobot = robotPosBlue.get(0);

            robotFacingTop = facingDirection(redCircleRobot.y, blueCircleRobot.y);
            robotFacingLeft = facingDirection(redCircleRobot.x, blueCircleRobot.x);

            String balls = incoder.ballPositions(ballPositions);
            String robotPos1 = incoder.starPos(blueCircleRobot);

            double[] line = lineCreation.getSlopeAndBegin(blueCircleRobot, redCircleRobot);


            if (line != null) {
                Point targetBall = ballTracker.checkIfBallIsOnLine(line, ballPositions, 10.0);
                if (targetBall != null && !targetingBall) {
                    server.writeMessage(incoder.lineAB(line));
                    server.writeMessage(incoder.targetBall(targetBall));
                    System.out.println("ballFound");
                    targetingBall = true;
                }
            }
            if (targetingBall) {
                if (robotPosBlue.size() > 0 && robotPosRed.size() > 0) {
                    server.writeMessage(incoder.starPos(blueCircleRobot));
                }
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
    private boolean facingDirection(int firstCoordinate, int secondCoordinate){
        return firstCoordinate-secondCoordinate < 0;
    }
}
