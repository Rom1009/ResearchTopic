import java.util.ArrayList;
import java.util.List;

public class Test {
    public static void main(String[] args) {
        UncertainDatabase database = new UncertainDatabase("data/T40I10D100K.txt", 0.79, Math.sqrt(0.61));

        PFIT P = new PFIT();
        PFMIoS PM = new PFMIoS();
        PFMIoSplus PMplus = new PFMIoSplus();

        wPFIT wP = new wPFIT();
        wPFMIoS wPM = new wPFMIoS();
        wPFMIoSplus wPMplus = new wPFMIoSplus();
        List<String> ro= new ArrayList<String>();
        ro.add("Root");
        // Define the batch size
        int batchSize = 10000;
        // Get the transaction lists
        List<UncertainTransaction> transactionLists = database.getTransactionLists();
        // Process transactions in batches
        List<UncertainTransaction> batch = transactionLists.subList(0, batchSize);
        for (int i =0; i <batch.size(); i++) {
            List<String> res = new ArrayList<String>();
            List<Double> res1 = new ArrayList<Double>();
            List<Double> res2 = new ArrayList<Double>();


            for (int j = 0; j < database.name.get(i).size(); j++){
                res.add(database.name.get(i).get(j));
                res1.add(database.prob.get(i).get(j));
                res2.add(database.weight.get(i).get(j));

            }
            database.name1.add(res);
            database.prob1.add(res1);
            database.weight1.add(res2);

        }

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
        long startTime = System.nanoTime();

        // P.Buildtree(root, batchSize, 0.9, 0.9);
        wP.Buildtree(root, batchSize, 0.1, 0.00001);
        
        long endTime = System.nanoTime();
        System.out.println("Execution Time: " + (endTime - startTime)/1_000_000.0 + " ms");
        // for (PFITNode child : new ArrayList<>(root.getChildren())){
        //     root.getChildren().add(child);
        // }

        // long startTime1 = System.nanoTime();
        // for (int i = batchSize ;i < transactionLists.size(); i++){
        //     database.name1.add(database.name.get(i));
        //     database.prob1.add(database.prob.get(i));
        //     database.weight1.add(database.weight.get(i));
        //     wPM.ADDTRANS(root, i, database, 0.01, 0.9);
        //     wPM.DelTran(root, i, database, 0.01, 0.9);
        //     // PM.ADDTRANS(root, i, database, 0.9, 0.1);
        //     // PM.DelTran(root, i, database, 0.9, 0.1);
        // }

        // long endTime1 = System.nanoTime();
        // System.out.println("PFMIoS Algorithm");
        // System.out.println("Execution Time: " + (endTime1 - startTime1)/1_000_000.0 + " ms");



        long startTime2 = System.nanoTime();
        for (int i = batchSize ;i < transactionLists.size(); i++){
            database.name1.add(database.name.get(i));
            database.prob1.add(database.prob.get(i));
            database.weight1.add(database.weight.get(i));
            wPMplus.ADDTRANS(root, i, database, 0.1, 0.00001);
            wPMplus.DelTran(root, i, database, 0.1, 0.00001);
            // PMplus.ADDTRANS(root, i, database, 0.9, 0.9);
            // PMplus.DelTran(root, i, database, 0.9, 0.9);

        }
        long endTime2 = System.nanoTime();
        System.out.println("PFMIoS+ Algorithm");
        System.out.println("Execution Time: " + (endTime2 - startTime2) / 1_000_000.0 + " ms");
        // for (PFITNode node: root.getChildren()){
        //     System.out.println(node);
        // }
    }
}