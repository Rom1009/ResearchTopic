import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class PFIT {

    // Utilize a ForkJoinPool to potentially parallelize some of the operations.
    private ForkJoinPool pool = new ForkJoinPool();

    public void Buildtree(PFITNode nXs, int US, double minisup, double miniprob) {
        // long startTime = System.nanoTime();

        // Use RecursiveAction for parallel execution if applicable.
        pool.invoke(new RecursiveAction() {
            @Override
            protected void compute() {
                List<PFITNode> xs = new ArrayList<>();
                nXs.getChildren().parallelStream().forEach(nX -> processNode(nX, miniprob, minisup, xs));
                nXs.getChildren().addAll(xs);
            }
        });

        // long endTime = System.nanoTime();
        // System.out.println("Execution Time: " + (endTime - startTime) + " nanoseconds");
    }

    private void processNode(PFITNode nX, double miniprob, double minisup, List<PFITNode> xs) {
        updateNodeMetrics(nX, miniprob);
        if (!nX.isFrequent(minisup, nX.getUB())) {
            return;
        }        
        nX.setProb(nX.ProbabilityFrequents(nX.getItems(), miniprob, nX.database.name1, nX.database.prob1));
        nX.getRightSiblings().parallelStream().forEach(node -> {
            if (node.isFrequent(minisup, node.getUB())) {
                PFITNode child = nX.generateChildNode(node);
                updateNodeMetrics(child, miniprob);
                if (child.getLB() <= minisup && child.getUB() >= minisup){
                    child.setProb(node.ProbabilityFrequents(child.getItems(), miniprob,nX.database.name1, nX.database.prob1));
                }
                synchronized (xs) {
                    xs.add(child);
                }
            }
        });
    }

    private void updateNodeMetrics(PFITNode node, double miniprob) {
        // Assume these methods are optimized as well.
        node.setSupport(node.Supporteds(node.getItems(), node.database.name1));
        node.setExpSup(node.ExpSups(node.getItems(),node.database.name1, node.database.prob1));
        node.setLB(node.LBs(node.getExpSup(),miniprob));
        node.setUB(node.UBs(node.getExpSup(), miniprob, node.getSupport()));
    }
}
