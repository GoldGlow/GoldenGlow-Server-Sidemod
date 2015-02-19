package com.goldenglow.commands;

import com.goldenglow.GoldenGlow;
import com.goldenglow.team.TeamHandler;
import com.goldenglow.util.Reference;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

public class TeamCommand extends CommandBase
{
    @Override
    public String getCommandName() {
        return "team";
    }

    @Override
    public String getCommandUsage(ICommandSender iCommandSender) {
        return "/team <Reload>:<Team Name> <Members:Moveset> [member]";
    }

    @Override
    public void processCommand(ICommandSender iCommandSender, String[] args)
    {
        iCommandSender.addChatMessage(new ChatComponentText(Reference.messagePrefix + "THIS COMMAND IS A WORK IN PROGRESS!"));
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
            else if(GoldenGlow.instance.teamHandler.getTeam(args[0])!=null)
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
