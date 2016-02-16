package com.devirax.avoidthevoid.commands;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import com.devirax.avoidthevoid.AvoidTheVoid;
import com.devirax.avoidthevoid.utils.Messenger;
import com.devirax.avoidthevoid.utils.Messenger.State;

public class CommandHandler implements CommandExecutor
{
    private HashMap<String, SubCommand> commands;
    
    public CommandHandler(final Plugin plugin) {
        super();
        this.commands = new HashMap<String, SubCommand>();
        this.loadCommands();
    }
    
    private void loadCommands() {
        this.commands.put("spawn", new Spawn());
        this.commands.put("setspawn", new SetSpawn());
        this.commands.put("create", new Create());
        this.commands.put("settings", new Settings());
        this.commands.put("calc", new Calc());
        this.commands.put("expel", new Expel());
        this.commands.put("setlever", new SetLever());
        this.commands.put("act", new Act());
        this.commands.put("setloc", new SetLocation());
        this.commands.put("gennether", new GenerateNether());
        this.commands.put("veteran", new Veteran());
        this.commands.put("top", new Top());
        this.commands.put("info", new Info());
        this.commands.put("invite", new Invite());
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String commandLabel, String[] args) {
      
    	if(!AvoidTheVoid.hasDatabaseConnection()) {
			Messenger.message(sender, "You cannot do this whilst the plugin is in safe mode", State.BAD);
			return true;
		}
    	
        if (args == null || args.length < 1) {
            return true;
        }
        
        final String sub = args[0];
        final Vector<String> arguments = new Vector<String>();
        arguments.addAll(Arrays.asList(args));
        arguments.remove(0);
        args = arguments.toArray(new String[0]);
        if (!this.commands.containsKey(sub)) {
        	Messenger.message(sender, "That command does not exist", State.BAD);
            return true;
        }
        try {
            this.commands.get(sub).onCommand(sender, args);
        }
        catch (Exception e) {
            e.printStackTrace();
            Messenger.message(sender, "An error has occured", State.BAD);
        }
        return true;
    }
}
