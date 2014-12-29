package com.ryanv97.pixelgyms.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

public class GymCommands extends CommandBase{
    @Override
    public String getCommandName() {
        return "gym";
    }

    @Override
    public String getCommandUsage(ICommandSender iCommandSender) {
        return "/gym <position/leave>";
    }

    @Override
    public void processCommand(ICommandSender iCommandSender, String[] strings) {

    }
}
