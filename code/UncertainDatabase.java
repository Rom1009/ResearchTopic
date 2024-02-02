import java.util.*;

class UncertainItem {
    String name;
    double probability;

    UncertainItem(String name, double probability) {
        this.name = name;
        this.probability = probability;
    }
}

class UncertainItemset {
    List<UncertainItem> uncertainitem;

    UncertainItemset(List<UncertainItem> uncertainitem) {
        this.uncertainitem = uncertainitem;
    }
}

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
    public List<List<String>> name;
    public List<List<Double>> prob;

    public UncertainDatabase() {
        transactionLists = new ArrayList<>();
        try {
            // Assuming ReadDataFile class reads data and initializes name and prob
            // with corresponding values from the file.
            ReadDataFile rf = new ReadDataFile();
            rf.readDataWithProbabilities("file.txt");
            name = rf.getName();
            prob = rf.getProbs();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<UncertainTransaction> getTransactionLists() {
        int size = name.size();
        transactionLists = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            List<String> itemNames = name.get(i);
            List<Double> itemProbs = prob.get(i);
            int itemSize = itemNames.size();

            List<UncertainItem> itemLists = new ArrayList<>(itemSize);
            for (int j = 0; j < itemSize; j++) {
                itemLists.add(new UncertainItem(itemNames.get(j), itemProbs.get(j)));
            }

            UncertainItemset uncertainItems = new UncertainItemset(itemLists);
            UncertainTransaction uncertainTransaction = new UncertainTransaction("UT" + i, uncertainItems);
            transactionLists.add(uncertainTransaction);
        }

        return transactionLists;
    }

    public void addNewTransaction(List<String> titles, List<Double> probs) {
        int size = titles.size();

        if (size != probs.size()) {
            System.out.println("Error: Unequal lengths of names and probabilities");
            return;
        }

        name.add(titles);
        prob.add(probs);
        List<UncertainItem> itemLists = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            itemLists.add(new UncertainItem(titles.get(i), probs.get(i)));
        }

        UncertainItemset uncertainItems = new UncertainItemset(itemLists);
        UncertainTransaction uncertainTransaction = new UncertainTransaction("UT" + size, uncertainItems);
        transactionLists.add(uncertainTransaction);
    }

    public List<List<String>> distinctItem() {
        Set<String> distinctSet = new LinkedHashSet<>();

        for (List<String> row : name) {
            distinctSet.addAll(row);
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
