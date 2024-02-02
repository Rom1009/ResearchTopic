import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Node {
    private List<String> itemset;
    private List<Node> children;
    private Node parent;

    public Node(List<String> itemset, Node parent) {
        this.itemset = itemset;
        this.children = new ArrayList<>();
        this.parent = parent;
    }

    public void addChild(Node child) {
        children.add(child);
    }

    public List<Node> getChildren() {
        return children;
    }

    public List<String> getItemset() {
        return itemset;
    }

    public List<Node> getRightSiblings() {
        if (parent == null) {
            return new ArrayList<>();
        }

        boolean found = false;
        List<Node> rightSiblings = new ArrayList<>();

        for (Node sibling : parent.children) {
            if (found) {
                rightSiblings.add(sibling);
            } else if (sibling == this) {
                found = true;
            }
        }

        return rightSiblings;
    }

    public List<List<String>> getRightSiblingItemsets() {
        return getRightSiblings().stream().map(Node::getItemset).collect(Collectors.toList());
    }


    
}
