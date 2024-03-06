import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/*
 * Class UncertainItem
 * Description: Create 
 */
class UncertainItem {
    String name;
    double probability;

    UncertainItem(String name, double probability) {
        this.name = name;
        this.probability = probability;
    }
}

/*
 * 
 */
class UncertainItemset {
    List<UncertainItem> uncertainItems;

    UncertainItemset(List<UncertainItem> uncertainItems) {
        this.uncertainItems = uncertainItems;
    }
}

/*
 * 
 */
class UncertainTransaction {
    String id;
    UncertainItemset uncertainItemset;

    UncertainTransaction(String id, UncertainItemset uncertainItemset) {
        this.id = id;
        this.uncertainItemset = uncertainItemset;
    }
}


public class UncertainDatabase {

    public List<UncertainTransaction> transactionLists;
    public List<List<String>> name = new ArrayList<List<String>>();
    public List<List<String>> name1 = new ArrayList<List<String>>();

    public List<List<Double>> prob =  new ArrayList<List<Double>>();
    public List<List<Double>> prob1 =  new ArrayList<List<Double>>();

    public List<List<Double>> weight =  new ArrayList<List<Double>>();
    public List<List<Double>> weight1 =  new ArrayList<List<Double>>();



    public UncertainDatabase(String path, double mean, double std) {
        
        try {
            // Assuming ReadDataFile class reads data and initializes name and prob
            // with corresponding values from the file.
            ReadDataFile rf = new ReadDataFile();
            rf.readDataWithProbabilities(path, mean, std);
            name = rf.getName();
            prob = rf.getProbs();
            weight = rf.getWeight();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    * 
    */
    public List<UncertainTransaction> getTransactionLists() {
        transactionLists = new ArrayList<>();
        int size = name.size();

        for (int i = 0; i < size; i++) {
            List<String> itemNames = name.get(i);
            List<Double> itemProbs = prob.get(i);
            int itemSize = itemNames.size();

            List<UncertainItem> itemList = new ArrayList<>(itemSize);
            for (int j = 0; j < itemSize; j++) {
                itemList.add(new UncertainItem(itemNames.get(j), itemProbs.get(j)));
            }

            UncertainItemset uncertainItems = new UncertainItemset(itemList);
            UncertainTransaction uncertainTransaction = new UncertainTransaction("UT" + i, uncertainItems);
            transactionLists.add(uncertainTransaction);
        }

        return transactionLists;
    }

    /*
    * 
    */
    public void addNewTransaction(List<String> titles, List<Double> probs) {
        int size = titles.size();

        if (size != probs.size()) {
            System.out.println("Error: Unequal lengths of names and probabilities");
            return;
        }

        name1.add(titles);
        prob1.add(probs);
    }

    /*
    * 
    */
    public List<List<String>> computeDistinctItemForBatch(List<UncertainTransaction> batch) {
        Set<String> distinctSet = new LinkedHashSet<>();
    
        for (UncertainTransaction transaction : batch) {
            UncertainItemset itemset = transaction.uncertainItemset;
            List<UncertainItem> itemList = itemset.uncertainItems;
            for (UncertainItem item : itemList) {
                distinctSet.add(item.name);
            }
        }
    
        List<List<String>> distinctList = new ArrayList<>(distinctSet.size());
        for (String item : distinctSet) {
            List<String> itemList = new ArrayList<>(1);
            itemList.add(item);
            distinctList.add(itemList);
        }
    
        return distinctList;
    }
    
}
