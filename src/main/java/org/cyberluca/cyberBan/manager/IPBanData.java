package org.cyberluca.cyberBan.manager;

public class IPBanData {

    private final String ip;
    private final long bis;
    private final String grund;

    public IPBanData(String ip, long bis, String grund) {
        this.ip = ip;
        this.bis = bis;
        this.grund = grund;
    }

    public String getIp() {
        return ip;
    }

    public long getBis() {
        return bis;
    }

    public String getGrund() {
        return grund;
    }
}
