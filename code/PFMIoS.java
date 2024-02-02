import java.util.ArrayList;
import java.util.List;

public class PFMIoS {

    private Double OLB;
    private Double OUB;
    private Double OPS;
    PFIT P = new PFIT();

    public void measureExecutionTime(Runnable code) {
        long startTime = System.nanoTime();
        code.run();
        long endTime = System.nanoTime();
        long executionTime = endTime - startTime;

        System.out.println("Execution Time: " + executionTime + " nanoseconds");
    }
    
    public void ADDTRANS(PFITNode nX,int US ,UncertainDatabase database, double minisup, double miniprob) {
        measureExecutionTime(() -> {
        int len = database.name.size() - 1;
        List<String> list = database.name.get(len);
        List<PFITNode> childrenCopy = new ArrayList<>();
        List<PFITNode> newfre = new ArrayList<>();
        List<PFITNode> frequent = new ArrayList<>();
        
        if (nX.getChildren() == null) {
            return;
        }

        for (PFITNode nY: nX.getChildren()) {
            OLB = nY.getLB();
            OUB = nY.getUB();
            OPS = nY.getProb();

            if (nY.isSingleElementSubset(nY.getItems(), list)){
                nY.setSupport(nY.Supporteds(nY.getItems()));
                nY.setExpSup(nY.ExpSups(nY.getItems()));
                nY.setLB(nY.LBs(nY.getItems(), miniprob));
                nY.setUB(nY.UBs(nY.getItems(), miniprob));
                if (minisup >= nY.getLB() && minisup <= nY.getUB()) {
                    nY.setProb(nY.ProbabilityFrequents(nY.getItems(), miniprob));
                }
            }
            
            if (nY.checkNewFrequent(OLB, OUB, OPS, nX.getLB(), nY.getUB(), nY.getProb(), minisup)){
                newfre.add(nY);
                List<PFITNode> nZs = nY.getRightSiblings();
                for (PFITNode nZ : nZs){
                    PFITNode child = nY.generateChildNode(nZ);
                    child.setSupport(child.Supporteds(child.getItems()));
                    child.setExpSup(child.ExpSups(child.getItems()));
                    child.setLB(child.LBs(child.getItems(), miniprob));
                    child.setUB(child.UBs(child.getItems(), miniprob));
                    nY.addChild(child);
                    childrenCopy.add(child);
                    if (minisup >= child.getLB() && minisup <= child.getUB()) {
                        child.setProb(child.ProbabilityFrequents(child.getItems(), miniprob));
                    }
                }
            }
            if (nY.checkFrequent(OLB, OUB, OPS, nY.getLB(), nY.getUB(), nY.getProb(), minisup) && nY.isSingleElementSubset(nY.getItems(), list) ){
                frequent.add(nY);
            }
        }
        for (PFITNode nY : frequent){
            List<PFITNode> nZs = nY.getRightSiblings();
            for (PFITNode nZ : nZs){
                if (newfre.contains(nZ)){
                    PFITNode child = nY.generateChildNode(nZ);
                    child.setSupport(child.Supporteds(child.getItems()));
                    child.setExpSup(child.ExpSups(child.getItems()));
                    child.setLB(child.LBs(child.getItems(), miniprob));
                    child.setUB(child.UBs(child.getItems(), miniprob));
                    childrenCopy.add(child);
                    nY.addChild(child);
                    if (minisup >= child.getLB() && minisup <= child.getUB()) {
                        child.setProb(child.ProbabilityFrequents(child.getItems(), miniprob));
                    }
                }
            }
        }

        for(PFITNode child : childrenCopy){
            nX.addChild(child);
        }
        
        });
    }


    public void DelTran(PFITNode nX,int US ,UncertainDatabase database, double minisup, double miniprob) {
        measureExecutionTime(() -> {
        
        List<String> list = database.name.get(0);
        List<PFITNode> toRemove = new ArrayList<>();
        List<PFITNode> infre = new ArrayList<>();
        List<PFITNode> frequentdel = new ArrayList<>();

        if (nX.getChildren().isEmpty()) {
            return;
        }
        
        for (PFITNode nY: nX.getChildren()){
            OLB = nY.getLB();
            OUB = nY.getUB();
            OPS = nY.getProb();
            if(nY.isSingleElementSubset(nY.getItems(), list)){
                nY.setSupport(nY.Supporteds(nY.getItems()));
                nY.setExpSup(nY.ExpSups(nY.getItems()));
                nY.setLB(nY.LBs(nY.getItems(), miniprob));
                nY.setUB(nY.UBs(nY.getItems(), miniprob));
                if (minisup >= nY.getLB() && minisup <= nY.getUB()) {
                    nY.setProb(nY.ProbabilityFrequents(nY.getItems(), miniprob));
                }
            }

            if (nY.checkInfrequent(OLB, OUB, OPS, nX.getLB(), nY.getUB(), nY.getProb(), minisup)){
                infre.add(nY);
                nY.setProb(0);
                for (int i =0; i <  nY.getChildren().size(); i++) {
                    toRemove.add(nY.getChildren().get(i));
                }   
            }
            if (nY.checkFrequenDel(OLB, OUB, OPS, nX.getLB(), nY.getUB(), nY.getProb(), minisup)){
                frequentdel.add(nY);
            }
        }
        
        for (PFITNode nY : frequentdel){
            List<PFITNode> nZs = nY.getRightSiblings();
            for (PFITNode nZ : nZs){
                if (infre.contains(nZ)){
                    for (int i =0; i <  nY.getChildren().size(); i++) {
                        toRemove.add(nY.getChildren().get(i));
                    }  
                    
                }
            }
        }
        nX.getChildren().removeAll(toRemove);

        });
    }
}
