package com.goldenglow.team;

import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;

import java.util.ArrayList;
import java.util.List;

public class Team
{
    private final String name;
    private List<EntityPixelmon> members =  new ArrayList<EntityPixelmon>();


    public Team(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public EntityPixelmon getMember(int slot)
    {
        if(slot>members.size())
            return null;
        return members.get(slot);
    }

    public List<EntityPixelmon> getMembers()
    {
        return members;
    }

    public void addMember(EntityPixelmon pixelmon)
    {
        if(members.size()<6)
        {
            members.add(pixelmon);
        }
    }
}
