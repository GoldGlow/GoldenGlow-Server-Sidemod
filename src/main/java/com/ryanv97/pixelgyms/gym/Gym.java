package com.ryanv97.pixelgyms.gym;

import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.List;

public class Gym {
    private final String name;
    private final double[] warp;
    private final int levelCap;
    private final List<EntityPlayer> players = new ArrayList<EntityPlayer>();

    public Gym(String name, double[] warp, int levelCap) {
        this.name = name;
        this.warp = warp;
        this.levelCap = levelCap;
    }

    public String getId() {
        return this.name;
    }

    public double[] getWarp()
    {
        return this.warp;
    }

    public int getLevelCap()
    {
        return this.levelCap;
    }

    public List<EntityPlayer> getPlayers()
    {
        return this.players;
    }
}
