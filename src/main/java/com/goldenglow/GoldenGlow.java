package com.goldenglow;

import com.goldenglow.commands.*;
import com.goldenglow.util.PixelmonEventHandler;
import com.goldenglow.util.Reference;
import com.goldenglow.util.TickHandler;
import com.goldenglow.config.GymConfiguration;
import com.goldenglow.gym.GymHandler;
import com.goldenglow.team.TeamHandler;
import com.pixelmonmod.pixelmon.Pixelmon;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.common.MinecraftForge;
import noppes.npcs.entity.EntityCustomNpc;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

@Mod(modid="goldglow", name="Golden Glow", version = "1.0.0", dependencies = "required-after:pixelmon;required-after:customnpcs", acceptableRemoteVersions="*")
public class GoldenGlow
{
    public static final String MODID = "goldglow";
    public static final String NAME = "Golden Glow";
    public static final String VERSION = "1.0.0";

    @Mod.Instance("goldglow")
    public static GoldenGlow instance;
    public static GymConfiguration config;
    public static GymHandler gymHandler;
    public static TeamHandler teamHandler;
    public static PixelmonEventHandler pixelmonEventHandler;
    public static final Logger logger = LogManager.getLogger("GoldenGlow");

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger.info("Initializing GoldenGlow v"+VERSION+" sidemod...");
        if(!new File(Reference.configDir).exists())
            new File(Reference.configDir).mkdir();
        config = new GymConfiguration();
        gymHandler = new GymHandler(this);
        gymHandler.loadGyms();
        teamHandler = new TeamHandler(this);
    }

    @Mod.EventHandler
    public void load(FMLInitializationEvent event)
    {
        FMLCommonHandler.instance().bus().register(new TickHandler());
        pixelmonEventHandler = new PixelmonEventHandler();
        Pixelmon.instance.EVENT_BUS.register(pixelmonEventHandler);
    }

    @Mod.EventHandler
    public void onServerStart(FMLServerStartingEvent event)
    {
        event.registerServerCommand(new GymAdminCommands());
        event.registerServerCommand(new GymCommands());
        event.registerServerCommand(new CreateBattleCommand());
        event.registerServerCommand(new OpenDialogCommand());
        event.registerServerCommand(new TeamCommand());
        teamHandler.init();
    }
}