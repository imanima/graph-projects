package statfunctions;

import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;

import java.util.*;
import java.util.function.Function;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summingInt;

public class NPS {
    @UserFunction
    @Description("NPS calculator example: [1,2,3,9,0,5,3,7,8,10] , [0,6] , [7,8], [9,10] ")
    public Map<String, Double> NPS(
            @Name("inputScores") List<Double> inputScores,
            @Name("detractors") List<Double> detractors,
            @Name("passives") List<Double> passives,
            @Name("promoters") List<Double> promoters

            ) throws Exception {

        if (inputScores == null || detractors == null || promoters == null || passives == null) {
            throw new RuntimeException("null value argument passed ");
        }

        Map<Double, Integer> sortedScores = new TreeMap<Double, Integer>
                (inputScores.stream().collect(groupingBy(Function.identity(), summingInt(e -> 1))));


        Map<String, Double> results = new HashMap<>();

        Interval lowerBracket = new Interval("detractors", detractors.get(0), detractors.get(1));
        Interval upperBracket = new Interval("promoters", promoters.get(0), promoters.get(1));
        Interval midBracket = new Interval("passives", passives.get(0), passives.get(1));


        Double sumLower = 0.0;
        Double sumUpper = 0.0;
        Double sumMiddle = 0.0;

        for (Double score : sortedScores.keySet()) {
            if (score >= lowerBracket.start && score <= lowerBracket.end)
                sumLower += sortedScores.get(score);
            else if (score >= upperBracket.start && score <= upperBracket.end)
                sumUpper += sortedScores.get(score);
            else
                sumMiddle += sortedScores.get(score);
        }

        results.put(lowerBracket.toString(), sumLower);
        results.put(upperBracket.toString(), sumUpper);
        results.put(midBracket.toString(), sumMiddle);
        results.put("total", inputScores.size() * 1.0);
        results.put("NPS", (double) calculateNPS(sumLower, sumUpper, inputScores.size()));
        return results;
    }


    private class Interval {
        private Double start;
        private Double end;
        private String prefix;

        public Interval(String prefix, Double start, Double end) {
            this.start = start;
            this.end = end;
            this.prefix = prefix;
        }

        @Override
        public String toString() {
            return prefix + "( " + String.valueOf(start) + " - " + String.valueOf(end) + " )";
        }
    }

    private long calculateNPS(Double sumLower, Double sumHigher, Integer count) {
        return Math.round(((sumHigher - sumLower) / count) * 100);
    }

}