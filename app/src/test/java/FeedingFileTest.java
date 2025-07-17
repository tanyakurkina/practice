import edu.vsu.entity.FeedingRecord;
import edu.vsu.services.FeedingFileHandler;
import edu.vsu.services.FeedingRecordGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

public class FeedingFileTest {
    @TempDir
    Path tempDir;

    @Mock
    private FeedingRecordGenerator mockGenerator;

    private FeedingFileHandler handler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        handler = new FeedingFileHandler(mockGenerator);
    }

    // Тестовые данные
    private List<FeedingRecord> createTestRecords() {
        return Arrays.asList(
                new FeedingRecord(LocalDate.of(2023, 1, 1), "Лев", Arrays.asList("Мясо"), 5.0),
                new FeedingRecord(LocalDate.of(2023, 1, 2), "Тигр", Arrays.asList("Рыба", "Мясо"), 7.5)
        );
    }

    private List<String> createTestFileContent() {
        return Arrays.asList(
                "01-01-2023;Лев;Мясо;5.0",
                "02-01-2023;Тигр;Рыба,Мясо;7.5"
        );
    }

    @Test
    void readFeedingRecords_shouldParseFileCorrectly() throws Exception {
        // Arrange
        Path testFile = tempDir.resolve("test_records.txt");
        Files.write(testFile, Arrays.asList(
                "01-01-2023;Лев;Мясо,Курица;5.7",
                "15-01-2023;Жираф;Листья,Фрукты;12.3"
        ));

        // Act
        List<FeedingRecord> records = handler.readFeedingRecords(testFile.toString());

        // Assert
        assertEquals(2, records.size());
        assertRecordEquals(records.get(0), LocalDate.of(2023, 1, 1), "Лев",
                Arrays.asList("Мясо", "Курица"), 5.7);
        assertRecordEquals(records.get(1), LocalDate.of(2023, 1, 15), "Жираф",
                Arrays.asList("Листья", "Фрукты"), 12.3);
    }

    @Test
    void generateFeedingRecordsFile_shouldWriteCorrectFormat() throws Exception {
        // Arrange
        Path outputFile = tempDir.resolve("output_records.txt");
        when(mockGenerator.generateRandomRecords(anyInt())).thenReturn(createTestRecords());

        // Act
        handler.generateFeedingRecordsFile(outputFile.toString(), 2);

        // Assert
        List<String> writtenLines = Files.readAllLines(outputFile);
        assertEquals(createTestFileContent(), writtenLines);
    }

    // Вспомогательные методы
    private void assertRecordEquals(FeedingRecord record, LocalDate expectedDate,
                                    String expectedAnimal, List<String> expectedProducts,
                                    double expectedWeight) {
        assertEquals(expectedDate, record.getDate());
        assertEquals(expectedAnimal, record.getAnimalName());
        assertEquals(expectedProducts, record.getProducts());
        assertEquals(expectedWeight, record.getFoodWeight(), 0.001);
    }
}
