package Javagrupo.leitorCSV;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

public class App {

    public static void main(String[] args) {
        String directoryPath = "."; // Substitua pelo caminho do diretório

        File folder = new File(directoryPath);
        File[] listOfFiles = folder.listFiles((dir, name) -> name.endsWith(".csv"));

        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    System.out.println("Lendo arquivo: " + file.getName());
                    readCSV(file.toPath());
                }
            }
        }
    }

    private static void readCSV(Path filePath) {
        try (CSVReader csvReader = new CSVReader(new FileReader(filePath.toFile()))) {
            String[] nextRecord;
            while ((nextRecord = csvReader.readNext()) != null) {
                System.out.println("Linha lida: " + String.join(", ", nextRecord));
            }
        } catch (IOException | CsvValidationException e) {
            System.err.println("Erro ao ler o arquivo: " + e.getMessage());
        }
    }
}
