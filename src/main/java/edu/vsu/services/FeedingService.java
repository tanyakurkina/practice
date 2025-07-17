package edu.vsu.services;

import edu.vsu.entity.FeedingRecord;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class FeedingService {
    public static String findAnimalWithMostFoodLastMonth(List<FeedingRecord> records) {
        LocalDate now = LocalDate.now();
        LocalDate lastMonth = now.minusMonths(1);

        return records.stream()
                .filter(record -> record.getDate().getMonth().equals(lastMonth.getMonth()))
                .collect(Collectors.groupingBy(FeedingRecord::getAnimalName, Collectors.summingDouble(FeedingRecord::getFoodWeight)))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("No data");
    }

    public static String findMonthWithMostVariety(List<FeedingRecord> records) {
        return records.stream()
                .collect(Collectors.groupingBy(record -> record.getDate().getMonth(),
                        Collectors.flatMapping(record -> record.getProducts().stream(), Collectors.toSet())))
                .entrySet().stream()
                .max(Comparator.comparingInt(entry -> entry.getValue().size()))
                .map(entry -> entry.getKey().toString())
                .orElse("No data");
    }

    private static Map<String, Set<String>> getProductsByMonth(List<FeedingRecord> records, LocalDate date) {
        return records.stream()
                .filter(record -> record.getDate().getMonth().equals(date.getMonth()))
                .collect(Collectors.groupingBy(FeedingRecord::getAnimalName,
                        Collectors.flatMapping(record -> record.getProducts().stream(), Collectors.toSet())));
    }
}
