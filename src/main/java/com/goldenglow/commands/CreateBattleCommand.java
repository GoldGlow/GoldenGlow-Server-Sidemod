package com.goldenglow.commands;

import com.goldenglow.CustomNPCBattle;
import com.goldenglow.GoldenGlow;
import com.goldenglow.team.Team;
import com.goldenglow.util.Reference;
import com.pixelmonmod.pixelmon.battles.BattleRegistry;
import com.pixelmonmod.pixelmon.battles.controller.BattleController2Participant;
import com.pixelmonmod.pixelmon.battles.controller.participants.PlayerParticipant;
import com.pixelmonmod.pixelmon.battles.controller.participants.TrainerParticipant;
import com.pixelmonmod.pixelmon.comm.CommandChatHandler;
import com.pixelmonmod.pixelmon.config.PixelmonEntityList;
import com.pixelmonmod.pixelmon.entities.npcs.EntityTrainer;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.pixelmonmod.pixelmon.storage.PixelmonStorage;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import noppes.npcs.controllers.DialogController;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.EntityNPCInterface;

import java.util.List;

public class CreateBattleCommand extends CommandBase
{
    GoldenGlow mod;
    public CreateBattleCommand(){this.mod=GoldenGlow.instance;}

    @Override
    public String getCommandName() {
        return "cbattle";
    }

    @Override
    public String getCommandUsage(ICommandSender iCommandSender) {
        return "/cbattle <player> <team> [entityID] [windiagID] [losediagID]";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void processCommand(ICommandSender iCommandSender, String[] args)
    {
        if(checkValidity(iCommandSender, args))
        {
            String teamName="";
            for(String arg : args)
            {
                if(!arg.equals(args[0]))
                    teamName=teamName.concat(arg);
            }
            EntityPlayerMP player = getPlayer(iCommandSender, args[0]);
            Team npcTeam = mod.teamHandler.getTeam(teamName);

            if(BattleRegistry.getBattle(player)!=null)
                CommandChatHandler.sendChat(iCommandSender, "pixelmon.command.battle.cannotchallenge", new Object[]{args[0]});
            try{
                EntityTrainer npc = (EntityTrainer) PixelmonEntityList.createEntityByName(iCommandSender.getCommandSenderName(), player.worldObj);
                npc.setPosition(player.posX, player.posY, player.posZ);
                for(EntityPixelmon p : npcTeam.getMembers())
                {
                    npc.getPokemonStorage().addToParty(p);
                }

                EntityPixelmon pFirst = PixelmonStorage.PokeballManager.getPlayerStorage(player).getFirstAblePokemon(player.worldObj);
                if(pFirst!=null) {
                    PlayerParticipant playerParticipant = new PlayerParticipant(player, new EntityPixelmon[]{pFirst});
                    TrainerParticipant trainerParticipant = new TrainerParticipant(npc, player, 1);

                    BattleController2Participant bc = new BattleController2Participant(playerParticipant, trainerParticipant);

                    GoldenGlow.instance.logger.info("Creating Battle");
                    CustomNPCBattle battle = new CustomNPCBattle(bc, player);
                    if(args.length==4)
                    {
                        if(DialogController.instance.hasDialog(Integer.parseInt(args[2]))&&DialogController.instance.hasDialog(Integer.parseInt(args[3])))
                        {
                            battle.addWinDialog(DialogController.instance.dialogs.get(args[2]));
                            battle.addLoseDialog(DialogController.instance.dialogs.get(args[3]));
                        }
                    }
                    mod.pixelmonEventHandler.addBattle(battle);
                }else{
                    player.addChatComponentMessage(new ChatComponentText(Reference.messagePrefix + Reference.colorRed + "You have no pokemon that are able to battle!"));
                }
            }catch(Exception e){
            }
        }
    }

    public boolean checkValidity(ICommandSender iCommandSender, String[] strings) {
        if (strings.length < 1) {
            iCommandSender.addChatMessage(new ChatComponentText(Reference.messagePrefix + Reference.colorRed + "No Player Specified!"));
            iCommandSender.addChatMessage(new ChatComponentText(Reference.messagePrefix + Reference.colorRed + getCommandUsage(iCommandSender)));
            return false;
        }
        if (strings.length < 2) {
            iCommandSender.addChatMessage(new ChatComponentText(Reference.messagePrefix + Reference.colorRed + "No Team Specified!"));
            iCommandSender.addChatMessage(new ChatComponentText(Reference.messagePrefix + Reference.colorRed + "/cbattle " + strings[0] + " <team>"));
            return false;
        }
        if (getPlayer(iCommandSender, strings[0]) == null) {
            iCommandSender.addChatMessage(new ChatComponentText(Reference.messagePrefix + Reference.colorRed + "Player does not exist!"));
            return false;
        }
        String teamName="";
        for(String arg : strings)
        {
            if(!arg.equals(strings[0]))
                teamName=teamName.concat(arg);
        }
        if (mod.teamHandler.getTeam(teamName) == null)
        {
            iCommandSender.addChatMessage(new ChatComponentText(Reference.messagePrefix + Reference.colorRed + "Team not found!"));
            return false;
        }
        if(strings.length==4)
        {
            iCommandSender.addChatMessage(new ChatComponentText(Reference.messagePrefix + Reference.colorRed + "You must supply both a win dialog ID and a lose dialog ID."));
            iCommandSender.addChatMessage(new ChatComponentText(Reference.messagePrefix + Reference.colorRed + "/cbattle " + strings[0] + " "+strings[1]+" "+strings[2]+" [losediagID]"));
            return false;
        }
        if(strings.length==5)
        {
            try {
                Integer.parseInt(strings[2]);
                Integer.parseInt(strings[3]);
            } catch (NumberFormatException var7) {
                iCommandSender.addChatMessage(new ChatComponentText(Reference.messagePrefix + Reference.colorRed + "DialogID must be a number!"));
                return false;
            }
        }

        return true;
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        if(args.length==1)
            return getListOfStringsMatchingLastWord(MinecraftServer.getServer().getAllUsernames());
        else
            return null;
    }
}