import java.util.ArrayList;
import java.util.List;

public class wPFMIoSplus {
    /*
    * 
    */
    public void ADDTRANS(PFITNode nX,int US ,UncertainDatabase database, double minisup, double miniprob) {
        List<String> value = database.name1.get(database.name1.size() - 1);
        List<Double> prob = database.prob1.get(database.prob1.size() - 1);
        List<Double> weight = database.weight1.get(database.weight1.size() - 1);

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
                List<List<String>> a = new ArrayList<>();
                List<List<Double>> b = new ArrayList<>();
                List<List<Double>> c = new ArrayList<>();

                a.add(value);
                b.add(prob);
                c.add(weight);
                if (nY.isSingleElementSubset(nY.getItems(), value)){
                    nY.setSupport(nY.getSupport()+nY.weightedSupporteds(nY.getItems(),a,c));
                    nY.setExpSup(nY.getExpSup()+nY.weightedExpSups(nY.getItems(),a,b,c));
                    nY.setLB(nY.LBs(nY.weightedExpSups(nY.getItems(),a,b,c), miniprob) + nY.getLB());
                    nY.setUB(nY.UBs(nY.weightedExpSups(nY.getItems(),a,b,c), miniprob, nY.weightedSupporteds(nY.getItems(),a,c)) + nY.getUB());
                    if (minisup >= nY.getLB() && minisup <= nY.getUB()) {
                        nY.setProb(nY.getProb()+nY.Probability(nY.weightedSupporteds(nY.getItems(),a,c), nY.weightedExpSups(nY.getItems(),a,b,c), miniprob));
                    }
                }
                if (nY.checkNewFrequent(OLB, OUB, OPS, nY.getLB(), nY.getUB(), nY.getProb(), minisup)){
                    newfre.add(nY);
                    List<PFITNode> nZs = nY.getRightSiblings();
                    for (PFITNode nZ : nZs){
                        PFITNode child = nY.generateChildNode(nZ);
                        child.setSupport(child.weightedSupporteds(child.getItems(),database.name1,database.weight1));
                        child.setExpSup(child.weightedExpSups(child.getItems(),database.name1,database.prob1,database.weight1));
                        child.setLB(child.LBs(child.getExpSup(), miniprob));
                        child.setUB(child.UBs(child.getExpSup(), miniprob, child.getSupport()));
                        childrenCopy.add(child);
                        nY.addChild(child);

                        if (minisup >= child.getLB() && minisup <= child.getUB()) {
                            child.setProb(child.Probability(child.getSupport(), child.getExpSup(), miniprob));
                        }
                    }
                }
                if (nY.checkFrequent(OLB, OUB, OPS, nY.getLB(), nY.getUB(), nY.getProb(), minisup) && nY.isSingleElementSubset(nY.getItems(), value) ){                    
                    frequent.add(nY);
                }
                
            }
            for (PFITNode nY : frequent){
                List<PFITNode> nZs = nY.getRightSiblings();
                for (PFITNode nZ : nZs){
                    if (newfre.contains(nZ)){
                        PFITNode child = nY.generateChildNode(nZ);
                        child.setSupport(child.weightedSupporteds(child.getItems(),database.name1,database.weight1));
                        child.setExpSup(child.weightedExpSups(child.getItems(),database.name1,database.prob1,database.weight1));
                        child.setLB(child.LBs(child.getExpSup(),miniprob));
                        child.setUB(child.UBs(child.getExpSup(),miniprob, child.getSupport()));
                        childrenCopy.add(child);
                        nY.addChild(child);

                        if (minisup >= child.getLB() && minisup <= child.getUB()) {
                            child.setProb(child.Probability(child.getSupport(), child.getExpSup(), miniprob));
                        }
                        else{
                            nY.setProb(0);
                        }
                    }
                }
            }
           nX.getChildren().addAll(childrenCopy);
    }
    

    /*
    * 
    */

    public void DelTran(PFITNode nX, int US, UncertainDatabase database, double minisup, double miniprob) {
        // Lấy danh sách và danh sách xác suất từ cơ sở dữ liệu
        List<String> list = database.name1.get(0);
        List<Double> list1 = database.prob1.get(0);
        List<Double> list2 = database.weight1.get(0);
        List<PFITNode> infre = new ArrayList<>();
        if (nX.getChildren().isEmpty()) {
            return;
        }
        List<PFITNode> copy = new ArrayList<>(nX.getChildren());
        
        // Loại bỏ danh sách và danh sách xác suất khỏi cơ sở dữ liệu
        database.name1.remove(0);
        database.prob1.remove(0);
        database.weight1.remove(0);
    
        // Tạo danh sách a và b từ list và list1 để sử dụng lại trong vòng lặp
        
        // Lặp qua các nút con của nX
        for (PFITNode nY : copy) {
            double OLB = nY.getLB();
            double OUB = nY.getUB();
            double OPS = nY.getProb();
            List<List<String>> a = new ArrayList<>();
            List<List<Double>> b = new ArrayList<>();
            List<List<Double>> c = new ArrayList<>();

            a.add(list);
            b.add(list1);
            c.add(list2);
            if (nX.isSingleElementSubset(nY.getItems(), list)) {
                // Thực hiện các tính toán mà không cần truy cập vào cơ sở dữ liệu
                nY.setSupport(nY.getSupport() - nY.weightedSupporteds(nY.getItems(), a,c));
                nY.setExpSup(nY.getExpSup() - nY.weightedExpSups(nY.getItems(), a, b,c));
                nY.setLB(nY.getLB() - nY.LBs(nY.weightedExpSups(nY.getItems(), a, b, c), miniprob));
                nY.setUB(nY.getUB() - nY.UBs(nY.weightedExpSups(nY.getItems(), a, b, c), miniprob, nY.weightedSupporteds(nY.getItems(), a, c)));
                if (minisup >= nY.getLB() && minisup <= nY.getUB()) {
                    nY.setProb(nY.getProb() - nY.Probability(nY.weightedSupporteds(nY.getItems(), a, c), (nY.weightedExpSups(nY.getItems(), a, b, c)), miniprob));
                }
    
                
            }
    
            for(PFITNode nZ: nY.getChildren()){
                if (nZ.checkInfrequent(OLB, OUB, OPS, nZ.getLB(), nZ.getUB(), nZ.getProb(), minisup)) {
                    nX.getChildren().remove(nZ);
                    nY.setProb(0);
                    infre.add(nZ);
                }
                if (nZ.checkFrequenDel(OLB, OUB, OPS, nZ.getLB(), nZ.getUB(), nZ.getProb(), minisup) && nY.isSingleElementSubset(nY.getItems(), list)) {
                    nX.getChildren().removeAll(nZ.getChildren());
                }
            }
        }
    }
}
