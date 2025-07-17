package edu.vsu.services;

import edu.vsu.entity.FeedingRecord;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class FeedingRecordGenerator {


    private static final List<String> ANIMAL_NAMES = Arrays.asList(
            "Лев", "Тигр", "Медведь", "Жираф", "Зебра",
            "Слон", "Обезьяна", "Панда", "Кенгуру", "Носорог"
    );

    private static final List<String> FOOD_PRODUCTS = Arrays.asList(
            "Мясо", "Рыба", "Фрукты", "Овощи", "Зерно",
            "Сено", "Листья", "Корм", "Ягоды", "Орехи"
    );

    public List<FeedingRecord> generateRandomRecords(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> new FeedingRecord(
                        generateRandomDate(),
                        generateRandomAnimalName(),
                        generateRandomProducts(),
                        generateRandomWeight()
                ))
                .collect(Collectors.toList());
    }

    private LocalDate generateRandomDate() {
        int year = ThreadLocalRandom.current().nextInt(2020, 2024);
        int month = ThreadLocalRandom.current().nextInt(1, 13);
        int day = ThreadLocalRandom.current().nextInt(1, 29);
        return LocalDate.of(year, month, day);
    }

    private String generateRandomAnimalName() {
        return ANIMAL_NAMES.get(ThreadLocalRandom.current().nextInt(ANIMAL_NAMES.size()));
    }

    private List<String> generateRandomProducts() {
        int productCount = ThreadLocalRandom.current().nextInt(1, 4);
        Collections.shuffle(FOOD_PRODUCTS);
        return FOOD_PRODUCTS.subList(0, productCount);
    }

    private double generateRandomWeight() {
        return ThreadLocalRandom.current().nextDouble(0.5, 15.0);
    }
}
