package com.example.signalementrenouvelement;

public class Medicament {
    private String codeCIS;
    private String nom;
    private long nbsignalement;
    private String medicament;

    public Medicament(){}

    public Medicament(String codeCIS, String nom, long nbsignalement){
        this.codeCIS = codeCIS;
        this.nom = nom;
        this.nbsignalement = nbsignalement;
    }

    public String getCode(){
        return codeCIS;
    }

    public String getMedicament() { return medicament; }
    public String getNom(){
        return nom;
    }

    public long getNbsignalement() {
        return nbsignalement;
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(codeCIS + " ");
        sb.append(nom + " ");
        sb.append(nbsignalement);
        return sb.toString();
    }
}

