import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReadDataFile {
    private List<List<String>> items;
    private List<List<Double>> probs;

    public ReadDataFile() {
        items = new ArrayList<>();
        probs = new ArrayList<>();
    }

    public double computeMean(double[] arr) {
        double sum = 0;
        for (double num : arr) {
            sum += num;
        }
        return sum / arr.length;
    }

    // Function to compute the standard deviation of an array of doubles
    public double computeStdDev(double[] arr, double mean) {
        double sum = 0;
        for (double num : arr) {
            sum += Math.pow(num - mean, 2);
        }
        double variance = sum / arr.length;
        return Math.sqrt(variance);
    }

    public void readDataWithProbabilities(String filePath) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                List<String> rowItemsList = new ArrayList<>();
                List<Double> rowProbabilitiesList = new ArrayList<>();
                String[] itemsStr = line.split(" ");
                double[] itemProbabilities = Arrays.stream(itemsStr)
                        .mapToDouble(Double::parseDouble)
                        .toArray();
                double rowMean = computeMean(itemProbabilities);
                double rowStdDev = computeStdDev(itemProbabilities, rowMean);
                for (double itemProbability : itemProbabilities) {
                    rowProbabilitiesList.add(computeProbability(itemProbability, rowMean, rowStdDev));
                }
                probs.add(rowProbabilitiesList);

                for (String i : itemsStr) {
                    rowItemsList.add(i);
                }
                items.add(rowItemsList);
            }
        }
    }

    private double computeProbability(double item, double mean, double stdDev) {
        return 1 / (stdDev * Math.sqrt(2 * Math.PI)) * Math.exp(-Math.pow(item - mean, 2) / (2 * Math.pow(stdDev, 2)));
    }

    public List<List<String>> getName() {
        return items;
    }

    public List<List<Double>> getProbs() {
        return probs;
    }

    
}
