package com.goldenglow.gym;

import com.goldenglow.GoldenGlow;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

public class GymLeader implements IExtendedEntityProperties {

    public final static String EXT_PROP_NAME = "GymLeader";
    private final EntityPlayer player;

    private static String gym;

    public GymLeader(EntityPlayer player, String gym)
    {
        this.player=player;
        this.gym=gym;
    }

    public static final void register(EntityPlayer player, String gym)
    {
        player.registerExtendedProperties(GymLeader.EXT_PROP_NAME, new GymLeader(player, gym));
    }

    public static final GymLeader get(EntityPlayer player)
    {
        return (GymLeader) player.getExtendedProperties(EXT_PROP_NAME);
    }

    @Override
    public void saveNBTData(NBTTagCompound compound)
    {
        NBTTagCompound properties = new NBTTagCompound();

        properties.setString("Gym", this.gym);

        compound.setTag(EXT_PROP_NAME, properties);
    }

    @Override
    public void loadNBTData(NBTTagCompound compound)
    {
        NBTTagCompound properties = (NBTTagCompound) compound.getTag(EXT_PROP_NAME);

        this.gym = properties.getString("Gym");

        System.out.print("[GYM PROPS] Loaded GymLeader data "+this.gym);
    }

    @Override
    public void init(Entity entity, World world)
    {
    }

    public static String getSaveKey(EntityPlayer player)
    {
        return player.getDisplayName()+":"+EXT_PROP_NAME;
    }

    public static void saveGymHandlerData(EntityPlayer player)
    {
        GymLeader data = GymLeader.get(player);
        NBTTagCompound savedData = new NBTTagCompound();

        data.saveNBTData(savedData);
        GoldenGlow.gymHandler.storeGymLeaderData(getSaveKey(player), savedData);
    }

    public static void loadGymHandlerData(EntityPlayer player)
    {
        GymLeader data = GymLeader.get(player);
        NBTTagCompound savedData = GoldenGlow.gymHandler.getGymLeaderData(getSaveKey(player));
        if(savedData!=null)
            data.loadNBTData(savedData);
    }
}
