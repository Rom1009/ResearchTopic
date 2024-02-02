import java.util.ArrayList;
import java.util.List;

public class Test {
    public static void main(String[] args) {
        UncertainDatabase database = new UncertainDatabase();

        PFIT P = new PFIT();
        PFMIoS PM = new PFMIoS();

        List<List<String>> items = database.distinctItem();
        List<String> ro= new ArrayList<String>();
        ro.add("Root");
        PFITNode root = new PFITNode(ro, database);
        for (List<String> item: items) {   
            root.addChild(new PFITNode(item, database));
        }
        P.Buildtree(root, 0, 4, 0.1);
        
        
        
        // List<String> item = new ArrayList<String>();
        // List<Double> prob = new ArrayList<Double>();
        // item.add("B");
        // item.add("C");
        // item.add("D");
        // item.add("E");
        // prob.add(1.0);
        // prob.add(0.7);
        // prob.add(0.9);
        // prob.add(0.8);
        // database.addNewTransaction(item, prob);
        // ReadDataFile rf = new ReadDataFile();
        // try {
        //     rf.readDataWithProbabilities("b.txt");
        // }
        // catch (Exception e){
        //     e.printStackTrace();
        // }
        // for (int i = 0; i < rf.getName().size(); i++) {
        //     database.addNewTransaction(rf.getName().get(i), rf.getProbs().get(i));
        // }
        // PM.ADDTRANS(root, 0, database, 4, 0.1);
        // database.transactionLists.remove(0);

        // PM.DelTran(root,0, database, 4, 0.1);
    }
}
