package net.izenith.Commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.permissions.Permission;

import net.izenith.Main.Util;
import net.izenith.Main.Vars;

public class Report implements HubCommand, Listener {

	static int pageSize = 8;

	@Override
	public String getName() {
		return "report";
	}

	@Override
	public String[] getAliases() {
		return null;
	}

	@Override
	public void onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		try {
			if (sender.hasPermission("report.admin")) {
				if (args[0].equalsIgnoreCase("list")) {
					List<String> list = Vars.main.getConfig().getStringList("reports");

					int size = list.size() / 2;
					if (size == 0) {
						sender.sendMessage(ChatColor.GRAY + "There are " + ChatColor.AQUA + size + ChatColor.GRAY + " reports.");
					} else {
						int pages = (int) Math.ceil((double) size / (double) pageSize);
						int page = 0;

						try {
							page = Integer.parseInt(args[1]) - 1;
						} catch (Exception e) {
							page = 0;
						}

						sender.sendMessage(ChatColor.GRAY + "Reports page " + ChatColor.AQUA + (page + 1) + ChatColor.GRAY + " of " + ChatColor.AQUA + pages + ChatColor.BLACK + ":");
						for (int i = page * pageSize; i < size && i < (page + 1) * pageSize; i++) {
							sender.sendMessage(ChatColor.GRAY + "[" + i + "]" + ChatColor.AQUA + list.get(i * 2 + 1) + ChatColor.BLACK + ": " + ChatColor.GREEN + list.get(i * 2));
						}
						sender.sendMessage(ChatColor.GRAY + "Use " + ChatColor.AQUA + "/report list <page> " + ChatColor.GRAY + "to view other pages.");
					}
					return;
				} else if (args[0].equalsIgnoreCase("remove")) {
					int i = Integer.parseInt(args[1]);
					List<String> list = Vars.main.getConfig().getStringList("reports");
					if (list == null)
						list = new ArrayList<String>();
					list.remove(i * 2);
					list.remove(i * 2);
					Vars.main.getConfig().set("reports", list);
					Vars.main.saveConfig();
					sender.sendMessage(ChatColor.GREEN + "Removed " + ChatColor.GRAY + i);
					return;
				}
			}
			
			if(args.length == 0)
				throw(new ArrayIndexOutOfBoundsException());
			
			String message = "";
			for (String s : args) {
				message += s + " ";
			}
			List<String> list = Vars.main.getConfig().getStringList("reports");
			if (list == null)
				list = new ArrayList<String>();
			list.add(message);
			list.add(sender.getName());
			Vars.main.getConfig().set("reports", list);
			Vars.main.saveConfig();
			sender.sendMessage(ChatColor.GREEN + "Reported: " + ChatColor.RED + message);
			for (Player player : Bukkit.getOnlinePlayers()) {
				if (player.hasPermission("report.admin")) {
					player.sendMessage(ChatColor.GREEN + sender.getName() + ChatColor.BLUE + " submitted a report!");
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			sender.sendMessage(ChatColor.RED + "Usage: " + ChatColor.WHITE + "/report <message>");
		}
	}

	@Override
	public boolean onlyPlayers() {
		return false;
	}

	@Override
	public boolean hasPermission() {
		return false;
	}

	@Override
	public Permission getPermission() {
		return null;
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		List<String> reports = Util.getConfig().getStringList("reports");
		if (e.getPlayer().hasPermission("report.admin") && reports != null && reports.size() > 0) {
			e.getPlayer().sendMessage(ChatColor.BLUE + "There are " + ChatColor.GREEN + reports.size() / 2 + ChatColor.BLUE + " reports!");
		}
	}

}
