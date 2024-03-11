import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class wPFIT {
    private ForkJoinPool pool = new ForkJoinPool();

    /*
    * Name: Buildtree
    * Input: Similar to PFIT
    * Output: Similar to PFIT
    * Description: Different in this implementation for compute weight in each transaction with expected support, support 
    * and probability support.
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
    * Input: Similar to PFIT
    * Output: Similar to PFIT
    * Description: Different in this implementation for compute weight in each transaction with expected support, support 
    * and probability support.
    */
    private void processNode(PFITNode nX, double miniprob, double minisup, List<PFITNode> xs) {
        updateNodeMetrics(nX, miniprob);
        if (!nX.isFrequent(minisup, nX.getUB())) {
            return;
        }        
        nX.setProb(nX.weightedProbabilityFrequents(nX.getItems(), miniprob,nX.database.name1, nX.database.prob1, nX.database.weight1));
        // nX.setProb(nX.Probability(nX.getSupport(), nX.getExpSup(), miniprob));
        nX.getRightSiblings().parallelStream().forEach(node -> {
            if (node.isFrequent(minisup, node.getSupport())) {
                PFITNode child = nX.generateChildNode(node);
                updateNodeMetrics(child, miniprob);
                node.addChild(child);
                if (child.getLB() <= minisup && child.getUB() >= minisup){
                    child.setProb(node.weightedProbabilityFrequents(child.getItems(), miniprob,nX.database.name1, nX.database.prob1, nX.database.weight1));
                    // child.setProb(child.Probability(child.getSupport(), child.getExpSup(), miniprob));
                }
                synchronized (xs) {
                    xs.add(child);
                }
            }
        });
    }

    /*
    * Name: updateNodeMetrics
    * Input: Similar to PFIT
    * Output: Similar to PFIT
    * Description: Different in this implementation for compute weight in each transaction with expected support, support 
    * and probability support.
    */
    private void updateNodeMetrics(PFITNode node, double miniprob) {
        // Assume these methods are optimized as well.
        node.setSupport(node.weightedSupporteds(node.getItems(), node.database.name1,node.database.weight1));
        node.setExpSup(node.weightedExpSups(node.getItems(),node.database.name1, node.database.prob1,node.database.weight1));
        node.setLB(node.LBs(node.getExpSup(),miniprob));
        node.setUB(node.UBs(node.getExpSup(), miniprob, node.getSupport()));
    }
}
