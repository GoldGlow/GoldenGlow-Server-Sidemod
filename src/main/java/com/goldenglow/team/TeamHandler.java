package com.goldenglow.team;

import com.goldenglow.GoldenGlow;
import com.goldenglow.util.Reference;
import com.pixelmonmod.pixelmon.battles.attacks.Attack;
import com.pixelmonmod.pixelmon.config.PixelmonEntityList;
import com.pixelmonmod.pixelmon.config.PixelmonItemsHeld;
import com.pixelmonmod.pixelmon.database.DatabaseMoves;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.EVsStore;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.Gender;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.IVStore;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.Moveset;
import com.pixelmonmod.pixelmon.enums.EnumNature;
import com.pixelmonmod.pixelmon.enums.EnumPokemon;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class TeamHandler
{
    static GoldenGlow mod;
    public static TeamHandler instance;

    File teamFile = new File(Reference.configDir, "teams.cfg");
    ArrayList<Team> teams = new ArrayList<Team>();

    public TeamHandler()
    {
        this.mod = GoldenGlow.instance;
        this.instance = this;
    }

    public void init()
    {
        if(!teamFile.exists())
            try {
                teamFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        try {
            loadTeams();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadTeams() throws IOException
    {
        BufferedReader reader = new BufferedReader(new FileReader(teamFile));
        String readLine;
        String teamName = "";
        ArrayList<String> file = new ArrayList<String>();
        EntityPixelmon pixelmon = null;

        while((readLine=reader.readLine())!=null)
        {
            file.add(readLine);
        }
        for(String line : file)
        {
            if(line.startsWith("===")) {
                teamName = line.replace("===","");
                teamName = teamName.replace(" ","");
                createTeam(teamName);
                mod.logger.info("Created new Team: "+teamName+"!");
            }
            if(line.contains("@"))
            {
                String pixelName = line.split(" @ ")[0];
                Gender gender = Gender.Female;
                if(line.contains("(M)")) {
                    pixelName = pixelName.replace(" (M)", "");
                    gender = Gender.Male;
                }
                if(line.contains("(F)"))
                    pixelName = pixelName.replace(" (F)","");

                if(EnumPokemon.hasPokemonAnyCase(pixelName))
                {
                    pixelmon = (EntityPixelmon) PixelmonEntityList.createEntityByName(pixelName, DimensionManager.getWorld(0));
                    pixelmon.gender = gender;
                    if((PixelmonItemsHeld.getHeldItem(line.split(" @ ")[1]))!=null)
                    {
                        pixelmon.setHeldItem(new ItemStack(PixelmonItemsHeld.getHeldItem(line.split(" @ ")[1])));
                    }else{
                        mod.logger.error("HeldItem not found: '" + line.split(" @ ")[1] + "' on Pokemon: " + pixelName);
                    }
                }else{
                    mod.logger.error("Pokemon not found: "+pixelName);
                }
            }
            //ToDo: Add Abilities
            if(line.startsWith("Level:"))
            {
                int lvl = Integer.parseInt(line.replace("Level: ",""));
                if(pixelmon!=null&&lvl<=100&&lvl>0)
                    pixelmon.getLvl().setLevel(lvl);
                else
                    mod.logger.error("Could not set a pokemons level!");
            }
            if(line.startsWith("Shiny: ")&&line.contains("Yes"))
            {
                pixelmon.setIsShiny(true);
                mod.logger.info("Made pokemon: "+pixelmon.getName()+" a shiny!");
            }
            if(line.startsWith("EVs:"))
            {
                line = line.replace("EVs: ","");
                EVsStore evStore = pixelmon.stats.EVs;
                for(String evs : line.split(" / "))
                {
                    int ev = Integer.parseInt(evs.split(" ")[0]);
                    if(evs.split(" ")[1].equalsIgnoreCase("hp")&&ev<=255&&ev>0)
                        evStore.HP=ev;
                    if(evs.split(" ")[1].equalsIgnoreCase("atk")&&ev<=255&&ev>0)
                        evStore.Attack=ev;
                    if(evs.split(" ")[1].equalsIgnoreCase("def")&&ev<=255&&ev>0)
                        evStore.Defence=ev;
                    if(evs.split(" ")[1].equalsIgnoreCase("spa")&&ev<=255&&ev>0)
                        evStore.SpecialAttack=ev;
                    if(evs.split(" ")[1].equalsIgnoreCase("spd")&&ev<=255&&ev>0)
                        evStore.SpecialDefence=ev;
                    if(evs.split(" ")[1].equalsIgnoreCase("spe")&&ev<=255&&ev>0)
                        evStore.Speed=ev;
                    mod.logger.info("Added EV: '"+evs.split(" ")[1]+"-"+ev+"' to pokemon: "+pixelmon.getName());
                }
                pixelmon.stats.EVs = evStore;
            }
            if(line.startsWith("IVs:"))
            {
                line = line.replace("IVs: ","");
                IVStore ivStore = pixelmon.stats.IVs;
                for(String ivs : line.split(" / "))
                {
                    int iv = Integer.parseInt(ivs.split(" ")[0]);
                    if(ivs.split(" ")[1].equalsIgnoreCase("hp")&&iv<=31&&iv>0)
                        ivStore.HP=iv;
                    if(ivs.split(" ")[1].equalsIgnoreCase("atk")&&iv<=31&&iv>0)
                        ivStore.Attack=iv;
                    if(ivs.split(" ")[1].equalsIgnoreCase("def")&&iv<=31&&iv>0)
                        ivStore.Defence=iv;
                    if(ivs.split(" ")[1].equalsIgnoreCase("spa")&&iv<=31&&iv>0)
                        ivStore.SpAtt=iv;
                    if(ivs.split(" ")[1].equalsIgnoreCase("spd")&&iv<=31&&iv>0)
                        ivStore.SpDef=iv;
                    if(ivs.split(" ")[1].equalsIgnoreCase("spe")&&iv<=31&&iv>0)
                        ivStore.Speed=iv;
                    mod.logger.info("Added IV: '"+ivs.split(" ")[1]+"-"+iv+"' to pokemon: "+pixelmon.getName());
                }
                pixelmon.stats.IVs = ivStore;
            }
            if(line.contains(" Nature")&&!line.contains("Nature Power"))
            {
                line = line.replace(" Nature","");
                if(EnumNature.hasNature(line))
                {
                    pixelmon.setNature(EnumNature.natureFromString(line));
                    mod.logger.info("Added Nature: '"+line+"' to pokemon: "+pixelmon.getName());
                }
            }
            if(line.startsWith("Moves:"))
            {
                Moveset moveset = new Moveset();
                line = line.replace("Moves: ","");
                String[] moves = line.split(" / ");
                for(String move : moves)
                {
                    Attack attack = DatabaseMoves.getAttack(move);
                    if(attack!=null) {
                        if (moveset.size() < 4) {
                            moveset.add(attack);
                            mod.logger.info("Added move: "+move+" to pokemon: "+pixelmon.getName());
                        }else{
                            mod.logger.error("Pokemon: "+pixelmon.getName()+" already has a full moveset!");
                            mod.logger.error("Skipping move: "+move);
                        }
                    }else{
                        mod.logger.error("Move not found: "+move);
                    }
                }
                pixelmon.setMoveset(moveset);
                getTeam(teamName).addMember(pixelmon);
                mod.logger.info("Finished adding pokemon: "+pixelmon.getName()+" to team: "+teamName);
            }
        }
    }

    public void createTeam(String name)
    {
        Team team = new Team(name);
        this.teams.add(team);
    }

    public Team getTeam(String name)
    {
        for(Team team : this.teams)
        {
            if(team.getName().equalsIgnoreCase(name)){
                return team;
            }
        }
        return null;
    }
}