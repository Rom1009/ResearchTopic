import java.util.ArrayList;
import java.util.List;

public class Test {
    public static void main(String[] args) {
        UncertainDatabase database = new UncertainDatabase();

        PFIT P = new PFIT();
        PFMIoS PM = new PFMIoS();

        List<String> ro= new ArrayList<String>();
        ro.add("Root");
        
        // Define the batch size
        int batchSize = 10000;
        // Get the transaction lists
        List<UncertainTransaction> transactionLists = database.getTransactionLists();
        // Process transactions in batches
        List<UncertainTransaction> batch = transactionLists.subList(0, batchSize);
        for (UncertainTransaction transaction : batch) {
            List<String> res = new ArrayList<String>();
            List<Double> res1 = new ArrayList<Double>();

            for (int i = 0; i < transaction.uncertainItemset.uncertainItems.size(); i++){
                res.add(transaction.uncertainItemset.uncertainItems.get(i).name);
                res1.add(transaction.uncertainItemset.uncertainItems.get(i).probability);

            }
            database.name1.add(res);
            database.prob1.add(res1);
        }

        long startTime = System.nanoTime();
        PFITNode root = new PFITNode(ro, database, database.name1);
        // // Compute distinct items for the current batch
        // // Process distinct items
        List<List<String>> distinctItems = database.computeDistinctItemForBatch(batch);
        for (int i = 0; i < batchSize; i += batchSize) {
            for (List<String> distinctItem : distinctItems) {
                // Create a new root node for the current distinct item
                PFITNode newNode = new PFITNode(distinctItem, database, database.name1);
                // Add the new node as a child to the root
                root.addChild(newNode);
            }
        }
        P.Buildtree(root, batchSize, 0.9, 0.9);
        
        long endTime = System.nanoTime();
        System.out.println("Execution Time: " + (endTime - startTime) + " nanoseconds");

        long startTime1 = System.nanoTime();
        for (int i = batchSize ;i < transactionLists.size(); i++){
            database.name1.add(database.name.get(i));
            database.prob1.add(database.prob.get(i));
            PM.ADDTRANS(root, i, database, 0.9, 0.9);
            PM.DelTran(root, i, database, 0.9, 0.9);
        }

        long endTime1 = System.nanoTime();
        System.out.println("Execution Time: " + (endTime1 - startTime1) + " nanoseconds");

        System.out.println(database.name1.size());
        // List<List<String>> items = database.distinctItem();
        
        // database.getTransactionLists();
        // for (List<String> item: items) {   
        //     root.addChild(new PFITNode(item, database));
        // }
        // P.Buildtree(root, 0, 1, 0.9);
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
        // for (int i = 0; i < rf.getName().size(); i++) {
        //     database.addNewTransaction(rf.getName().get(i), rf.getProbs().get(i));
        // }
        // PM.ADDTRANS(root, 0, database, 4, 0.1);
        
        // PM.DelTran(root,0, database, 4, 0.1);
        // for (PFITNode child : root.getChildren()) {
        //     System.out.println(child);
        // }
        
    }
}
