package org.cyberluca.cyberBan.manager;

public class BanData {

    private final String spieler;
    private final String gebanntVon;
    private final long bis; // -1 permanent
    private final String grund;

    public BanData(String spieler, String gebanntVon, long bis, String grund) {
        this.spieler = spieler;
        this.gebanntVon = gebanntVon;
        this.bis = bis;
        this.grund = grund;
    }

    public String getSpieler() {
        return spieler;
    }

    public String getGebanntVon() {
        return gebanntVon;
    }

    public long getBis() {
        return bis;
    }

    public String getGrund() {
        return grund;
    }
}
