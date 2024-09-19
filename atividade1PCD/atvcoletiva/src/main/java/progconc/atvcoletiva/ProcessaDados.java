package progconc.atvcoletiva;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProcessaDados {
    private Map<String, Map<Integer, Map<Integer, TemperaturaMensal>>> monthlyStats;

    public ProcessaDados() {
        monthlyStats = new HashMap<>();
    }

    // Método para processar uma lista de temperaturas diárias
    
    public void processardados(List<TemperaturaDiaria> temperaturasDiarias) {
        for (TemperaturaDiaria tempDiaria : temperaturasDiarias) {
            String cidade = tempDiaria.getcidade();
            String pais = tempDiaria.getpais();
            int mes = tempDiaria.getmes();
            int ano = tempDiaria.getano();

    // Criar ou obter a temperatura mensal
            Map<Integer, Map<Integer, TemperaturaMensal>> temperaturasPorAno = monthlyStats
                .computeIfAbsent(cidade, k -> new HashMap<>());

            Map<Integer, TemperaturaMensal> temperaturasPorMes = temperaturasPorAno
                .computeIfAbsent(ano, k -> new HashMap<>());

            TemperaturaMensal temperaturaMensal = temperaturasPorMes
                .computeIfAbsent(mes, k -> new TemperaturaMensal(cidade, pais, mes, ano));

            // Adicionar a temperatura diária à mensal
            temperaturaMensal.addTemp(tempDiaria.gettemperatura());
        }

        // Calcular a média de temperatura para cada mês
        for (Map<Integer, Map<Integer, TemperaturaMensal>> anos : monthlyStats.values()) {
            for (Map<Integer, TemperaturaMensal> meses : anos.values()) {
                for (TemperaturaMensal tempMensal : meses.values()) {
                    tempMensal.calculaTempMed(); // Calcula a média para cada mês
                }
            }
        }
    }

    // Método para obter as estatísticas mensais
    public Map<String, Map<Integer, Map<Integer, TemperaturaMensal>>> getmonthlyStats() {
        return monthlyStats;
    }
}
