import java.util.ArrayList;
import java.util.List;

public class PWFMIoS {
    /*
     * Name: ADDTRANS
     * Input: Similar to PFMIoS
     * Output: Similar to PFMIoS
     * Description: Different in this implementation for compute weights in different transactions in expected support, support 
     * and minimun prbability support. 
     */
    public void ADDTRANS(PFITNode nX,int US ,UncertainDatabase database, double minisup, double miniprob) {
        List<String> value = database.name1.get(database.name1.size() - 1);
        List<Double> prob = database.prob1.get(database.prob1.size() - 1);
        List<PFITNode> childrenCopy = new ArrayList<>();
        List<PFITNode> newfre = new ArrayList<>();
        List<PFITNode> frequent = new ArrayList<>();
        if (nX.getChildren() == null) {
            return;
        }
        for (PFITNode nY: nX.getChildren()) {
            double OLB = nY.getLB();
            double OUB = nY.getUB();
            double OPS = nY.getProb();

            if (nY.isSingleElementSubset(nY.getItems(), value)){
                nY.setSupport(nY.getSupport()+nY.Supporteds(nY.getItems(),List.of(value)));
                nY.setExpSup(nY.getExpSup()+nY.ExpSups(nY.getItems(),List.of(value),List.of(prob)));
                nY.setLB(nY.LBs(nY.getExpSup(), miniprob));
                nY.setUB(nY.UBs(nY.getExpSup(), miniprob, nY.getSupport()));
                nY.setProb(0);
                if (minisup >= nY.getLB() && minisup <= nY.getUB()) {
                    double w1 = nY.weightAverage(nY.getItems(), nY.database.name1, nY.database.weight1, nY.getSupport());
                    nY.setProb(w1*nY.ProbabilityFrequents(nY.getItems(), miniprob,database.name1, database.prob1));                        
                }
            }
            if (nY.checkNewFrequent(OLB, OUB, OPS, nY.getLB(), nY.getUB(), nY.getProb(), minisup)){
                newfre.add(nY);
                List<PFITNode> nZs = nY.getRightSiblings();
                for (PFITNode nZ : nZs){
                    if (nZ.isFrequent(minisup, nZ.getSupport())){
                        if (database.name1.stream().anyMatch(sublist -> sublist.containsAll(nY.getItems()))){
                            PFITNode child = nY.generateChildNode(nZ);
                            childrenCopy.add(child);
                            nY.addChild(child);
                            child.setSupport(child.Supporteds(child.getItems(),database.name1));
                            child.setExpSup(child.ExpSups(child.getItems(),database.name1,database.prob1));
                            child.setLB(child.LBs(child.getExpSup(), miniprob));
                            child.setUB(child.UBs(child.getExpSup(), miniprob,child.getSupport()));
                            if (minisup >= child.getLB() && minisup <= child.getUB()) {
                                child.setProb(child.ProbabilityFrequents(child.getItems(), miniprob,database.name1,database.prob1));
                            }
                        }

                    }
                }
            }
            else if (nY.checkFrequent(OLB, OUB, OPS, nY.getLB(), nY.getUB(), nY.getProb(), minisup) && nY.isSingleElementSubset(nY.getItems(), value) ){                    
                frequent.add(nY);
            }
            
        }
        for (PFITNode nY : frequent){
            List<PFITNode> nZs = nY.getRightSiblings();
            for (PFITNode nZ : nZs){
                if (newfre.contains(nZ)){
                    if (database.name1.stream().anyMatch(sublist -> sublist.containsAll(nY.getItems()))){
                        PFITNode child = nY.generateChildNode(nZ);
                        childrenCopy.add(child);
                        nY.addChild(child);
                        child.setSupport(child.Supporteds(child.getItems(),database.name1));
                        child.setExpSup(child.ExpSups(child.getItems(),database.name1,database.prob1));
                        child.setLB(child.LBs(child.getExpSup(), miniprob));
                        child.setUB(child.UBs(child.getExpSup(), miniprob,child.getSupport()));
                        if (minisup >= child.getLB() && minisup <= child.getUB()) {
                            child.setProb(child.ProbabilityFrequents(child.getItems(), miniprob,database.name1,database.prob1));
                        }
                    }
                }
            }
        }
        nX.getChildren().addAll(childrenCopy);
    }
    

    /*
     * Name: DelTran
     * Input: Similar to PFMIoS
     * Output: Similar to PFMIoS
     * Description: Different in this implementation for compute weights in different transactions in expected support, support 
     * and minimun prbability support. 
     */
    public void DELTRANS(PFITNode nX, int US, UncertainDatabase database, double minisup, double miniprob) {
        List<String> list = database.name1.get(0);
        List<Double> list1 = database.prob1.get(0);

        List<PFITNode> infre = new ArrayList<>();
        if (nX.getChildren().isEmpty()) {
            return;
        }
        List<PFITNode> copy = new ArrayList<>(nX.getChildren());
        
        database.name1.remove(0);
        database.prob1.remove(0);
        database.weight1.remove(0);
    
        
        for (PFITNode nY : copy) {
            double OLB = nY.getLB();
            double OUB = nY.getUB();
            double OPS = nY.getProb();
            if (nX.isSingleElementSubset(nY.getItems(), list)) {
                nY.setSupport(nY.getSupport() - nY.Supporteds(nY.getItems(), List.of(list)));
                nY.setExpSup(nY.getExpSup() - nY.ExpSups(nY.getItems(), List.of(list),List.of(list1)));
                nY.setLB(nY.LBs(nY.getExpSup(), miniprob));
                nY.setUB(nY.UBs(nY.getExpSup(), miniprob, nY.getSupport()));
                if (minisup >= nY.getLB() && minisup <= nY.getUB()) {
                    double w1 = nY.weightAverage(nY.getItems(), nY.database.name1, nY.database.weight1, nY.getSupport());
                    nY.setProb(w1*nY.ProbabilityFrequents(nY.getItems(), miniprob,database.name1, database.prob1));
                }
                else{
                    nY.setProb(0);
                }
            }
    
            for(PFITNode nZ: nY.getChildren()){
                if (nZ.checkInfrequent(OLB, OUB, OPS, nZ.getLB(), nZ.getUB(), nZ.getProb(), minisup)) {
                    nX.getChildren().remove(nZ);
                    infre.add(nZ);
                }
                if (nZ.checkFrequenDel(OLB, OUB, OPS, nZ.getLB(), nZ.getUB(), nZ.getProb(), minisup) && nZ.isSingleElementSubset(nZ.getItems(), list)) {
                    nX.getChildren().removeAll(nZ.getChildren());
                }
            }
        }
    }
}
