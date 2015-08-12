package me.commandcraft.CustomBan;

import static org.bukkit.ChatColor.RED;
import static org.bukkit.ChatColor.YELLOW;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class InventoryManager implements Listener{
	
	public static HashMap<Player, Integer> staff = new HashMap<Player, Integer>();
	public static HashMap<Player, String> next = new HashMap<Player, String>();
	private static ArrayList<Inventory> inventories = new ArrayList<Inventory>();

	public static void openInventory(Player p) {
		if (p.hasPermission("cb.inventory")) {
			createInventories();
			staff.put(p, 0);
			p.openInventory(inventories.get(0));
		} else {
			p.sendMessage(RED + "You don't have permission to execute that command");
		}
	}
	
	private static void createInventories() {
		inventories.clear();
		ArrayList<String> players = new ArrayList<String>();
		for (OfflinePlayer p : Bukkit.getServer().getOfflinePlayers()) {
			players.add(p.getName());
		}
		Inventory myInventory = Bukkit.createInventory(null, 54, "Select the player to ban");
		int j = 0;
		while (players.size() > 0) {
			ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
			SkullMeta meta = (SkullMeta) item.getItemMeta();
			meta.setOwner(players.remove(0));
			item.setItemMeta(meta);
			myInventory.setItem(j, item);
			j++;
			if (j >= 45) {
				if (inventories.size() >= 1) {
					item = new ItemStack(Material.REDSTONE_BLOCK, 1);
					ItemMeta metadata = item.getItemMeta();
					metadata.setDisplayName("Previous page");
					item.setItemMeta(metadata);
					myInventory.setItem(48, item);
				}
				if (players.size() > 45) {
					item = new ItemStack(Material.REDSTONE_BLOCK, 1);
					ItemMeta metadata = item.getItemMeta();
					metadata.setDisplayName("Next page");
					item.setItemMeta(metadata);
					myInventory.setItem(50, item);
				}
				inventories.add(myInventory);
				myInventory = Bukkit.createInventory(null, 54, "Select the player to ban");
			}
		}
		if (!myInventory.getContents()[0].equals(null)) {
			inventories.add(myInventory);
		}
	}
	
	private static void nextStage(ItemStack  item, Player p) {
		SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
		Inventory myInventory = Bukkit.createInventory(null, 9, "Punish " + skullMeta.getOwner());
		ItemStack quartz;
		ItemMeta meta;
		if (Main.banned.contains(skullMeta.getOwner())) {
			quartz = new ItemStack(Material.WOOL, 1, (byte) 5);
			meta = quartz.getItemMeta();
			meta.setDisplayName(YELLOW + "UnBan player");
			quartz.setItemMeta(meta);
			myInventory.setItem(0, quartz);
		} else {
			quartz = new ItemStack(Material.WOOL, 1, (byte) 14);
			meta = quartz.getItemMeta();
			meta.setDisplayName(YELLOW + "Ban player");
			quartz.setItemMeta(meta);
			myInventory.setItem(0, quartz);
		}
		
		quartz = new ItemStack(Material.WOOL, 1);
		meta = quartz.getItemMeta();
		meta.setDisplayName(YELLOW + "Warn player");
		quartz.setItemMeta(meta);
		myInventory.setItem(6, quartz);

		quartz = new ItemStack(Material.WOOL, 1);		
		meta = quartz.getItemMeta();
		meta.setDisplayName(YELLOW + "Kick player");
		quartz.setItemMeta(meta);
		myInventory.setItem(8, quartz);
		
		if (Main.muted.contains(skullMeta.getOwner())) {
			quartz = new ItemStack(Material.WOOL, 1, (byte) 5);
			meta = quartz.getItemMeta();
			meta.setDisplayName(YELLOW + "Unmute player");
			quartz.setItemMeta(meta);
			myInventory.setItem(2, quartz);
		} else {
			quartz = new ItemStack(Material.WOOL, 1, (byte) 14);
			meta = quartz.getItemMeta();
			meta.setDisplayName(YELLOW + "mute player");
			quartz.setItemMeta(meta);
			myInventory.setItem(2, quartz);
		}
		
		quartz = new ItemStack(Material.DIAMOND, 1);
		meta = quartz.getItemMeta();
		meta.setDisplayName(RED + "Go back");
		quartz.setItemMeta(meta);
		myInventory.setItem(4, quartz);
		
		p.openInventory(myInventory);
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Player p = (Player) event.getWhoClicked();
		if (staff.containsKey(p)) {
			event.setCancelled(true);
			if (event.getSlot() == 48) {
				int current = staff.get(p);
				staff.remove(p);
				staff.put(p, current - 1);
				createInventories();
				p.openInventory(inventories.get(current - 1));
			} else if (event.getSlot() == 50) {
				int current = staff.get(p);
				staff.remove(p);
				staff.put(p, current + 1);
				createInventories();
				p.openInventory(inventories.get(current + 1));
			} else {
				ItemStack item = event.getInventory().getItem(event.getSlot());
				nextStage(item, p);
				staff.remove(p);
				SkullMeta skullmeta = (SkullMeta) item.getItemMeta();
				next.put(p, skullmeta.getOwner());
			}
		} else if (next.containsKey(p)) {
			event.setCancelled(true);
			int slot = event.getSlot();
			if (slot == 0) {
				if (Main.banned.contains(next.get(p))) {
					Main.banned.remove(next.get(p));
					Bukkit.getServer().broadcastMessage(YELLOW + next.get(p) + " has been unbanned.");
					next.remove(p);
					p.closeInventory();
				} else {
					Bukkit.getServer().broadcastMessage(YELLOW + next.get(p) + " has been banned.");
					Main.banned.add(next.get(p));
					Bukkit.getServer().getPlayer(next.get(p)).kickPlayer("You have been banned");
					next.remove(p);
					p.closeInventory();
				}
			} else if (slot == 2) {
				if (Main.muted.contains(next.get(p))) {
					Bukkit.getServer().broadcastMessage(YELLOW + next.get(p) + " has been unmuted.");
					Main.muted.remove(next.get(p));
					next.remove(p);
					p.closeInventory();
				} else {
					Main.muted.add(next.get(p));
					Bukkit.getServer().broadcastMessage(YELLOW + next.get(p) + " has been muted.");
					next.remove(p);
					p.closeInventory();
				}
			} else if (slot == 4) {
				next.remove(p);
				p.closeInventory();
				openInventory(p);
			} else if (slot == 6) {
				Bukkit.getServer().getPlayer(next.get(p)).sendMessage(RED + "You have  been warned by " + p.getName());
				next.remove(p);
				p.closeInventory();
			} else if (slot == 8) {
				Bukkit.getServer().broadcastMessage(YELLOW + next.get(p) + " has been kicked.");
				Bukkit.getServer().getPlayer(next.get(p)).kickPlayer("You have been kicked");
				next.remove(p);
				p.closeInventory();
			}
		}
	}
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event){
		Player p = (Player) event.getPlayer();
		if (staff.containsKey(p)) {
			staff.remove(p);
		}
		if (next.containsKey(p)){
			next.remove(p);
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player p = event.getPlayer();
		if (Main.banned.contains(p.getName())){
			event.setJoinMessage("");
			event.getPlayer().kickPlayer("You are banned");
		}
	}
	
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		Player p = event.getPlayer();
		if (Main.muted.contains(p.getName()))  {
			event.setCancelled(true);
			p.sendMessage(RED + "You are muted");
		}
	}
}