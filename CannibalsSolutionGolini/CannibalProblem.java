package cannibals;

import java.util.ArrayList;
import java.util.Arrays;

import cannibals.UUSearchProblem.UUSearchNode;


// for the first part of the assignment, you might not extend UUSearchProblem,
//  since UUSearchProblem is incomplete until you finish it.

public class CannibalProblem extends UUSearchProblem{

	// the following are the only instance variables you should need.
	//  (some others might be sinherited from UUSearchProblem, but worry
	//  about that later.)

	private int goalm, goalc, goalb;
	private int totalMissionaries;
	private int totalCannibals; 
	private int mk, E; // missionaries killed, E = number of edible missionaries

	// The last variable represents the total number of missionaries that can be eaten
	public CannibalProblem(int sm, int sc, int sb, int gm, int gc, int gb, int ak) {
		// I (djb) wrote the constructor; nothing for you to do here.
		startNode = new CannibalNode(sm, sc, sb, 0, 0);
		goalm = gm;
		goalc = gc;
		goalb = gb;
		totalMissionaries = sm;
		totalCannibals = sc;
		mk = 0;
		E = ak;
	}
	
	// node class used by searches.  Searches themselves are implemented
	//  in UUSearchProblem.
	// implements UUSearchNode 
	private class CannibalNode implements UUSearchNode{

		// do not change BOAT_SIZE without considering how it affect
		// getSuccessors. 
		
		private final static int BOAT_SIZE = 2;
	
		// how many missionaries, cannibals, and boats
		// are on the starting shore
		private int[] state; 
		
		// how far the current node is from the start.  Not strictly required
		//  for search, but useful information for debugging, and for comparing paths
		private int depth;  
		
		// new part of the state is e -> number of missionaries that can be eaten 
		public CannibalNode(int m, int c, int b, int d, int mk) {
			state = new int[4];
			this.state[0] = m;
			this.state[1] = c;
			this.state[2] = b;
			this.state[3] = mk;
			
			depth = d;
		}
		//  EXTENSION: LOSSY MISSIONARIES AND CANNIBALS -> Can eat up to E missionaries 
		// My assumption to this extension is that the cannibals will eat a missionary if they still have missionaries they can eat 
		//		(missionaries eaten < E), and there are more cannibals on one side than missionaries. Then The cannibals can eat a missionary, 
		//      or missionaries, in order to get to another legal state. 
		// So for example if the state has 2 missionaries that can be eaten, and is 231, then the cannibals can eat both missionaries. 
		// If the state only has 1 missionary that can be eaten, then 231 is still not a legal state because if the cannibals eat one missionary,
		// 		the state will be 311 with no more missionaries to be eaten, thus is illegal
		// Eating a missionary 
		public ArrayList<UUSearchNode> getSuccessors() {
			ArrayList<UUSearchNode> successors = new ArrayList<UUSearchNode>();
			int m = state[0];
			int c = state[1];
			int b = state[2];
			// missionaries killed
			int mk = state[3];
			
			int mside2 = totalMissionaries - m - mk;
			int cside2 = totalCannibals - c;
				
			// If this is a state with more cannibals than missionaries, the cannibals MUST eat the missionaries
			//		until the num of cannibals and missionaries are equal, and make sure there are still missionaries to eat
			// This will only happen if the state is safe - as in cannibal can eat missionaries until number of missionaries >= cannibals or 0
			// Want to make it so that if there are more cannibals than misisonaries on one side, the successor states are only for evening 
			//  	out the numbers. If missionaries need to be eaten, never bring anyone across the  river
			// This is necessary to make sure that if a state was reached to be safe with more cannibals than missionaries on one side, 
			//		it was only safe knowing the missionaries would be eaten. 
			// This if loop is the only place where missionaries will be eaten
			if((c > m && c > 0 && m > 0)|| (cside2 > mside2 && cside2 > 0 && mside2 > 0)){
				if(c > m && c > 0 && m > 0){
					// System.out.println("more cannibals than missionaries on start side");
					// while missionaries can still be killed and so that the number of cannibals = number of missionaries
					while(mk < E && m-mk > 0){
						mk++;
						m--;
						// System.out.println("killing one missionary");
						CannibalNode safeSuccessor = new CannibalNode(m, c, b, depth, mk);
						if(isSafeState(m, c, b, depth, mk)){
							//	System.out.println(safeSuccessor.toString());
							successors.add(safeSuccessor);
						}
					}
				}
	
				// Same as above, but if there are more cannibals on the goal side than missionaries
				if(cside2 > mside2 && cside2 > 0 && mside2 > 0){
					// System.out.println("more cannibals than missionaries on goal side");
					// while missionaries can still be killed and so that the number of cannibals = number of missionaries
					while(mk < E && mside2-mk > 0){
						mk++;
						mside2--;
						// System.out.println("killing one missionary");
						CannibalNode safeSuccessor = new CannibalNode(m, c, b, depth, mk);
						if(isSafeState(m, c, b, depth, mk)){
							// System.out.println(safeSuccessor.toString());
							successors.add(safeSuccessor);
						}

					}
				}
			// All of these states will have legal numbers of c and m on each side as abiding to the rules of the unextended game
			// Must find all states that are legal to the rules of the unextended game, and then all of the states
			//		that have more cannibals than missionaries on one side, but there are enough possible missionaries to kill
			// 		to rebalance the states
			} else {
				// If the boat is on the starting side, one or two people can be sent over. 
				// The nested for loop tests each combination of fewer missionaries and/or cannibals that are on the starting side
				// Inside the loops, there will be a check to make sure only 1 or 2 people cross at a time
				// For each combination of cannibals and missionaries, isSafeState is called to make sure the new state is legal
				if(b == 1){
					b = 0;
					for(int i = m; i >= 0; --i){
						for(int j = c; j >=0; --j){
							// E-mk is the number of missionaries that can still be eaten
							for(int k = (E-mk); k >= 0; --k){
							// Action can either be cross the river to a state where a missionary can be eaten on either side 
							// 		-> factored into isSafeState when E > 0
						    // OR if state can be found where no missionaries need to be eaten
							// Check if cannibals can eat one or more missionaries without anyone crossing the river
								//(i-k) is the number of missionaries crossing
								if((m+c != (i-k)+j) && ((m+c) - ((i-k)+j)) <= BOAT_SIZE  && (m - i-k) >= 0){
									if(isSafeState(i-k, j, b, depth, mk+k)){
										CannibalNode safeSuccessor = new CannibalNode(i-k, j, b, depth, mk);
										// String statePrint = safeSuccessor.toString();
										if(!successors.contains(safeSuccessor)){
											// System.out.println(statePrint);
											successors.add(safeSuccessor);
										}
									}
								}
							}

						}
					}
				} else{
					b = 1;
					for(int i = m; i <= (totalMissionaries-mk); i++){
						for(int j = c; j <= totalCannibals; j++){
							for(int k = (E-mk); k >= 0; --k){
								if((m+c != (i+j)) && ((i+j) - (c+m)) <= BOAT_SIZE && (i >= 0)){ 
									if(isSafeState(i, j, b, depth, mk+k)){
										CannibalNode safeSuccessor = new CannibalNode(i, j, b, depth, mk);
										// String statePrint = safeSuccessor.toString();
										if(!successors.contains(safeSuccessor)){
											// System.out.println(statePrint);
											successors.add(safeSuccessor);
										}
									}
								}
							}
						}
					}
				}
			}
			return successors;
		}
		
		private boolean isSafeState(int m, int c, int b, int d, int mk){
			boolean safe = true;
			int mside1 = m;
			int mside2 = totalMissionaries - m - mk;
			int cside1 = c;
			int cside2 = totalCannibals-c;
			int killsLeft = E - mk;
			// Safe states with no more missionaries to be eaten
			// if no more missionaries can be eaten and there are more cannibals than missionaries (at least one missionary) on the same side

			if(killsLeft == 0){
				if(mside2 < 0)
					safe = false;
				if(mside1 > 0 && mside1 < cside1) 
					safe = false;
				if(mside2 > 0 && mside2 < cside2)
					safe = false;
			// If more missionaries can be eaten: is not safe if number of cannibals > missionaries by more than mk
			} else {
				// If you leave more cannibals than missionaries on one side, must make sure there are enough kills left to get to legal state
				if(mside2 < 0)
					safe = false;
				if(mside1 > 0 && mside1+killsLeft < cside1){
					safe = false;
				}
				if(mside2 > 0 && mside2+killsLeft < cside2){
					safe = false;
				}
			}
			
			// if number of missionaries is less than zero or outside missionary bound, then the state is not safe
			if(mside1 < 0 || (mside1 > (totalMissionaries - mk)))
				safe = false;
			if(mside2 < 0 || (mside2 > (totalMissionaries - mk)))
				safe = false;
			if(m == 0 && c == 0 && b == 0){
				safe = true;
			}
			
			return safe;
		}
		
		// @Override
		public boolean goalTest() {
			// you write this method.  (It should be only one line long.)
			return (state[0] == 0 && state[1] == 0 && state[2] == 0);
		}

		// an equality test is required so that visited lists in searches
		// can check for containment of states
		@Override
		public boolean equals(Object other) {
			return Arrays.equals(state, ((CannibalNode) other).state);
		}

		@Override
		public int hashCode() {
			return state[0] * 100 + state[1] * 10 + state[2];
		}

		@Override
		public String toString() {
			// you write this method
			int mk = state[3];
			int m1 = state[0];
			int m2 = totalMissionaries - mk - m1;
			int c1 = state[1];
			int c2 = totalCannibals - c1;
			int b = state[2];
			String side1 = "Goal Side: " + m2 + " m" + " and " + c2 + " c ";
			String river = "=== ";
			String side2 = "Start Side: " + m1 + " m" + " and " + c1 + " c. ";
			String boat = " [ ] ";
			String leftToEat = mk + " missionarie(s) have been eaten";
			String result;
			
			if(b == 0){
				result = side1 + boat + river + side2 + leftToEat + "\n"; 
			} else{
				result = side1 + river + boat + side2 + leftToEat + "\n";
				
			}
			return result;
		}

//        You might need this method when you start writing 
//        (and debugging) UUSearchProblem.
		@Override
		public int getDepth() {
			return depth;
		}
	}
	
	public static void main(String[] args){
		// Used for testing getSuccessor
		// CannibalProblem test = new CannibalProblem(3,3,1,0,0,0,0);
		// CannibalProblem.CannibalNode testNode = test.new CannibalNode(3,2,1,5,1);
		// ArrayList<UUSearchNode> successors = new ArrayList<UUSearchNode>(); 
		// successors = testNode.getSuccessors();
	}
}



//totalMissionaries = m; tempE = E;
//// Check if boat can be moved to a state where missionaries can be eaten
//
//// Check if boat can be moved to a state where no missionaries need to be eaten
//if((m+c != i+j) && ((m+c) - (i+j)) <= BOAT_SIZE){ 
//	if(isSafeState(i, j, b, 5, mk,  true)){ // if the current action yields a legal state, add state to successor states
//		b = 0;
//		CannibalNode safeSuccessor = new CannibalNode(i, j, b, 5, mk);
//		String statePrint = safeSuccessor.toString();
//		if(!successors.contains(safeSuccessor)){
//			System.out.println(statePrint);
//			successors.add(safeSuccessor);
//		}
//	}
//}

//for(int i = m; i <= (totalMissionaries-mk); i++){
//for(int j = c; j <= totalCannibals; j++){
//	if((m+c != i+j) && ((i+j) - (c+m)) <= BOAT_SIZE){ 
//		if(isSafeState(i, j, b, depth, mk, true)){
//			CannibalNode safeSuccessor = new CannibalNode(i, j, b, 5, mk);
//			String statePrint = safeSuccessor.toString();
//			System.out.println(statePrint);
//			successors.add(safeSuccessor);
//		}
//	}
//}
//}