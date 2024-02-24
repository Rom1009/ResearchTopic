import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    public List<PFITNode> getRightSiblings() {
        if (parent == null) {
            return new ArrayList<>();
        }

        int sizeOfCurrentNode = this.itemset.size();
        List<PFITNode> rightSiblings = new ArrayList<>();
        int currentIndex = parent.getChildren().indexOf(this);

        for (int i = currentIndex + 1; i < parent.getChildren().size(); i++) {
            PFITNode sibling = parent.getChildren().get(i);
            if (sibling.getItems().size() == sizeOfCurrentNode) {
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

    public PFITNode generateChildNode(PFITNode nY) {
        // Use a HashSet for better performance
        Set<String> combinedItemset = new HashSet<>(this.itemset);
        combinedItemset.addAll(nY.itemset);
    
        // Convert the set back to a list
        List<String> newItemset = new ArrayList<>(combinedItemset);
        
        // Create a new node with the combined, unique itemset
        PFITNode childNode = new PFITNode(newItemset, database, batch);


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
        // Không thể là tập con nếu 'name' có nhiều phần tử hơn 'items'
        if (name.size() > items.size()) {
            return false;
        }
        
        // Nếu 'name' chỉ có một phần tử, ta chỉ cần kiểm tra xem nó có tồn tại trong 'items' không
        if (name.size() == 1) {
            return items.contains(name.get(0));
        }
        
        // Dùng containsAll() trực tiếp từ List; không cần chuyển đổi sang HashSet
        // Điều này lợi dụng độ phức tạp tốt hơn trong trường hợp 'name' chỉ có một phần tử
        return items.containsAll(name);
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
    
    public double Probability(double sup, double esup, double miniprob){
        double a = (2 * Math.sqrt(-2*esup*Math.log(1-miniprob)) -Math.log(miniprob) + Math.sqrt(Math.pow(Math.log(miniprob), 2) - 8*esup*Math.log(miniprob)))/2*batch.size();
        double b = (2 * esup - Math.log(miniprob) + Math.sqrt(Math.pow(Math.log(miniprob), 2) - 8*esup*Math.log(miniprob)))/2*batch.size();
        double c = (sup - esup + Math.sqrt(-2*esup*Math.log(1-miniprob))) / batch.size();
        double d = sup / batch.size();
        return findMin(a, b, c, d); 
    }

    private double findMin(double a, double b, double c, double d) {
        double min = a;
        if (b < min) {
            min = b;
        }
        if (c < min) {
            min = c;
        }
        if (d < min) {
            min = d;
        }
        return min;
    }

    public double ProbabilityFrequents(List<String> requiredItems, double minValue, List<List<String>> name1, List<List<Double>> prob1) {
        int count = 0;
        for (int i = 0; i < name1.size(); i++) {
            if (name1.get(i).containsAll(requiredItems)) {
                double transactionProbability = 1.0;
                int index = name1.get(i).indexOf(requiredItems.get(0));
                double probability = prob1.get(i).get(index);
                if (requiredItems.contains(name1.get(i).get(index)) && probability >= minValue) {
                    transactionProbability *= probability;
                } else {
                    transactionProbability = 0.0; // Set probability to 0 if any item's probability is less than minValue
                    break;
                }
                if (transactionProbability > 0) {
                    count++;
                }
            }
        }
        return count;
    }


    public double LBs(double expectedSupport, double miniprob) {
        double v = Math.sqrt(-2 * expectedSupport * Math.log(1 - miniprob));
        double lowerBound = expectedSupport - v;
        return Max(lowerBound, 0);
    }

    // Upper Bound (ub)
    public double UBs(double expectedSupport, double miniprob, double support) {
        double upperBound = (2 * expectedSupport - Math.log(miniprob) + Math.sqrt(Math.log(2 * miniprob) - 8 * expectedSupport * Math.log(miniprob))) / 2;
        return Min(upperBound, support);
    }
    
    private double Max(double a, double b) {
        return a > b? a : b;
    }

    private double Min(double a, double b) {
        return a < b? a : b;
    }
}
