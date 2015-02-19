package com.goldenglow.commands;

import com.goldenglow.util.Reference;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.controllers.Dialog;
import noppes.npcs.controllers.DialogController;
import noppes.npcs.entity.EntityNPCInterface;

public class OpenDialogCommand extends CommandBase
{
    @Override
    public String getCommandName() {
        return "opendialog";
    }

    @Override
    public String getCommandUsage(ICommandSender iCommandSender) {
        return "/opendialog <Player> <DialogID>";
    }

    @Override
    public void processCommand(ICommandSender iCommandSender, String[] args)
    {
        if(isValid(iCommandSender, args))
        {
            Dialog dialog;
            if((dialog = DialogController.instance.dialogs.get(Integer.parseInt(args[1])))!=null)
            {
                NoppesUtilServer.openDialog(getPlayer(iCommandSender,args[0]), (EntityNPCInterface)iCommandSender, dialog);
            }
        }
    }

    public boolean isValid(ICommandSender iCommandSender, String[] args)
    {
        if(iCommandSender instanceof EntityNPCInterface) {
            if (args.length < 1) {
                iCommandSender.addChatMessage(new ChatComponentText(Reference.messagePrefix + Reference.colorRed + "No Player Specified!"));
                iCommandSender.addChatMessage(new ChatComponentText(Reference.messagePrefix + Reference.colorRed + getCommandUsage(iCommandSender)));
                return false;
            }
            if (getPlayer(iCommandSender, args[0]) == null) {
                iCommandSender.addChatMessage(new ChatComponentText(Reference.messagePrefix + Reference.colorRed + "Couldn't find player: +" + args[0] + "!"));
                return false;
            }
            if (args.length < 2) {
                iCommandSender.addChatMessage(new ChatComponentText(Reference.messagePrefix + Reference.colorRed + "No DialogID Specified!"));
                iCommandSender.addChatMessage(new ChatComponentText(Reference.messagePrefix + Reference.colorRed + "/opendialog " + args[0] + args[1] + " <DialogID>"));
                return false;
            }
            try {
                Integer.parseInt(args[1]);
            } catch (NumberFormatException var7) {
                iCommandSender.addChatMessage(new ChatComponentText(Reference.messagePrefix + Reference.colorRed + "DialogID must be a number!"));
                return false;
            }
            if (!DialogController.instance.hasDialog(Integer.parseInt(args[1]))) {
                iCommandSender.addChatMessage(new ChatComponentText(Reference.messagePrefix + Reference.colorRed + "Dialog not found for ID: " + args[1] + "!"));
                return false;
            }
        }else{
            iCommandSender.addChatMessage(new ChatComponentText(Reference.messagePrefix + Reference.colorRed + "This command is only executable by CustomNPCs!"));
            return false;
        }
        return true;
    }
}
