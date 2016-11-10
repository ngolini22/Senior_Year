//package MazeWorld;

import java.io.IOException;

public class MazeWorldDriver {
    public static void main(String args[]) throws IOException {
        int r = 15; // number of rows in the maze
        int c = 15; // number columns in the maze
        int n = 3; // number of bots
//        int[] starts = {0, 0, 1, 0, 2, 0};
//        int[] goals = {0, 2, 0, 4, 4, 3};
        int[] starts = {11, 0, 12, 0, 1, 1};
        int[] goals = {14, 14, 0, 5, 6, 0};
        MazeProblem world = new MazeProblem(r, c, "src/maze3.txt");
        world.printMaze();
        MultiRobot botTest = new MultiRobot(starts, goals, r, c, n, world);
        botTest.Driver();
    }
}