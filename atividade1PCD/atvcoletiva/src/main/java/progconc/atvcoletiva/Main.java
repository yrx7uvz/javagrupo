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

public class Main {

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis(); // Captura o tempo inicial

        String directoryPath = "arquivos\\temperaturas_cidades"; // Substitua pelo caminho do diretório

        File folder = new File(directoryPath);
        File[] listOfFiles = folder.listFiles((dir, name) -> name.endsWith(".csv"));

        // Lista para armazenar todas as leituras de temperatura diárias
        List<TemperaturaDiaria> listaDeTemperaturas = new ArrayList<>();
        
        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    System.out.println("Lendo arquivo: " + file.getName());
                    // Adicionar as temperaturas lidas para processar depois
                    listaDeTemperaturas.addAll(readCSV(file.toPath()));
                }
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
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("tempo_execucao.txt"))) {
            writer.write("Tempo de execução (ms): " + executionTime);
            System.out.println("Tempo de execução salvo no arquivo: tempo_execucao.txt");
        } catch (IOException e) {
            System.err.println("Erro ao salvar o arquivo: " + e.getMessage());
        }
    }

    private static List<TemperaturaDiaria> readCSV(Path filePath) {
        List<TemperaturaDiaria> temperaturas = new ArrayList<>();

        try (CSVReader csvReader = new CSVReader(new FileReader(filePath.toFile()))) {
            String[] nextRecord;
            // Pular o cabeçalho, se necessário
            csvReader.readNext(); // Ignora a primeira linha (cabeçalho)

            // Lê cada linha e transforma em uma instância de TemperaturaDiaria
            while ((nextRecord = csvReader.readNext()) != null) {
                String country = nextRecord[0];
                String city = nextRecord[1];
                int month = Integer.parseInt(nextRecord[2]);
                int day = Integer.parseInt(nextRecord[3]);
                int year = Integer.parseInt(nextRecord[4]);
                double avgTemperature = Double.parseDouble(nextRecord[5]);

                // Cria uma instância de TemperaturaDiaria
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
