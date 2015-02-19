package com.goldenglow.commands;

import com.goldenglow.GoldenGlow;
import com.goldenglow.gym.Gym;
import com.goldenglow.gym.GymLeader;
import com.goldenglow.util.Reference;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.util.ArrayList;
import java.util.List;

public class GymCommands extends CommandBase
{
    @Override
    public String getCommandName() {
        return "gym";
    }

    @Override
    public String getCommandUsage(ICommandSender iCommandSender) {
        return "/gym <accept/position/leave/listLeaders>";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void processCommand(ICommandSender iCommandSender, String[] args)
    {
        EntityPlayer player = getPlayer(iCommandSender, iCommandSender.getCommandSenderName());
        if(args.length>0)
        {
            if(args[0].equalsIgnoreCase("accept"))
            {
                if(GoldenGlow.gymHandler.isInQueue(player))
                {
                    Gym gym = GoldenGlow.gymHandler.getGym(player);
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
                    GoldenGlow.gymHandler.removePlayer(player, iCommandSender);
                }
            }

            if (args[0].equalsIgnoreCase("listLeaders")) {
                List<String> list = new ArrayList<String>();
                List<EntityPlayer> players = MinecraftServer.getServer().getConfigurationManager().playerEntityList;
                for(EntityPlayer p : players)
                {
                    if(p.getExtendedProperties(GymLeader.EXT_PROP_NAME)!=null)
                        list.add(p.getDisplayName());
                }
                if(list.size()<1)
                    iCommandSender.addChatMessage(new ChatComponentText(Reference.messagePrefix+Reference.colorRed+"No leaders online!"));
                else
                    iCommandSender.addChatMessage(new ChatComponentText(Reference.messagePrefix+Reference.colorGreen+"Players: "+ EnumChatFormatting.RESET+list));
            }
        }
    }
}
