/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ic.portaltracker;

import java.util.List;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Robin
 */
public class PortalTracker extends JavaPlugin{
    Server server;
    World overworld;
    World nether;
    static int broadcastRange = 7;
    PortalListener portalListener;
    
    @Override
    public void onEnable(){
        server = this.getServer();
        overworld = server.getWorld("world");
        nether = server.getWorld("world_nether");
        
        portalListener = new PortalListener();
        server.getPluginManager().registerEvents(portalListener, this);
        
        System.out.println("PortalTracker enabled!");
    }
    
    @Override
    public void onDisable(){
        HandlerList.unregisterAll(portalListener);
        
        System.out.println("PortalTracker disabled!");
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        return false;
    }
    
    private boolean isEndPortal(List<Block> list){
        for(Block b : list){
            if(b.getType().equals(Material.ENDER_PORTAL_FRAME)){
                return true;
            }
        }
        return false;
    }
    
    private Location getOverworldCoords(List<Block> list){
        double x = 0;
        double y = 0;
        double z = 0;
        
        for(Block b : list){
            x += b.getX();
            y += b.getY();
            z += b.getZ();
        }
        
        //Portal location in the overworld
        x = x / list.size();
        y = y / list.size();
        z = z / list.size();
        
        //Return location
        return new Location(overworld, x, y, z);
    }
    
    private Location getNetherCoords(Location overworldLoc){
        double x = overworldLoc.getX() / 8;
        double z = overworldLoc.getZ() / 8;
        
        return new Location(nether, Math.round(x), 0, Math.round(z));
        
        
    }
    
    private void sendCoords(Player p, Location portal){
        p.sendMessage(ChatColor.DARK_PURPLE+"You just created a new portal!\n"+
                "To link up your portal correctly, build a second portal on the following coordinates:\n"+
                ChatColor.LIGHT_PURPLE+"X = "+portal.getX()+"   Z = "+portal.getZ());
        
    }
    
    public class PortalListener implements Listener{
        @EventHandler
        public void portalIgnition(PortalCreateEvent e){
            System.out.println("1");
            Location overworldLocation;
            Location netherCoords;
            
            //Check if portal is a nether portal
            if(isEndPortal(e.getBlocks())){System.out.println("2");return;}
            
            //Check if portal is created in the overworld
            if(!e.getWorld().equals(overworld)){System.out.println("3");return;}
            
            overworldLocation = getOverworldCoords(e.getBlocks());
            netherCoords = getNetherCoords(overworldLocation);
            
            server.broadcastMessage(overworldLocation.toString());
            
            for(Entity entity : e.getWorld().getNearbyEntities(overworldLocation, broadcastRange, broadcastRange, broadcastRange)){
                System.out.println("Entity: "+entity.getType());
                if(entity instanceof Player){
                    System.out.println("ja");
                    sendCoords((Player)entity, netherCoords);
                }
            }
            
            
        }
    }
}
