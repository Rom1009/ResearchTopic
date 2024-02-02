import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

class ProbabilityGenerator {
    private static final double MEAN = 0.78;
    private static final double STANDARD_DEVIATION = Math.sqrt(0.65);
    private static final Random random = new Random();

    public static double generateProbability() {
        double probability = Math.exp(-(Math.pow(random.nextGaussian() - MEAN, 2) / (2 * Math.pow(STANDARD_DEVIATION, 2)))) / (Math.sqrt(2 * Math.PI) * STANDARD_DEVIATION);
        probability = Math.max(0, Math.min(probability, 1));
        return probability;
    }
}

public class ReadDataFile {
    private List<List<String>> items;
    private List<List<Double>> probs;

    public ReadDataFile() {
        items = new ArrayList<>();
        probs = new ArrayList<>();
    }

    public void readDataWithProbabilities(String filePath) throws FileNotFoundException {
        try (Scanner scanner = new Scanner(new File(filePath))) {
            while (scanner.hasNextLine()) {
                String[] lineItems = scanner.nextLine().split(" "); // Assuming comma-separated values
                List<String> itemList = new ArrayList<>(Arrays.asList(lineItems));
                List<Double> probList = new ArrayList<>();

                for (String item : itemList) {
                    probList.add(ProbabilityGenerator.generateProbability());
                }

                probs.add(probList);
                items.add(itemList);
            }
        }
    }

    public List<List<String>> getName() {
        return items;
    }

    public List<List<Double>> getProbs() {
        return probs;
    }
}
