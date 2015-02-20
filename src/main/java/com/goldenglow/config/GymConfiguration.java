package com.goldenglow.config;

import com.goldenglow.GoldenGlow;
import com.goldenglow.gym.GymHandler;
import com.goldenglow.util.Reference;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GymConfiguration
{
    Configuration config = new Configuration(new File(Reference.configDir, "gyms.cfg"));
    List<String> gyms = new ArrayList<String>();

    public GymConfiguration()
    {
        this.config.get("main", "gyms", (String[])this.gyms.toArray(new String[this.gyms.size()]));
        this.config.save();
    }

    public void addGym(String name, double posX, double posY, double posZ, int levelCap, EntityPlayer player)
    {
        Property gyms = this.config.get("main", "gyms", (String[])this.gyms.toArray(new String[this.gyms.size()]));
        player.addChatComponentMessage(new ChatComponentText(gyms.getString()));
        List gymList = new ArrayList(Arrays.asList(gyms.getStringList()));

        for (String gym : gyms.getStringList())
        {
            String[] args = gym.split("_");
            if(args[0].equalsIgnoreCase(name))
            {
                player.addChatComponentMessage(new ChatComponentText(Reference.messagePrefix+Reference.colorRed+"Error: Gym with this name already exists!"));
                return;
            }
        }

        String gymInfo = name+"_"+(int)posX+"_"+(int)posY+"_"+(int)posZ+"_"+levelCap;
        gymList.add(gymInfo);
        gyms.set((String[]) gymList.toArray(new String[this.gyms.size()]));

        GymHandler.instance.createGym(name, posX,posY,posZ, levelCap);

        this.config.save();

        player.addChatComponentMessage(new ChatComponentText(Reference.messagePrefix + "Successfully created Gym: '"+name+"' with level cap "+levelCap));
    }

    public int[] getGymInfo(String name)
    {
        int[] gymInfo = null;
        for (String gym : Arrays.asList(this.config.get("main", "gyms", (String[])this.gyms.toArray(new String[this.gyms.size()])).getStringList()))
        {
            String[] args = gym.split("_");
            if(args[0].equalsIgnoreCase(name))
            {
                gymInfo = new int[]{Integer.parseInt(args[1]),Integer.parseInt(args[2]),Integer.parseInt(args[3]),Integer.parseInt(args[4])};;
            }
        }
        return gymInfo;
    }

    public List<String> getGyms()
    {
        return this.gyms;
    }

    public Configuration getConfig()
    {
        return this.config;
    }

    public void removeGym(String name, EntityPlayer player)
    {
        int[] gymInfo = getGymInfo(name);
        if(gymInfo==null) {
            return;
        }

        Property gyms = this.config.get("main", "gyms", (String[])this.gyms.toArray(new String[this.gyms.size()]));
        List gymList = new ArrayList(Arrays.asList(gyms.getStringList()));

        gymList.remove(name + "_" + gymInfo[0] + "_" + gymInfo[1] + "_" + gymInfo[2] + "_" + gymInfo[3]);
        gyms.set((String[])gymList.toArray(new String[gymList.size()]));
        this.config.save();

        GymHandler.instance.removeGym(name, player);
    }

    public void reload()
    {
        this.config.get("main", "gyms", (String[])this.gyms.toArray(new String[this.gyms.size()]));
        this.config.save();
    }
}
