package com.goldenglow.commands;

import com.goldenglow.GoldenGlow;
import com.goldenglow.util.Reference;
import com.goldenglow.gym.GymLeader;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;

import java.util.List;

public class GymAdminCommands extends CommandBase
{

    private final String[] commands = new String[]{"create","remove","list","tp","addPlayer","removePlayer","listPlayers","nextPlayer","reload"};

    @Override
    public String getCommandName() {
        return "gymadmin";
    }

    @Override
    public String getCommandUsage(ICommandSender iCommandSender) {
        return Reference.messagePrefix+Reference.colorRed+"/gymadmin "+Reference.colorRed+"<create/remove/list/tp/addPlayer/removePlayer/listPlayers"+Reference.colorRed+"/nextPlayer/reload>";
    }

    @Override
    public void processCommand(ICommandSender commandSender, String[] args) {

        if (args.length < 1) {
            commandSender.addChatMessage(new ChatComponentText(getCommandUsage(commandSender)));
            return;
        }

        if (args[0].equalsIgnoreCase("create")) {
            if(args.length==1){
                commandSender.addChatMessage(new ChatComponentText(Reference.colorRed+"/gymadmin create <Gym Name> <Level Cap> - Creates a Gym "+Reference.colorRed+"and sets the warp at your position"));
            }
            if(args.length==2){
                commandSender.addChatMessage(new ChatComponentText(Reference.messagePrefix + Reference.colorRed + "Error: Please specify a level cap!"));
                commandSender.addChatMessage(new ChatComponentText(Reference.messagePrefix + Reference.colorRed+"/gymadmin create "+args[1]+" <Level Cap>"));
            }
            if (args.length > 2) {
                EntityPlayer player = getPlayer(commandSender, commandSender.getCommandSenderName());
                GoldenGlow.config.addGym(args[1], player.posX, player.posY, player.posZ, Integer.parseInt(args[2]), player);
            }
        }
        if (args[0].equalsIgnoreCase("remove")) {
            if(args.length==1){
                commandSender.addChatMessage(new ChatComponentText(Reference.colorRed+"/gymadmin remove <Gym Name> - Removes the given Gym."));
            }
            if (args.length>=2) {
                GoldenGlow.config.removeGym(args[1], getPlayer(commandSender, commandSender.getCommandSenderName()));
            }
        }
        if (args[0].equalsIgnoreCase("tp")) {
            if(args.length==1){
                commandSender.addChatMessage(new ChatComponentText(Reference.colorRed+"/gymadmin tp <Gym Name> [Player Name]- Teleports you or the given player to a specific gym warp."));
            }else {
                EntityPlayer player = null;
                if (args.length > 2) {
                    try {
                        player = getPlayer(commandSender, args[2]);
                    } catch (Exception excep) {
                        commandSender.addChatMessage(new ChatComponentText(Reference.messagePrefix + Reference.colorRed + "Error: Player is not online or does not exist!"));
                    }
                }else{
                    player = getPlayer(commandSender, commandSender.getCommandSenderName());
                }
                GoldenGlow.gymHandler.teleportPlayer(player, args[1]);
            }
        }
        if (args[0].equalsIgnoreCase("addPlayer")) {
            if(args.length==1) {
                commandSender.addChatMessage(new ChatComponentText(Reference.colorRed+"/gymadmin addPlayer <Player Name> <Gym Name> - Adds a player to the given Gym."));
            }
            if (args.length > 2) {
                GoldenGlow.gymHandler.addPlayer(getPlayer(commandSender, args[1]), args[2]);
            } else {
                commandSender.addChatMessage(new ChatComponentText(Reference.messagePrefix + Reference.colorRed + "Error: No Player specified!"));
            }
        }
        if (args[0].equalsIgnoreCase("removePlayer")) {
            if(args.length==1){
                commandSender.addChatMessage(new ChatComponentText(Reference.colorRed+"/gymadmin removePlayer <Player Name> - Removes a player "+Reference.colorRed+"from the queue they're in."));
            }
            if (args.length > 1) {
                GoldenGlow.gymHandler.removePlayer(getPlayer(commandSender, args[1]), commandSender);
            }
        }
        if (args[0].equalsIgnoreCase("nextPlayer")) {
            if(args.length==1){
                commandSender.addChatMessage(new ChatComponentText(Reference.colorRed+"/gymadmin nextPlayer <Gym Name> - Moves the queue along for the given Gym."));
            }else {
                GoldenGlow.gymHandler.nextPlayer(args[1], commandSender);
            }
        }
        if (args[0].equalsIgnoreCase("listPlayers")) {
            if(args.length==1){
                commandSender.addChatMessage(new ChatComponentText(Reference.colorRed+"/gymadmin listPlayers <Gym Name> - Lists all players in this Gym's queue."));
            }
            GoldenGlow.gymHandler.listPlayers(commandSender, args[1]);
        }
        if (args[0].equalsIgnoreCase("list")) {
            GoldenGlow.gymHandler.listGyms(commandSender);
        }
        if (args[0].equalsIgnoreCase("reload")) {
            GoldenGlow.gymHandler.loadGyms();
            GoldenGlow.config.reload();
            commandSender.addChatMessage(new ChatComponentText(Reference.messagePrefix+"Successfully reloaded Gyms!"));
        }
        if (args[0].equalsIgnoreCase("addLeader")) {
            if(args.length==1) {
                commandSender.addChatMessage(new ChatComponentText(Reference.colorRed+"/gymadmin addLeader <Player Name> <Gym Name>"));
            }else if(args.length==2){
                commandSender.addChatMessage(new ChatComponentText(Reference.colorRed+"/gymadmin addLeader "+args[1]+" <Gym Name>"));
            }else{
                EntityPlayer player = getPlayer(commandSender, args[1]);
                if(player!=null)
                    GymLeader.register(player, args[2]);
            }
        }
        if (args[0].equalsIgnoreCase("removeLeader")) {
            if(args.length==1) {
                commandSender.addChatMessage(new ChatComponentText(Reference.colorRed+"/gymadmin removeLeader <Player Name>"));
            }else{
                EntityPlayer player = getPlayer(commandSender, args[1]);
                if(player!=null)
                    GymLeader.register(player, "");
            }
        }
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        if(args.length==1)
        {
            return getListOfStringsMatchingLastWord(args, this.commands);
        }
        if(args.length==2) {
            if (args[0].equalsIgnoreCase("addPlayer"))
            {
                return getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());
            }
            if (args[0].equalsIgnoreCase("removePlayer"))
            {
                return getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());
            }
        }
        if(args.length==3)
        {
            if(args[0].equalsIgnoreCase("tp")){
                return getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());
            }
        }
        return null;
    }
}