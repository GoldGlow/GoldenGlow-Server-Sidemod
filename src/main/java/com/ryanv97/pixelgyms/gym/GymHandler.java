package com.ryanv97.pixelgyms.gym;

import com.pixelmonmod.pixelmon.storage.PixelmonStorage;
import com.pixelmonmod.pixelmon.storage.PlayerNotLoadedException;
import com.pixelmonmod.pixelmon.storage.PlayerStorage;
import com.ryanv97.pixelgyms.PixelGyms;
import com.ryanv97.pixelgyms.util.Reference;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.config.Configuration;

import java.util.*;

public class GymHandler
{
    public static GymHandler gymHandler;
    public Map<EntityPlayer, double[]> locations = new HashMap<EntityPlayer, double[]>();
    private final List<Gym> gyms = new ArrayList<Gym>();

    static PixelGyms mod;
    public GymHandler(PixelGyms pixelGyms)
    {
        mod = pixelGyms;
    }

    public static GymHandler getGymHandler()
    {
        return gymHandler;
    }

    public Gym getGym(String name)
    {
        for(Gym gym : gyms)
        {
            if(gym.getId().equalsIgnoreCase(name))
                return gym;
        }
        return null;
    }

    public void addPlayer(EntityPlayer player, String name)
    {
        if(!isInQueue(player)) {

            Gym gym = this.getGym(name);
            if (gym == null) {
                player.addChatComponentMessage(new ChatComponentText(Reference.messagePrefix + Reference.colorRed + "Error: Invalid Gym!"));
                return;
            }

            PlayerStorage playerStorage = null;
            try
            {
                playerStorage = PixelmonStorage.PokeballManager.getPlayerStorage((EntityPlayerMP)player);
                if(playerStorage.getHighestLevel()<=gym.getLevelCap())
                {
                    gym.getPlayers().add(player);
                    double[] loc = new double[3];
                    loc[0] = player.posX;
                    loc[1] = player.posY;
                    loc[2] = player.posZ;
                    locations.put(player, loc);

                    player.addChatComponentMessage(new ChatComponentText(Reference.messagePrefix + "You are now in the gym queue!"));
                    return;
                }else{
                    player.addChatComponentMessage(new ChatComponentText(Reference.messagePrefix + Reference.colorRed + "Error: You exceed this Gym's level cap of: "+gym.getLevelCap()+"!"));
                    return;
                }

            } catch (PlayerNotLoadedException e) {
            }
        }
        player.addChatComponentMessage(new ChatComponentText(Reference.messagePrefix + Reference.colorRed + "Error: You may only queue for one gym at a time!"));
        return;
    }

    public void removePlayer(EntityPlayer player)
    {
        Gym gym = null;
        for(Gym g : this.gyms)
        {
            if(g.getPlayers().contains(player))
            {
                gym = g;
            }
        }

        if(gym==null)
        {
            player.addChatComponentMessage(new ChatComponentText(Reference.messagePrefix + Reference.colorRed + "Error: Player is not queued for any Gyms."));
            return;
        }

        gym.getPlayers().remove(player);

        double[] loc = locations.get(player);
        player.setPositionAndUpdate(loc[0],loc[1],loc[2]);
    }

    public void nextPlayer(String name, EntityPlayer sender)
    {
        Gym gym = getGym(name);
        if(gym==null) {
            sender.addChatComponentMessage(new ChatComponentText(Reference.messagePrefix + Reference.colorRed + "Error: Gym not found!"));
            return;
        }
        List<EntityPlayer> players = gym.getPlayers();
        if(players.size()==0) {
            sender.addChatComponentMessage(new ChatComponentText(Reference.messagePrefix+Reference.colorRed+"Error: No players waiting!"));
            return;
        }
        EntityPlayer player = players.get(0);
        teleportPlayer(player, name);
    }

    public void teleportPlayer(EntityPlayer player, String name)
    {
        Gym gym = this.getGym(name);
        if (gym == null) {
            player.addChatComponentMessage(new ChatComponentText(Reference.messagePrefix + Reference.colorRed + "Error: Invalid Gym!"));
            return;
        }
        double[] loc = gym.getWarp();
        player.setPositionAndUpdate(loc[0],loc[1],loc[2]);
        player.addChatComponentMessage(new ChatComponentText(Reference.messagePrefix+"You have been warped to the gym!"));
    }

    public Gym createGym(String name, double x, double y, double z, int levelCap)
    {
        double warp[] = new double[3];
        warp[0]=x;
        warp[1]=y;
        warp[2]=z;
        Gym newGym = new Gym(name, warp, levelCap);
        this.gyms.add(newGym);

        return newGym;
    }

    public void removeGym(String name, EntityPlayer sender)
    {
        Gym gym = this.getGym(name);
        if (gym == null)
            return;
        if (this.gyms.contains(gym)) {
            for(EntityPlayer player : gym.getPlayers())
            {
                removePlayer(player);
                player.addChatComponentMessage(new ChatComponentText(Reference.messagePrefix+Reference.colorRed+"The Gym you were queued for has been deleted or reset."));
            }
            this.gyms.remove(gym);
            sender.addChatComponentMessage(new ChatComponentText(Reference.messagePrefix + "Successfully removed Gym: " + name + "!"));
        }
    }

    public void loadGyms()
    {
        this.gyms.clear();
        Configuration config = mod.config.getConfig();
        List<String> gymList = mod.config.getGyms();

        for (String gym : Arrays.asList(config.get("main", "gyms", (String[]) gymList.toArray(new String[gymList.size()])).getStringList()))
        {
            String[] args = gym.split("_");
            int[] gymInfo = new int[]{Integer.parseInt(args[1]),Integer.parseInt(args[2]),Integer.parseInt(args[3]), Integer.parseInt(args[4])};
            createGym(args[0],(double)gymInfo[0],(double)gymInfo[1],(double)gymInfo[2], gymInfo[3]);
        }
    }

    public void listGyms(EntityPlayer player)
    {
        if(gyms.size()==0)
        {
            player.addChatComponentMessage(new ChatComponentText(Reference.messagePrefix+"There are no Gyms!"));
        }
        for(Gym entry : gyms)
        {
            double[] warp = entry.getWarp();
            player.addChatComponentMessage(new ChatComponentText(entry.getId()+": X"+(int)warp[0]+" Y"+(int)warp[1]+" Z"+(int)warp[2]+" Level Cap: "+entry.getLevelCap()));
        }
    }

    public void listPlayers(EntityPlayer player, String name)
    {
        Gym gym = this.getGym(name);
        if(gym==null) {
            player.addChatComponentMessage(new ChatComponentText(Reference.messagePrefix + Reference.colorRed+"Error: Gym not found!"));
            return;
        }
        if(gym.getPlayers().size()==0) {
            player.addChatComponentMessage(new ChatComponentText(Reference.messagePrefix + "No Players in queue for " + gym.getId() + "."));
            return;
        }
        int i = 1;
        for(EntityPlayer p : gym.getPlayers())
        {
            player.addChatComponentMessage(new ChatComponentText(Reference.messagePrefix+i+": "+p.getDisplayName()));
            i++;
        }
    }

    public boolean isInQueue(EntityPlayer player)
    {
        for(Gym g : this.gyms)
        {
            if(g.getPlayers().contains(player))
                return true;
        }
        return false;
    }
}