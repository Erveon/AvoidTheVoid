package com.devirax.avoidthevoid.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.devirax.avoidthevoid.AvoidTheVoid;
import com.devirax.avoidthevoid.utils.Messenger;
import com.devirax.avoidthevoid.utils.Messenger.State;

public class SetTitle implements SubCommand {

	@Override
	public boolean onCommand(CommandSender sender, String[] args) {
		
		if(!(sender instanceof Player)) {
			sender.sendMessage("You may not execute this command from the console.");
			return true;
		}
		
		Player p = (Player) sender;
		
		if(args.length == 1) {
			p.sendMessage("§4Usage: /atv settitle [TITLE]");
			return true;
		}
		if(AvoidTheVoid.getWorldEdit().getSelection(p) == null) {
			Messenger.message(sender, "You must have a WorldEdit selection to do this", State.BAD);
			return true;
		}
		StringBuilder sb = new StringBuilder();
		for(String arg : args) {
			if(arg.equalsIgnoreCase("settitle"))
				continue;
			sb.append(arg);
			sb.append(" ");
		}
		String title = sb.toString();
		if(title.endsWith(" "))
			title = title.substring(0, title.length() - 1);
		boolean found = false;
		int id = 1;
		while(!found) {
			if(AvoidTheVoid.getPlugin().getConfig().contains("titleregion."+id)) {
				++id;
			} else {
				found = true;
			}
		}
		AvoidTheVoid.getPlugin().getConfig().set("titleregion."+id+".title", title);
		AvoidTheVoid.getPlugin().saveConfig();
		Messenger.message(sender, "The title has been set", State.GOOD);
		p.sendMessage(AvoidTheVoid.getPlugin().getConfig().getString("titleregion."+id+".title"));
		
		return true;
	}

}
