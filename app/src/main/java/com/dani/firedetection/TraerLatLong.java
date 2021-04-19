package com.dani.firedetection;

public class TraerLatLong {

    private double latitud;
    private double longitud;
    private String humo,columna,vegetacion;

    public TraerLatLong() {
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public String getHumo() {
        return humo;
    }

    public void setHumo(String humo) {
        this.humo = humo;
    }

    public String getColumna() {
        return columna;
    }

    public void setColumna(String columna) {
        this.columna = columna;
    }

    public String getVegetacion() {
        return vegetacion;
    }

    public void setVegetacion(String vegetacion) {
        this.vegetacion = vegetacion;
    }


}
