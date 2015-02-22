package com.goldenglow;

import com.goldenglow.battles.CustomBattleHandler;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.comm.packetHandlers.SelectPokemonListPacket;
import com.pixelmonmod.pixelmon.comm.packetHandlers.camera.ClientCameraPacket;
import com.pixelmonmod.pixelmon.config.StarterList;
import net.minecraft.entity.player.EntityPlayerMP;
import noppes.npcs.entity.EntityNPCInterface;

public class NPCFunctions
{
    public static void openStarterGui(EntityPlayerMP player)
    {
        Pixelmon.instance.network.sendTo(new SelectPokemonListPacket(StarterList.StarterList), player);
    }

    public static void createCustomBattle(EntityPlayerMP player, String teamName, int winDialogID, int loseDialogID, EntityNPCInterface npc)
    {
        CustomBattleHandler.instance.createCustomBattle(player, teamName, winDialogID, loseDialogID, npc);
    }

    public static void setCamera(EntityPlayerMP player, int posX, int posY, int posZ, int targetX, int targetY, int targetZ)
    {
        Pixelmon.network.sendTo(new ClientCameraPacket(posX, posY, posZ, targetX, targetY, targetZ), player);
    }
}
