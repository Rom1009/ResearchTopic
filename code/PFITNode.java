import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PFITNode {
    private List<String> itemset; // Itemset
    private double sup; // Support
    private double esup; // Expected suppot
    private double psup; // probability support
    private double lb; // lower bound
    private double ub; // upper bound
    private PFITNode Sibling; // parent node
    private List<PFITNode> children; // List of children
    public UncertainDatabase database; // database

    
    public PFITNode(List<String> itemset,UncertainDatabase database) {
        this.itemset = itemset;
        this.children = new ArrayList<>();
        this.database = database;
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

    public PFITNode getSibling() {
        return Sibling;
    }

    public void setSibling(PFITNode Sibling) {
        this.Sibling = Sibling;
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

   /* 
    * Name: getRightSiblings
    * Input: None
    * Output: List<PFITNode> - List of right sibling nodes.
    * Description: This method retrieves all sibling nodes that are to the right of the current node and share the same parent. 
    *              It iteratively checks each subsequent sibling in the parent's child list. If a sibling node has the same 
    *              size itemset as the current node, it is considered a right sibling and is added to the result list. 
    *              This method is useful for algorithms that need to consider sibling relationships among nodes in a tree 
    *              structure, such as in certain tree traversal or pattern mining tasks.
    */

    public List<PFITNode> getRightSiblings() {
        if (Sibling == null) {
            return new ArrayList<>();
        }

        int sizeOfCurrentNode = this.itemset.size();
        List<PFITNode> rightSiblings = new ArrayList<>();
        int currentIndex = Sibling.getChildren().indexOf(this);

        for (int i = currentIndex + 1; i < Sibling.getChildren().size(); i++) {
            PFITNode sibling = Sibling.getChildren().get(i);
            if (sibling.getItems().size() == sizeOfCurrentNode) {
                rightSiblings.add(sibling);
            }
        }

        return rightSiblings;
    }


    /*
    * Name: addChild
    * Input: PFITNode child - The child node to be added.
    * Output: None.
    * Description: This method adds a child node to the current PFITNode instance. If a child with the same itemset already
    * exists, it replaces the existing child with the new one. Otherwise, it simply adds the new child to the children list.
    * After adding the child, it also sets the current node as the parent of the child node.
    */
    public void addChild(PFITNode child) {
        int existingChildIndex = indexOfChildWithItemset(child.getItems());
        if (existingChildIndex != -1) {
            // Replace the existing child
            this.children.set(existingChildIndex, child);
        } else {
            // Add as a new child
            this.children.add(child);
        }
        child.setSibling(this);
    }
    

    /*
    * Name: indexOfChildWithItemset
    * Input: List<String> itemset - A list of strings representing an itemset to find among the children.
    * Output: int - The index of the child with the given itemset or -1 if not found.
    * Description: This method searches through the children of the current node to find a child with the
    *              exact itemset provided. If such a child is found, its index is returned. Otherwise, -1
    *              is returned to indicate that no such child exists. This method uses HashSet for
    *              comparison to handle unordered lists where the itemset might not be in the same order
    *              but still represents the same set of items.
    */
    private int indexOfChildWithItemset(List<String> itemset) {
        for (int i = 0; i < children.size(); i++) {
            if (new HashSet<>(children.get(i).getItems()).equals(new HashSet<>(itemset))) {
                return i;
            }
        }
        return -1; // Return -1 if no match is found
    }


    /*
    * Name: generateChildNode
    * Input: PFITNode nY - The node with which the itemset is to be combined.
    * Output: PFITNode - A new PFITNode instance representing the combined itemset or null if the node already exists.
    * Description: This method combines the itemset of the current node with the itemset of the provided node (nY) to
    *              create a new node with the union of both itemsets. It first ensures that the combined itemset is unique
    *              by using a HashSet. If the resulting child node does not already exist among the current node's children,
    *              it instantiates and returns this new child node. Otherwise, it returns null, indicating that a node
    *              with this combined itemset already exists.
    */
    public PFITNode generateChildNode(PFITNode nY) {
        // Use a HashSet for better performance
        Set<String> combinedItemset = new HashSet<>(this.itemset);
        combinedItemset.addAll(nY.itemset);
    
        // Convert the set back to a list
        List<String> newItemset = new ArrayList<>(combinedItemset);
        
        // Create a new node with the combined, unique itemset
        PFITNode childNode = new PFITNode(newItemset, database);


        // Check if the child node already exists
        if (!isChildNodeExists(childNode)) {
            return childNode;
        } else {
            return null; // Return null if the child node already exists
        }
    }
    
    /*
    * Name: isChildNodeExists
    * Input: PFITNode childNode - The child node to check for existence in the current node's children.
    * Output: boolean - Returns true if the childNode exists among the current node's children, otherwise false.
    * Description: This method checks whether the specified childNode is present in the list of children of the current node.
    *              It uses the List.contains method to check for the presence of the childNode based on the 'equals' method
    *              implementation of the PFITNode class. If the child node is found, it returns true; otherwise, it returns false.
    */

    private boolean isChildNodeExists(PFITNode childNode) {
        return children.contains(childNode);
    }


    private Double cachedUBsResult; // Cache result for optimization
    /*
    * Name: isFrequent
    * Input: double minisup - The minimum support threshold.
    *        double ub - The upper bound value to check against the minimum support.
    * Output: boolean - Returns true if the cached upper bound (or provided ub if not cached) is greater than 
    *         or equal to the minimum support threshold; otherwise, returns false.
    * Description: This method assesses whether the cached upper bound result (or the provided upper bound if
    *              no cached result exists) meets or exceeds the specified minimum support threshold (minisup).
    *              The method first checks if there is a cached result for the upper bound; if not, it caches
    *              the provided upper bound value. Then, it compares the cached (or provided) upper bound with
    *              the minimum support threshold and returns true if the upper bound is sufficient to consider
    *              the itemset frequent; otherwise, it returns false.
    */
    public boolean isFrequent(double minisup, double expectedSupport) {
        if (cachedUBsResult == null) {
            cachedUBsResult = expectedSupport;
        }
        return cachedUBsResult >= minisup;
    }
    
    /*
    * Name: isSingleElementSubset
    * Input: List<String> name - The candidate subset to check within 'items'.
    *        List<String> items - The itemset to check against.
    * Output: boolean - Returns true if 'name' is a subset of 'items'; otherwise, returns false.
    * Description: This method determines whether the list 'name' is a subset of 'items'. If 'name' has more elements than
    *              'items', it cannot be a subset, and the method returns false. If 'name' has only one element, the method
    *              simply checks whether this single element is contained in 'items'. If 'name' has more than one element,
    *              the method checks if 'items' contains all elements of 'name' using the containsAll method, which is 
    *              more efficient than converting to a HashSet, especially for small sizes of 'name'.
    */

    public boolean isSingleElementSubset(List<String> name, List<String> items) {
        if (name.size() > items.size()) {
            return false;
        }
        
        if (name.size() == 1) {
            return items.contains(name.get(0));
        }
        
        return items.containsAll(name);
    }
    
    /*
    * Name: checkProb
    * Input: double lb - The lower bound value.
    *        double ub - The upper bound value.
    *        double minisup - The minimum support threshold.
    * Output: boolean - Returns true if the itemset is potentially frequent, false otherwise.
    * Description: This method checks if the itemset is potentially frequent based on its lower and upper bound values relative to the minimum support threshold.
    *              The itemset is considered potentially frequent if its lower bound is less than or equal to the minimum support and its upper bound is greater than or equal to the minimum support.
    */
    public boolean checkProb(double lb, double ub, double minisup){
        return lb <= minisup && ub >= minisup;
    }

    /*
    * Name: checkFrequenDel
    * Input: double OLB, OUB, OPS, ULB, UUB, UPS (Old and Updated Lower Bounds, Upper Bounds, and Probabilistic 
    Supports), double minisupp (minimum support threshold).
    * Output: boolean - Returns true if the itemset satisfies the conditions for being considered frequently deleted, false otherwise.
    * Description: This method checks specific conditions combining old and updated statistical 
    measures against the minimum support threshold to determine if an itemset qualifies as frequently 
    deleted based on its probabilistic and bounds characteristics. (f - f)
    */
    public boolean checkFrequenDel(double OLB, double OUB, double OPS, double ULB, double UUB, double UPS, double minisupp){
        boolean ans1 = OLB < minisupp && OUB >= minisupp && ULB < minisupp && UUB >= minisupp && OPS >= minisupp && UPS >= minisupp;
        boolean ans2 = OLB >= minisupp && ULB >= minisupp;
        boolean ans3 = OLB >= minisupp && ULB < minisupp;
       
        return ans1 || ans2 || ans3;
    }
    
    
    /*
    * Name: checkInfrequent
    * Input: Similar to checkFrequenDel with different variables representing bounds and supports.
    * Output: boolean - Identifies if the itemset is infrequent.
    * Description: Evaluates a set of conditions to determine if an itemset is considered 
    infrequently present within transactions, based on a comparison of old and updated lower and upper bounds 
    as well as support values against a minimum support threshold. (f - i)
    */
    public boolean checkInfrequent(double OLB, double OUB, double OPS, double ULB, double UUB, double UPS, double minisupp){
        boolean ans1 = OLB < minisupp && OUB >= minisupp && ULB < minisupp && UUB >= minisupp && OPS >= minisupp && UPS < minisupp;
        boolean ans2 = OLB < minisupp && OUB >= minisupp && UUB < minisupp && OPS >= minisupp;
        boolean ans3 = OUB >= minisupp && ULB < minisupp && UPS < minisupp;
       
        return ans1 || ans2 || ans3;
    }

    /*
    * Name: checkNewFrequent
    * Input: Similar to checkFrequenDel with different conditions to evaluate.
    * Output: boolean - Determines if an itemset should be considered newly frequent.
    * Description: Checks if an itemset transitions to being considered frequent 
    in the updated data by examining changes in its statistical measures against the minimum support threshold. (i - f)
    */

    public boolean checkNewFrequent(double OLB, double OUB, double OPS, double ULB, double UUB, double UPS, double minisupp){
        boolean ans1 = OLB < minisupp && OUB >= minisupp && ULB < minisupp && UUB >= minisupp && OPS < minisupp && UPS >= minisupp;
        boolean ans2 = OLB < minisupp && OUB >= minisupp && ULB >= minisupp && OPS < minisupp;
        boolean ans3 = OUB < minisupp && UUB >= minisupp && UPS >= minisupp;
       
        return ans1 || ans2 || ans3;
    }

    /*
    * Name: checkFrequent
    * Input: Various statistical bounds and support values to assess, along with a minimum support threshold.
    * Output: boolean - Indicates if the itemset is frequent.
    * Description: Applies a set of logical conditions to determine whether an itemset meets the criteria to be 
    deemed frequent based on its statistical characteristics and the minimum support threshold. (f - f)
    */
    public boolean checkFrequent(double OLB, double OUB, double OPS, double ULB, double UUB, double UPS, double minisupp){
        boolean ans1 = OLB < minisupp && OUB >= minisupp && ULB < minisupp && UUB >= minisupp && OPS >= minisupp;
        boolean ans2 = OLB < minisupp && OUB >= minisupp && ULB >= minisupp && OPS >= minisupp;
        boolean ans3 = OLB >= minisupp;
        return ans1 || ans2 || ans3; 
    }

    /*
    * Name: Supporteds
    * Input: List<String> requiredItems, List<List<String>> name1 - A list of transactions.
    * Output: double - The support count of the required itemset within the transactions.
    * Description: Computes how many times the required itemset appears in the provided list of transactions.
    */
    public double Supporteds(List<String> requiredItems, List<List<String>> name1) {
        int count = 0;
        for (List<String> transaction : name1) {
            if (transaction.containsAll(requiredItems)) {
                count++;
            }
        }
        return count;
    }    
    
    /*
    * Name: ExpSups
    * Input: requiredItems, name1 (transactions), prob1 (probability values associated with each item in transactions).
    * Output: double - The expected support of the required itemset.
    * Description: Determines the expected support for an itemset based on the occurrence 
    and associated probability values of its items in the transaction data.
    */
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
    

    /*
    * Name: ProbabilityFrequents
    * Input: requiredItems, minValue (threshold for probability), name1 (transactions), prob1 (probability values).
    * Output: double - The frequency count based on a probability threshold.
    * Description: Counts the occurrences of an itemset in transactions where the itemset's probability exceeds 
    a given threshold.
    */
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

    public double weightAverage(List<String> requiredItems, List<List<String>> name1, List<List<Double>> weight1, double support){
        double weightedExpSup = 0.0;
        for (int i = 0; i < name1.size(); i++) {
            List<String> transaction = name1.get(i);
            List<Double> transactionWeights = weight1.get(i);
            if (transaction.containsAll(requiredItems)) {
                double transactionWeightedProb = 1.0;
                for (String item : requiredItems) {
                    int index = transaction.indexOf(item);
                    transactionWeightedProb *= transactionWeights.get(index);
                }
                weightedExpSup += transactionWeightedProb;
            }
        }
        return weightedExpSup/support;
    }
    
    /*
    * Name: Probability
    * Input: sup (support), esup (expected support), miniprob (minimum probability threshold).
    * Output: double - A probability value.
    * Description: Calculates a probability metric based on support, expected support, 
    and a minimum probability threshold to assess the likelihood of an itemset being frequent.
    */
    public double Probability(double sup, double esup, double miniprob){
        double a = (2 * Math.sqrt(-2*esup*Math.log(1-miniprob)) -Math.log(miniprob) + Math.sqrt(Math.pow(Math.log(miniprob), 2) - 8*esup*Math.log(miniprob)))/2*database.name1.size();
        double b = (2 * esup - Math.log(miniprob) + Math.sqrt(Math.pow(Math.log(miniprob), 2) - 8*esup*Math.log(miniprob)))/2*database.name1.size();
        double c = (sup - esup + Math.sqrt(-2*esup*Math.log(1-miniprob))) / database.name1.size();
        double d = sup / database.name1.size();
        return findMin(a, b, c, d); 
    }

    /* 
    * Name: findMin
    * Input: Four double values (a, b, c, d).
    * Output: double - The minimum of the four values.
    * Description: Helper method to find and return the minimum value among four double inputs.
    */
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

    /*
    * Name: LBs
    * Input: expectedSupport, miniprob (minimum probability).
    * Output: double - Computed lower bound.
    * Description: Calculates the lower bound for an itemset's support based on its expected support and a minimum probability threshold.
    */
    public double LBs(double expectedSupport, double miniprob) {
        double v = Math.sqrt(-2 * expectedSupport * Math.log(1 - miniprob));
        double lowerBound = expectedSupport - v;
        return Max(lowerBound, 0);
    }

    /*
    * Name: UBs
    * Input: expectedSupport, miniprob, and support values.
    * Output: double - Computed upper bound.
    * Description: Determines the upper bound for an itemset's support considering its expected support, actual support, and a probability threshold.
    */
    // Upper Bound (ub)
    public double UBs(double expectedSupport, double miniprob, double support) {
        double upperBound = (2 * expectedSupport - Math.log(miniprob) + Math.sqrt(Math.log(2 * miniprob) - 8 * expectedSupport * Math.log(miniprob))) / 2;
        return Min(upperBound, support);
    }
    
    /*
    * Name: Max
    * Input: Two double values (a and b).
    * Output: double - The larger of the two values.
    * Description: Utility method to return the maximum of two double values.
    */
    private double Max(double a, double b) {
        return a > b? a : b;
    }

    /*
    * Name: Min
    * Input: Two double values (a and b).
    * Output: double - The smaller of the two values.
    * Description: Utility method to return the minimum of two double values.
    */
    private double Min(double a, double b) {
        return a < b? a : b;
    }
}