package org.cyberluca.cyberBan.manager;

import java.util.UUID;

public class BanEntry {
    private final UUID player;
    private final String banner;
    private final String grund;
    private final long end; // -1 = permanent

    public BanEntry(UUID player, String banner, String grund, long end) {
        this.player = player;
        this.banner = banner;
        this.grund = grund;
        this.end = end;
    }

    public UUID getPlayer() { return player; }
    public String getBanner() { return banner; }
    public String getGrund() { return grund; }
    public long getEnd() { return end; }

    public boolean isPermanent() {
        return end <= 0;
    }
}
