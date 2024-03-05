import java.util.ArrayList;
import java.util.List;

public class Test {
    public static void main(String[] args) {
        UncertainDatabase database = new UncertainDatabase("Gazeele.txt", 0.5, Math.sqrt(0.58));

        PFIT P = new PFIT();
        PFMIoS PM = new PFMIoS();
        PFMIoSplus PMplus = new PFMIoSplus();


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
        PFITNode root = new PFITNode(ro, database);
        // // Compute distinct items for the current batch
        // // Process distinct items
        List<List<String>> distinctItems = database.computeDistinctItemForBatch(batch);
        for (int i = 0; i < batchSize; i += batchSize) {
            for (List<String> distinctItem : distinctItems) {
                // Create a new root node for the current distinct item
                PFITNode newNode = new PFITNode(distinctItem, database);
                // Add the new node as a child to the root
                root.addChild(newNode);
            }
        }
        P.Buildtree(root, batchSize, 0.9, 0.9);
        
        long endTime = System.nanoTime();
        System.out.println("Execution Time: " + (endTime - startTime)/1_000_000.0 + " ms");

        long startTime1 = System.nanoTime();
        for (int i = batchSize ;i < transactionLists.size(); i++){
            database.name1.add(database.name.get(i));
            database.prob1.add(database.prob.get(i));
            PM.ADDTRANS(root, i, database, 0.9, 0.9);
            PM.DelTran(root, i, database, 0.9, 0.9);
        }

        long endTime1 = System.nanoTime();
        System.out.println("PFMIoS Algorithm");
        System.out.println("Execution Time: " + (endTime1 - startTime1)/1_000_000.0 + " ms");



        long startTime2 = System.nanoTime();
        for (int i = batchSize ;i < transactionLists.size(); i++){
            database.name1.add(database.name.get(i));
            database.prob1.add(database.prob.get(i));
            PMplus.ADDTRANS(root, i, database, 0.9, 0.9);
            PMplus.DelTran(root, i, database, 0.9, 0.9);
        }

        long endTime2 = System.nanoTime();
        System.out.println("PFMIoS+ Algorithm");
        System.out.println("Execution Time: " + (endTime2 - startTime2) / 1_000_000.0 + " ms");

        System.out.println("Result");


    }
}
