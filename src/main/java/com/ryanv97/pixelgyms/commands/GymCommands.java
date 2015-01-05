package com.ryanv97.pixelgyms.commands;

import com.ryanv97.pixelgyms.PixelGyms;
import com.ryanv97.pixelgyms.gym.Gym;
import com.ryanv97.pixelgyms.util.Reference;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;

public class GymCommands extends CommandBase
{
    @Override
    public String getCommandName() {
        return "gym";
    }

    @Override
    public String getCommandUsage(ICommandSender iCommandSender) {
        return "/gym <accept/position/leave>";
    }

    @Override
    public void processCommand(ICommandSender iCommandSender, String[] args)
    {
        EntityPlayer player = getPlayer(iCommandSender, iCommandSender.getCommandSenderName());
        if(args.length>0)
        {
            if(args[0].equalsIgnoreCase("accept"))
            {
                if(PixelGyms.gymHandler.isInQueue(player))
                {
                    Gym gym = PixelGyms.gymHandler.getGym(player);
                    if(gym.isCounting && gym.getCurrentPlayer().isEntityEqual(player))
                    {
                        gym.isCounting=false;
                        double[] loc = gym.getWarp();
                        player.setPositionAndUpdate(loc[0],loc[1],loc[2]);
                        player.addChatComponentMessage(new ChatComponentText(Reference.messagePrefix+"You have been warped to the Gym!"));
                    }else{
                        player.addChatComponentMessage(new ChatComponentText(Reference.messagePrefix + Reference.colorRed + "Error: You've not been summoned to this gym yet!"));
                    }
                }else{
                    player.addChatComponentMessage(new ChatComponentText(Reference.messagePrefix + Reference.colorRed + "Error: You're not queued for any gyms!"));
                }
            }

            if (args[0].equalsIgnoreCase("removePlayer")) {
                if (args.length > 1) {
                    PixelGyms.gymHandler.removePlayer(player, iCommandSender);
                }
            }
        }
    }
}
