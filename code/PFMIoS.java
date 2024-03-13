import java.util.ArrayList;
import java.util.List;

public class PFMIoS {

    /*
    * Name: ADDTRANS
    * Input: PFITNode nX - The current node to process.
    *        int US - Unused parameter in the provided context, potentially for future use or additional thresholds.
    *        UncertainDatabase database - The database from which to get the latest transaction data.
    *        double minisup - The minimum support threshold.
    *        double miniprob - The minimum probability threshold.
    * Output: void - This method does not return a value but updates the tree structure based on the new transaction.
    * Description: This method processes a new transaction by updating the tree starting from node nX. It updates node
    *              metrics based on the new transaction, checks if nodes become frequent or newly frequent, and adds
    *              child nodes accordingly. It expands the tree recursively by exploring right siblings and creating
    * 
    */ 
    public void ADDTRANS(PFITNode nX,int US ,UncertainDatabase database, double minisup, double miniprob) {
        List<String> value = database.name1.get(database.name1.size() - 1);
        List<Double> prob = database.prob1.get(database.prob1.size() - 1);
        List<PFITNode> newfre = new ArrayList<>();
        List<PFITNode> childrenCopy = new ArrayList<>();
        List<PFITNode> frequent = new ArrayList<>();
        System.out.println(US);
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
                    nY.setProb(nY.ProbabilityFrequents(nY.getItems(), miniprob,database.name1,database.prob1));
                }
                if (nY.checkNewFrequent(OLB, OUB, OPS, nY.getLB(), nY.getUB(), nY.getProb(), minisup)){
                    newfre.add(nY);
                    List<PFITNode> nZs = nY.getRightSiblings();
                    for (PFITNode nZ : nZs){
                        PFITNode child = nY.generateChildNode(nZ);
                        child.setSupport(child.Supporteds(child.getItems(),database.name1));
                        child.setExpSup(child.ExpSups(child.getItems(),database.name1,database.prob1));
                        child.setLB(child.LBs(child.getExpSup(), miniprob));
                        child.setUB(child.UBs(child.getExpSup(), miniprob,child.getSupport()));
                        childrenCopy.add(child);
                        nY.addChild(child);   
                        if (minisup >= child.getLB() && minisup <= child.getUB()) {
                            child.setProb(child.ProbabilityFrequents(child.getItems(), miniprob,database.name1,database.prob1));
                        }
                    }
                }
                if (nY.checkFrequent(OLB, OUB, OPS, nY.getLB(), nY.getUB(), nY.getProb(), minisup) && nY.isSingleElementSubset(nY.getItems(), value) ){                    
                    frequent.add(nY);
                }
            }
        }
        for (PFITNode nY : frequent){
            List<PFITNode> nZs = nY.getRightSiblings();
            for (PFITNode nZ : nZs){
                if (newfre.contains(nZ)){
                    PFITNode child = nY.generateChildNode(nZ);
                    child.setSupport(child.Supporteds(child.getItems(),database.name1));
                    child.setExpSup(child.ExpSups(child.getItems(),database.name1,database.prob1));
                    child.setLB(child.LBs(child.getExpSup(), miniprob));
                    child.setUB(child.UBs(child.getExpSup(), miniprob,child.getSupport()));
                    childrenCopy.add(child);
                    if (minisup >= child.getLB() && minisup <= child.getUB()) {
                        child.setProb(child.ProbabilityFrequents(child.getItems(), miniprob,database.name1,database.prob1));
                    }
                    nY.addChild(child);
                }
            }
        }
        nX.getChildren().addAll(childrenCopy);
    }
    

    /*
    * Name: DelTran
    * Input: PFITNode nX - The current node to process.
    *        int US - Unused parameter, potentially for additional thresholds or controls.
    *        UncertainDatabase database - The database from which to remove and process the oldest transaction data.
    *        double minisup - The minimum support threshold.
    *        double miniprob - The minimum probability threshold.
    * Output: void - This method does not return a value but updates the tree structure based on transaction removal.
    * Description: This method processes the removal of the oldest transaction from the tree starting at node nX. It updates
    *              the support and expected support of nodes impacted by the transaction removal, recalculates bounds and
    *              probability, and removes or adjusts nodes and their children based on these updated metrics. The method
    *              ensures the tree reflects the current state of the database after the oldest transaction is removed.
    */
    public void DelTran(PFITNode nX, int US, UncertainDatabase database, double minisup, double miniprob) {
        // Lấy danh sách và danh sách xác suất từ cơ sở dữ liệu
        List<String> list = database.name1.get(0);
        List<Double> list1 = database.prob1.get(0);
        List<PFITNode> infre = new ArrayList<>();
        if (nX.getChildren().isEmpty()) {
            return;
        }
        List<PFITNode> copy = new ArrayList<>(nX.getChildren());
        
        // Loại bỏ danh sách và danh sách xác suất khỏi cơ sở dữ liệu
        database.name1.remove(0);
        database.prob1.remove(0);
    
        // Tạo danh sách a và b từ list và list1 để sử dụng lại trong vòng lặp
        
        // Lặp qua các nút con của nX
        for (PFITNode nY : copy) {
            double OLB = nY.getLB();
            double OUB = nY.getUB();
            double OPS = nY.getProb();
    
            if (nX.isSingleElementSubset(nY.getItems(), list)) {
                // Thực hiện các tính toán mà không cần truy cập vào cơ sở dữ liệu
                nY.setSupport(nY.getSupport() - nY.Supporteds(nY.getItems(), List.of(list)));
                nY.setExpSup(nY.getExpSup() - nY.ExpSups(nY.getItems(), List.of(list), List.of(list1)));
                nY.setLB( nY.LBs(nY.getExpSup(), miniprob));
                nY.setUB(nY.UBs(nY.getExpSup(), miniprob, nY.getSupport()));
                if (minisup >= nY.getLB() && minisup <= nY.getUB()) {
                    nY.setProb(nY.ProbabilityFrequents(nY.getItems(), miniprob,database.name1, database.prob1));
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