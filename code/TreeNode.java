import java.util.ArrayList;
import java.util.List;

public class TreeNode {
    private List<Character> items;
    private List<TreeNode> children;

    public TreeNode(List<Character> items) {
        this.items = items;
        this.children = new ArrayList<>();
    }

    public List<Character> getItems() {
        return items;
    }

    public List<TreeNode> getChildren() {
        return children;
    }

    public void addChild(TreeNode child) {
        children.add(child);
    }
}
