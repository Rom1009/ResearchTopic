import java.util.ArrayList;
import java.util.List;

public class PFIT {

    public void measureExecutionTime(Runnable code) {
        long startTime = System.nanoTime();
        code.run();
        long endTime = System.nanoTime();
        long executionTime = endTime - startTime;

        System.out.println("Execution Time: " + executionTime + " nanoseconds");
    }

    public void Buildtree(PFITNode nXs, int US, double minisup, double miniprob) {
        measureExecutionTime(() -> {
            List<PFITNode> xs = new ArrayList<>();
            for (PFITNode nX : nXs.getChildren()) {
                processNode(nX, miniprob, minisup, xs);
            }
            nXs.getChildren().addAll(xs);
        });
    }

    private void processNode(PFITNode nX, double miniprob, double minisup, List<PFITNode> xs) {
        boolean isFrequentX = nX.isFrequent(miniprob, minisup);
        updateNodeMetrics(nX, miniprob);

        if (!isFrequentX) {
            nX.setProb(nX.ProbabilityFrequents(nX.getItems(), miniprob));
            return;
        }

        List<PFITNode> nodes = nX.getRightSiblings();
        for (PFITNode node : nodes) {
            if (node.isFrequent(miniprob, minisup)) {
                PFITNode child = nX.generateChildNode(node);
                updateNodeMetrics(child, miniprob);
                xs.add(child);
                if (child.checkProb(child.getLB(), child.getUB(), minisup)) {
                    child.setProb(child.ProbabilityFrequents(child.getItems(), miniprob));
                }
            }
        }
    }

    private void updateNodeMetrics(PFITNode node, double miniprob) {
        node.setSupport(node.Supporteds(node.getItems()));
        node.setExpSup(node.ExpSups(node.getItems()));
        node.setLB(node.LBs(node.getItems(), miniprob));
        node.setUB(node.UBs(node.getItems(), miniprob));
    }
}
