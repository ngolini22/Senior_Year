import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.*;
import java.util.Queue;

/**
 * Created by Nicky on 10/2/16.
 * Class with robot arm states
 *
 */
public class Robot {

//    ArrayList<Double> thetas, linkLengths;
    int l = 75;

    ArrayList<Rectangle> obstacles; // holds all of the obstacles as Circle shapes -> easy to check if intersection
    ArrayList<ArmNode> alpha; // holds the randomly generated arm states -> should be ArmNodes
    HashMap<ArmNode, ArrayList<ArmNode>> neighbors;
    ArmNode startNode;
    ArmNode goalNode;
    ArrayList<ArmNode> shortestPath;

    int obsHeight = 10;
    int N = 1000;
    int panelHeight = 450;
    int panelWidth = 450;
    int k;

    public Robot(ArrayList<Double> t){

        startNode = new ArmNode(t);
        goalNode = generateGoal();
        obstacles = makeObstacles();
        alpha = generateRandomSampling();
        k = 20;
        neighbors = createNeighborGraph(k);
        shortestPath = breadthFirstSearch();
        Draw diagram = new Draw(startNode.getLinksToShapes(), obstacles, alpha, neighbors, k, shortestPath);

    }

    // This class will hold an instance of a possible robot arm configuration
    // Alpha will hold ArmNodes, not Point2Ds.
    // ArmNodes contain Point2Ds to keep logic for collisions the same using java Shapes
    public class ArmNode{
        ArrayList<Double> thetas;
        ArrayList<Line2D> botLinks; // holds the links of the bot as Lines
        Point2D point;

        public ArmNode(ArrayList<Double> thetas){
            this.thetas = thetas;
            this.botLinks = getLinksToShapes();
            this.point = botLinks.get(botLinks.size()-1).getP2(); // the point is the endpoint of the last link
        }

        // Puts the links into an arraylist of Lines -> will be useful when comparing for collisions
        // Need to somehow change the line thickness here -> you do that to the graphic in Draw, so don't know what this
        //      will look like
        public ArrayList<Line2D> getLinksToShapes(){
            ArrayList<Line2D> link = new ArrayList<>();
            ArrayList<Double> endPoints = endpointCalculation();
            double pointX1, pointX2, pointY1, pointY2;

            for(int i = 0; i < endPoints.size()-2; i = i + 2) {
                pointX1 = endPoints.get(i);
                pointY1 = endPoints.get(i+1);
                pointX2 = endPoints.get(i+2);
                pointY2 = endPoints.get(i+3);
                Line2D newLink = new Line2D.Double();
                newLink.setLine(pointX1, pointY1, pointX2, pointY2);
                link.add(newLink);
            }
            return link;
        }

        // Used to create the lines to represent the links of the bot
        public ArrayList<Double> endpointCalculation(){
            ArrayList<Double> endPoints = new ArrayList<>(); // In <x1, y1, x2, y2, ..., xn, yn> order
            double currPointX = 0;
            double currPointY = 0;
            double currTheta = 0;
            endPoints.add((double)0); endPoints.add((double)0); // add origin as first enpoint

            // calculates x, y coordinate of next link
            for(int i = 2; i < thetas.size()*2+2; i=i+2){
                currTheta = currTheta + thetas.get(i/2 - 1);
                currPointX = currPointX + l * Math.cos(currTheta);
                currPointY = currPointY + l * Math.sin(currTheta);
                endPoints.add(i, currPointX);
                endPoints.add(i+1, currPointY);
            }
            return endPoints;
        }

    }

    public boolean atGoal(ArmNode curr){
        for(int i = 0; i < curr.thetas.size(); i++){
            if(goalNode.thetas.get(i) != curr.thetas.get(i))
                return false;
        }
        return true;
    }

    // returns true if there is a collision with the robot arm and the obstacles
    // Help with this intersection method from Stack Overflow:
    //                  http://stackoverflow.com/questions/15690846/java-collision-detection-between-two-shape-objects
    // Returns true if there is a collision with one of the robot links and an obstacle
    public boolean collision(ArmNode currNode, ArmNode neighbor) {
        int n = 50; // number of points to look at between two points
        double t1 = 0;
        ArrayList<ArmNode> checkNodes = new ArrayList<>();

        for(int i = 1; i < n; i++){
            ArrayList<Double> t = new ArrayList<>();
            for(int j = 0; j < currNode.thetas.size(); j++) {
                t1 = ((neighbor.thetas.get(j) - currNode.thetas.get(j)) * (i)) / (n) + currNode.thetas.get(j);
                t.add(t1);
            }
            ArmNode checkNode = new ArmNode(t);
            checkNodes.add(checkNode);
        }

        for(int i = 0; i < checkNodes.size(); i++){
            ArmNode curr = checkNodes.get(i);
            for(int j = 0; j < curr.botLinks.size(); j++){
                for(int k = 0; k < obstacles.size(); k++){
                    if(obstacles.get(k).intersectsLine(curr.botLinks.get(j))) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public ArrayList<ArmNode> getNeighbors(ArmNode curr){
        ArrayList<ArmNode> kneighbors = neighbors.get(curr);

        return kneighbors;
    }

    public ArrayList<ArmNode> breadthFirstSearch(){
        ArrayList<ArmNode> path = new ArrayList<>();
        Queue<ArmNode> frontier = new LinkedList<>();
        frontier.add(startNode);

        HashMap<Integer,ArmNode> visited = new HashMap<>();
        visited.put(startNode.hashCode(), startNode);

        while(!frontier.isEmpty()){
            ArmNode currNode = frontier.remove();
            if(atGoal(currNode)){
                System.out.println("FOUND GOAL");
                path = backchain(currNode, visited);
            }
            // For each successor found from getSuccessors of the current node
            for(ArmNode successor : getNeighbors(currNode)){
                if(!visited.containsKey(successor.hashCode())){
                    visited.put(successor.hashCode(), currNode);
                    frontier.add(successor);
                }
            }
        }
        return path;
    }

    //  backchain should only be used by bfs, not the recursive dfs
    private ArrayList<ArmNode> backchain(ArmNode node, HashMap<Integer, ArmNode> visited) {
        // you will write this method
        ArrayList<ArmNode> chain = new ArrayList<ArmNode>();
        while(!visited.get(node.hashCode()).equals(node)){
            chain.add(0,node);
            node = visited.get(node.hashCode());
        }
        chain.add(0,node);
        return chain;
    }

    // Creates k-neighbor map
    // Map will be represented by a HashMap. The Key is one of the randomly generated points, and the Value
    //      will be a MaxPriority queue. If a new point is closer to the current point than one of the points
    //      in the queue, then pop the max in the queue, and add the neighbor. Also make sure that the queue size
    //      doesn't exceed k
    // k is the number of
    public HashMap<ArmNode, ArrayList<ArmNode>> createNeighborGraph(int k){
        Comparator<PQNode> comparator = new maxPQComparator();
        HashMap<ArmNode, PriorityQueue<PQNode>> neighborhoodTemp = new HashMap<>();
        HashMap<ArmNode, ArrayList<ArmNode>> neighborhood = new HashMap<>();

        // looking at each point to find every point's k nearest neighbors
        for(int i = 0; i < alpha.size(); i++){ // looking at all of the points
            ArmNode currPoint = alpha.get(i);
            PriorityQueue<PQNode> kNeighbors = new PriorityQueue(k, comparator);
            for(int j = 0; j < alpha.size(); j++){
                ArmNode currNeighbor = alpha.get(j);
                // two nodes with the same arm end location should not be considered
                if (currNeighbor.thetas != currPoint.thetas){
                    if(kNeighbors.size() < k) { // if priority queue is less than k, just add next point
                        if(collision(currPoint, currNeighbor)) {
                            PQNode currNeighborNode = new PQNode(currNeighbor, angleDistance(currPoint.thetas, currNeighbor.thetas));
                            kNeighbors.add(currNeighborNode);
                        }
                    } else{ // if priority queue already has k neighbors, see if current neighbor is close, and add to PQ if so
                        if(collision(currPoint, currNeighbor)) {
                            double furthestCurrNeighbor = angleDistance(currPoint.thetas, kNeighbors.peek().node.thetas);
                            // if the current neighbor point being examined is
                            if (angleDistance(currPoint.thetas, currNeighbor.thetas) < furthestCurrNeighbor) {
                                kNeighbors.remove();
                                PQNode currNeighborNode = new PQNode(currNeighbor, angleDistance(currPoint.thetas, currNeighbor.thetas));
                                kNeighbors.add(currNeighborNode);
                            }
                        }
                    }
                }
            }
            neighborhoodTemp.put(currPoint, kNeighbors);
        }

        // neighborhoodTemp is single directional
        // neighborhood makes neighborhood Temp bi-directional
        for(ArmNode curr : neighborhoodTemp.keySet()){
            ArrayList<ArmNode> currNeighbors = new ArrayList<>();
            for(PQNode n: neighborhoodTemp.get(curr)){
                ArmNode neighbor = n.node;
                currNeighbors.add(neighbor);
            }
            neighborhood.put(curr, currNeighbors);
        }

        for(ArmNode curr : neighborhood.keySet()){
            for(ArmNode neighbor : neighborhood.get(curr)){
                ArrayList<ArmNode> n = neighborhood.get(neighbor);
                if(!n.contains(curr))
                    n.add(curr);
                neighborhood.put(neighbor, n);
            }
        }
        System.out.println(neighborhood);
        return neighborhood;
    }

    public double angleDistance(ArrayList<Double> t1, ArrayList<Double> t2){
        double dist = 0;

        // gets the sum of the differences between the link thetas
        for(int i = 0; i < t1.size(); i++){
            double dt = Math.abs(t1.get(i) - t2.get(i));
            dist = dist + dt;
        }

        return dist;
    }

    public class PQNode{
        ArmNode node;
        double d;
        public PQNode(ArmNode node, double d){
            this.node = node;
            this.d = d;
        }
    }

    public static class aStarNode{
        ArmNode node;
        double priority;
        public aStarNode(double p, ArmNode a){
            this.node = a;
            this.priority = p;
        }
    }


    // comparator for A* PQ
    public class aStarComparator implements Comparator<aStarNode>{
        public int compare(aStarNode n1, aStarNode n2){
            double d1 = n1.priority;
            double d2 = n2.priority;

            if(d1 > d2)
                return 1;
            else if(d1 < d2)
                return -1;
            else
                return 0;
        }
    }

    // comparator for max priority queue
    public class maxPQComparator implements Comparator<PQNode> {
        public int compare(PQNode n1, PQNode n2){
            double d1 = n1.d;
            double d2 = n2.d;

            if(d1 > d2)
                return -1;
            if(d1 < d2)
                return 1;
            else
                return 0;
        }
    }

    // creates random distribution of points in the open space
    public ArrayList<ArmNode> generateRandomSampling(){
        ArrayList<ArmNode> alpha = new ArrayList<>();
        int currX, currY;
        if(pointCheck(startNode))
            alpha.add(startNode);
        for(int i = 1; i < N-1; i++){
            // create randomly generated possible angles
            ArrayList<Double> newNodeThetas = new ArrayList<>();
            for(int j = 0; j < startNode.thetas.size(); j++){
                newNodeThetas.add(Math.toRadians(Math.random()*360));
            }
            ArmNode possNode = new ArmNode(newNodeThetas);
            if (pointCheck(possNode))
                alpha.add(possNode);
            else // if there is a collision with a point and an obstacle, decrement i so ther ecan be N points
                i--;
        }
        if(pointCheck(goalNode)) {
            alpha.add(goalNode);
            System.out.println("PASS");
        }
        return alpha;
    }

    // returns true if the point is not contained within an obstacle, and false if it does
    // this is used in creating a distribution of points over the open space
    public boolean pointCheck(ArmNode check){
        for(int i = 0; i < obstacles.size(); i++){
            for(int j = 0; j < check.botLinks.size(); j++){
                if(obstacles.get(i).intersectsLine(check.botLinks.get(j)))
                    return false;
            }
        }
        return true;
    }

    // function to create some obstacles
    public ArrayList<Rectangle> makeObstacles(){
        ArrayList<Integer> obstacleBounds = new ArrayList<>();
        ArrayList<Rectangle> obstacles = new ArrayList<>();
        obstacleBounds.add(-70);
        obstacleBounds.add(-90);
        obstacleBounds.add(-70);
        obstacleBounds.add(70);
        obstacleBounds.add(50);
        obstacleBounds.add(-20);
        obstacleBounds.add(-150);
        obstacleBounds.add(5);

        for(int i = 0; i < obstacleBounds.size(); i = i + 2){
            Rectangle newObs = new Rectangle(obstacleBounds.get(i), obstacleBounds.get(i+1), obsHeight, obsHeight);
            obstacles.add(newObs);
        }
        return obstacles;
    }


    public ArmNode generateGoal(){
        ArrayList<Double> theta = new ArrayList<>();
        theta.add(Math.toRadians(0));
        theta.add(Math.toRadians(90));
//        theta.add(Math.toRadians(0));
//        theta.add(Math.toRadians(90));
        ArmNode goalNode = new ArmNode(theta);
        return goalNode;
    }

    public static void main(String args[]){
        ArrayList<Double> theta = new ArrayList<>();
        theta.add(Math.toRadians(270));
        theta.add(Math.toRadians(270));
//        theta.add(Math.toRadians(270));
//        theta.add(Math.toRadians(90));

        Robot bot = new Robot(theta);
    }
}
