import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents an uncertain item with a name and associated probability.
 */
class UncertainItem {
    String name;
    double probability;

    /**
     * Constructor for UncertainItem.
     * 
     * Name: UncertainItem
     * Input: String name (the name of the item), double probability (the probability of the item)
     * Output: An instance of UncertainItem
     * Description: Initializes an instance of UncertainItem with given name and probability.
     * Complexity: O(1)
     */
    UncertainItem(String name, double probability) {
        this.name = name;
        this.probability = probability;
    }
}

/**
 * Represents a set of uncertain items.
 */
class UncertainItemset {
    List<UncertainItem> uncertainItems;

    /**
     * Constructor for UncertainItemset.
     * 
     * Name: UncertainItemset
     * Input: List<UncertainItem> uncertainItems (the list of uncertain items)
     * Output: An instance of UncertainItemset
     * Description: Initializes an instance of UncertainItemset with the given list of uncertain items.
     * Complexity: O(1)
     */
    UncertainItemset(List<UncertainItem> uncertainItems) {
        this.uncertainItems = uncertainItems;
    }
}

/**
 * Represents a transaction containing uncertain items.
 */
class UncertainTransaction {
    String id;
    UncertainItemset uncertainItemset;

    /**
     * Constructor for UncertainTransaction.
     * 
     * Name: UncertainTransaction
     * Input: String id (the transaction ID), UncertainItemset uncertainItemset (the set of uncertain items)
     * Output: An instance of UncertainTransaction
     * Description: Initializes an instance of UncertainTransaction with a unique ID and a set of uncertain items.
     * Complexity: O(1)
     */
    UncertainTransaction(String id, UncertainItemset uncertainItemset) {
        this.id = id;
        this.uncertainItemset = uncertainItemset;
    }
}

/**
 * Represents a database of uncertain transactions.
 */
public class UncertainDatabase {

    public List<UncertainTransaction> transactionLists;
    public List<List<String>> name = Collections.synchronizedList(new ArrayList<>());
    public List<List<String>> name1 = Collections.synchronizedList(new ArrayList<>());
    public List<List<Double>> prob = Collections.synchronizedList(new ArrayList<>());
    public List<List<Double>> prob1 = Collections.synchronizedList(new ArrayList<>());
    public List<List<Double>> weight = Collections.synchronizedList(new ArrayList<>());
    public List<List<Double>> weight1 = Collections.synchronizedList(new ArrayList<>());

    /**
     * Constructor for UncertainDatabase.
     * 
     * Name: UncertainDatabase
     * Input: String path (path to data file), double mean (mean value for processing), double std (standard deviation for processing)
     * Output: An instance of UncertainDatabase
     * Description: Initializes an UncertainDatabase instance and populates it with data from a specified file.
     * Complexity: O(n), where n is the number of items in the data file.
     */
    public UncertainDatabase(String path, double mean, double std) {
        try {
            ReadDataFile rf = new ReadDataFile();
            rf.readDataWithProbabilities(path, mean, std);
            name = rf.getName();
            prob = rf.getProbs();
            weight = rf.getWeight();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Generates a list of uncertain transactions.
     * 
     * Name: getTransactionLists
     * Input: None
     * Output: List<UncertainTransaction> (the list of uncertain transactions)
     * Description: Creates a list of uncertain transactions based on the data contained in this database.
     * Complexity: O(m * n), where m is the number of transactions and n is the average number of items per transaction.
     */
    public List<UncertainTransaction> getTransactionLists() {
        transactionLists = Collections.synchronizedList(new ArrayList<>());
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

    /**
     * Adds a new transaction to the database.
     * 
     * Name: addNewTransaction
     * Input: List<String> titles (item names), List<Double> probs (item probabilities)
     * Output: None
     * Description: Adds a new transaction with the specified titles and probabilities to the database.
     * Complexity: O(1)
     */
    public void addNewTransaction(List<String> titles, List<Double> probs) {
        if (titles.size() != probs.size()) {
            System.out.println("Error: Unequal lengths of names and probabilities");
            return;
        }

        name1.add(titles);
        prob1.add(probs);
    }

    /**
     * Computes a distinct set of items from a batch of transactions.
     * 
     * Name: computeDistinctItemForBatch
     * Input: List<UncertainTransaction> batch (the batch of transactions)
     * Output: List<List<String>> (a list of distinct item names)
     * Description: Computes a distinct set of item names from a batch of uncertain transactions.
     * Complexity: O(p + q), where p is the total number of items in the batch and q is the number of distinct items.
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
