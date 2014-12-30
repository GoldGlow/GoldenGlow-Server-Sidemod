package com.ryanv97.pixelgyms.gym;

import com.pixelmonmod.pixelmon.storage.PixelmonStorage;
import com.pixelmonmod.pixelmon.storage.PlayerNotLoadedException;
import com.pixelmonmod.pixelmon.storage.PlayerStorage;
import com.ryanv97.pixelgyms.PixelGyms;
import com.ryanv97.pixelgyms.util.Reference;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.config.Configuration;

import java.util.*;

public class GymHandler
{
    public Map<EntityPlayer, double[]> locations = new HashMap<EntityPlayer, double[]>();
    private final List<Gym> gyms = new ArrayList<Gym>();

    static PixelGyms mod;
    public GymHandler(PixelGyms pixelGyms)
    {
        mod = pixelGyms;
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
    }

    public void removePlayer(EntityPlayer player, ICommandSender sender)
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
            sender.addChatMessage(new ChatComponentText(Reference.messagePrefix + Reference.colorRed + "Error: Player is not queued for any Gyms."));
            return;
        }

        gym.getPlayers().remove(player);
    }

    public void removePlayer(EntityPlayer player)
    {
        double[] loc = locations.get(player);
        player.setPositionAndUpdate(loc[0],loc[1],loc[2]);
        player.addChatComponentMessage(new ChatComponentText(Reference.messagePrefix+"You have been warped out of the gym!"));
    }

    public void nextPlayer(String name, ICommandSender sender)
    {
        Gym gym = getGym(name);
        if(gym==null)
        {
            sender.addChatMessage(new ChatComponentText(Reference.messagePrefix + Reference.colorRed + "Error: Gym not found!"));
            return;
        }

        List<EntityPlayer> players = gym.getPlayers();
        EntityPlayer currentPlayer = gym.getCurrentPlayer();

        if(currentPlayer==null)
        {
            if(players.size()>0)
            {
                //If there are players waiting, but no current player in the gym.
                EntityPlayer nextPlayer = players.get(0);
                double[] loc = new double[3];

                loc[0] = nextPlayer.posX;
                loc[1] = nextPlayer.posY;
                loc[2] = nextPlayer.posZ;
                locations.put(nextPlayer, loc);

                gym.setCurrentPlayer(nextPlayer);
                teleportPlayer(nextPlayer, name);
                gym.getPlayers().remove(nextPlayer);
            }else{
                //If there are no players waiting, or in the gym.
                sender.addChatMessage(new ChatComponentText(Reference.messagePrefix+Reference.colorRed+"No players waiting!"));
            }
        }else{
            if(players.size()>0)
            {
                //If there are players waiting, and a current player in the gym.
                removePlayer(currentPlayer);
                currentPlayer.addChatComponentMessage(new ChatComponentText(Reference.messagePrefix+"You have been warped out of the gym!"));

                EntityPlayer nextPlayer = players.get(0);
                double[] loc = new double[3];

                loc[0] = nextPlayer.posX;
                loc[1] = nextPlayer.posY;
                loc[2] = nextPlayer.posZ;
                locations.put(nextPlayer, loc);

                gym.setCurrentPlayer(nextPlayer);
                teleportPlayer(nextPlayer, name);
                gym.getPlayers().remove(nextPlayer);
            }else{
                //If there are no players waiting, but a current player in the gym.
                removePlayer(currentPlayer);
                currentPlayer.addChatComponentMessage(new ChatComponentText(Reference.messagePrefix+"You have been warped out of the gym!"));
                gym.setCurrentPlayer(null);
            }
        }
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
                removePlayer(player, sender);
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

    public void listGyms(ICommandSender player)
    {
        if(gyms.size()==0)
        {
            player.addChatMessage(new ChatComponentText(Reference.messagePrefix+"There are no Gyms!"));
        }
        player.addChatMessage(new ChatComponentText(Reference.messagePrefix+Reference.bold+"Current Gyms:"));
        for(Gym entry : gyms)
        {
            double[] warp = entry.getWarp();
            player.addChatMessage(new ChatComponentText(Reference.messagePrefix+entry.getId()+": x"+(int)warp[0]+", y"+(int)warp[1]+", z"+(int)warp[2]+" Level Cap: "+entry.getLevelCap()));
        }
    }

    public void listPlayers(ICommandSender commandSender, String name)
    {
        Gym gym = this.getGym(name);
        if(gym==null) {
            commandSender.addChatMessage(new ChatComponentText(Reference.messagePrefix + Reference.colorRed + "Error: Gym not found!"));
            return;
        }
        if(gym.getCurrentPlayer()!=null) {
            commandSender.addChatMessage(new ChatComponentText(Reference.messagePrefix + Reference.bold + "Current Challenger: " + EnumChatFormatting.RESET + gym.getCurrentPlayer().getDisplayName()));
        }else{
            commandSender.addChatMessage(new ChatComponentText(Reference.messagePrefix + "No current challenger."));
        }
        if(gym.getPlayers().size()==0) {
            commandSender.addChatMessage(new ChatComponentText(Reference.messagePrefix + "No Players in queue for " + gym.getId() + "."));
            return;
        }
        int i = 1;
        commandSender.addChatMessage(new ChatComponentText(Reference.messagePrefix + Reference.bold + "Currently queued players:"));
        for(EntityPlayer p : gym.getPlayers())
        {
            commandSender.addChatMessage(new ChatComponentText(Reference.messagePrefix + i + ": " + p.getDisplayName()));
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