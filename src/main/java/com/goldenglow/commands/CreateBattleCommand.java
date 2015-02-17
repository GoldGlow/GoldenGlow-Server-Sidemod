package com.goldenglow.commands;

import com.goldenglow.GoldenGlow;
import com.goldenglow.team.Team;
import com.goldenglow.team.TeamHandler;
import com.goldenglow.util.Reference;
import com.pixelmonmod.pixelmon.battles.BattleRegistry;
import com.pixelmonmod.pixelmon.battles.controller.BattleController2Participant;
import com.pixelmonmod.pixelmon.battles.controller.BattleControllerBase;
import com.pixelmonmod.pixelmon.battles.controller.participants.BattleParticipant;
import com.pixelmonmod.pixelmon.battles.controller.participants.PlayerParticipant;
import com.pixelmonmod.pixelmon.battles.controller.participants.TrainerParticipant;
import com.pixelmonmod.pixelmon.comm.CommandChatHandler;
import com.pixelmonmod.pixelmon.entities.npcs.EntityTrainer;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.pixelmonmod.pixelmon.storage.PixelmonStorage;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;

import java.util.Arrays;
import java.util.List;

public class CreateBattleCommand extends CommandBase
{
    GoldenGlow mod;
    public CreateBattleCommand(GoldenGlow mod){this.mod=mod;}

    @Override
    public String getCommandName() {
        return "cbattle";
    }

    @Override
    public String getCommandUsage(ICommandSender iCommandSender) {
        return "/cbattle <player> <team>";
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
                EntityPixelmon pFirst = PixelmonStorage.PokeballManager.getPlayerStorage(player).getFirstAblePokemon(player.worldObj);
                EntityTrainer npc = new EntityTrainer(player.worldObj);
                npc.setPosition(player.posX, player.posY, player.posZ);
                for(EntityPixelmon p : npcTeam.getMembers())
                {
                    npc.getPokemonStorage().addToParty(p);
                    mod.logger.info("Gave NPC "+npc.getNPCName()+" pokemon "+p.getName());
                }
                PlayerParticipant playerParticipant = new PlayerParticipant(player, new EntityPixelmon[]{pFirst});
                TrainerParticipant trainerParticipant = new TrainerParticipant(npc, player, npcTeam.getMembers().size());
                BattleController2Participant bc = new BattleController2Participant(playerParticipant,trainerParticipant);
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
        else{
            return true;
        }
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        return (args.length!=1) ? null : getListOfStringsMatchingLastWord(MinecraftServer.getServer().getAllUsernames());
    }
}