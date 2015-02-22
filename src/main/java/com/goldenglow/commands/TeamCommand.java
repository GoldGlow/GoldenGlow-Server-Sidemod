package com.goldenglow.commands;

import com.goldenglow.GoldenGlow;
import com.goldenglow.NPCFunctions;
import com.goldenglow.team.Team;
import com.goldenglow.team.TeamHandler;
import com.goldenglow.util.Reference;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.comm.packetHandlers.battles.BattleGuiClosed;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChunkCoordinates;
import noppes.npcs.entity.EntityNPCInterface;

import java.io.IOException;

public class TeamCommand extends CommandBase
{
    @Override
    public String getCommandName() {
        return "team";
    }

    @Override
    public String getCommandUsage(ICommandSender iCommandSender) {
        return "/team reload:list";
        //return "/team 'Reload':'List':<Team Name> <Members:Moveset> [member]";
    }

    @Override
    public void processCommand(ICommandSender iCommandSender, String[] args)
    {
        if(isValid(iCommandSender, args))
        {
            if(args[0].equalsIgnoreCase("reload")){
                try {
                    TeamHandler.instance.loadTeams();
                } catch (IOException e) {
                    e.printStackTrace();
                    iCommandSender.addChatMessage(new ChatComponentText(Reference.messagePrefix + Reference.colorRed + "ERROR LOADING TEAM FILE!"));
                }
            }
            if(args[0].equalsIgnoreCase("list"))
            {
                iCommandSender.addChatMessage(new ChatComponentText(Reference.messagePrefix + Reference.colorBlue + Reference.bold + "Loaded Teams:"));
                for(Team team : TeamHandler.instance.getTeams())
                {
                    iCommandSender.addChatMessage(new ChatComponentText(Reference.messagePrefix + team.getName()));
                }
            }
        }
    }

    boolean isValid(ICommandSender iCommandSender, String[] args)
    {
        if(args.length<1){
            iCommandSender.addChatMessage(new ChatComponentText(Reference.messagePrefix + Reference.colorRed + getCommandUsage(iCommandSender)));
            return false;
        }
        if(args.length<2){
            if(args[0].equalsIgnoreCase("reload"))
                return true;
            else if(TeamHandler.instance.getTeam(args[0])!=null)
                return true;
            else{
                iCommandSender.addChatMessage(new ChatComponentText(Reference.messagePrefix + Reference.colorRed + "Team not found."));
                return false;
            }
        }
        if(args.length<3){
            if(args[1].equalsIgnoreCase("members"))
                return true;
            else if(args[1].equalsIgnoreCase("")){
                iCommandSender.addChatMessage(new ChatComponentText(Reference.messagePrefix + Reference.colorRed + "/team "+args[0]+" moveset [member]"));
                return false;
            }
        }
        if(args.length<4){

        }
        return true;
    }
}
