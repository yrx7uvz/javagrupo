package progconc.atvcoletiva;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

public class Experimento10 {

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis(); // Captura o tempo inicial

        String directoryPath = "arquivos\\temperaturas_cidades"; // Substitua pelo caminho do diretório
        File folder = new File(directoryPath);
        File[] listOfFiles = folder.listFiles((dir, name) -> name.endsWith(".csv"));

        if (listOfFiles == null || listOfFiles.length < 320) {
            System.err.println("Número insuficiente de arquivos, deve haver pelo menos 320 arquivos CSV.");
            return;
        }

        // Lista compartilhada para armazenar todas as leituras de temperatura diárias
        List<TemperaturaDiaria> listaDeTemperaturas = Collections.synchronizedList(new ArrayList<>());

        // Cria 320 threads, cada uma processando 1 arquivo
        Thread[] threads = new Thread[320];
        for (int i = 0; i < 320; i++) {
            final int index = i;
            threads[index] = new Thread(() -> processFiles(listOfFiles[index], listaDeTemperaturas));
        }

        // Inicia as threads
        for (Thread thread : threads) {
            thread.start();
        }

        // Aguarda as threads terminarem
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Processa as temperaturas após a leitura de todos os arquivos
        ProcessaDados processador = new ProcessaDados();
        processador.processardados(listaDeTemperaturas);

        // Exibe as estatísticas finais de temperaturas
        showStats(processador);

        long endTime = System.currentTimeMillis(); // Captura o tempo final
        long executionTime = endTime - startTime;  // Calcula o tempo total

        // Salvar o tempo de execução em um arquivo txt
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("tempo_execucao_experimento10.txt"))) {
            writer.write("Tempo de execução com 320 threads (ms): " + executionTime);
            System.out.println("Tempo de execução salvo no arquivo: tempo_execucao_experimento10.txt");
        } catch (IOException e) {
            System.err.println("Erro ao salvar o arquivo: " + e.getMessage());
        }
    }

    // Método que processa o arquivo de uma cidade específica
    private static void processFiles(File file, List<TemperaturaDiaria> temperaturasCompartilhadas) {
        System.out.println("Lendo arquivo: " + file.getName());
        List<TemperaturaDiaria> temperaturas = readCSV(file.toPath());
        synchronized (temperaturasCompartilhadas) {
            temperaturasCompartilhadas.addAll(temperaturas);
        }
    }

    // Método que lê os arquivos CSV e retorna as instâncias de TemperaturaDiaria
    private static List<TemperaturaDiaria> readCSV(Path filePath) {
        List<TemperaturaDiaria> temperaturas = new ArrayList<>();

        try (CSVReader csvReader = new CSVReader(new FileReader(filePath.toFile()))) {
            String[] nextRecord;
            csvReader.readNext(); // Ignora a primeira linha (cabeçalho)

            while ((nextRecord = csvReader.readNext()) != null) {
                String country = nextRecord[0];
                String city = nextRecord[1];
                int month = Integer.parseInt(nextRecord[2]);
                int day = Integer.parseInt(nextRecord[3]);
                int year = Integer.parseInt(nextRecord[4]);
                double avgTemperature = Double.parseDouble(nextRecord[5]);

                TemperaturaDiaria temp = new TemperaturaDiaria(country, city, day, month, year, avgTemperature);
                temperaturas.add(temp);
            }
        } catch (IOException | CsvValidationException e) {
            System.err.println("Erro ao ler o arquivo: " + e.getMessage());
        }

        return temperaturas;
    }

    private static void showStats(ProcessaDados processador) {
        Map<String, Map<Integer, Map<Integer, TemperaturaMensal>>> estatisticas = processador.getmonthlyStats();
        
        // Exibindo os dados processados para cada cidade, ano e mês
        for (String cidade : estatisticas.keySet()) {
            Map<Integer, Map<Integer, TemperaturaMensal>> anos = estatisticas.get(cidade);
            for (Integer ano : anos.keySet()) {
                Map<Integer, TemperaturaMensal> meses = anos.get(ano);
                for (Integer mes : meses.keySet()) {
                    TemperaturaMensal tempMensal = meses.get(mes);
                    System.out.println(tempMensal);
                }
            }
        }
    }
}

