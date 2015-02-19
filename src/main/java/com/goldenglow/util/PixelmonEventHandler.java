package com.goldenglow.util;

import com.goldenglow.CustomNPCBattle;
import com.goldenglow.GoldenGlow;
import com.pixelmonmod.pixelmon.api.events.PlayerBattleEndedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.util.ChunkCoordinates;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.entity.EntityNPCInterface;

import java.util.ArrayList;
import java.util.List;

public class PixelmonEventHandler
{
    private List<CustomNPCBattle> customBattles = new ArrayList<CustomNPCBattle>();

    @SubscribeEvent
    public void onBeatTrainer(PlayerBattleEndedEvent event)
    {
        GoldenGlow.instance.logger.info("Battle ended!");
        for(CustomNPCBattle battle : customBattles)
        {
            if(battle.hasPlayer(event.player))
            {
                if(event.result==PlayerBattleEndedEvent.battleResults.victory)
                {
                    NoppesUtilServer.openDialog(event.player, new EntityNPCInterface(event.player.worldObj) {
                        @Override
                        public ChunkCoordinates getPlayerCoordinates() {
                            return null;
                        }
                    }, battle.getWinDialog());
                }
                if(event.result==PlayerBattleEndedEvent.battleResults.defeat)
                {
                    NoppesUtilServer.openDialog(event.player, new EntityNPCInterface(event.player.worldObj) {
                    @Override
                    public ChunkCoordinates getPlayerCoordinates() {
                        return null;
                    }
                }, battle.getLoseDialog());
                }
            }
        }
    }

    public void addBattle(CustomNPCBattle battle)
    {
        customBattles.add(battle);
        GoldenGlow.instance.logger.info("Registered Battle");
    }

    public boolean hasBattle(CustomNPCBattle battle)
    {
        return customBattles.contains(battle);
    }

    public CustomNPCBattle getBattle(int id)
    {
        return customBattles.get(id);
    }
}
