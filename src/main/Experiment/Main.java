package main.Experiment;

public class Main {

    public static void main(String[] args) {
        String directoryPath = "/Users/ygormachado/Downloads/temperaturas_cidades/"; // Substitua pelo caminho real do diretório contendo os arquivos CSV
        int numThreads = 2; // Número de threads desejado no teste.

        FileReader fileReader = new FileReader();
        fileReader.processCsvFiles(directoryPath, numThreads);
    }
}