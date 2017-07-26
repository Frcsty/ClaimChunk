package com.cjburkey.claimchunk.cmd;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.cjburkey.claimchunk.Utils;

public class CommandHandler implements CommandExecutor {
	
	private final Queue<ICommand> cmds = new ConcurrentLinkedQueue<>();
	
	public void registerCommand(Class<? extends ICommand> cls) {
		try {
			ICommand cmd = cls.newInstance();
			if (cmd != null && cmd.getCommand() != null && !cmd.getCommand().trim().isEmpty() && !hasCommand(cmd.getCommand())) {
				Utils.log(" Registered cmd: " + cmd.getCommand() + " - " + cmd.getDescription());
				cmds.add(cmd);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public ICommand[] getCmds() {
		return cmds.toArray(new ICommand[cmds.size()]);
	}
	
	public boolean hasCommand(String name) {
		return getCommand(name) != null;
	}
	
	public ICommand getCommand(String name) {
		for (ICommand c : cmds) {
			if (c.getCommand().equalsIgnoreCase(name)) {
				return c;
			}
		}
		return null;
	}
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		runCommands(sender, args);
		return true;
	}
	
	private void runCommands(CommandSender sender, String[] suppliedArguments) {
		if (!(sender instanceof Player)) {
			Utils.msg(sender, "Only in-game players may use ClaimChunk");
			return;
		}
		Player player = (Player) sender;
		if (!player.hasPermission("claimchunk.base")) {
			Utils.toPlayer(player, Utils.getConfigColor("errorColor"), Utils.getLang("NoPermToUse"));
		}
		if (suppliedArguments.length < 1) {
			displayHelp(player);
			return;
		}
		String name = suppliedArguments[0];
		List<String> outArgs = new ArrayList<>();
		for (int i = 1; i < suppliedArguments.length; i ++) {
			outArgs.add(suppliedArguments[i]);
		}
		ICommand cmd = getCommand(name);
		if (cmd == null) {
			displayHelp(player);
			return;
		}
		if (outArgs.size() < cmd.getRequiredArguments() || outArgs.size() > cmd.getPermittedArguments().length) {
			displayUsage(player, cmd);
			return;
		}
		cmd.onCall(player, outArgs.toArray(new String[outArgs.size()]));
	}
	
	private void displayHelp(Player ply) {
		Utils.msg(ply, "&4Invalid command. See: &6/chunk help&r");
	}
	
	private void displayUsage(Player ply, ICommand cmd) {
		StringBuilder out = new StringBuilder();
		out.append("&4Usage: &6/chunk ");
		out.append(cmd.getCommand());
		for (int i = 0; i < cmd.getPermittedArguments().length; i ++) {
			out.append(' ');
			boolean req = i < cmd.getRequiredArguments();
			out.append((req) ? '<' : '[');
			out.append(cmd.getPermittedArguments()[i]);
			out.append((req) ? '>' : ']');
		}
		Utils.msg(ply, out.toString());
	}
	
}