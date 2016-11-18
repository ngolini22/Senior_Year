/**
 * Created by Nicky on 10/31/16.
 *
 * Driver function to initialize instances of circuitboard CSP and map coloring CSP, and calling backtracking on them
 */
public class CSPDriver {

    public static void main(String[] args){
        int[] assignment;
//        CSP circuitBoard = new CircuitBoardCSP();
//        assignment = circuitBoard.BackTrackingSearch();
        CSP mapProb = new MapColoringCSP();
        assignment = mapProb.BackTrackingSearch();
    }
}
