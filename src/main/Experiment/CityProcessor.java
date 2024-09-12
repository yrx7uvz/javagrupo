package main.Experiment;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CityProcessor {
    private final CityData cityData;

    public CityProcessor(CityData cityData) {
        this.cityData = cityData;
    }

    public void processFile(String filePath) {
        Path path = Paths.get(filePath);

        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            reader.readLine(); // Ignora a linha do cabeçalho
            while ((line = reader.readLine()) != null) {
                processLine(line);
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo: " + e.getMessage());
        }
    }

    private void processLine(String line) {
        String[] parts = line.split(",");
        if (parts.length != 6) {
            System.out.println("Linha com formato inesperado: " + line);
            return;
        }

        try {
            int month = Integer.parseInt(parts[2]);
            double temperature = Double.parseDouble(parts[5]);
            cityData.addTemperature(month, temperature);
        } catch (NumberFormatException e) {
            System.out.println("Erro ao processar linha: " + line + " - " + e.getMessage());
        }
    }
}