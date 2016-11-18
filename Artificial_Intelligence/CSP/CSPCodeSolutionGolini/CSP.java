import java.util.*;

/**
 * Created by Nicky on 10/26/16.
 *
 * Class to hold generic variables, domain, and constraints of a CSProblem
 *
 * ** State ** of the CSP problem
 ***
 *
 *
 */
public class CSP {
    String[] variables;
    Constraint constraints;
    HashMap<Integer, List<Integer>> domain = new HashMap<>();
    HashMap<Integer, List<Integer>> removedFromDomain = new HashMap<>();
    HashMap<Integer, List<Integer>> varMap = new HashMap<>();
    HashMap<Integer, Integer> varDegMap = new HashMap<>(); // used for DH value picker
    int DH = 0;
    int MRV = 0;
    int triedAssignments = 0;

    protected CSPNode startNode;

    /*
    * Can put the domain and constraints in a wrapper node that has assign, domain, and constraint, and pass
    *       in a starting node which includes the translated constraints and domains from MapColoringCSP and deconstruct
    *       the package in teh CSP constructor with no arguments. Will be similar to how the commented constructor takes
    *       in the domains and constrictions by argument. Then backtrack can be passed an initial assign
    * */

    protected interface CSPNode{
        Constraint getConstraints();
        HashMap<Integer, List<Integer>> getDomain();
        int[] getAssignment();
        String[] getVariables();
        String AssignToString(int[] assignment);
    }

    public void createVarMap(){
        for(List<Integer> curr : constraints.conMap.keySet()){
            if(constraints.conMap.get(curr).size() == 6) {
                int a = curr.get(0);
                int b = curr.get(1);
                List<Integer> newval = new ArrayList<>();
                if (!varMap.containsKey(a)) {
                    newval.add(b);
                    varMap.put(a, newval);
                } else {
                    newval = varMap.get(a);
                    newval.add(b);
                    varMap.remove(a);
                    varMap.put(a, newval);
                }
            }
        }

        for(List<Integer> curr : constraints.conMap.keySet()){
            if(!varMap.containsKey(curr.get(0))){
                varMap.put(curr.get(0), null);
            }
        }

//        System.out.println("\nvar map" + varMap);
    }

    public void createVarDegMap(){
        // just have to look at first one -> constraints are both ways so if looking at pair: V, T there is a (V, T) and (T, V) key in constraints
        for(List<Integer> curr : constraints.conMap.keySet()){
            int a = curr.get(0);
            // THE 6 HERE IS A HACK -> Want the number of pairs in which the first territory constrains the other territory
            if(constraints.conMap.get(curr).size() == 6) {
                if (!varDegMap.containsKey(a)) {
                    varDegMap.put(a, 1);
                } else {
                    int currScore = varDegMap.get(a);
                    currScore++;
                    varDegMap.put(a, currScore);
                }
            }
        }

        // if territory not in vardegmap yet, it needs to be a 0
        for(List<Integer> curr : constraints.conMap.keySet()){
            if(!varDegMap.containsKey(curr.get(0))){
                varDegMap.put(curr.get(0), 0);
            }
        }
//        System.out.println(varDegMap);
    }



    public int DHChooser(int[] assign, CSP csp){
        int var = -1;
        int currLargest = -1;

        for(int key : domain.keySet()){
            if(domain.get(key).size() > currLargest && assign[key] == -1){
                currLargest = domain.get(key).size();
                var = key;
            }
        }

        return var;
    }

    public int MRVChooser(int[] assign, CSP csp){
        int var = -1;

        int fewest = 1000;
        for(int k : this.domain.keySet()){
            if(this.domain.get(k).size() < fewest && assign[k] == -1)    {
                var = k;
                fewest = this.domain.get(k).size();
            }
        }

        return var;
    }

    public int[] BackTrackingSearch(){
        constraints = startNode.getConstraints();
        domain = startNode.getDomain();
        createVarMap();
        if(DH == 1){
            createVarDegMap();
        }
        int[] startAssignment = startNode.getAssignment();
        variables = startNode.getVariables();
        int[] final_assignment = BackTrack(startAssignment, this);
        System.out.println(startNode.AssignToString(final_assignment));
        System.out.println("NODES VISITED: " + triedAssignments);
        return final_assignment;
    }

    public synchronized int[] BackTrack(int[] assign, CSP csp){
        int var;

//        System.out.println(this.constraints);
//        System.out.println(this.domain);

        if(constraints.isSatisfied(assign, true, domain) == 2){
            return assign;
        }
        if(MRV == 1){
            var = MRVChooser(assign, this);
        } else if(DH == 1){
            var = DHChooser(assign, this);
        } else{
            var = ChooseUnassignedVar(assign);
        }

        HashMap<Integer, List<Integer>> tempdomain = new HashMap<>(domain);

        List<Integer> vals = new ArrayList<>(domain.get(var));

        for(int val : vals){
            triedAssignments++;

            assign[var] = val;
            if(constraints.isSatisfied(assign, true, this.domain) == 2){
                return assign;
            }
            if(constraints.isSatisfied(assign, true, this.domain) > 0) {
                if(MAC3(csp, var, assign)) { // if inferences do not lead to failure
                    deleteFromDomain();
                }
                BackTrack(assign, csp);
            }

            // restore constraints
            if(constraints.isSatisfied(assign, true, this.domain) != 2) {
                assign[var] = -1;
                domain = new HashMap<>(tempdomain);
            } else{
                return assign;
            }
        }
        System.out.println("No assignment found");

        return null;
    }

    public synchronized boolean MAC3(CSP csp, int var, int[] assign){
//        System.out.println("IN AC3");
//        System.out.println("Domain before AC3" + domain);
        removedFromDomain = new HashMap<>();
        Stack<List<Integer>> q = new Stack<>();

        for(int i = 0; i < assign.length; i++){
            if(assign[i] == -1){
                List<Integer> arc = new ArrayList<>();
                arc.add(i); arc.add(var);
                q.add(arc);
//                System.out.println(arc.toString());
            }
        }

        int xi, xj;
        while(!q.isEmpty()){
            removedFromDomain = new HashMap<>();
            List<Integer> X = q.pop();
            xi = X.get(0);
            xj = X.get(1);

            boolean rev = Revise(csp, xi, xj);
            if(rev){
                int xx = removedFromDomain.get(xi).size();
                int yy = domain.get(xi).size();
                if(xx == yy){
                    return false;
                }

                List<Integer> neighbors = varMap.get(xi);
                if(neighbors != null) {
                    for (int xk : neighbors) {
                        if(xk != xj) {
                            List<Integer> newarc = new ArrayList<>();
                            newarc.add(xk);
                            newarc.add(xi);
                            q.add(newarc);
                        }
                    }
                }
            }
        }
        return true;
    }

    public void deleteFromDomain(){
//        HashMap<Integer, List<Integer>> dom = (HashMap<Integer, List<Integer>>) domain.clone();
//        System.out.println("REMOVING: " + removedFromDomain);
        for(int key : removedFromDomain.keySet()){
            for(int val : removedFromDomain.get(key)){
                if(domain.containsKey(key)) {
                    int curr;
                    for(Iterator itr = domain.get(key).iterator(); itr.hasNext();) {
                        curr = (int) itr.next();
                        if (curr == val){
                            itr.remove();
                        }
                    }
                }
            }
        }
//        System.out.println("DOMAIN AFTER REMOVALS" + domain);
    }

    public synchronized boolean Revise(CSP csp, int xi, int xj){
        boolean revised = false;
        List<Integer> vars = new ArrayList<>();
        vars.add(xj); vars.add(xi);
        int x;

//        System.out.println("In revise");
        for(Iterator<Integer> itr = this.domain.get(xi).iterator(); itr.hasNext();){
            revised = false;
            x = itr.next();
            boolean inc = false;
            List<Integer> vals = new ArrayList<>();
            List<Integer> ys = domain.get((xj));
            for(int y : ys){
                vals.add(0, x); vals.add(1, y);
                if(constraints.conMap.get(vars).contains(vals)){
                    inc = true;
                }
            }

            if(!inc){
                // removing x fro xy
                if(removedFromDomain.containsKey(xi)) {
                    List<Integer> newVal = removedFromDomain.get(xi);
                    if(!newVal.contains(x))
                        newVal.add(x);
                    removedFromDomain.put(xi, newVal);
                } else{
                    List<Integer> newVal = new ArrayList<>();
                    newVal.add(x);
                    removedFromDomain.put(xi, newVal);
                }
//                System.out.println(removedFromDomain);
                revised = true;
            }
        }
        return revised;
    }

    public int ChooseUnassignedVar(int[] assign){
        for(int i = 0; i < assign.length; i++){
            if(assign[i] == -1)
                return i;
        }
        return -1;
    }

    public void printAssignment(int[] assignment){
        System.out.println("ASSIGNMENT: ");
        for(int i = 0; i < assignment.length; i++){
            System.out.print(assignment[i] + " ");
        }
    }


}


//        int largestSize = -1;
//        for(int i = 0; i < assign.length; i++){
//            int size = 0;
//            if(assign[i] == -1){
//                for(int color : this.domain.get(i)){
//                    assign[i] = color;
//                    if(csp.constraints.isSatisfied(assign, false, domain) != 0){
//                        size++;
//                    }
//                    assign[i] = -1;
//                }
//            }
//            if(size > largestSize){
//                largestSize = size;
//                var = i;
//            }
//        }