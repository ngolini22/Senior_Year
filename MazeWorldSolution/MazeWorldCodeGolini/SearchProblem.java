/*
 * Created by Nicky Golini
 * SearchProblem Class for MazeWorld problem
 * Contains interface for RobotNode
 * Contains class for HeapNode containing a SearchNode and a priority
 * Code extended from Balkcom's C&M starting code
 */
import java.io.FileNotFoundException;
import java.util.*;

public abstract class SearchProblem {
    protected SearchNode startNode;

    protected interface SearchNode {
        int priority = 0;
        ArrayList<SearchNode> getNeighboringSpaces();
        int getX();
        int getY();
        boolean goalCoordinate();
        boolean robotAtGoal();
        int getTurn();
        int heuristic(SearchNode search);
        void updateWorld();
        void mazeToString();
        int updateTurn(int c);
        boolean doRobotsCollide(int x, int y, SearchNode currNode);
        ArrayList<Integer> getState();
    }

    // wrapper class to add nodes into the heap
    // priority is depth+heuristic -> need to store depth because we may need to get priority to an old node from
    //      queue. There's no way to look up a specific heapNode's priority fmor the queue, so must calculate it
    //      using old depth for the node.
    public class heapNode {
        int priority;
        SearchNode node;
        public heapNode(int pr, SearchNode node){
            this.priority = pr;
            this.node = node;
        }
    }

    // Overriding comparator to take in Heap Nodes
    public class heapComparator implements Comparator<heapNode>{
        public int compare(heapNode node1, heapNode node2){
            if(node1.priority > node2.priority)
                return 1;
            if(node1.priority < node2.priority)
                return -1;
            else
                return 0;
        }
    }

    // Driver for multi robot searching
    public boolean Driver() throws FileNotFoundException {
        boolean goals = true; // returns true if all bots get to their goals
        int turn;
        System.out.println("Start Positions: " + startNode.toString());
        SearchNode currNode = startNode;
        turn = currNode.getTurn();
        List<SearchNode> path;
        currNode.updateWorld();
        currNode.mazeToString();

        // while not all of the robots are at their respective goals
        while(goals){
            // getting path for a single robot in a node
            path = aStarSearch(currNode);
            // if there is a path, then a robot can move -> second condition (or stay at its goal -> first condition in if statement)
            if(path != null){
                if(path.size() == 1)
                    currNode = path.get(0);
                else
                    currNode = path.get(1);
            }
//            else{

                // Bot cannot move and must wait for another robot to move out of its way
//                System.out.println("Bot will wait till its next turn");
//            }
            // go to next robot's turn and update the world.
            currNode.updateTurn(turn);
            turn = currNode.getTurn();
            currNode.updateWorld();
            // Print after each robot moves -> stylistic choice
            if(turn%3 == 0) {
                System.out.println("Maze after Moves");
                currNode.mazeToString();
            }
            // if all of the robots are at their goals, end the loop!

            if(currNode.goalCoordinate()) {
                System.out.println("Robots Found Goals");
                currNode.mazeToString();
                goals = false;
            }
        }
        return goals;
    }

	public List<SearchNode> aStarSearch(SearchNode searchStartNode) throws FileNotFoundException {
        // Data structures used in A*. These are explained in depth in my attached report.
        // Rest of algorithm also in detail in report. See comments below to see what main chunks of code are doing
		Comparator<heapNode> comparator = new heapComparator(); // custom comparator for heapNodes in PQ
		PriorityQueue<heapNode> heap = new PriorityQueue<>(100000, comparator); // PQ of heapNodes -> holds priority, node
        HashMap<SearchNode, SearchNode> predMap = new HashMap<>(); // maps nodes to their predecessors for backchaining
        HashSet<SearchNode> visited = new HashSet<>(); // Contains set of all nodes that have been processed as a currentCoord -> slightly different from obsInHeap
        Map<ArrayList<Integer>, Integer> f_g = new HashMap<>(); // g(node) -> holds a node's g(node) function mapping
        Map<ArrayList<Integer>, Integer> f_h = new HashMap<>(); // h(node) -> holds heuristic cost + node's g(node) function mapping
        HashSet<ArrayList<Integer>> obsInHeap = new HashSet<>();

        heapNode hn = new heapNode(searchStartNode.heuristic(searchStartNode), searchStartNode);
        visited.add(searchStartNode);
		predMap.put(searchStartNode, null);
        f_g.put(searchStartNode.getState(), 0);
        f_h.put(searchStartNode.getState(), searchStartNode.heuristic(searchStartNode));
        heap.add(hn);

        // while there are still nodes in the PQ
		while(!heap.isEmpty()){
            SearchNode currNode = heap.peek().node;

            if(currNode.robotAtGoal()) {
                List<SearchNode> path;
                path = backchain(currNode, predMap);
                return path;
            }
            // If the node has already eben visited, do not process it
			if(visited.contains(currNode) && currNode != searchStartNode) {
                continue;
            }
            heap.remove(); // remove node so it is not processed again
            visited.add(currNode);
            for(SearchNode neighbor : currNode.getNeighboringSpaces()){
                // If this node has gone through this for loop already, doesn't nee to be checked again
                if(visited.contains(neighbor))
                    continue;
                // depth to currentNode -> all neighbors are 1 away from currentNode, so g(neighbor) is g(curr) + 1
                int tempG = f_g.get(currNode.getState())+1 + neighbor.heuristic(neighbor);
                // if the current node has not been processed, found a new node!
                if(!obsInHeap.contains(neighbor.getState())){
                    hn = new heapNode(tempG + neighbor.heuristic(neighbor), neighbor);
                    obsInHeap.add(neighbor.getState());
                    heap.add(hn);
				// if the node has been visited, see if new priority from heuristic is better than old priority
				} else{
                    if(tempG >= f_g.get(neighbor.getState())+f_h.get(neighbor.getState()))
                        continue;
                    // if it found a better path, then the code outside the next bracket will complete and update the node's information
                }
                // update node's attributes in data structures
				predMap.put(neighbor, currNode);
				f_g.put(neighbor.getState(), tempG);
				f_h.put(neighbor.getState(), tempG + neighbor.heuristic(neighbor));
			}
		}
//		System.out.println("no path found");
		return null;
	}

	// Backchain to get the path found by A*
    private List<SearchNode> backchain(SearchNode coord, HashMap<SearchNode, SearchNode> cameFrom) {
        List<SearchNode> chain = new ArrayList<>();

        while(cameFrom.get(coord) != null){
            chain.add(0,coord);
            coord = cameFrom.get(coord);
        }
        chain.add(0,coord);
        return chain;
    }
}
