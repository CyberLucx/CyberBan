package org.cyberluca.cyberBan.manager;

public class MuteData {

    private final String spieler;
    private final String mutetVon;
    private final long bis;
    private final String grund;

    public MuteData(String spieler, String mutetVon, long bis, String grund) {
        this.spieler = spieler;
        this.mutetVon = mutetVon;
        this.bis = bis;
        this.grund = grund;
    }

    public String getSpieler() {
        return spieler;
    }

    public String getMutetVon() {
        return mutetVon;
    }

    public long getBis() {
        return bis;
    }

    public String getGrund() {
        return grund;
    }
}
