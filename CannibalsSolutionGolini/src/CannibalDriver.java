package cannibals;

import java.util.List;

public class CannibalDriver {
	public static void main(String args[]) {
	
		final int MAXDEPTH = 5000;

		// interesting starting state:  
		//  8, 5, 1  (IDS slow, but uses least memory.)


		// set up the "standard" 331 problem:
		// The last parameter is the number of allowed missionary eatings
		// Works well with 4m 4c 1b 2ak
		UUSearchProblem mcProblem = new CannibalProblem(3, 3, 1, 0, 0, 0, 2);
		
		List<UUSearchProblem.UUSearchNode> path;
		
		// The BFS path prints top down, and the rest of them print bottom up
		path = mcProblem.breadthFirstSearch();	
		System.out.println("bfs path length:  " + path.size() + " " + path);
		mcProblem.printStats();
		System.out.println("--------");
		
	
		path = mcProblem.depthFirstMemoizingSearch(MAXDEPTH);
		System.out.println("Path: " + path);
		System.out.println("dfs memoizing path length:" + path.size());
		mcProblem.printStats();
		System.out.println("--------");
		
		path = mcProblem.depthFirstPathCheckingSearch(MAXDEPTH);
		System.out.println(path);
		System.out.println("dfs path checking path length:" + path.size());
		mcProblem.printStats();
		
		
		System.out.println("--------");
		path = mcProblem.IDSearch(MAXDEPTH);
		System.out.println(path);
		System.out.println("Iterative deepening (path checking) path length:" + path.size());
		mcProblem.printStats();
//		
	}
}