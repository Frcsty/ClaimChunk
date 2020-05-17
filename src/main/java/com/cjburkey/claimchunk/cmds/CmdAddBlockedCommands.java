package com.cjburkey.claimchunk.cmds;

import com.cjburkey.claimchunk.ClaimChunk;
import com.cjburkey.claimchunk.Config;
import com.cjburkey.claimchunk.Utils;
import com.cjburkey.claimchunk.cmd.Argument;
import com.cjburkey.claimchunk.cmd.ICommand;
import com.cjburkey.claimchunk.cmd.MainHandler;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class CmdAddBlockedCommands implements ICommand
{

    @Override
    public String getCommand()
    {
        return "disableCmds";
    }

    @Override
    public String getDescription()
    {
        return "disables listed commands within a chunk";
    }

    @Override
    public boolean hasPermission(CommandSender sender)
    {
        return Utils.hasPerm(sender, true, "disablecmds");
    }

    @Override
    public String getPermissionMessage()
    {
        return ClaimChunk.getInstance().getMessages().accessNoPerm;
    }

    @Override
    public Argument[] getPermittedArguments()
    {
        return new Argument[] {new Argument("add", Argument.TabCompletion.NONE), new Argument("remove", Argument.TabCompletion.NONE)};
    }

    @Override
    public int getRequiredArguments()
    {
        return 0;
    }

    @Override
    public boolean onCall(String cmdUsed, Player executor, String[] args)
    {
        if (args.length == 0)
        {
            MainHandler.listBlockedCommands(executor, executor.getLocation().getChunk());
            return true;
        }

        if (args.length == 2)
        {
            String commands = "";
            for (String arg : Arrays.copyOfRange(args, 1, args.length))
            {
                commands = commands.concat(arg + ",");
            }
            switch (args[0])
            {
                case "remove":
                    MainHandler.removeBlockedCommands(executor, executor.getLocation().getChunk(), commands);
                    break;
                case "add":
                    if (!ClaimChunk.getInstance().getConfig().getStringList("chunks.disabledBlockedCommands").isEmpty())
                    {
                        for (String arg : Arrays.copyOfRange(args, 1, args.length)) {
                            if (ClaimChunk.getInstance().getConfig().getStringList("chunks.disabledBlockedCommands").contains(arg))
                            {
                                Utils.msg(executor, "&6&l[!]&r &6Command '" + arg + "' cannot be disabled!");
                                return true;
                            }
                        }
                    }
                    MainHandler.addBlockedCommands(executor, executor.getLocation().getChunk(), commands);
                    break;
            }
        }
        return true;
    }
}
