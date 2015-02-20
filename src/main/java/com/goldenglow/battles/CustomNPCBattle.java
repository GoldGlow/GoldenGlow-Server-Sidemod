package com.goldenglow.battles;

import com.goldenglow.GoldenGlow;
import com.pixelmonmod.pixelmon.battles.controller.BattleController2Participant;
import com.pixelmonmod.pixelmon.battles.controller.participants.BattleParticipant;
import com.pixelmonmod.pixelmon.battles.controller.participants.PlayerParticipant;
import com.pixelmonmod.pixelmon.battles.controller.participants.TrainerParticipant;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.controllers.Dialog;
import noppes.npcs.entity.EntityNPCInterface;

public class CustomNPCBattle extends BattleController2Participant
{
    private Dialog winDiag;
    private Dialog loseDiag;
    public static EntityPlayer player;
    private static EntityNPCInterface npc;

    public CustomNPCBattle(PlayerParticipant player, TrainerParticipant trainer, EntityNPCInterface npc, Dialog winDialog, Dialog loseDialog) throws Exception
    {
        super(player,trainer);
        this.npc=npc;
        this.winDiag=winDialog;
        this.loseDiag=loseDialog;
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

    public EntityNPCInterface getNpc()
    {
        return this.npc;
    }

    public boolean hasPlayer(EntityPlayer participant)
    {
        if(participant==player)
            return true;
        else
            return false;
    }
}
