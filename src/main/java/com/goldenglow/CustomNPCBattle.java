package com.goldenglow;

import com.pixelmonmod.pixelmon.battles.controller.BattleController2Participant;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.controllers.Dialog;

public class CustomNPCBattle
{
    private Dialog winDiag;
    private Dialog loseDiag;
    public static BattleController2Participant bc;
    public static EntityPlayer player;

    public CustomNPCBattle(BattleController2Participant bc, EntityPlayer player)
    {
        GoldenGlow.logger.info("New custom battle with Player: "+player.getCommandSenderName());
        this.bc = bc;
        this.player = player;
    }

    public CustomNPCBattle(BattleController2Participant bc, EntityPlayer player, Dialog winDiag, Dialog loseDiag)
    {
        GoldenGlow.logger.info("New custom battle with Player: "+player.getCommandSenderName()+" with Win Dialog!");
        this.winDiag=winDiag;
        this.loseDiag=loseDiag;
        this.bc=bc;
        this.player=player;
    }

    public void addWinDialog(Dialog winDiag)
    {
        this.winDiag=winDiag;
    }

    public void addLoseDialog(Dialog loseDiag)
    {
        this.loseDiag=loseDiag;
    }

    public Dialog getWinDialog()
    {
        return this.winDiag;
    }

    public Dialog getLoseDialog()
    {
        return this.loseDiag;
    }

    public boolean hasPlayer(EntityPlayer participant)
    {
        if(participant==player)
            return true;
        else
            return false;
    }
}
