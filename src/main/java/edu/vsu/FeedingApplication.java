package edu.vsu;

import edu.vsu.entity.FeedingRecord;
import edu.vsu.services.FeedingFileHandler;
import edu.vsu.services.FeedingRecordGenerator;
import edu.vsu.services.FeedingService;

import java.util.List;

/*
Смотритель зоопарка ведет статистику кормежки животных, записывает в файл информацию в следующем формате:
Дата;название животного;список продуктов;вес корма
Необходимо найти следующую информацию:
  Животное, которое съело больше всего корма за последний месяц
  Найти месяц, в который животные получают самое разнообразные питание
  Для каждого животного, найти продукт, которое оно его в прошлом месяце, но не его в этом
 */

public class FeedingApplication {
    private static final String PATH = "data.txt";
    public static void main(String[] args) {
         FeedingFileHandler feedingFileHandler = new FeedingFileHandler(new FeedingRecordGenerator());

        feedingFileHandler.generateFeedingRecordsFile(PATH, 10);

        List<FeedingRecord> records = feedingFileHandler.readFeedingRecords(PATH);

        System.out.println("Animal with most food last month: " +
                FeedingService.findAnimalWithMostFoodLastMonth(records));

        System.out.println("Month with most variety: " +
                FeedingService.findMonthWithMostVariety(records));

        System.out.println("Products not repeated: " +
                FeedingService.findProductsNotRepeated(records));
    }
}
