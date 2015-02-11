package com.example.antonio.provaanagrafe;

public class Utente {
    public static String[] campi = {"Nome", "Cognome"};
    private String Nome, Cognome;

    public Utente(String nome, String cognome) {
        Nome = nome;
        Cognome = cognome;
    }

    public String getNome() {
        return Nome;
    }

    public String getCognome() {
        return Cognome;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Utente other = (Utente) obj;
        if (Cognome == null) {
            if (other.Cognome != null)
                return false;
        } else if (!Cognome.equalsIgnoreCase(other.Cognome))
            return false;
        if (Nome == null) {
            if (other.Nome != null)
                return false;
        } else if (!Nome.equalsIgnoreCase(other.Nome))
            return false;
        return true;
    }

}
