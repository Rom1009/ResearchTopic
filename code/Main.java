import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Tạo root node với itemset rỗng (đại diện cho node gốc)
        Node root = new Node(new ArrayList<>(), null);

        // Tạo các node con và thêm vào root
        Node nodeA = new Node(Arrays.asList("A"), root);
        root.addChild(nodeA);
        
        Node nodeB = new Node(Arrays.asList("B"), root);
        root.addChild(nodeB);

        Node nodeC = new Node(Arrays.asList("C"), root);
        root.addChild(nodeC);

        Node nodeD = new Node(Arrays.asList("D"), root);
        root.addChild(nodeD);

        Node nodeE = new Node(Arrays.asList("E"), root);
        root.addChild(nodeE);

        // Lấy và in ra các itemset bên phải của nodeB
        List<List<String>> rightSiblingItemsetsB = nodeB.getRightSiblingItemsets();
        System.out.println("Các itemset bên phải của 'B': " + rightSiblingItemsetsB);

        // Thêm các node con cho nodeB
        Node nodeBA = new Node(Arrays.asList("B", "A"), nodeB);
        nodeB.addChild(nodeBA);

        Node nodeBB = new Node(Arrays.asList("B", "B"), nodeB);
        nodeB.addChild(nodeBB);

        // Lấy và in ra các itemset bên phải của nodeBA
        List<List<String>> rightSiblingItemsetsBA = nodeBA.getRightSiblingItemsets();
        System.out.println("Các itemset bên phải của ['B', 'A']: " + rightSiblingItemsetsBA);
    }
}
