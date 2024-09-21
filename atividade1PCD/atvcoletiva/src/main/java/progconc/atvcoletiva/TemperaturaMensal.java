package progconc.atvcoletiva;

public class TemperaturaMensal {

    private String pais;
    private String cidade;
    private int mes;
    private int ano;
    private double tempMax;
    private double tempMin;
    private double tempMed;
    private int ndeleituras;

    public TemperaturaMensal(String cidade, String pais, int mes, int ano) {
        this.cidade = cidade;
        this.pais = pais;
        this.mes = mes;
        this.ano = ano;
        this.tempMax = Double.MAX_VALUE;
        this.tempMin = Double.MIN_VALUE;
        this.tempMed = 0;
        this.ndeleituras = 0;
        }

//add nova temperatura

public void addTemp(double temp) {
   
    if (temp > tempMax) {
        tempMax = temp;
    }
    if (temp < tempMin) {
        tempMin = temp;
    }
    tempMed += temp;
    ndeleituras++;
}

//calculo de temperatura média

public void calculaTempMed() {
    if (ndeleituras > 0) {
        tempMed /= ndeleituras;
    }
}

public String getCidade() {
    return cidade;
}

public String getPais() {
    return pais;
}

public int getMes() {
    return mes;
}

public int getAno() {
    return ano;
}

public double getTemperaturaMaxima() {
    return tempMax;
}

public double getTemperaturaMinima() {
    return tempMin;
}

public double getTemperaturaMedia() {
    return tempMed;
}

@Override
public String toString() {
    return "Cidade: " + cidade + ", País: " + pais + ", Ano: " + ano + ", Mês: " + mes +
           " | Max: " + tempMax + " | Min: " + tempMin + " | Média: " + tempMed;
} 

}
