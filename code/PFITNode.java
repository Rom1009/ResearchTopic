import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;



public class PFITNode {
    private List<String> itemset;
    private double sup;
    private double esup;
    private double psup;
    private double lb;
    private double ub;
    private PFITNode parent;
    private List<PFITNode> children;
    public UncertainDatabase database;
    public List<List<String>> batch;


    public PFITNode(List<String> itemset,UncertainDatabase database, List<List<String>> batch) {
        this.itemset = itemset;
        this.children = new ArrayList<>();
        this.database = database;
        this.batch = batch;
        this.sup = 0.0;
        this.esup = 0.0;
        this.psup = 0.0;
        this.lb = 0.0;
        this.ub = 0.0;        
    }
    
    public List<PFITNode> getRightSiblings() {
        if (parent == null) {
            return new ArrayList<>();
        }
    
        int sizeOfCurrentNode = this.getItems().size(); // Giả sử 'size' là phương thức trả về kích cỡ của node
        List<PFITNode> rightSiblings = new ArrayList<>();
        boolean found = false;
    
        for (PFITNode sibling : parent.getChildren()) {
            if (sibling == this) {
                found = true;
            } else if (found && sibling.getItems().size() == sizeOfCurrentNode) {
                rightSiblings.add(sibling);
            }
        }
    
        return rightSiblings;
    }

    // Additional methods as required

    public void addChild(PFITNode child) {
        int existingChildIndex = indexOfChildWithItemset(child.getItems());
        if (existingChildIndex != -1) {
            // Replace the existing child
            this.children.set(existingChildIndex, child);
        } else {
            // Add as a new child
            this.children.add(child);
        }
        child.setParent(this);
    }
    
    private int indexOfChildWithItemset(List<String> itemset) {
        for (int i = 0; i < children.size(); i++) {
            if (new HashSet<>(children.get(i).getItems()).equals(new HashSet<>(itemset))) {
                return i;
            }
        }
        return -1; // Return -1 if no match is found
    }

    public List<PFITNode> getChildren() {
        return this.children;
    }

    public List<String> getItems(){
        return itemset;
    }

    public PFITNode getParent() {
        return parent;
    }

    private void setParent(PFITNode parent) {
        this.parent = parent;
    }

    public double getSupport() {
        return sup;
    }

    public void setSupport(double sup) {
        this.sup = sup;
    }

    public double getExpSup() {
        return esup;
    }

    public void setExpSup(double esup) {
        this.esup = esup;
    }

    public double getProb() {
        return psup;
    }

    public void setProb(double psup) {
        this.psup = psup;
    }

    public double getLB() {
        return lb;
    }

    public void setLB(double lb) {
        this.lb = lb;
    }

    public double getUB() {
        return ub;
    }

    public void setUB(double ub) {
        this.ub = ub;
    }

    @Override
    public String toString() {
        return "PFITNode{" +
                "itemset=" + itemset +
                ", sup=" + sup +
                ", esup=" + esup +
                ", psup=" + psup +
                ", lb=" + lb +
                ", ub=" + ub +
                '}';
    }

    public PFITNode generateChildNode(PFITNode nY) {
        // Use a HashSet for better performance
        Set<String> combinedItemset = new HashSet<>(this.itemset);
        combinedItemset.addAll(nY.itemset);
    
        // Convert the set back to a list
        List<String> newItemset = new ArrayList<>(combinedItemset);
        
    
        // Create a new node with the combined, unique itemset
        PFITNode childNode = new PFITNode(newItemset, database, batch);

        // childNode.setSupport(Supporteds(newItemset));

        // Check if the child node already exists
        if (!isChildNodeExists(childNode)) {
            return childNode;
        } else {
            return null; // Return null if the child node already exists
        }
    }
    
    private boolean isChildNodeExists(PFITNode childNode) {
        return children.contains(childNode);
    }


    private Double cachedUBsResult; // Cache result for optimization

    public boolean isFrequent(double minisup, double ub) {
        if (cachedUBsResult == null) {
            cachedUBsResult = ub;
        }
        return cachedUBsResult >= minisup;
    }
    
    public boolean isSingleElementSubset(List<String> name, List<String> items) {
        // If the size of 'name' is greater than 'items', 'name' cannot be a subset of 'items'
        if (name.size() > items.size()) {
            return false;
        }
        
        Set<String> itemSet = new HashSet<>(items);
    
        // Iterate over each element in 'name'
        for (String element : name) {
            // Check if the current element is not in the HashSet
            if (!itemSet.contains(element)) {
                return false;
            }
        }
        // If all elements of 'name' are found in 'items', return true
        return true;
    }
    
    public boolean checkProb(double lb, double ub, double minisup){
        return lb <= minisup && ub >= minisup;
    }

    public boolean checkFrequenDel(double OLB, double OUB, double OPS, double ULB, double UUB, double UPS, double minisupp){
        boolean ans1 = OLB < minisupp && OUB >= minisupp && ULB < minisupp && UUB >= minisupp && OPS >= minisupp && UPS >= minisupp;
        boolean ans2 = OLB >= minisupp && ULB >= minisupp;
        boolean ans3 = OLB >= minisupp && ULB < minisupp;
       
        return ans1 || ans2 || ans3;
    }
    
    

    public boolean checkInfrequent(double OLB, double OUB, double OPS, double ULB, double UUB, double UPS, double minisupp){
        boolean ans1 = OLB < minisupp && OUB >= minisupp && ULB < minisupp && UUB >= minisupp && OPS >= minisupp && UPS < minisupp;
        boolean ans2 = OLB < minisupp && OUB >= minisupp && UUB < minisupp && OPS >= minisupp;
        boolean ans3 = OUB >= minisupp && ULB < minisupp && UPS < minisupp;
       
        return ans1 || ans2 || ans3;
    }

    public boolean checkNewFrequent(double OLB, double OUB, double OPS, double ULB, double UUB, double UPS, double minisupp){
        boolean ans1 = OLB < minisupp && OUB >= minisupp && ULB < minisupp && UUB >= minisupp && OPS < minisupp && UPS >= minisupp;
        boolean ans2 = OLB < minisupp && OUB >= minisupp && ULB >= minisupp && OPS < minisupp;
        boolean ans3 = OUB < minisupp && UUB >= minisupp && UPS >= minisupp;
       
        return ans1 || ans2 || ans3;
    }

    public boolean checkFrequent(double OLB, double OUB, double OPS, double ULB, double UUB, double UPS, double minisupp){
        boolean ans1 = OLB < minisupp && OUB >= minisupp && ULB < minisupp && UUB >= minisupp && OPS >= minisupp;
        boolean ans2 = OLB < minisupp && OUB >= minisupp && ULB >= minisupp && OPS >= minisupp;
        boolean ans3 = OLB >= minisupp;
        return ans1 || ans2 || ans3; 
    }

    // public double Supporteds(List<String> requiredItems) {
    //     return database.transactionLists.stream()
    //             .filter(a -> a.uncertainItemset.uncertainitem.stream().map(t -> t.name).collect(Collectors.toList()).containsAll(requiredItems))
    //             .count();
    // }
    // public double Supporteds(List<String> requiredItems, List<List<String>> name1) {
        
    //     return name1.stream().filter(transaction -> transaction.containsAll(requiredItems))
    //     .count();
    // }

    public double Supporteds(List<String> requiredItems, List<List<String>> name1) {
        int count = 0;
        for (List<String> transaction : name1) {
            if (transaction.containsAll(requiredItems)) {
                count++;
            }
        }
        return count;
    }
    
    public double ExpSups(List<String> requiredItems, List<List<String>> name1, List<List<Double>> prob1) {
        double sum = 0.0;
        for (int i = 0; i < name1.size(); i++) {
            List<String> transactionItemNames = name1.get(i);
            if (transactionItemNames.containsAll(requiredItems)) {
                double transactionProbability = 1.0;
                for (String item : requiredItems) {
                    int index = transactionItemNames.indexOf(item);
                    transactionProbability *= (index != -1) ? prob1.get(i).get(index) : 1.0;
                }
                sum += transactionProbability;
            }
        }
        return sum;
    }
    
    public double ProbabilityFrequents(List<String> requiredItems, double minValue, List<List<String>> name1, List<List<Double>> prob1) {
        int count = 0;
        for (int i = 0; i < name1.size(); i++) {
            if (name1.get(i).containsAll(requiredItems)) {
                double transactionProbability = 1.0;
                for (int j = 0; j < requiredItems.size(); j++) {
                    int index = name1.get(i).indexOf(requiredItems.get(j));
                    double probability = prob1.get(i).get(index);
                    if (requiredItems.contains(name1.get(i).get(index)) && probability >= minValue) {
                        transactionProbability *= probability;
                    } else {
                        transactionProbability = 0.0; // Set probability to 0 if any item's probability is less than minValue
                        break;
                    }
                }
                if (transactionProbability > 0) {
                    count++;
                }
            }
        }
        return count;
    }


    // public double ExpSups(List<String> requiredItems ) {
    //     return batch.stream()
    //             .filter(a -> a.uncertainItemset.uncertainItems.stream().map(t -> t.name).collect(Collectors.toList()).containsAll(requiredItems))
    //             .mapToDouble(transaction ->
    //                     transaction.uncertainItemset.uncertainItems.stream()
    //                             .filter(t -> requiredItems.contains(t.name))
    //                             .mapToDouble(t -> t.probability)
    //                             .reduce(1, (acc, probability) -> acc * probability)
    //             )
    //             .sum();
    // }

    // public double ExpSups(List<String> requiredItems, List<List<String>> name1, List<List<Double>> prob1) {
    //     return IntStream.range(0, name1.size())
    //         .filter(i -> {
    //             List<String> transactionItemNames = name1.get(i);
    //             return transactionItemNames.containsAll(requiredItems);
    //         })
    //         .mapToDouble(i -> {
    //             double transactionProbability = IntStream.range(0, requiredItems.size())
    //                     .mapToDouble(j -> {
    //                         int index = name1.get(i).indexOf(requiredItems.get(j));
    //                         return (index != -1) ? prob1.get(i).get(index) : 1.0;
    //                     })
    //                     .reduce(1, (acc, probability) -> acc * probability);

    //             return transactionProbability;
    //         })
    //         .sum();
    // }

    // public double ProbabilityFrequents(List<String> requiredItems, double minValue) {
    //     return batch.stream()
    //         .filter(transaction -> transaction.uncertainItemset.uncertainItems.stream()
    //                 .map(t -> t.name)
    //                 .collect(Collectors.toList())
    //                 .containsAll(requiredItems))
    //         .mapToDouble(transaction ->
    //                 transaction.uncertainItemset.uncertainItems.stream()
    //                         .filter(t -> requiredItems.contains(t.name) && t.probability >= minValue)
    //                         .mapToDouble(t -> t.probability)
    //                         .reduce(1, (acc, probability) -> acc * probability)
    //         )
    //         .count();
    // }

    // public double ProbabilityFrequents(List<String> requiredItems, double minValue, List<List<String>> name1, List<List<Double>> prob1) {
    //     return IntStream.range(0, name1.size())
    //             .filter(i -> name1.get(i).containsAll(requiredItems))
    //             .mapToDouble(i ->
    //                     prob1.get(i).stream()
    //                             .filter(probability -> requiredItems.contains(name1.get(i).get(prob1.get(i).indexOf(probability))) && probability >= minValue)
    //                             .reduce(1.0, (acc, probability) -> acc * probability)
    //             )
    //             .filter(result -> result > 0)  // Consider only transactions with non-zero probability
    //             .count();
    // }


    // public double LBs(List<String> requiredItems, double value) {
    //     double expSups = ExpSups(requiredItems);
    //     double sqrtTerm = Math.sqrt(-2 * expSups * Math.log(1 - value));
    
    //     double maxLB = database.getTransactionLists().stream()
    //         .filter(a -> a.uncertainItemset.uncertainitem.stream().anyMatch(t -> requiredItems.contains(t.name)))
    //         .mapToDouble(t -> Math.round((expSups - sqrtTerm) * 10.0) / 10.0)
    //         .max()
    //         .orElse(0.0);
    
    //     return Math.max(maxLB, 0);
    // }
    // public double LBs(List<String> requiredItems, double value) {
    //     double expSups = ExpSups(requiredItems);
    //     double sqrtTerm = Math.sqrt(-2 * expSups * Math.log(1 - value));
    
    //     double maxLB = IntStream.range(0, database.name.size())
    //             .filter(i -> database.name.get(i).stream().anyMatch(t -> requiredItems.contains(t)))
    //             .mapToDouble(i -> Math.round((expSups - sqrtTerm) * 10.0) / 10.0)
    //             .max()
    //             .orElse(0.0);
    
    //     return Math.max(maxLB, 0);
    // }

    public double LBs(double expectedSupport, double miniprob) {
        double v = Math.sqrt(-2 * expectedSupport * Math.log(1 - miniprob));
        double lowerBound = expectedSupport - v;
        return Max(lowerBound, 0);
    }

    // Upper Bound (ub)
    public double UBs(double expectedSupport, double miniprob, double support) {
        double upperBound = 2 * expectedSupport - Math.log(miniprob) + Math.sqrt(Math.log(2 * miniprob) - 8 * expectedSupport * Math.log(miniprob)) / 2;
        return Min(upperBound, support);
    }
    

    // public double UBs(List<String> requiredItems, double value) {
    //     double expSups = ExpSups(requiredItems);
    //     double logValue = Math.log(value);
    //     double sqrtTerm = Math.sqrt(Math.pow(logValue, 2) - 8 * expSups * logValue);
    
    //     OptionalDouble optionalMax = database.getTransactionLists().stream()
    //             .filter(a -> a.uncertainItemset.uncertainitem.stream().anyMatch(t -> requiredItems.contains(t.name)))
    //             .mapToDouble(t -> Math.round(((2 * expSups - logValue + sqrtTerm) / 2) * 10.0) / 10.0)
    //             .max();
    
    //     double v = optionalMax.orElse(0.0); // Giả sử 0.0 là giá trị mặc định phù hợp
    //     return Math.min(v, Supporteds(requiredItems));
    // }

    // public double UBs(List<String> requiredItems, double value) {
    //     double expSups = ExpSups(requiredItems);
    //     double logValue = Math.log(value);
    //     double sqrtTerm = Math.sqrt(Math.pow(logValue, 2) - 8 * expSups * logValue);
    
    //     OptionalDouble optionalMax = IntStream.range(0, database.name.size())
    //             .filter(i -> database.name.get(i).stream().anyMatch(t -> requiredItems.contains(t)))
    //             .mapToDouble(i -> Math.round(((2 * expSups - logValue + sqrtTerm) / 2) * 10.0) / 10.0)
    //             .max();
    
    //     double v = optionalMax.orElse(0.0); // Giả sử 0.0 là giá trị mặc định phù hợp
    //     return Math.min(v, Supporteds(requiredItems));
    // }
    
    

    private double Max(double a, double b) {
        return a > b? a : b;
    }

    private double Min(double a, double b) {
        return a < b? a : b;
    }
}
