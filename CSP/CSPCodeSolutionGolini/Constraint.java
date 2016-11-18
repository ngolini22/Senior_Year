import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Nicky on 10/26/16.
 *
 * Constraint wrapper class -> contains constraint hashmap and has isSatisfied function
 */
public class Constraint {
    HashMap<List<Integer>, ArrayList<List<Integer>>> conMap = new HashMap<>();


    public Constraint(HashMap<List<Integer>, ArrayList<List<Integer>>> con){
        this.conMap = con;
//        printConstraints(conMap);
    }


    // make use of ahsh code because arrays not working as hash key
    public int hashCode(int[] c){
        return c[1]*10 + c[0];
    }

    /**
     *
     *  return 0 if not satisfied
     *  return 1 if is satisfied and not complete solution
     *  return 2 if is complete solution
     *
     */

    public int isSatisfied(int[] assignment, boolean p, HashMap<Integer, List<Integer>> domain) {
         if(p) {
//             System.out.println("IN IS SATISFIED");
//             System.out.println("Domain: " + domain);
//             System.out.println("Assignment: " + assignment[0] + " " + assignment[1] + " " + assignment[2] + " " + assignment[3] + " " + assignment[4] + " " + assignment[5] + " " + assignment[6]);
             printAssignment(assignment);
         }
//       printConstraints(constraints);

         for (int i = 0; i < assignment.length; i++) {
            if (assignment[i] != -1) {
                for (int j = 0; j < assignment.length; j++) {
                    if (i != j && assignment[j] != -1) {
                        List<Integer> key = new ArrayList<>();
                        key.add(i);
                        key.add(j);
                        List<Integer> val = new ArrayList<>();
                        val.add(assignment[i]);
                        val.add(assignment[j]);
//                      System.out.println("Key: " +  key.get(0) + " " + key.get(1) + " Val: " + val[0] + " " + val[1]);
//                      System.out.println(key.get(0) + " " + key.get(1));
//                        System.out.println("VAL" + key);
                        if (!conMap.get(key).contains(val)) {
//                            System.out.println("\n Key: " + key + " Val: " + val.get(0) + val.get(1));
//                            System.out.println("\n Not in Table");
                            return 0;
                        }
                    }
                }
            }
         }

         if (allNegative(assignment)){
//             System.out.println("All var unassigned");
             return 0;
         }

         if (noNegative(assignment)){
             System.out.println("Complete Assignment Found!");
             return 2;
         }
         if(p) {
//             System.out.println("Solution passes!");
         }
        return 1;
    }

    private boolean noNegative(int[] assignment){
        for(int i = 0; i < assignment.length; i++){
            if(assignment[i] == -1){
                return false;
            }
        }
        return true;
    }

    private boolean allNegative(int[] assignment){
        for(int i = 0; i < assignment.length; i++){
            if(assignment[i] != -1){
                return false;
            }
        }
        return true;
    }

    private void printConstraints(HashMap<List<Integer>, ArrayList<List<Integer>>> con){

        for(List<Integer> currKey : con.keySet()){

            System.out.println("\n (" + currKey.get(0) + " " + currKey.get(1) + "): ");
            for(List<Integer> currVal : con.get(currKey)){
                System.out.print(" (" + currVal.get(0) + ", " + currVal.get(1) + "), ");
            }
        }

    }

    public void printAssignment(int[] assignment){
        System.out.print("\n ");
        for(int i = 0; i < assignment.length; i++){
            System.out.print(assignment[i] + " ");
        }
    }

//    // will this be just using numbers?
//    public boolean involves(){
//
//
//        return true;
//    }
}
