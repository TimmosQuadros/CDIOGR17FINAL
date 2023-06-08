package Pathfinding;

import org.opencv.core.Point;

import java.util.*;
import java.util.List;

public class Pathfinder {


    public List<Point> findPath(Point[] checkpoints, int maxCheckpoints, Point[] obstacles, Point start, Point goal) {
        if(maxCheckpoints>checkpoints.length){
            maxCheckpoints = checkpoints.length;
        }

        PriorityQueue<Node> openList = new PriorityQueue<>();
        List<Node> closedList = new ArrayList<>();

        Node startNode = new Node(start, null);
        Node goalNode = new Node(goal, null);

        openList.add(startNode);

        while (!openList.isEmpty()) {
            Node currentNode = openList.poll();

            closedList.add(currentNode);

            if (currentNode.point.equals(goalNode.point) && getCheckpointsCollected(currentNode) <= maxCheckpoints) {
                List<Point> path = new ArrayList<Point>();
                while (currentNode != null) {
                    path.add(currentNode.point);
                    currentNode = currentNode.parent;
                }
                Collections.reverse(path);
                return path;
            }

            List<Node> successors = new ArrayList<>();
            for (Point newPoint : new Point[]{new Point(-1, 0), new Point(1, 0), new Point(0, -1), new Point(0, 1)}) {
                Node neighbor = new Node(new Point(currentNode.point.x + newPoint.x, currentNode.point.y + newPoint.y), currentNode);
                successors.add(neighbor);
            }

            for (Node successor : successors) {
                if (closedList.contains(successor) || containsPoint(obstacles, successor.point))
                    continue;

                successor.costFromStart = currentNode.costFromStart + distance(successor.point, currentNode.point);
                successor.costHeuristic = distance(successor.point, goalNode.point);
                successor.costTotal = successor.costFromStart + successor.costHeuristic;

                Node existingNode = null;
                for (Node node : openList) {
                    if (node.point.equals(successor.point)) {
                        existingNode = node;
                        break;
                    }
                }

                if (existingNode != null && successor.costTotal >= existingNode.costTotal)
                    continue;

                openList.add(successor);
            }
        }

        return new ArrayList<Point>(openList);
    }

    private boolean containsPoint(Point[] points, Point target) {
        for (Point point : points) {
            if (point.equals(target))
                return true;
        }
        return false;
    }

    private int getCheckpointsCollected(Node node) {
        int checkpoints = 0;
        while (node != null) {
            if (node.parent != null && isCheckpoint(node.point, node.parent.point)) {
                checkpoints++;
            }
            node = node.parent;
        }
        return checkpoints;
    }

    private boolean isCheckpoint(Point point, Point checkpoint) {
        return point.equals(checkpoint);
    }

    private class Node implements Comparable<Node> {
        Point point;
        Node parent;
        double costFromStart; // Cost from start node to current node
        double costHeuristic; // estimated cost from current node to goal node
        double costTotal; // Total cost (g + h)

        public Node(Point point, Node parent) {
            this.point = point;
            this.parent = parent;
            this.costFromStart = 0;
            this.costHeuristic = 0;
            this.costTotal = 0;
        }

        @Override
        public int compareTo(Node other) {
            return Double.compare(this.costTotal, other.costTotal);
        }
    }

    private double distance(Point point1, Point point2) {
        return Math.sqrt(Math.pow(point1.x - point2.x, 2) + Math.pow(point1.y - point2.y, 2));
    }
}