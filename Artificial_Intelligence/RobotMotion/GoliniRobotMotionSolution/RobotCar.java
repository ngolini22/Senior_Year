import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Nicky on 10/10/16.
 */
public class RobotCar {

    int panelHeight = 700;
    int panelWidth = 700;
    int stepSize;
    ArrayList<Line2D> walls;
    ArrayList<CarNode> path;
    HashMap<CarNode, ArrayList<CarNode>> rrt = new HashMap<>();
    HashMap<CarNode, CarNode> predMap = new HashMap<>();

    CarNode startNode, goalNode;


    public RobotCar(){
        walls = createObstacles();
        startNode = new CarNode(-1*(panelWidth/2)+50, -1*(panelHeight/2)+50, 0, null);
        goalNode = new CarNode((panelWidth/2) - 50, (panelHeight/2) - 70, 0, null);
        rrt = buildRRT(startNode, stepSize);
        path = backtrack();
        DrawCar drawing = new DrawCar(rrt, walls, path);

    }

    public class CarNode{

        double x, y, theta;
        CarNode parent;

        public CarNode(double x, double y, double t, CarNode p){
            this.x = x;
            this.y = y;
            this.theta = t;
            this.parent = p;
            stepSize = 10;

        }

        // The goal can be within a 10 pixel range of the x,y coordinate
        public boolean nearGoal(){
            double rangeXP = goalNode.x + 10;
            double rangeXM = goalNode.x - 10;
            double rangeYP = goalNode.y + 10;
            double rangeYM = goalNode.y - 10;

            // if the current bot is within a range of the goal
            if(this.x > rangeXM && this.x < rangeXP && this.y > rangeYM && this.y < rangeYP)
                return true;

            return false;
        }
    }


    /*
     * When making moves, do it by omega and velocity -> if omega is 0, it is not rotating
     * If it is rotating, point to rotate around is v/w -> in direction of -sin(theta), cos(theta)
     *          - rotation center: ((-v/w)sin(theta) + x, (v/w)cos(theta) + y)
     * */
    public HashMap<CarNode, ArrayList<CarNode>> buildRRT(CarNode start, int s){
        int step = s; // max step for the RRT
        boolean goal = false;
        HashMap<CarNode, ArrayList<CarNode>> tree = new HashMap<>();

        // start with hashmap of start node and its neighbors list is currently null
        ArrayList<CarNode> startList = new ArrayList<>();
        startList.add(startNode);
        startNode.parent = null;
        tree.put(start, startList);
        int i = 0;
        while(!goal){
            CarNode qs = getRandomNode();
            CarNode qn = getClosestNeighbor(qs, tree);

            // CHECK IF THIS IS IN A BOUNDARY: IF SO, CONTINUE
            // IF NOT, COME UP WITH POINT RIGHT OUTSIDE BOUNDARY
            // create nodes for each of the 6 moves, and add them to the tree
            ArrayList<CarNode> newNodes;
            newNodes = getSuccessorMoves(qn, step);

            for(CarNode curr : newNodes) {
                curr.parent = qn;
                // adding new node to RRT
                // if the closest neighbor is already a key in the HM, add the new neighbor to its value list
                if (tree.get(qn) != null) {
                    tree.get(qn).add(curr);
                } else {
                    ArrayList<CarNode> newList = new ArrayList<>();
                    newList.add(curr);
                    predMap.put(curr, qn);
                    tree.put(qn, newList);
                }

                if(curr.nearGoal()){
                    goalNode = curr;
                    goal = true;
                }

            }
//            if(i == 3000)
//                goal = true;
//            i++;
        }
        return tree;
    }

    public ArrayList<CarNode> backtrack(){
//        System.out.println("SDF" + map);
        ArrayList<CarNode> path = new ArrayList<>();
        CarNode curr = goalNode;
        path.add(curr);
        while(curr != null){
            curr = curr.parent;
            path.add(curr);
        }

        return path;
    }

    // returns false if node is in a collision
    // checks if a wall is in between two possible nodes
    public boolean collision(CarNode car, CarNode prev){
        Line2D path = new Line2D.Double();
        path.setLine(prev.x, prev.y, car.x, car.y);


        for(int i = 0; i < walls.size(); i++){
            if(path.intersectsLine(walls.get(i))) {
                return false;
            }
        }

        return true;
    }

    // This collision checks if there is a wall between the current node and the new node being created
    // This is just an approximation. Because the movements are so small, this approximation is sufficient

    public ArrayList<CarNode> getSuccessorMoves(CarNode qn, int step){
        ArrayList<CarNode> newNodes = new ArrayList<>();
        double rotAngle =  Math.toRadians(30);

        CarNode forward = new CarNode(qn.x + 10*Math.cos(rotAngle), qn.y + 10*Math.sin(rotAngle), qn.theta, qn);
        if(collision(qn, forward)) {
            newNodes.add(forward);
        }else
            System.out.println("collision with forward");

        CarNode backward = new CarNode(qn.x - 10*Math.cos(rotAngle), qn.y - 10*Math.sin(rotAngle), qn.theta, qn);
        if(collision(qn, backward)) {
            newNodes.add(backward);
        } else
            System.out.println("collision with back");

        // forward moving counter clockwise v = 1 w = 1 rotAngle = rotAngle
        CarNode fcc = makeMove(10, 10, qn, rotAngle);
        if(collision(qn, fcc)) {
            newNodes.add(fcc);

        }else
            System.out.println("collision with for cc");

        // forward moving clockwise v = 1 w = 1 rotAngle = 360 - rotAngle
        CarNode fcw = makeMove(10, 10, qn, (2*Math.PI - rotAngle));
        if(collision(qn, fcw)) {
            newNodes.add(fcw);
        } else
            System.out.println("collision with for cw");

        // back moving counter clockwise v = 1 w = -1 rotAngle = rotAngle
        CarNode bcc = makeMove(10, -10, qn, rotAngle);
        if(collision(qn, bcc)){
            newNodes.add(bcc);
        } else
            System.out.println("collision with back cc");

        // back moving clockwise v = 1 w = 1 rotAngle = 360 - rotAngle
        CarNode bcw = makeMove(10, -10, qn, (2*Math.PI - rotAngle));
        if(collision(qn, bcw)) {
            newNodes.add(bcw);
        } else
            System.out.println("collision with back cw");


        return newNodes;
    }

    public CarNode makeMove(int v, int w, CarNode qn, double rotAngle){
        // coordinate the car will rotate around
        // correct
        int step = 1;
        double rotateAroundX = step*(qn.x + (-1*v/w) *  Math.sin(qn.theta));
        double rotateAroundY = step*(qn.y + (v/w) * Math.cos(qn.theta));

        // translate the point to rotate around the origin
        rotateAroundX = step*qn.x - rotateAroundX;
        rotateAroundY = step*qn.y - rotateAroundY;

        // calculate end x, y after rotation
        double newNodeX = rotateAroundX * Math.cos(rotAngle) - rotateAroundY * Math.sin(rotAngle);
        double newNodeY = rotateAroundY * Math.cos(rotAngle) + rotateAroundX * Math.sin(rotAngle);

        // translate the x, y position back
        newNodeX = newNodeX + step*qn.x;
        newNodeY = newNodeY + step*qn.y;

        // update angle
        double newTheta = qn.theta + rotAngle;

        // create new node
        CarNode newnode = new CarNode(newNodeX, newNodeY, qn.theta + newTheta, qn);

        return newnode;
    }

    // curr is the node that is finding its nearest neighbor
    public CarNode getClosestNeighbor(CarNode curr, HashMap<CarNode, ArrayList<CarNode>> tree){
        CarNode closest = startNode; // initialize closest as the beginning of the tree
        double currentShortest = 1000;

        for(CarNode c : tree.keySet()){
            ArrayList<CarNode> currNeighbors = tree.get(c);
            for(CarNode currNeighbor : currNeighbors){
                if(calculateDistance(currNeighbor, curr) < currentShortest){
                    currentShortest = calculateDistance(currNeighbor, curr);
                    closest = currNeighbor;
                }
            }
        }

        return closest;
    }

    public CarNode getRandomNode(){
        CarNode rand;
        int x = -1*(panelWidth/2) + (int)(Math.random()*panelWidth);
        int y = -1*(panelHeight/2) + (int)(Math.random()*panelHeight);
        int t = (int)(Math.random()*Math.toRadians(360));
        rand = new CarNode(x, y, t, null);

        return rand;
    }

    // calculates euclidean distance between two nodes
    public double calculateDistance(CarNode curr, CarNode possNeighbor){
        double dist;
        double x1, y1; double x2, y2;
        x1 = curr.x; y1 = curr.y;
        x2 = possNeighbor.x; y2 = possNeighbor.y;
        double dx = Math.abs(x1 - x2);
        double dy = Math.abs(y1 - y2);
        dist = Math.sqrt(dx*dx + dy*dy);
        return dist;
    }

    // method where the walls are created
    public ArrayList<Line2D> createObstacles(){
        ArrayList<Line2D> walls = new ArrayList<>();
        Line2D bottomWall = new Line2D.Double();
        bottomWall.setLine(-1*(panelWidth/2) + 30, -1*(panelHeight/2) + 30, (panelWidth/2 - 30),  -1*(panelHeight/2) + 30);
        Line2D leftWall = new Line2D.Double();
        leftWall.setLine(-1*(panelWidth/2) + 30, -1*(panelHeight/2) + 30, -1*(panelWidth/2 - 30),  (panelHeight/2) - 50);
        Line2D topWall = new Line2D.Double();
        topWall.setLine(-1*(panelWidth/2) + 30, 1*(panelHeight/2) -50, (panelWidth/2 - 30),  (panelHeight/2) - 50);
        Line2D rightWall = new Line2D.Double();
        rightWall.setLine((panelWidth/2) - 30, 1*(panelHeight/2) -50, (panelWidth/2 - 30),  -1*(panelHeight/2) + 30);
        Line2D mid1 = new Line2D.Double();
        mid1.setLine(-1*(panelWidth/2) + 30, -75, 0, -75);
        Line2D mid2 = new Line2D.Double();
        mid2.setLine(-75, 90, (panelWidth/2 - 30), 90);
        Line2D mid3 = new Line2D.Double();
        mid3.setLine(150, -75, panelHeight/2, -75);
        Line2D mid4 = new Line2D.Double();
        mid4.setLine(150, -50, panelHeight/2, -50);
        Line2D mid5 = new Line2D.Double();
        mid5.setLine(-1*(panelWidth/2) + 30, -50, 0, -50);
        Line2D mid6 = new Line2D.Double();
        mid6.setLine(-75, 30, -75, 130);
        Line2D mid7 = new Line2D.Double();
        mid7.setLine(150, 1*(panelHeight/2) -50, 150, 250);

        walls.add(bottomWall);
        walls.add(leftWall);
        walls.add(topWall);
        walls.add(rightWall);
        walls.add(mid1);
        walls.add(mid2);
        walls.add(mid3);
//        walls.add(mid4);
//        walls.add(mid5);
//        walls.add(mid6);
//        walls.add(mid7);

        return walls;
    }

    public static void main(String[] args){ RobotCar bot = new RobotCar(); }
}
