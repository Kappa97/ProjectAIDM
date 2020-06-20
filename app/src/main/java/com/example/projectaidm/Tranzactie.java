package com.example.projectaidm;

public class Tranzactie {
    private String valuta1;
    private String valuta2;
    private Double suma1;
    private Double suma2;
    private String data;

    public Tranzactie() {
    }

    public String getValuta1() {
        return valuta1;
    }

    public void setValuta1(String valuta1) {
        this.valuta1 = valuta1;
    }

    public String getValuta2() {
        return valuta2;
    }

    public void setValuta2(String valuta2) {
        this.valuta2 = valuta2;
    }

    public Double getSuma1() {
        return suma1;
    }

    public void setSuma1(Double suma1) {
        this.suma1 = suma1;
    }

    public Double getSuma2() {
        return suma2;
    }

    public void setSuma2(Double suma2) {
        this.suma2 = suma2;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
