package com.goldenglow.battles;

import com.goldenglow.GoldenGlow;
import com.goldenglow.team.Team;
import com.goldenglow.team.TeamHandler;
import com.goldenglow.util.Reference;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.events.PlayerBattleEndedEvent;
import com.pixelmonmod.pixelmon.battles.BattleRegistry;
import com.pixelmonmod.pixelmon.battles.controller.participants.PlayerParticipant;
import com.pixelmonmod.pixelmon.battles.controller.participants.TrainerParticipant;
import com.pixelmonmod.pixelmon.comm.packetHandlers.StarterListPacket;
import com.pixelmonmod.pixelmon.config.PixelmonEntityList;
import com.pixelmonmod.pixelmon.entities.npcs.EntityTrainer;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.pixelmonmod.pixelmon.enums.EnumPokemon;
import com.pixelmonmod.pixelmon.storage.PixelmonStorage;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.controllers.Dialog;
import noppes.npcs.controllers.DialogController;
import noppes.npcs.entity.EntityNPCInterface;

import java.util.ArrayList;
import java.util.List;

public class CustomBattleHandler
{
    static GoldenGlow mod;
    public static CustomBattleHandler instance;

    public List<CustomNPCBattle> battles = new ArrayList<CustomNPCBattle>();

    public CustomBattleHandler()
    {
        this.mod = GoldenGlow.instance;
        this.instance = this;
    }

    public void createCustomBattle(EntityPlayerMP player, String teamName, int winDialogID, int loseDialogID, EntityNPCInterface npc) {
        Team npcTeam = TeamHandler.instance.getTeam(teamName);

        if (BattleRegistry.getBattle(player) != null){
            player.addChatComponentMessage(new ChatComponentText(Reference.messagePrefix + Reference.colorRed + "Cannot Battle!"));
            return;
        }else
        try {
            EntityTrainer trainer = (EntityTrainer) PixelmonEntityList.createEntityByName(npc.display.name, player.worldObj);
            trainer.setPosition(player.posX,player.posY,player.posZ);
            for(EntityPixelmon p : npcTeam.getMembers())
            {
                trainer.getPokemonStorage().addToParty(p);
            }

            EntityPixelmon playerFirst = PixelmonStorage.PokeballManager.getPlayerStorage(player).getFirstAblePokemon(player.worldObj);
            if(playerFirst!=null)
            {
                PlayerParticipant playerParticipant = new PlayerParticipant(player, playerFirst);
                TrainerParticipant trainerParticipant = new TrainerParticipant(trainer, player, 1);

                Dialog winDialog = DialogController.instance.dialogs.get(winDialogID);
                Dialog loseDialog = DialogController.instance.dialogs.get(loseDialogID);

                CustomNPCBattle customNPCBattle = new CustomNPCBattle(playerParticipant, trainerParticipant, npc, winDialog, loseDialog);
                battles.add(customNPCBattle);
            }else{
                player.addChatComponentMessage(new ChatComponentText(Reference.messagePrefix + Reference.colorRed + "You have no pokemon that are able to battle!"));
            }
        }
        catch (Exception e){}
    }

    @SubscribeEvent
    public void onPlayerBattleEnded(PlayerBattleEndedEvent event)
    {
        if(event.battleController instanceof CustomNPCBattle && battles.contains(event.battleController))
        {
            CustomNPCBattle battle = (CustomNPCBattle)event.battleController;
            battles.remove(battle);
            BattleRegistry.deRegisterBattle(battle);
            if(event.result==PlayerBattleEndedEvent.battleResults.victory) {
                NoppesUtilServer.openDialog(event.player, battle.getNpc(), battle.getWinDialog());
            }
            if(event.result==PlayerBattleEndedEvent.battleResults.defeat){
                NoppesUtilServer.openDialog(event.player, battle.getNpc(), battle.getLoseDialog());
            }
        }
    }
}
