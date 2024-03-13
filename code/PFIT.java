import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class PFIT {
    private ForkJoinPool pool = new ForkJoinPool();

    /*
    * Name: Buildtree
    * Input: PFITNode nXs - The starting node from which to build the tree.
    *        int US - Unused in the provided context but could be meant for additional parameters or thresholds.
    *        double minisup - The minimum support threshold.
    *        double miniprob - The minimum probability threshold.
    * Output: void - The method does not return a value but builds the tree recursively.
    * Description: This method initiates the parallel construction of the PFIT tree from node nXs. It processes each child of nXs in parallel,
    *              expanding the tree based on the frequency and probability thresholds provided. The method encapsulates a RecursiveAction for
    *              parallel execution, where each child node and its siblings are processed to potentially add new nodes to the tree.
    */
    public void Buildtree(PFITNode nXs, int US, double minisup, double miniprob) {
        pool.invoke(new RecursiveAction() {
            @Override
            protected void compute() {
                List<PFITNode> xs = new ArrayList<>();
                nXs.getChildren().parallelStream().forEach(nX -> processNode(nX, miniprob, minisup, xs));
                nXs.getChildren().addAll(xs);
            }
        });
    }

    /*
    * Name: processNode
    * Input: PFITNode nX - The node to process.
    *        double miniprob - The minimum probability threshold.
    *        double minisup - The minimum support threshold.
    *        List<PFITNode> xs - The list to which new nodes are added.
    * Output: void - The method does not return a value but modifies the list of new nodes (xs).
    * Description: This method processes a single node (nX) by first updating its metrics. If the node is frequent based on its updated metrics,
    *              it examines right siblings for possible combinations to form new child nodes. Each valid new child node is added to the list xs.
    *              The method checks the frequency and calculates probability for each potential child node, only adding those that meet the criteria.
    */
    private void processNode(PFITNode nX, double miniprob, double minisup, List<PFITNode> xs) {
        updateNodeMetrics(nX, miniprob);
        if (!nX.isFrequent(minisup, nX.getSupport())) {
            return;
        }        
        nX.setProb(nX.ProbabilityFrequents(nX.getItems(), miniprob, nX.database.name1, nX.database.prob1));
        // nX.setProb(nX.Probability(nX.getSupport(), nX.getExpSup(), miniprob));
        nX.getRightSiblings().parallelStream().forEach(node -> {
            if (node.isFrequent(minisup, node.getSupport())) {
                PFITNode child = nX.generateChildNode(node);
                updateNodeMetrics(child, miniprob);
                if (child.getLB() <= minisup && child.getUB() >= minisup){
                    child.setProb(node.ProbabilityFrequents(child.getItems(), miniprob,nX.database.name1, nX.database.prob1));
                    // child.setProb(child.Probability(child.getSupport(), child.getExpSup(), miniprob));
                }
                node.addChild(child);
                synchronized (xs) {
                    xs.add(child);
                }
            }
        });
    }

    /*
    * Name: updateNodeMetrics
    * Input: PFITNode node - The node for which metrics should be updated.
    *        double miniprob - The minimum probability threshold used for updating metrics.
    * Output: void - The method updates the node in place and does not return any value.
    * Description: This utility method updates various metrics for a given node, including its support, expected support, and bounds (lower and upper).
    *              These updates are crucial for determining whether the node (or its descendants) should be included in the tree or considered frequent.
    */
    private void updateNodeMetrics(PFITNode node, double miniprob) {
        // Assume these methods are optimized as well.
        node.setSupport(node.Supporteds(node.getItems(), node.database.name1));
        node.setExpSup(node.ExpSups(node.getItems(),node.database.name1, node.database.prob1));
        node.setLB(node.LBs(node.getExpSup(),miniprob));
        node.setUB(node.UBs(node.getExpSup(), miniprob, node.getSupport()));
    }
}