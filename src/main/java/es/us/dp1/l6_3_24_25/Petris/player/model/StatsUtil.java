package es.us.dp1.l6_3_24_25.Petris.player.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StatsUtil {
    public static List<Double> calculateBoxPlotStats(List<Integer> data) {
        if (data == null || data.isEmpty()) {
            return List.of(0.0, 0.0, 0.0, 0.0, 0.0);
        }

        List<Integer> sortedData = new ArrayList<>(data);
        Collections.sort(sortedData);

        double min = sortedData.get(0);
        double max = sortedData.get(sortedData.size() - 1);
        double q1 = getPercentile(sortedData, 25);
        double median = getPercentile(sortedData, 50);
        double q3 = getPercentile(sortedData, 75);

        return List.of(min, q1, median, q3, max);
    }

    private static double getPercentile(List<Integer> sortedData, double percentile) {
        int n = sortedData.size();
        double pos = (percentile / 100.0) * (n - 1);

        int lowerIndex = (int) Math.floor(pos);
        int upperIndex = (int) Math.ceil(pos);

        if (lowerIndex == upperIndex) {
            return sortedData.get(lowerIndex);
        }

        double weight = pos - lowerIndex;
        return sortedData.get(lowerIndex) * (1 - weight) + sortedData.get(upperIndex) * weight;
    }
}
