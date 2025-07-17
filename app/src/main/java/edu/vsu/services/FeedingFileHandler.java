package edu.vsu.services;

import edu.vsu.entity.FeedingRecord;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FeedingFileHandler {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final String DELIMITER = ";";
    private static final String PRODUCTS_SEPARATOR = ",";


    private final FeedingRecordGenerator feedingRecordGenerator;

    public FeedingFileHandler(FeedingRecordGenerator feedingRecordGenerator) {
        this.feedingRecordGenerator = feedingRecordGenerator;
    }

    public List<FeedingRecord> readFeedingRecords(String filePath) {
        List<FeedingRecord> records = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        try (Stream<String> lines = Files.lines(Paths.get(filePath))) {
            records = lines.map(line -> {
                String[] parts = line.split(";");
                LocalDate date = LocalDate.parse(parts[0], formatter);
                String animalName = parts[1];
                List<String> products = Arrays.asList(parts[2].split(","));
                double foodWeight = Double.parseDouble(parts[3]);
                return new FeedingRecord(date, animalName, products, foodWeight);
            }).collect(Collectors.toList());
        } catch (IOException e) {
            return records;
        }

        return records;
    }

    public void generateFeedingRecordsFile(String filePath, int recordCount) {
        List<FeedingRecord> records = feedingRecordGenerator.generateRandomRecords(recordCount);
        Path path = Paths.get(filePath);

        List<String> lines = records.stream()
                .map(record -> {
                    String dateStr = record.getDate().format(DATE_FORMATTER);
                    String productsStr = String.join(PRODUCTS_SEPARATOR, record.getProducts());
                    return String.join(DELIMITER,
                            dateStr,
                            record.getAnimalName(),
                            productsStr,
                            String.valueOf(record.getFoodWeight())
                    );
                })
                .collect(Collectors.toList());

        try {
        Files.write(path, lines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
