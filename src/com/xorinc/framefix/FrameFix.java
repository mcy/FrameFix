package com.xorinc.framefix;

import java.lang.reflect.Field;

import net.minecraft.server.v1_7_R1.EntityItemFrame;

import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftItemFrame;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class FrameFix extends JavaPlugin implements Listener {
	
	private static final String FIELD = "invulnerable";
	
	public void onEnable(){
		
		getServer().getPluginManager().registerEvents(this, this);
		
	}
	
	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
	public void onHangingPlace(HangingPlaceEvent event){
		
		if(!(event.getEntity() instanceof CraftItemFrame))
			return;
		
		Field f = null;
		
		try {
			f = net.minecraft.server.v1_7_R1.Entity.class.getDeclaredField(FIELD);
		} catch (Exception e1) {
			getLogger().severe(e1 + "");
			return;
		}
		
		f.setAccessible(true);			
			
		EntityItemFrame frame = ((CraftItemFrame) event.getEntity()).getHandle();
				
		try {
			f.set(frame, true);
		} catch (Exception e1) {
			getLogger().severe(e1 + "");
			return;
		}
			

		
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void onChunkLoad(ChunkLoadEvent event){
		
		Chunk chunk = event.getChunk();
		
		Field f = null;
		
		try {
			f = net.minecraft.server.v1_7_R1.Entity.class.getDeclaredField(FIELD);
		} catch (Exception e1) {
			getLogger().severe(e1 + "");
			return;
		}
		
		f.setAccessible(true);
		
		for(Entity e : chunk.getEntities()){
			
			if(!(e instanceof CraftItemFrame))
				continue;
			
			EntityItemFrame frame = ((CraftItemFrame) e).getHandle();
			
			
			try {
				f.set(frame, true);
			} catch (Exception e1) {
				getLogger().severe(e1 + "");
				return;
			}
			
		}
				
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void onChunkUnload(ChunkUnloadEvent event){
		
		Chunk chunk = event.getChunk();
		
		Field f = null;
		
		try {
			f = net.minecraft.server.v1_7_R1.Entity.class.getDeclaredField(FIELD);
		} catch (Exception e1) {
			getLogger().severe(e1 + "");
			return;
		}
		
		f.setAccessible(true);
		
		for(Entity e : chunk.getEntities()){
			
			if(!(e instanceof CraftItemFrame))
				continue;
			
			EntityItemFrame frame = ((CraftItemFrame) e).getHandle();
			
			
			try {
				f.set(frame, false);
			} catch (Exception e1) {
				getLogger().severe(e1 + "");
				return;
			}
			
		}
		
	}
	
	@EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event){
		
		if(!(event.getRightClicked() instanceof ItemFrame))
			return;
		
		if(event.getPlayer().isSneaking()){
			
			ItemFrame frame = (ItemFrame) event.getRightClicked();
			
			EntityDamageByEntityEvent edbee = new EntityDamageByEntityEvent(event.getPlayer(), frame, DamageCause.ENTITY_ATTACK, 1F);
			
			getServer().getPluginManager().callEvent(edbee);
			
			if(edbee.isCancelled())
				return;
			
			if(event.getPlayer().getGameMode() != GameMode.CREATIVE){
				ItemStack item = frame.getItem();
				
				World world = frame.getWorld();
				
				if(item.getType() != Material.AIR)
					world.dropItemNaturally(frame.getLocation(), item);
				
				world.dropItemNaturally(frame.getLocation(), new ItemStack(Material.ITEM_FRAME));
			}
			
			frame.remove();
			event.setCancelled(true);
			
		}
		
	}
	
}
