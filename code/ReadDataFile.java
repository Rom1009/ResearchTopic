import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/*
 * Class representing the probability distribution.
 * Description: Using random number to generate probability Gaussian distribution
 */
class ProbabilityGenerator {
    // private static final double MEAN = 0.78;
    // private static final double STANDARD_DEVIATION = Math.sqrt(0.65);
    private static final Random random = new Random();

    public static double generateProbability(double MEAN, double STANDARD_DEVIATION) {
        double probability = Math.exp(-(Math.pow(random.nextGaussian() - MEAN, 2) / (2 * Math.pow(STANDARD_DEVIATION, 2)))) / (Math.sqrt(2 * Math.PI) * STANDARD_DEVIATION);
        probability = Math.max(0, Math.min(probability, 1));
        return probability;
    }
}

/*
 * Class Weight of each item 
 * Description: Generate each item's weight based on the range
 */
class WeightGenerator {
    private static final double MIN_WEIGHT = 0; // Minimum weight
    private static final double MAX_WEIGHT = 1.0; // Maximum weight
    private static final Random random = new Random();

    public static double generateWeight() {
        // Generate a random weight between MIN_WEIGHT and MAX_WEIGHT
        double weight = MIN_WEIGHT + (MAX_WEIGHT - MIN_WEIGHT) * random.nextDouble();
        return weight;
    }
}

public class ReadDataFile {
    private List<List<String>> items = new ArrayList<>();
    private List<List<Double>> probs = new ArrayList<>();;
    private List<List<Double>> weights = new ArrayList<>();
    /*
     * Name: ReadDataWithProbabilities
     * Input: Name of path to the file (String)
     * Output: 2 List elements to save the results (List<List<String>> and List<<List<Double>>)
     * Description: Read data each line from the file. After that for each item in each rows I create the probability
     * approriate. 
     */
    public void readDataWithProbabilities(String filePath, double mean, double std) throws FileNotFoundException {
        Random weightRandomizer = new Random();
        try (Scanner scanner = new Scanner(new File(filePath))) {
            while (scanner.hasNextLine()) {
                String[] lineItems = scanner.nextLine().split(" "); // Assuming comma-separated values
                List<String> itemList = new ArrayList<>(Arrays.asList(lineItems));
                List<Double> probList = new ArrayList<>();
                List<Double> weightList = new ArrayList<>();
                for (String item : itemList) {
                    probList.add(ProbabilityGenerator.generateProbability(mean,std));
                    weightList.add(0.1 + (1.0 - 0.1) * weightRandomizer.nextDouble());
                }

                probs.add(probList);
                items.add(itemList);
                weights.add(weightList);
            }
        }
    }

    public List<List<String>> getName() {
        return items;
    }

    public List<List<Double>> getProbs() {
        return probs;
    }

    public List<List<Double>> getWeight() {
        return weights;
    }
}