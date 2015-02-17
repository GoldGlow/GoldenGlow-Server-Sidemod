package com.goldenglow.util;

import com.pixelmonmod.pixelmon.storage.PixelmonStorage;
import com.pixelmonmod.pixelmon.storage.PlayerNotLoadedException;
import com.pixelmonmod.pixelmon.storage.PlayerStorage;
import com.goldenglow.GoldenGlow;
import com.goldenglow.gym.Gym;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;

@SideOnly(Side.SERVER)
public class TickHandler
{
    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event)
    {
        if(event.phase==TickEvent.Phase.START)
        {
            for(Gym gym : GoldenGlow.gymHandler.gyms)
            {
                if(gym.isCounting)
                {
                    if(gym.countdown<=0)
                    {
                        EntityPlayer player = gym.getCurrentPlayer();
                        gym.isCounting=false;
                        double[] loc = gym.getWarp();
                        if(player!=null) {
                            PlayerStorage playerStorage = null;
                            try
                            {
                                playerStorage = PixelmonStorage.PokeballManager.getPlayerStorage((EntityPlayerMP)player);
                                if(playerStorage.getHighestLevel()<=gym.getLevelCap())
                                {
                                    player.setPositionAndUpdate(loc[0], loc[1], loc[2]);
                                    player.addChatComponentMessage(new ChatComponentText(Reference.messagePrefix + "You have been forcefully warped to the Gym after waiting too long!"));
                                    return;
                                }else{
                                    player.addChatComponentMessage(new ChatComponentText(Reference.messagePrefix + Reference.colorRed + "Error: You exceed this Gym's level cap of: "+gym.getLevelCap()+"!"));
                                    player.addChatComponentMessage(new ChatComponentText(Reference.messagePrefix + "Make sure none of your pokemon are above this level within 1 minute. Failure to do so will result in you being kicked from the gym queue."));
                                }

                            } catch (PlayerNotLoadedException e) {
                            }
                        }
                    }else{
                        gym.countdown -= 1;
                    }
                }
            }
        }
    }
}