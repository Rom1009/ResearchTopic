import java.util.ArrayList;
import java.util.List;

public class PFMIoS {

    // private Double OLB;
    // private Double OUB;
    // private Double OPS;
    PFIT P = new PFIT();

    public void measureExecutionTime(Runnable code) {
        long startTime = System.nanoTime();
        code.run();
        long endTime = System.nanoTime();
        long executionTime = endTime - startTime;

        System.out.println("Execution Time: " + executionTime + " nanoseconds");
    }
    
    public void ADDTRANS(PFITNode nX, int US, UncertainDatabase database, double minisup, double miniprob) {
        if (nX.getChildren() == null) {
            return;
        }
        List<String> value = database.name1.get(database.name1.size() - 1);
        List<Double> prob = database.prob1.get(database.prob1.size() - 1);

        System.out.println(US);
        List<PFITNode> childrenCopy = new ArrayList<>();
        List<PFITNode> newFrequent = new ArrayList<>();
        List<PFITNode> frequent = new ArrayList<>();
        for (PFITNode nY : nX.getChildren()) {
            List<List<String>> a = new ArrayList<>();
            List<List<Double>> b = new ArrayList<>();

            a.add(value);
            b.add(prob);
            double OLB = nY.getLB();
            double OUB = nY.getUB();
            double OPS = nY.getProb();
            
            if (nY.isSingleElementSubset(nY.getItems(), value)) {
                
                nY.setSupport(nY.getSupport() + nY.Supporteds(nY.getItems(), a));
                nY.setExpSup(nY.getExpSup() + nY.ExpSups(nY.getItems(),a,b));
                nY.setLB(nY.LBs(nY.getExpSup(), miniprob));
                nY.setUB(nY.UBs(nY.getExpSup(), miniprob, nY.getSupport()));
                if (minisup >= nY.getLB() && minisup <= nY.getUB()) {
                    nY.setProb(nY.getProb() + nY.ProbabilityFrequents(nY.getItems(), miniprob, a, b ));
                }
    
                if (nY.checkNewFrequent(OLB, OUB, OPS, nX.getLB(), nY.getUB(), nY.getProb(), minisup)) {
                    P.Buildtree(nY, US, minisup, miniprob);
                    newFrequent.add(nY);
                } 

                if (nY.checkFrequent(OLB, OUB, OPS, nY.getLB(), nY.getUB(), nY.getProb(), minisup)) {
                    frequent.add(nY);
                }
            }
        }
    
        // for (PFITNode nY : newFrequent) {
        //     List<PFITNode> nZs = nY.getRightSiblings();
        //     for (PFITNode nZ : nZs) {
        //         if (newFrequent.contains(nZ)) {
        //             PFITNode child = nY.generateChildNode(nZ);
        //             updateChildNode(child, minisup, miniprob);
        //             childrenCopy.add(child);
        //         }
        //     }
        // }
    
        for (PFITNode nY : frequent) {
            List<PFITNode> nZs = nY.getRightSiblings();
            for (PFITNode nZ : nZs) {
                if (newFrequent.contains(nZ)) {
                    PFITNode child = nY.generateChildNode(nZ);
                    updateChildNode(child, minisup, miniprob);
                    childrenCopy.add(child);
                }
            }
        }
    
        nX.getChildren().addAll(childrenCopy);
    }
    
    private void updateChildNode(PFITNode child, double minisup, double miniprob) {
        child.setSupport(child.Supporteds(child.getItems(), child.database.name1));
        child.setExpSup(child.ExpSups(child.getItems(), child.database.name1,child.database.prob1));
        child.setLB(child.LBs(child.getExpSup(), child.getProb()));
        child.setUB(child.UBs(child.getExpSup(), child.getProb(), child.getSupport()));
    
        if (minisup >= child.getLB() && minisup <= child.getUB()) {
            child.setProb(child.ProbabilityFrequents(child.getItems(), miniprob,child.database.name1,child.database.prob1));
        }
    }


    public void DelTran(PFITNode nX, int US, UncertainDatabase database, double minisup, double miniprob) {
        // Lấy danh sách và danh sách xác suất từ cơ sở dữ liệu
        List<String> list = database.name1.get(0);
        List<Double> list1 = database.prob1.get(0);
    
        List<PFITNode> toRemove = new ArrayList<>();
        List<PFITNode> infre = new ArrayList<>();
        List<PFITNode> frequentdel = new ArrayList<>();
        if (nX.getChildren().isEmpty()) {
            return;
        }
        
        // Loại bỏ danh sách và danh sách xác suất khỏi cơ sở dữ liệu
        database.name1.remove(0);
        database.prob1.remove(0);
    
        // Tạo danh sách a và b từ list và list1 để sử dụng lại trong vòng lặp
        List<List<String>> a = new ArrayList<>();
        List<List<Double>> b = new ArrayList<>();
        a.add(list);
        b.add(list1);
    
        // Lặp qua các nút con của nX
        for (PFITNode nY : nX.getChildren()) {
            double OLB = nY.getLB();
            double OUB = nY.getUB();
            double OPS = nY.getProb();
    
            if (nY.isSingleElementSubset(nY.getItems(), list)) {
                // Thực hiện các tính toán mà không cần truy cập vào cơ sở dữ liệu
                nY.setSupport(nY.getSupport() - nY.Supporteds(nY.getItems(), a));
                nY.setExpSup(nY.getExpSup() - nY.ExpSups(nY.getItems(), a, b));
                nY.setLB(nY.LBs(nY.getExpSup(), miniprob));
                nY.setUB(nY.UBs(nY.getExpSup(), miniprob, nY.getSupport()));
                if (minisup >= nY.getLB() && minisup <= nY.getUB()) {
                    nY.setProb(nY.getProb() - nY.ProbabilityFrequents(nY.getItems(), miniprob, a, b));
                }
            }
    
            if (nY.checkInfrequent(OLB, OUB, OPS, nX.getLB(), nY.getUB(), nY.getProb(), minisup)) {
                infre.add(nY);
                nY.setProb(0);
                toRemove.addAll(nY.getChildren());
            }
            if (nY.checkFrequenDel(OLB, OUB, OPS, nX.getLB(), nY.getUB(), nY.getProb(), minisup)) {
                frequentdel.add(nY);
            }
        }
    
        // Xử lý nút infrequent và frequentdel
        for (PFITNode nY : frequentdel) {
            List<PFITNode> nZs = nY.getRightSiblings();
            for (PFITNode nZ : nZs) {
                if (infre.contains(nZ)) {
                    toRemove.addAll(nY.getChildren());
                }
            }
        }
    
        // Loại bỏ các nút không cần thiết
        nX.getChildren().removeAll(toRemove);
    }
    
}
