import java.util.ArrayList;
import java.util.List;

public class ElementList {
    private List<String> elements;

    public ElementList(List<String> elements) {
        this.elements = elements;
    }

    public List<String> getRightElements(String element) {
        List<String> rightElements = new ArrayList<>();
        int index = elements.indexOf(element);

        for (int i = index + 1; i < elements.size(); i++) {
            rightElements.add(elements.get(i));
        }

        return rightElements;
    }

    // Phương thức chính để chạy chương trình
    public static void main(String[] args) {
        List<String> myList = new ArrayList<>();
        myList.add("A");
        myList.add("B");
        myList.add("C");
        myList.add("D");
        myList.add("E");

        ElementList myElementList = new ElementList(myList);
        System.out.println("Right of 'A': " + myElementList.getRightElements("A"));
        System.out.println("Right of 'C': " + myElementList.getRightElements("C"));
    }
}
