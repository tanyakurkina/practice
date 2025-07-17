package edu.vsu.service;

import edu.vsu.entity.FeedingRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class FeedingService {
    private static final Logger logger = LogManager.getLogger(FeedingService.class);

    public static String findAnimalWithMostFoodLastMonth(List<FeedingRecord> records) {
        logger.info("Поиск животного с наибольшим потреблением пищи за последний месяц");
        LocalDate now = LocalDate.now();
        LocalDate lastMonth = now.minusMonths(1);
        logger.debug("Текущая дата: {}, месяц назад: {}", now, lastMonth);

        try {
            String result = records.stream()
                    .filter(record -> {
                        boolean matches = record.getDate().getMonth().equals(lastMonth.getMonth());
                        logger.trace("Запись {} соответствует фильтру месяца: {}", record, matches);
                        return matches;
                    })
                    .collect(Collectors.groupingBy(FeedingRecord::getAnimalName,
                            Collectors.summingDouble(FeedingRecord::getFoodWeight)))
                    .entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(entry -> {
                        logger.debug("Найдено животное с максимальным потреблением: {} ({} кг)",
                                entry.getKey(), entry.getValue());
                        return entry.getKey();
                    })
                    .orElse("No data");

            logger.info("Результат поиска: {}", result);
            return result;
        } catch (Exception e) {
            logger.error("Ошибка при поиске животного с наибольшим потреблением пищи", e);
            return "Error occurred";
        }
    }

    public static String findMonthWithMostVariety(List<FeedingRecord> records) {
        logger.info("Поиск месяца с наибольшим разнообразием продуктов");

        try {
            String result = records.stream()
                    .collect(Collectors.groupingBy(record -> record.getDate().getMonth(),
                            Collectors.flatMapping(record -> record.getProducts().stream(), Collectors.toSet())))
                    .entrySet().stream()
                    .max(Comparator.comparingInt(entry -> {
                        int size = entry.getValue().size();
                        logger.trace("Месяц {}: {} уникальных продуктов", entry.getKey(), size);
                        return size;
                    }))
                    .map(entry -> {
                        logger.debug("Найден месяц с наибольшим разнообразием: {} ({} продуктов)",
                                entry.getKey(), entry.getValue().size());
                        return entry.getKey().toString();
                    })
                    .orElse("No data");

            logger.info("Результат поиска: {}", result);
            return result;
        } catch (Exception e) {
            logger.error("Ошибка при поиске месяца с наибольшим разнообразием продуктов", e);
            return "Error occurred";
        }
    }

    public static Map<String, List<String>> findProductsNotRepeated(List<FeedingRecord> records) {
        logger.info("Поиск продуктов, которые не повторялись в текущем месяце");
        LocalDate now = LocalDate.now();
        LocalDate lastMonth = now.minusMonths(1);
        logger.debug("Текущая дата: {}, месяц назад: {}", now, lastMonth);

        try {
            Map<String, Set<String>> productsThisMonth = getProductsByMonth(records, now);
            Map<String, Set<String>> productsLastMonth = getProductsByMonth(records, lastMonth);

            logger.debug("Продукты текущего месяца: {}", productsThisMonth);
            logger.debug("Продукты прошлого месяца: {}", productsLastMonth);

            Map<String, List<String>> result = productsLastMonth.entrySet().stream()
                    .filter(entry -> productsThisMonth.containsKey(entry.getKey()))
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            entry -> {
                                List<String> uniqueProducts = entry.getValue().stream()
                                        .filter(product -> !productsThisMonth.get(entry.getKey()).contains(product))
                                        .toList();
                                logger.trace("Для животного {} найдено {} уникальных продуктов",
                                        entry.getKey(), uniqueProducts.size());
                                return uniqueProducts;
                            }));

            logger.info("Результат поиска: {}", result);
            return result;
        } catch (Exception e) {
            logger.error("Ошибка при поиске неповторяющихся продуктов", e);
            return Collections.emptyMap();
        }
    }

    private static Map<String, Set<String>> getProductsByMonth(List<FeedingRecord> records, LocalDate date) {
        logger.debug("Получение продуктов по месяцу: {}", date.getMonth());
        return records.stream()
                .filter(record -> {
                    boolean matches = record.getDate().getMonth().equals(date.getMonth());
                    logger.trace("Запись {} соответствует фильтру месяца: {}", record, matches);
                    return matches;
                })
                .collect(Collectors.groupingBy(FeedingRecord::getAnimalName,
                        Collectors.flatMapping(record -> record.getProducts().stream(), Collectors.toSet())));
    }
}