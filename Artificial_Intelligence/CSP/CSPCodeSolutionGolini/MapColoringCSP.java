import java.util.*;

/**
 * Created by Nicky on 10/26/16.
 *
 *          CSP problem for Map coloring
 */
public class MapColoringCSP extends CSP{
    HashMap<Integer, List<Integer>> domain;
    HashMap<String, Integer> varToStringMap = new HashMap<>();
    HashMap<Integer, Character> domMap = new HashMap<>();
    // used for inferences where different values may have different domains
    HashMap<List<String>, ArrayList<List<Integer>>> strConstraints = new HashMap<>();
    String[] variable_list;
    Constraint constraints;

    public MapColoringCSP(){
        this.variable_list = loadMap();
        loadVars();
        this.domain = setDomain();
        this.constraints = setConstraints();
        int[] assignment = {-1, -1, -1, -1, -1, -1, -1};
        startNode = new MapNode(assignment, domain, constraints, variable_list);
        System.out.println("VARIABLES: " + printVars());
        System.out.println("DOMAIN: " + domain);
        System.out.println("CONSTRAINTS: " + strConstraints);

    }

    public class MapNode implements CSPNode{
        int[] assignment;
        String[] variables;
        Constraint constraints;
        HashMap<Integer, List<Integer>> domain;

        public MapNode(int[] a, HashMap<Integer, List<Integer>> d, Constraint c, String[] v){
            this.assignment = a;
            this.domain = d;
            this.constraints = c;
            this.variables = v;
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
            return this.variables;
        }

        public String AssignToString(int[] assignment){
            String result= "";
            for(int i = 0; i < assignment.length; i++){
                result = result + variables[i] + ": " + domMap.get(this.assignment[i]) + "\n" ;
            }
            return result;
        }
    }

    public String[] loadMap(){
        String[] territories = {"WA", "NT", "Q", "NSW", "V", "SA", "T"};
        return territories;
    }

    public String printVars(){
        String res = "{";
        for(int i = 0; i < variable_list.length; i++){
            if(i != variable_list.length - 1)
                res = res + variable_list[i] + ", ";
            else
                res = res + variable_list[i];
        }
        res = res + "}";
        return res;
    }

    public void loadVars(){
        varToStringMap.put("WA", 0);
        varToStringMap.put("NT", 1);
        varToStringMap.put("Q", 2);
        varToStringMap.put("NSW", 3);
        varToStringMap.put("V", 4);
        varToStringMap.put("SA", 5);
        varToStringMap.put("T", 6);
    }


    public HashMap<Integer, List<Integer>> setDomain(){
        // 0 = red, 1 = green, 2 = blue
        HashMap<Integer, List<Integer>> varDomMap = new HashMap<>();
        int[] colors = {0, 1, 2};
        domMap.put(0, 'r');
        domMap.put(1, 'g');
        domMap.put(2, 'b');

        for(int v : varToStringMap.values()){ // values of map are integers between 0 and 6
            List<Integer> domains = new ArrayList<>();
            domains.add(0); domains.add(1); domains.add(2);
            varDomMap.put(v, domains);
        }
        return varDomMap;
    }

    // Takes in a hashmap of constraints with Strings and converts it all to ints
    public HashMap<List<Integer>, ArrayList<List<Integer>>> constraintsToInt(HashMap<List<String>, ArrayList<List<Integer>>> strCon){
        HashMap<List<Integer>, ArrayList<List<Integer>>> cons = new HashMap<>();

        for(List<String> curr : strCon.keySet()){
            List<Integer> key = new ArrayList<>();
            key.add(varToStringMap.get(curr.get(0)));  key.add(varToStringMap.get(curr.get(1)));
            cons.put(key, strCon.get(curr));
        }

//        System.out.println(cons);

        return cons;
    }

    public Constraint setConstraints(){
        // combination of colors for two neighbors
        ArrayList<List<Integer>> neighbors = new ArrayList<>();
        List<Integer> node = new ArrayList<>();
        node.add(0); node.add(1); neighbors.add(node); node = new ArrayList<>();
        node.add(0); node.add(2); neighbors.add(node); node = new ArrayList<>();
        node.add(1); node.add(0); neighbors.add(node); node = new ArrayList<>();
        node.add(1); node.add(2); neighbors.add(node); node = new ArrayList<>();
        node.add(2); node.add(0); neighbors.add(node); node = new ArrayList<>();
        node.add(2); node.add(1); neighbors.add(node); node = new ArrayList<>();

        List<String> t = new ArrayList<>();
        t.add("WA"); t.add("SA"); strConstraints.put(t, neighbors); t = new ArrayList<>();
        t.add("WA"); t.add("NT"); strConstraints.put(t, neighbors); t = new ArrayList<>();
        t.add("NT"); t.add("SA"); strConstraints.put(t, neighbors); t = new ArrayList<>();
        t.add("NT"); t.add("Q"); strConstraints.put(t, neighbors); t = new ArrayList<>();
        t.add("SA"); t.add("Q"); strConstraints.put(t, neighbors); t = new ArrayList<>();
        t.add("SA"); t.add("V"); strConstraints.put(t, neighbors); t = new ArrayList<>();
        t.add("Q"); t.add("NSW"); strConstraints.put(t, neighbors); t = new ArrayList<>();
        t.add("NSW"); t.add("V"); strConstraints.put(t, neighbors); t = new ArrayList<>();
        t.add("SA"); t.add("WA"); strConstraints.put(t, neighbors); t = new ArrayList<>();
        t.add("NT"); t.add("WA"); strConstraints.put(t, neighbors); t = new ArrayList<>();
        t.add("SA"); t.add("NT"); strConstraints.put(t, neighbors); t = new ArrayList<>();
        t.add("Q"); t.add("NT"); strConstraints.put(t, neighbors); t = new ArrayList<>();
        t.add("Q"); t.add("SA"); strConstraints.put(t, neighbors); t = new ArrayList<>();
        t.add("V"); t.add("SA"); strConstraints.put(t, neighbors); t = new ArrayList<>();
        t.add("NSW"); t.add("Q"); strConstraints.put(t, neighbors); t = new ArrayList<>();
        t.add("V"); t.add("NSW"); strConstraints.put(t, neighbors); t = new ArrayList<>();
        t.add("SA"); t.add("NSW"); strConstraints.put(t, neighbors); t = new ArrayList<>();
        t.add("NSW"); t.add("SA"); strConstraints.put(t, neighbors); t = new ArrayList<>();
        // combination of colors for
        ArrayList<List<Integer>> nonneighbors = new ArrayList<>();
        node = new ArrayList<>();
        node.add(0); node.add(0); nonneighbors.add(node); node = new ArrayList<>();
        node.add(0); node.add(1); nonneighbors.add(node); node = new ArrayList<>();
        node.add(0); node.add(2); nonneighbors.add(node); node = new ArrayList<>();
        node.add(1); node.add(0); nonneighbors.add(node); node = new ArrayList<>();
        node.add(1); node.add(1); nonneighbors.add(node); node = new ArrayList<>();
        node.add(1); node.add(2); nonneighbors.add(node); node = new ArrayList<>();
        node.add(2); node.add(0); nonneighbors.add(node); node = new ArrayList<>();
        node.add(2); node.add(1); nonneighbors.add(node); node = new ArrayList<>();
        node.add(2); node.add(2); nonneighbors.add(node); node = new ArrayList<>();

        t = new ArrayList<>();
        t.add("WA"); t.add("Q"); strConstraints.put(t, nonneighbors); t = new ArrayList<>();
        t.add("WA"); t.add("NSW"); strConstraints.put(t, nonneighbors); t = new ArrayList<>();
        t.add("WA"); t.add("V"); strConstraints.put(t, nonneighbors); t = new ArrayList<>();
        t.add("WA"); t.add("T"); strConstraints.put(t, nonneighbors); t = new ArrayList<>();
        t.add("NT"); t.add("NSW"); strConstraints.put(t, nonneighbors); t = new ArrayList<>();
        t.add("NT"); t.add("V"); strConstraints.put(t, nonneighbors); t = new ArrayList<>();
        t.add("NT"); t.add("T"); strConstraints.put(t, nonneighbors); t = new ArrayList<>();
        t.add("SA"); t.add("T"); strConstraints.put(t, nonneighbors); t = new ArrayList<>();
        t.add("Q"); t.add("WA"); strConstraints.put(t, nonneighbors); t = new ArrayList<>();
        t.add("Q"); t.add("V"); strConstraints.put(t, nonneighbors); t = new ArrayList<>();
        t.add("Q"); t.add("T"); strConstraints.put(t, nonneighbors); t = new ArrayList<>();
        t.add("NSW"); t.add("WA"); strConstraints.put(t, nonneighbors); t = new ArrayList<>();
        t.add("NSW"); t.add("NT"); strConstraints.put(t, nonneighbors); t = new ArrayList<>();
        t.add("NSW"); t.add("T"); strConstraints.put(t, nonneighbors); t = new ArrayList<>();
        t.add("V"); t.add("WA"); strConstraints.put(t, nonneighbors); t = new ArrayList<>();
        t.add("V"); t.add("NT"); strConstraints.put(t, nonneighbors); t = new ArrayList<>();
        t.add("V"); t.add("Q"); strConstraints.put(t, nonneighbors); t = new ArrayList<>();
        t.add("V"); t.add("T"); strConstraints.put(t, nonneighbors); t = new ArrayList<>();
        t.add("T"); t.add("WA"); strConstraints.put(t, nonneighbors); t = new ArrayList<>();
        t.add("T"); t.add("NT"); strConstraints.put(t, nonneighbors); t = new ArrayList<>();
        t.add("T"); t.add("SA"); strConstraints.put(t, nonneighbors); t = new ArrayList<>();
        t.add("T"); t.add("Q"); strConstraints.put(t, nonneighbors); t = new ArrayList<>();
        t.add("T"); t.add("NSW"); strConstraints.put(t, nonneighbors); t = new ArrayList<>();
        t.add("T"); t.add("V"); strConstraints.put(t, nonneighbors); t = new ArrayList<>();

//        printConstraints(strConstraints);
        Constraint con = new Constraint(constraintsToInt(strConstraints));

        return con;
    }

    public void printConstraints(HashMap<List<String>, ArrayList<List<Integer>>> con){
        for(List<String> currKey : con.keySet()){

            System.out.println("\n (" + currKey.get(0).toString() + " " + currKey.get(1).toString() + "): ");
            for(List<Integer> currVal : con.get(currKey)){
                System.out.print("(" + currVal.get(0) + ", " + currVal.get(1) + "), ");
            }
        }

    }

    public static void main(String[] args){
        MapColoringCSP newMap = new MapColoringCSP();
        newMap.BackTrackingSearch();
    }

}
