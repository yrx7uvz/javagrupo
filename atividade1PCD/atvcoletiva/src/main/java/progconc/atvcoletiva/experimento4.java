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

public class Experimento4 {

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis(); // Captura o tempo inicial

        String directoryPath = "arquivos\\temperaturas_cidades"; // Substitua pelo caminho do diretório
        File folder = new File(directoryPath);
        File[] listOfFiles = folder.listFiles((dir, name) -> name.endsWith(".csv"));

        if (listOfFiles == null || listOfFiles.length < 320) {
            System.err.println("Número insuficiente de arquivos, deve haver pelo menos 320 arquivos CSV.");
            return;
        }

        // Divide a lista de arquivos em oito partes para serem processadas por cada thread
        File[] firstPartFiles = new File[40];
        File[] secondPartFiles = new File[40];
        File[] thirdPartFiles = new File[40];
        File[] fourthPartFiles = new File[40];
        File[] fifthPartFiles = new File[40];
        File[] sixthPartFiles = new File[40];
        File[] seventhPartFiles = new File[40];
        File[] eighthPartFiles = new File[40];

        System.arraycopy(listOfFiles, 0, firstPartFiles, 0, 40);
        System.arraycopy(listOfFiles, 40, secondPartFiles, 0, 40);
        System.arraycopy(listOfFiles, 80, thirdPartFiles, 0, 40);
        System.arraycopy(listOfFiles, 120, fourthPartFiles, 0, 40);
        System.arraycopy(listOfFiles, 160, fifthPartFiles, 0, 40);
        System.arraycopy(listOfFiles, 200, sixthPartFiles, 0, 40);
        System.arraycopy(listOfFiles, 240, seventhPartFiles, 0, 40);
        System.arraycopy(listOfFiles, 280, eighthPartFiles, 0, 40);

        // Lista compartilhada para armazenar todas as leituras de temperatura diárias
        List<TemperaturaDiaria> listaDeTemperaturas = Collections.synchronizedList(new ArrayList<>());

        // Cria oito threads, cada uma processando uma parte dos arquivos
        Thread thread1 = new Thread(() -> processFiles(firstPartFiles, listaDeTemperaturas));
        Thread thread2 = new Thread(() -> processFiles(secondPartFiles, listaDeTemperaturas));
        Thread thread3 = new Thread(() -> processFiles(thirdPartFiles, listaDeTemperaturas));
        Thread thread4 = new Thread(() -> processFiles(fourthPartFiles, listaDeTemperaturas));
        Thread thread5 = new Thread(() -> processFiles(fifthPartFiles, listaDeTemperaturas));
        Thread thread6 = new Thread(() -> processFiles(sixthPartFiles, listaDeTemperaturas));
        Thread thread7 = new Thread(() -> processFiles(seventhPartFiles, listaDeTemperaturas));
        Thread thread8 = new Thread(() -> processFiles(eighthPartFiles, listaDeTemperaturas));

        // Inicia as threads
        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();
        thread5.start();
        thread6.start();
        thread7.start();
        thread8.start();

        try {
            // Aguarda as threads terminarem
            thread1.join();
            thread2.join();
            thread3.join();
            thread4.join();
            thread5.join();
            thread6.join();
            thread7.join();
            thread8.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Processa as temperaturas após a leitura de todos os arquivos
        ProcessaDados processador = new ProcessaDados();
        processador.processardados(listaDeTemperaturas);

        // Exibe as estatísticas finais de temperaturas
        showStats(processador);

        long endTime = System.currentTimeMillis(); // Captura o tempo final
        long executionTime = endTime - startTime;  // Calcula o tempo total

        // Salvar o tempo de execução em um arquivo txt
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("tempo_execucao_threads8.txt"))) {
            writer.write("Tempo de execução com 8 threads (ms): " + executionTime);
            System.out.println("Tempo de execução salvo no arquivo: tempo_execucao_threads8.txt");
        } catch (IOException e) {
            System.err.println("Erro ao salvar o arquivo: " + e.getMessage());
        }
    }

    // Método que processa os arquivos de uma parte específica
    private static void processFiles(File[] files, List<TemperaturaDiaria> temperaturasCompartilhadas) {
        for (File file : files) {
            System.out.println("Lendo arquivo: " + file.getName());
            List<TemperaturaDiaria> temperaturas = readCSV(file.toPath());
            synchronized (temperaturasCompartilhadas) {
                temperaturasCompartilhadas.addAll(temperaturas);
            }
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
