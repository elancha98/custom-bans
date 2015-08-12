package me.commandcraft.CustomBan;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class Main extends JavaPlugin {
	public final static Logger logger = Logger.getLogger("Minecraft");
	public static ArrayList<String> muted = new ArrayList<String>();
	public static ArrayList<String> banned = new ArrayList<String>();

	public void onEnable() {
		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvents(new InventoryManager(), this);
		
		File carpet = new File(System.getProperty("user.dir") + "/plugins/CustomBans");
		File muted_file = new File(System.getProperty("user.dir") + "/plugins/CustomBans/muted.json");
		File banned_file = new File(System.getProperty("user.dir") + "/plugins/CustomBans/banned.json");
		Gson gson = new Gson();
		
		if (carpet.exists() && muted_file.exists()) {
			try {
				Type type = new TypeToken<List<String>>(){}.getType();
				BufferedReader br = new BufferedReader(new FileReader(muted_file));
				muted = gson.fromJson(br, type);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (carpet.exists() && banned_file.exists()) {
			try {
				Type type = new TypeToken<List<String>>(){}.getType();
				BufferedReader br = new BufferedReader(new FileReader(banned_file));
				banned = gson.fromJson(br, type);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void onDisable() {
		Gson gson = new Gson();
		String json = gson.toJson(muted);
		File carpet = new File(System.getProperty("user.dir") + "/plugins/CustomBans");
		File muted_file = new File(System.getProperty("user.dir") + "/plugins/CustomBans/muted.json");
		if (!carpet.exists()) {
			carpet.mkdir();
		}
		if (!muted_file.exists()) {
			try {
				muted_file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}	
		try {
			FileWriter writer = new FileWriter(muted_file);
			writer.write(json);
			writer.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
		
		json = gson.toJson(banned);
		File banned_file = new File(System.getProperty("user.dir") + "/plugins/CustomBans/banned.json");
		if (!banned_file.exists()) {
			try {
				banned_file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}	
		try {
			FileWriter writer = new FileWriter(banned_file);
			writer.write(json);
			writer.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		if (cmd.getName().equalsIgnoreCase("cban") || label.equalsIgnoreCase("cban")) {
			InventoryManager.openInventory((Player) sender);
		}
		return true;
	}
}
