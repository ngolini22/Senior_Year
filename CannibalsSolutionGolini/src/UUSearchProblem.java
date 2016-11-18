
// CLEARLY INDICATE THE AUTHOR OF THE FILE HERE (YOU),
//  AND ATTRIBUTE ANY SOURCES USED (INCLUDING THIS STUB, BY
//  DEVIN BALKCOM).

package cannibals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;


public abstract class UUSearchProblem {
	
	// used to store performance information about search runs.
	//  these should be updated during the process of searches

	// see methods later in this class to update these values
	protected int nodesExplored;
	protected int maxMemory;

	protected UUSearchNode startNode;
	
	protected interface UUSearchNode {
		public ArrayList<UUSearchNode> getSuccessors();
		public boolean goalTest();
		public int getDepth();
	}

	// breadthFirstSearch:  return a list of connecting Nodes, or null
	// no parameters, since start and goal descriptions are problem-dependent.
	//  therefore, constructor of specific problems should set up start
	//  and goal conditions, etc.
	// The Integer is the hash code, which is the state written in integer form - 
	//					3 missionaries, 3 cannibals, and boat on start side = 331
	// The hash codes will be unique for each state, thus can be the keys
	// If the hash code has a value added to it, it has not been visited yet, and vice versa. 
	// If a specific state does have a value attached to it in the HT, it has been visited
	// The Value for each state in the HT is the backtrack -> The state it was previously at
	
	public List<UUSearchNode> breadthFirstSearch(){
		resetStats();
		List<UUSearchNode> path = new ArrayList<UUSearchNode>();
		Queue<UUSearchNode> frontier = new LinkedList<UUSearchNode>();
		frontier.add(startNode);
		nodesExplored++;
		
		HashMap<Integer,UUSearchNode> visited = new HashMap<Integer,UUSearchNode>();		
		visited.put(startNode.hashCode(), startNode);
		
		while(!frontier.isEmpty()){
			UUSearchNode currNode = frontier.remove();
			if(currNode.goalTest()){
				// Make backtracking happen here
				// Make backtracking function?
				path = backchain(currNode, visited);
			}
			// For each successor found from getSuccessors of the current node
			for(UUSearchNode successor : currNode.getSuccessors()){
				if(!visited.containsKey(successor.hashCode())){
					nodesExplored++;
					visited.put(successor.hashCode(), currNode);
					frontier.add(successor);
				}
			}	
		}
		return path;
	}
	
	//  backchain should only be used by bfs, not the recursive dfs
	private List<UUSearchNode> backchain(UUSearchNode node, HashMap<Integer, UUSearchNode> visited) {
		// you will write this method
		List<UUSearchNode> chain = new ArrayList<UUSearchNode>();
		while(!visited.get(node.hashCode()).equals(node)){
			chain.add(0,node);
			node = visited.get(node.hashCode());
		}
		chain.add(0,node);
		return chain;
	}

	public List<UUSearchNode> depthFirstMemoizingSearch(int maxDepth) {
		// You will write this method
		resetStats(); 
		HashMap<Integer,UUSearchNode> visited = new HashMap<Integer,UUSearchNode>();
		visited.put(startNode.hashCode(), startNode);
		nodesExplored++;
		return dfsrm(startNode, visited, 0, maxDepth);
	}

	// recursive memoizing dfs. Private, because it has the extra parameters needed for recursion. 
	// RIGHT NOW, DOES NOT STOP WHEN FINDING THE BASE CASE.... WHY??
	
	private List<UUSearchNode> dfsrm(UUSearchNode currentNode, HashMap<Integer, UUSearchNode> visited, int depth, int maxDepth) {
		// keep track of stats; these calls charge for the current node
		updateMemory(visited.size());
		incrementNodeCount();		
		
		// If the search is too deep, do not keep going
		// Should this be a failure?
		if(depth > maxDepth){
			System.out.println("Search Too Deep!");
			return null;
		}

		// BASE CASE -> If the current node is the goal case, then the solution has been found
		if(currentNode.goalTest()){
			List<UUSearchNode> path = new LinkedList<UUSearchNode>();
			path.add(currentNode);
			return path;
		// RECURSIVE CASE -> If the current node is not the goal case, find successors and search them recursively
		} else{
			for(UUSearchNode successor : currentNode.getSuccessors()){
				if(!visited.containsKey(successor.hashCode())){
					nodesExplored++;
					visited.put(successor.hashCode(), currentNode);
					// if return null, go to next successor
					// if not return null, return the dfsrm
					List<UUSearchNode> path = dfsrm(successor, visited, depth+1, maxDepth);
					if(path != null){
						path.add(currentNode);
						return path;
					}
				}
			}
		}
		// Here there are no successors that have not been not yet  been visited -> all of this states' successors have been visited
		// Have to trace back to where the loop began
		return null;
	}
	
	
	// set up the iterative deepening search, and make use of dfsrpc
	public List<UUSearchNode> IDSearch(int maxDepth) {
		resetStats();
		List<UUSearchNode> path = new LinkedList<UUSearchNode>();
		HashSet<UUSearchNode> currentPath = new HashSet<UUSearchNode>();
		nodesExplored++;
		currentPath.add(startNode);
		for(int depth = 0; depth <= maxDepth; depth++){
			path = dfsrpc(startNode, currentPath, depth, maxDepth);
			if(path != null){
				return path;
			}
		}
		return null;
	}

	// set up the depth-first-search (path-checking version), 
	//  but call dfspc to do the real work
	public List<UUSearchNode> depthFirstPathCheckingSearch(int maxDepth) {
		resetStats();
		// I wrote this method for you.  Nothing to do.
		HashSet<UUSearchNode> currentPath = new HashSet<UUSearchNode>();
		nodesExplored++;
		currentPath.add(startNode);
		return dfsrpc(startNode, currentPath, 0, maxDepth);
	}

	// recursive path-checking dfs. Private, because it has the extra parameters needed for recursion.
	private List<UUSearchNode> dfsrpc(UUSearchNode currentNode, HashSet<UUSearchNode> currentPath, int depth, int maxDepth) {
		// you write this method
		// if you get back to a node you've already seen (already in the path), go to new successor
		// similar to memo dfs, but only saves current path, not all nodes visited
		// Hash set is easy to save things in and look stuff up, and adding to list is O(1) 
		if(depth > maxDepth){
			System.out.println("Searched too far");
			return null;
		}
		// BASE CASE
		if(currentNode.goalTest()){
			System.out.println("GOAL FOUND! \n" + currentNode);
			List<UUSearchNode> path = new LinkedList<UUSearchNode>();
			path.add(currentNode);
			return path;
		// RECURSIVE CASE
		}else{
			for(UUSearchNode successor : currentNode.getSuccessors()){
				if(!currentPath.contains(successor)){
					currentPath.add(successor);
					nodesExplored++;
					List<UUSearchNode> path = dfsrpc(successor, currentPath, depth+1, maxDepth);
					if(path != null){
						path.add(currentNode);
						return path;
					}
				}
			}
		}
		return null;
	}

	protected void resetStats() {
		nodesExplored = 0;
		maxMemory = 0;
	}
	
	protected void printStats() {
		System.out.println("Nodes explored during last search:  " + nodesExplored);
		System.out.println("Maximum memory usage during last search " + maxMemory);
	}
	
	protected void updateMemory(int currentMemory) {
		maxMemory = Math.max(currentMemory, maxMemory);
	}
	
	protected void incrementNodeCount() {
		nodesExplored++;
	}

}
