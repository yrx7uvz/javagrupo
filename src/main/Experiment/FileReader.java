package main.Experiment;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class FileReader {

    private final CityData cityData = new CityData();

    public void processCsvFiles(String directoryPath, int numThreads) {
        Path path = Paths.get(directoryPath);

        // Captura o tempo de início
        long startTime = System.currentTimeMillis();

        // Lista todos os arquivos CSV no diretório
        List<Path> csvFiles = new ArrayList<>();
        try {
            Files.list(path)
                .filter(Files::isRegularFile)
                .filter(p -> p.toString().endsWith(".csv"))
                .forEach(csvFiles::add);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // Cria um ExecutorService com o número de threads especificado
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        List<Future<Void>> futures = new ArrayList<>();

        // Divide os arquivos entre as threads
        List<List<Path>> dividedFiles = divideFiles(csvFiles, numThreads);

        for (List<Path> fileGroup : dividedFiles) {
            futures.add(executor.submit(new CsvFileProcessorTask(fileGroup)));
        }

        // Aguarda a conclusão de todas as tarefas
        for (Future<Void> future : futures) {
            try {
                future.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // Solicita o término ordenado do ExecutorService. Isso significa que o executor não aceitará novas tarefas
        // mas continuará a processar as tarefas que já foram submetidas até que todas estejam concluídas.
        // após o shutdown(), o executor não pode mais ser usado para enviar novas tarefas.
        executor.shutdown();

        // Captura o tempo após a finalização do processamento
        long endTime = System.currentTimeMillis();
        System.out.println("Tempo total de execução com " + numThreads + " threads: " + (endTime - startTime) + " ms");

        // Exibe os resultados processados
        displayCityData(cityData);
    }

    private List<List<Path>> divideFiles(List<Path> files, int numThreads) {
        // Lista para armazenar as sub-listas de arquivos
        List<List<Path>> dividedFiles = new ArrayList<>();
        
        // Calcula o número de arquivos que cada thread deve processar
        int totalFiles = files.size();
        int filesPerThread = (int) Math.ceil((double) totalFiles / numThreads);
        
        // Divide a lista de arquivos em sub-listas
        for (int start = 0; start < totalFiles; start += filesPerThread) {
            // Calcula o índice final para a sub-lista
            int end = Math.min(start + filesPerThread, totalFiles);
            
            // Cria uma sub-lista com arquivos do índice 'start' ao índice 'end'
            List<Path> fileSubset = new ArrayList<>(files.subList(start, end));
            
            // Adiciona a sub-lista à lista principal
            dividedFiles.add(fileSubset);
        }

        // Retorna a lista de sub-listas
        return dividedFiles;
    }
    
    private void displayCityData(CityData cityData) {
        // Exibe os dados processados
        System.out.println("Dados processados:");
        for (Map.Entry<Integer, MonthData> entry : cityData.getMonthData().entrySet()) {
            int month = entry.getKey();
            MonthData monthData = entry.getValue();
            System.out.printf("Mês %d: Média = %.2f, Máxima = %.2f, Mínima = %.2f%n",
                month,
                monthData.getAverage(),
                monthData.getMax(),
                monthData.getMin());
        }
    }

    private class CsvFileProcessorTask implements Callable<Void> {
        private final List<Path> files;

        CsvFileProcessorTask(List<Path> files) {
            this.files = files;
        }

        @Override
        public Void call() {
            CityProcessor processor = new CityProcessor(cityData);
            for (Path file : files) {
                processor.processFile(file.toString());
            }
            return null;
        }
    }
}