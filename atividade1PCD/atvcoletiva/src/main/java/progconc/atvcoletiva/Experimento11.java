package progconc.atvcoletiva;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

public class Experimento11 {

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis(); // Captura o tempo inicial

        String directoryPath = "arquivos\\temperaturas_cidades"; // Substitua pelo caminho do diretório
        File folder = new File(directoryPath);
        File[] listOfFiles = folder.listFiles((dir, name) -> name.endsWith(".csv"));

        List<TemperaturaDiaria> listaDeTemperaturas = new ArrayList<>();

        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    System.out.println("Lendo arquivo: " + file.getName());
                    listaDeTemperaturas.addAll(readCSV(file.toPath()));
                }
            }
        }

        // Processa as temperaturas usando threads por ano
        ProcessaDados processador = new ProcessaDados();
        List<Thread> threads = new ArrayList<>();

        // Agrupar as temperaturas por ano
        Map<Integer, List<TemperaturaDiaria>> temperaturasPorAno = processador.agruparPorAno(listaDeTemperaturas);

        for (Map.Entry<Integer, List<TemperaturaDiaria>> entry : temperaturasPorAno.entrySet()) {
            int ano = entry.getKey();
            List<TemperaturaDiaria> temperaturasDoAno = entry.getValue();

            Thread thread = new Thread(() -> {
                processador.processarDados(temperaturasDoAno);
            });
            threads.add(thread);
            thread.start();
        }

        // Aguardar todas as threads terminarem
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                System.err.println("Erro ao aguardar a thread: " + e.getMessage());
            }
        }

        // Exibe as estatísticas finais de temperaturas
        showStats(processador);

        long endTime = System.currentTimeMillis(); // Captura o tempo final
        long executionTime = endTime - startTime;  // Calcula o tempo total

        // Salvar o tempo de execução em um arquivo txt
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("exp11_tempo_execucao.txt"))) {
            writer.write("Tempo de execução (ms): " + executionTime);
            System.out.println("Tempo de execução salvo no arquivo: exp11_tempo_execucao.txt");
        } catch (IOException e) {
            System.err.println("Erro ao salvar o arquivo: " + e.getMessage());
        }
    }

    private static List<TemperaturaDiaria> readCSV(Path filePath) {
        List<TemperaturaDiaria> temperaturas = new ArrayList<>();

        try (CSVReader csvReader = new CSVReader(new FileReader(filePath.toFile()))) {
            String[] nextRecord;
            csvReader.readNext(); // Ignora a primeira linha (cabeçalho)

            while ((nextRecord = csvReader.readNext()) != null) {
                try {
                    String country = nextRecord[0];
                    String city = nextRecord[1];
                    int month = Integer.parseInt(nextRecord[2]);
                    int day = Integer.parseInt(nextRecord[3]);
                    int year = Integer.parseInt(nextRecord[4]);
                    double avgTemperature = Double.parseDouble(nextRecord[5]);

                    TemperaturaDiaria temp = new TemperaturaDiaria(country, city, day, month, year, avgTemperature);
                    temperaturas.add(temp);
                } catch (NumberFormatException e) {
                    System.err.println("Erro de formatação nos dados: " + String.join(", ", nextRecord));
                }
            }
        } catch (IOException | CsvValidationException e) {
            System.err.println("Erro ao ler o arquivo: " + e.getMessage());
        }

        return temperaturas;
    }

    private static void showStats(ProcessaDados processador) {
        Map<String, Map<Integer, Map<Integer, TemperaturaMensal>>> estatisticas = processador.getmonthlyStats();

        for (String cidade : estatisticas.keySet()) {
            System.out.println("Cidade: " + cidade);
            Map<Integer, Map<Integer, TemperaturaMensal>> anos = estatisticas.get(cidade);
            for (Integer ano : anos.keySet()) {
                System.out.println("  Ano: " + ano);
                Map<Integer, TemperaturaMensal> meses = anos.get(ano);
                for (Integer mes : meses.keySet()) {
                    TemperaturaMensal tempMensal = meses.get(mes);
                    System.out.println("    " + tempMensal);
                }
            }
        }
    }
}
