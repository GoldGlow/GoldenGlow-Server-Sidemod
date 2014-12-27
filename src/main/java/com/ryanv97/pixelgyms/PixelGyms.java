package com.ryanv97.pixelgyms;

import com.ryanv97.pixelgyms.commands.GymAdminCommand;
import com.ryanv97.pixelgyms.config.GymConfiguration;
import com.ryanv97.pixelgyms.gym.GymHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid="pixelgyms", name="PixelGyms", version = "1.0.0", dependencies = "required-after:pixelmon", acceptableRemoteVersions="*")
public class PixelGyms
{
    public static final String MODID = "pixelgym";
    public static final String NAME = "PixelGyms";
    public static final String VERSION = "1.0.0";

    @Mod.Instance("gymqueue")
    public static PixelGyms instance;
    public static GymConfiguration config;
    public static GymHandler gymHandler;
    public static final Logger logger = LogManager.getLogger("PixelGyms");

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        config = new GymConfiguration();
        gymHandler = new GymHandler(this);
        gymHandler.loadGyms();
        logger.info("Initializing PixelGyms "+VERSION+" addon...");
    }

    @Mod.EventHandler
    public void onServerStart(FMLServerStartingEvent event)
    {
        event.registerServerCommand(new GymAdminCommand());
    }
}