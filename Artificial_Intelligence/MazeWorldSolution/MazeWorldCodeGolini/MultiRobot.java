/*
 * Created by Nicky Golini
 * MultiRobot Class for MazeWorld problem
 * Contains subclass for a node in the robot state space
 * Code extended from Balkcom's C&M starting code
 */

//package MazeWorld;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;


// This is the class for multiple robots
// Individual Robots are represented as a RobotNode
public class MultiRobot extends SearchProblem {
    static MazeProblem MazeWorld;
    static char[][] world;

    // Should be read in from file
//    char worldHard[][] = {{'.', '.', '.', '#', '#'}, {'#', '.', '.', '.', '.'}, {'.', '.', '.', '#', '.'}, {'.', '.', '#', '.', '.'},
//            {'.', '.', '.', '#', '.', '.'}};

    private int rows, cols;
    private int n_robots;
    private int robotTurn = 0;
    int[] goals;

    // How to have starting x and y values and goal x and y values for n robots?
    public MultiRobot(int[] startCoords, int[] goalCoords, int r, int c, int n, MazeProblem w)
            throws FileNotFoundException {
        rows = r;
        cols = c;
        n_robots = n;
        MazeWorld = w;
        world = w.world;
        System.out.println(world[0][0]);
        goals = goalCoords;
        startNode = new RobotNode(startCoords);

    }

    // Holds state of location of up to 3 robots
    public class RobotNode implements SearchNode {
        // classes path and files
        int depth;
        private int[] state;

        // how far the current node is from the start.  Not strictly required
        //  for search, but useful information for debugging, and for comparing paths
        public RobotNode(int[] starts) {
            state = new int[n_robots * 2];
            this.state = loadState(starts);
//            this.mazeToString();
        }

        public ArrayList<Integer> getState() {
            ArrayList<Integer> stateList = new ArrayList<>();
            for (int i = 0; i < n_robots * 2; i++) {
                stateList.add(state[i]);
            }
            return stateList;
        }

        // loads robots' current positions into state[]
        // not necessary cause the types of state and the argument is int[] -> can just say state = starts
        public int[] loadState(int[] s) {
            int size = n_robots * 2;
            int[] states = new int[size];
            for (int i = 0; i < size; i++) {
                states[i] = s[i];
            }
            return states;
        }

        public ArrayList<SearchNode> getNeighboringSpaces() {
            ArrayList<SearchNode> neighbors = new ArrayList<>();
            int[] stateTemp;

            stateTemp = state.clone();
            int currX = stateTemp[robotTurn * 2];
            int currY = stateTemp[robotTurn * 2 + 1];

            // if north is open
            currY++;
            RobotNode safeNode = new RobotNode(stateTemp);
            if (currY < rows && !doRobotsCollide(currX, currY, safeNode) && noWall(currX, currY)) {
                stateTemp[robotTurn * 2 + 1] = currY;
                safeNode = new RobotNode(stateTemp);
                neighbors.add(safeNode);
            }
            currY--;
            stateTemp[robotTurn * 2 + 1] = currY;

            // East
            currX++;
            safeNode = new RobotNode(stateTemp);
            if (currX < cols && !doRobotsCollide(currX, currY, safeNode) && noWall(currX, currY)) {
                stateTemp[robotTurn * 2] = currX;
                safeNode = new RobotNode(stateTemp);
                neighbors.add(safeNode);
            }
            currX--;
            stateTemp[robotTurn * 2] = currX;

            // South
            currY--;
            safeNode = new RobotNode(stateTemp);
            if (currY >= 0 && !doRobotsCollide(currX, currY, safeNode) && noWall(currX, currY)) {
                stateTemp[robotTurn * 2 + 1] = currY;
                safeNode = new RobotNode(stateTemp);
                neighbors.add(safeNode);
            }
            currY++;
            stateTemp[robotTurn * 2 + 1] = currY;

            // West
            currX--;
            safeNode = new RobotNode(stateTemp);
            if (currX >= 0 && !doRobotsCollide(currX, currY, safeNode) && noWall(currX, currY)) {
                stateTemp[robotTurn * 2] = currX;
                safeNode = new RobotNode(stateTemp);
                neighbors.add(safeNode);
            }
            currX++;
            stateTemp[robotTurn * 2] = currX;

//            System.out.println(neighbors);
            return neighbors;
        }

        // returns true if there is no wall in specified coordinate -> returns true if coordinate is open
        public boolean noWall(int x, int y) {
            if (world[x][y] == '1')
                return true;
            else
                return false;
        }

        // getters for x and y of current robot in state
        public int getX() {
            return state[robotTurn * 2];
        }

        public int getY() {
            return state[robotTurn * 2 + 1];
        }

        // takes the average of the manhattan distances of the robots in the maze
        public int heuristic(SearchNode search) {
            int dx = 0, dy = 0;
            for (int i = 0; i < n_robots * 2; i = i + 2) {
                dx = dx + Math.abs(search.getX() - state[i]);
                dy = dy + Math.abs(search.getY() - state[i + 1]);
            }
            int total = dx + dy;
            return total / n_robots;
        }

        public int hashCode() {
            return robotTurn * 100 + state[robotTurn * 2] * 10 + state[robotTurn * 2 + 1];
        }

        @Override
        public String toString() {
            String result = "";
//            return "(" + state[robotTurn * 2] + ", " + state[robotTurn * 2 + 1] + ")";

            for (int i = 0; i < n_robots * 2; i = i + 2) {
                if (i == 0)
                    result = result + "(" + state[i] + ", " + state[i + 1] + ")";
                else
                    result = result + ", (" + state[i] + ", " + state[i + 1] + ")";
            }

            return result + this.priority;
        }

        // says if single robot is at the goal -> used in A*
        public boolean robotAtGoal() {
            return (state[robotTurn * 2] == goals[robotTurn * 2] && state[robotTurn * 2 + 1] == goals[robotTurn * 2 + 1]);
        }

        // returns true if there is a robot in the way of another bot -> used in driver for multi robot problem
        public boolean doRobotsCollide(int x, int y, SearchNode curr) {
            for (int i = 0; i < n_robots * 2; i = i + 2) {
                if (curr.getState().get(i) == x && curr.getState().get(i + 1) == y) {
//                    System.out.println(curr.getState().get(i) + " " + curr.getState().get(i + 1) + " " + x + " " + y);
                    return true;
                }
            }
            return false;
        }

        // says if every robot is at its goal
        public boolean goalCoordinate() {
            boolean goal = true;
            for (int i = 0; i < n_robots * 2; i++) {
                if (state[i] != goals[i]) {
                    return false;
                }
            }
            return goal;
        }

        public int getTurn() {
            return robotTurn;
        }

        // Adds in robots in their new coordinates after a robot is moved
        public void updateWorld() {
            int x, y;
            int nameA; // name in ascii
            char name = 'A';

            // Add robots to new coordinates
            for (int i = 0; i < n_robots * 2; i = i + 2) {
                x = state[i];
                y = state[i + 1];
                world[x][y] = name;
                nameA = (int) name;
                nameA++;
                name = (char) nameA;
            }

            // Take out robots from previous coordinates
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    // if at a coordinate that is a robot or a previous position of a robot
                    if (world[i][j] != '0' && world[i][j] != '1') {
                        for (int k = 0; k < n_robots * 2; k = k + 2) {
                            x = state[k];
                            y = state[k + 1];
                            if (i != x || j != y)
                                world[i][j] = '1';
                        }
                    }
                }
            }

        }

        public void mazeToString() {
            int x, y;
            char name = 'A';
            int nameA;
            for (int i = 0; i < n_robots * 2; i = i + 2) {
                x = state[i];
                y = state[i + 1];
                world[x][y] = name;
                nameA = (int) name;
                nameA++;
                name = (char) nameA;
            }

            for (int j = cols - 1; j >= 0; j--) {
                for (int i = 0; i < rows; i++) {
                    if (world[i][j] == '0') {
                        if (i == rows - 1)
                            System.out.print("# \n");
                        else
                            System.out.print("#");
                    } else if (world[i][j] == '1') {
                        if (i == rows - 1)
                            System.out.print(". \n");
                        else
                            System.out.print(".");
                    } else {
                        if (i == rows - 1)
                            System.out.print(world[i][j] + "\n");
                        else
                            System.out.print(world[i][j]);
                    }
                }
            }
        }

        public int updateTurn(int c) {
            c++;
            if (c == n_robots)
                c = 0;
            robotTurn = c;
            return c;
        }
    }

    // Main for testing neighboring states function
    public static void main(String[] args) throws IOException {
        int[] starts = {4, 2};
        int[] goals = {4, 3};
        MazeProblem world = new MazeProblem(5, 5, "maze1.txt");
        MultiRobot botTest = new MultiRobot(starts, goals, 5, 5, 1, world);
        RobotNode newBot = botTest.new RobotNode(starts);
        newBot.mazeToString();

        newBot.getNeighboringSpaces();
    }
}