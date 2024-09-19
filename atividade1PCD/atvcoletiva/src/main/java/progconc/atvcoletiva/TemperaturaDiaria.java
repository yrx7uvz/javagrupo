package progconc.atvcoletiva;

//esta classe armazenas os dados diários de temperatura de cada cidade em um dia especifico

public class TemperaturaDiaria {

    private String pais;
    private String cidade;
    private int mes;
    private int dia;
    private int ano;
    private double temperatura;

    public TemperaturaDiaria(String pais, String cidade, int mes, int dia, int ano, double temperatura) {
        this.pais = pais;
        this.cidade = cidade;
        this.mes = mes;
        this.dia = dia;
        this.ano = ano;
        this.temperatura = temperatura;
        
    }

    //acesso aos dados

    public String getpais(){
        return pais;
    }

    public String getcidade(){
        return cidade;
    }

    public int getmes(){
        return mes;
    }

    public int getdia(){
        return dia;
    }

    public int getano(){
        return ano;
    }

    public double gettemperatura(){
        return temperatura;
    }


    
}

