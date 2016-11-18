import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Nicky on 11/1/16.
 *
 * CSP creator for circuit board problem
 */

public class CircuitBoardCSP extends CSP{
    int m = 3;
    int n = 10;
    int[][] circuitboard = new int[m][n];
    List<List<Integer>> variables = new ArrayList<>();
    HashMap<List<Integer>, Integer> varMap = new HashMap<>();
    HashMap<List<Integer>, List<Integer>> domainCB = new HashMap<>();
    HashMap<Integer, List<Integer>> domain = new HashMap<>();
    HashMap<List<List<Integer>>, ArrayList<List<Integer>>> strConstraints = new HashMap<>();
    Constraint constraints;
    String[] variable_list;

    public CircuitBoardCSP(){
        this.variables = createParts();
        variable_list = new String[]{"A", "B", "C", "E"};
        System.out.println("VARIABLES: " + variables);

        this.domainCB = createDomain();
        this.domain = translateDomain();
        System.out.println("DOMAIN: " + domainCB);

        this.strConstraints = createConstratints();
        System.out.println("CONSTRAINTS: " + strConstraints);
        this.constraints = new Constraint(constraintToInt(strConstraints));

        int[] assignment = createAssign();
        startNode = new CircuitBoardNode(assignment, domain, constraints, variable_list);
    }

    public class CircuitBoardNode implements CSPNode{
        int[] assignment;
        String[] var;
        Constraint constraints;
        HashMap<Integer, List<Integer>> domain;

        public CircuitBoardNode(int[] a, HashMap<Integer, List<Integer>> d, Constraint c, String[] v){
            this.assignment = a;
            this.domain = d;
            this.constraints = c;
            this.var = v;
        }
        public Constraint getConstraints(){
            return this.constraints;
        }

        public HashMap<Integer, List<Integer>> getDomain(){
            return this.domain;
        }

        public int[] getAssignment(){
            return this.assignment;
        }

        public String[] getVariables(){
            return this.var;
        }

        public String AssignToString(int[] assignment){
            HashMap<Integer, Character> finalSol = new HashMap<>();
            String result= "";
            int spot;
            char[][] sol = new char[m][n];

            for(int i = 0; i < assignment.length; i++){
                spot = assignment[i];
                int w1 = variables.get(i).get(1);
                int h1 = variables.get(i).get(0);
                int tempw1;
                while(h1 > 0){
                    tempw1 = w1;
                    while(tempw1 > 0){
                        finalSol.put(spot, variable_list[i].charAt(0));
                        tempw1--;
                        spot++;
                    }
                    spot = spot - w1 + n;
                    h1--;
                }
//                System.out.println(finalSol);

            }

            for(int k = 0; k < m*n; k++){
                if(!finalSol.containsKey(k)){
                    finalSol.put(k, '.');
                }
            }

            for(int index : finalSol.keySet()){
                int y = index % n;
                int x = index / n;
//                System.out.println(index + " " + x + " " + y);
                sol[m-x-1][y] = finalSol.get(index);
            }
//            System.out.println("\n");
            for(int i = 0; i < m; i++){
                for(int j = 0; j < n; j++){
                    result =  result + sol[i][j];
//                    System.out.println(j+ " " + i);
                    if(j == n-1){
                        result = result + "\n";
                    }
                }
            }

            return result;
        }
    }

    /**
     * Variables will be a List of an x and y value, which are the width and height of the piece respectively
    * */
    public List<List<Integer>> createParts(){
        List<List<Integer>> vars = new ArrayList<>();
        List<Integer> var1 = new ArrayList<>(); var1.add(2); var1.add(3); vars.add(var1); // 2 by 3 block
        List<Integer> var2 = new ArrayList<>(); var2.add(2); var2.add(5); vars.add(var2); // 2 by 5 block
        List<Integer> var3 = new ArrayList<>(); var3.add(3); var3.add(2); vars.add(var3); // 3 by 2 block
        List<Integer> var4 = new ArrayList<>(); var4.add(1); var4.add(7); vars.add(var4); // 1 by 7 block
        varMap.put(var1, 0); varMap.put(var2, 1); varMap.put(var3, 2); varMap.put(var4, 3);

        return vars;
    }

    public int[] createAssign(){
        int[] a = new int[variables.size()];

        for (int i = 0; i < variables.size(); i++) {
            a[i] = -1;
        }

        return a;
    }

    public HashMap<Integer, List<Integer>> translateDomain(){
        HashMap<Integer, List<Integer>> dom = new HashMap<>();

        int i = 0;
        for(List<Integer> key : domainCB.keySet()){
            dom.put(i, domainCB.get(key));
            i++;
        }

        return dom;
    }

    public HashMap<List<Integer>, List<Integer>> createDomain(){
        HashMap<List<Integer>, List<Integer>> dom = new HashMap<>();
        for(int i = 0; i < variables.size();  i++){
            int xx = variables.get(i).get(0);
            int yy = variables.get(i).get(1);
            for(int j = 0; j < m*n; j++){
                // have to switch them because dimensions are rows x cols, but need x, y values to work with
                if(doesPieceFit(yy, xx, j)){
                    // add j to variable(i)'s domain
                    if(dom.containsKey(variables.get(i))){
                        dom.get(variables.get(i)).add(j);
                    } else{
                        List<Integer> newVar = new ArrayList<>();
                        newVar.add(j);
                        dom.put(variables.get(i), newVar);
                    }
                }
            }
        }

        return dom;
    }

    /**
     *
     *    circuit board:
     *
     *          (0, 2) (1, 2) (2, 2) (3, 2) (4, 2) (5, 2) (6, 2) (7, 2) (8, 2) (9, 2)
     *          (0, 1) (1, 1) (2, 1) (3, 1) (4, 1) (5, 1) (6, 1) (7, 1) (8, 1) (9, 1)
     *          (0, 0) (1, 0) (2, 0) (3, 0) (4, 0) (5, 0) (6, 0) (7, 0) (8, 0) (9, 0)
     *
     *          20 21 22 23 24 25 26 27 28 29
     *          10 11 12 13 14 15 16 17 18 19
     *          00 01 02 03 04 05 06 07 08 09
     *
     *          08 09 10 11
     *          04 05 06 07
     *          00 01 02 03
     *
     *          @param xx = width of piece
     *          @param yy = height of piece
     *          @param i = index of location in question
     *          @return true if the piece fits on the board
     *
     */

    public boolean doesPieceFit(int xx, int yy, int i){
        int row = ((i) / n);
        int xAvail = ((row+1)*n - i);
        int yAvail = m - row;

        if(xAvail < xx || yAvail < yy){
            return false;
        }
        return true;
    }

    public HashMap<List<List<Integer>>, ArrayList<List<Integer>>> createConstratints() {
        HashMap<List<List<Integer>>, ArrayList<List<Integer>>> con = new HashMap<>();
        for (int i = 0; i < variables.size(); i++) {
            List<Integer> a = variables.get(i);
            for (int j = 0; j < variables.size(); j++) {
                List<Integer> b = variables.get(j);
                if (!a.equals(b)) {
                    List<Integer> newKey = new ArrayList<>();
                    // constraint for variable i and variable j
                    newKey.add(i);
                    newKey.add(j);
                    for(int possSpot1 : domainCB.get(a)){
                        for(int possSpot2 : domainCB.get(b)){
                            // see if the two pieces overlap at the two possible positions
                            if(doPiecesFit(a, b, possSpot1, possSpot2)){
                                List<List<Integer>> varPair = new ArrayList<>();
                                varPair.add(variables.get(i)); varPair.add(variables.get(j));
                                List<Integer> newLoc = new ArrayList<>();
                                newLoc.add(possSpot1); newLoc.add(possSpot2);
                                if(!con.containsKey(varPair)){
                                    ArrayList<List<Integer>> newVal = new ArrayList<>();
                                    newVal.add(newLoc);
                                    con.put(varPair, newVal);
                                } else{
                                    con.get(varPair).add(newLoc);
                                }
                            }
                        }
                    }
                }
            }
        }
        return con;
    }

    public HashMap<List<Integer>, ArrayList<List<Integer>>> constraintToInt(HashMap<List<List<Integer>>, ArrayList<List<Integer>>> strcon){
        HashMap<List<Integer>, ArrayList<List<Integer>>> con = new HashMap<>();
        for(List<List<Integer>> key : strcon.keySet()){
            List<Integer> newKey = new ArrayList<>();
            newKey.add(varMap.get(key.get(0))); newKey.add(varMap.get(key.get(1)));
            con.put(newKey, strcon.get(key));
        }

        return con;
    }

    public boolean doPiecesFit(List<Integer> piece1, List<Integer> piece2, int spot1, int spot2){
        if(spot1 == spot2)
            return false;
        int w1 = piece1.get(1); int w2 = piece2.get(1);
        int h1 = piece1.get(0); int h2 = piece2.get(0);

        List<Integer> spotsInPiece1 = new ArrayList<>();
        List<Integer> spotsInPiece2 = new ArrayList<>();
//        System.out.println("Looking at piece: " + piece1 + " " + piece2 + " in spots: " + spot1 + " and " + spot2);

        int tempw1;
        while(h1 > 0){
            tempw1 = w1;
            while(tempw1 > 0){
                spotsInPiece1.add(spot1);
//                System.out.print(spot1 + " ");
                tempw1--;
                spot1++;
            }
            spot1 = spot1 - w1 + n;
            h1--;
        }

        int tempw2;
        while(h2 > 0){
            tempw2 = w2;
            while(tempw2 > 0){
                spotsInPiece2.add(spot2);
//                System.out.print(spot2 + " ");
                tempw2--;
                spot2++;
            }
            spot2 = spot2 - w2 + n;
            h2--;
        }

        if(Collections.disjoint(spotsInPiece1, spotsInPiece2)) {
//            System.out.println("FITS");
            return true;
        } else {
//            System.out.println("DOES NOT FIT");
            return false;
        }
    }
}
