package com.ryanv97.pixelgyms.util;

import com.ryanv97.pixelgyms.PixelGyms;
import com.ryanv97.pixelgyms.gym.GymLeader;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

public class EventHandler
{
    public void onEntityConstructing(EntityEvent.EntityConstructing event)
    {
        if(event.entity instanceof EntityPlayer && GymLeader.get((EntityPlayer)event.entity)==null){
            GymLeader.register((EntityPlayer)event.entity, "");
        }
    }

    @SubscribeEvent
    public void onLivingDeathEvent(LivingDeathEvent event)
    {
        if(!event.entity.worldObj.isRemote && event.entity instanceof EntityPlayer)
        {
            NBTTagCompound data = new NBTTagCompound();
            ((GymLeader)(event.entity.getExtendedProperties(GymLeader.EXT_PROP_NAME))).saveNBTData(data);
            PixelGyms.gymHandler.storeGymLeaderData(((EntityPlayer)event.entity).getDisplayName(), data);
            GymLeader.saveGymHandlerData((EntityPlayer) event.entity);
        }
    }

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event)
    {
        if(!event.entity.worldObj.isRemote && event.entity instanceof EntityPlayer)
        {
            NBTTagCompound data = PixelGyms.gymHandler.getGymLeaderData(((EntityPlayer)event.entity).getDisplayName());
            if(data!=null)
                ((GymLeader)(event.entity.getExtendedProperties(GymLeader.EXT_PROP_NAME))).loadNBTData(data);
        }
    }
}
