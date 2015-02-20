package com.goldenglow.commands;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.comm.packetHandlers.battles.BattleGuiClosed;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

public class ResetCamCommand extends CommandBase
{
    @Override
    public String getCommandName() {
        return "resetcam";
    }

    @Override
    public String getCommandUsage(ICommandSender iCommandSender) {
        return null;
    }

    @Override
    public void processCommand(ICommandSender iCommandSender, String[] strings)
    {
        Pixelmon.network.sendTo(new BattleGuiClosed(),getPlayer(iCommandSender,iCommandSender.getCommandSenderName()));
    }
}
