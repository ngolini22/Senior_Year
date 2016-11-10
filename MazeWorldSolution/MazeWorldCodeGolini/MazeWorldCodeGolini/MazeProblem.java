//package MazeWorld;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

//import java.util.stream.Stream;

// maze should jsut be own class without any extensions - world is input for multirobot class

public class MazeProblem{

    // coordinate of goal
    private int goalX, goalY;
    // number of robots
    private static int n_robots;
    // num rows and cols in the maze
    private static int rows;
    private static int cols;
    // File name of where maze is being loaded from
    private String filename;

    static char[][] world;
    private int[] startCoords;
    int r, c;

    // Should be read in from file
    char worldHard[][] = {{'.','.','.','#','#'},{'#','.','.','.','.'},{'.','.','.','#','.'},{'.','.','#','.','.'},{'.','.','.','#','.','.'}};

    // Initializes the maze and all the robots
    public MazeProblem(int r, int c, String filename) throws IOException {
        // Construct maze
        this.r = r;
        this.c = c;
        this.filename = filename;
        world = buildMaze(filename);

    }
//    public char[][] buildMaze(int r, int c, String filename){
//        // HOW ARE WE SUPPOSED TO READ IN A FILE?
//        char[][] tempWorld = new char[r][c];
////		BufferedReader br = new BufferedReader(new FileReader(filename));
//
//        for(int j = cols-1; j >= 0; j--){
//            for(int i = 0; i < rows; i++){
//                if(worldHard[cols-1-j][i] == '.')
//                    tempWorld[i][j] = '1';
//                else
//                    tempWorld[i][j] = 0;
//            }
//        }
//        return tempWorld;
//    }

    public char[][] buildMaze(String filename) throws IOException {
        System.out.println(r + " " + c);
        char[][] tempWorld = new char[r][c];
        int c = 0;
        FileReader in = new FileReader(filename);
        BufferedReader br = new BufferedReader(in);
        String line;
        int k = 0;
        while ((line = br.readLine()) != null) {
            for(int i = 0; i < line.length(); i++) {
                tempWorld[k][i] = line.charAt(i);
                c = i;
            }
            k++; // for each line
        }
        c++;
        k++;
        in.close();
        // To print out maze to see what it looks like
//        System.out.println("MAZE");
//        for(int i = 0; i < r; i++){
//            for(int j = 0; j < c; j++){
//                if(j == c-1)
//                    System.out.print("(" + i + "," + j + ")" + tempWorld[i][j]+"\n");
//                else
//                    System.out.print("(" + i + "," + j + ")" + tempWorld[i][j]);
//            }
//        }

//        tempWorld;

        char[][] world1 = new char[r][c];
        System.out.println(r + " " + c);

        for(int j = 0; j < r; j++){
            for(int i = 0; i < c; i++){
                if(tempWorld[r - 1- j][i] == '.')
                    world1[i][j] = '1';
                else
                    world1[i][j] = '0';
            }
        }

        // adding in robots to world
        int rTemp, x, y = 0;
        char rName = 'A'; // Robot name
        for(int i = 0; i < n_robots*2; i=i+2){
            x = startCoords[i];
            y = startCoords[i+1];
            tempWorld[x][y] = rName;
            rTemp = (int) rName;
            rTemp = rTemp + 1;
            rName = (char) rTemp;
        }

        return world1;
    }

    public void printMaze(){
        System.out.println("Starting World: ");
        for(int j = r-1; j >= 0; j--){
            for(int i = 0; i < c; i++){
                if(i == c-1){
                    System.out.print(world[i][j] + "\n");
                } else{
                    System.out.print(world[i][j]);
                }
            }
        }
    }
}