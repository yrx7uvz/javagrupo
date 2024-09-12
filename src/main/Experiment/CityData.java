package main.Experiment;

import java.util.HashMap;
import java.util.Map;

public class CityData {
    private final Map<Integer, MonthData> monthDataMap = new HashMap<>();

    // Adiciona uma temperatura ao mês correspondente
    public synchronized void addTemperature(int month, double temperature) {
        MonthData monthData = monthDataMap.computeIfAbsent(month, k -> new MonthData());
        monthData.addTemperature(temperature);
    }

    // Retorna os dados de temperatura por mês
    public synchronized Map<Integer, MonthData> getMonthData() {
        return monthDataMap;
    }
}