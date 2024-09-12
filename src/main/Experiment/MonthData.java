package main.Experiment;

class MonthData {
    private double sum = 0;
    private double max = Double.NEGATIVE_INFINITY;
    private double min = Double.POSITIVE_INFINITY;
    private int count = 0;

    public synchronized void addTemperature(double temperature) {
        sum += temperature;
        if (temperature > max) max = temperature;
        if (temperature < min) min = temperature;
        count++;
    }

    public synchronized double getAverage() {
        return count > 0 ? sum / count : Double.NaN;
    }

    public synchronized double getMax() {
        return max;
    }

    public synchronized double getMin() {
        return min;
    }
}