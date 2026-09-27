package com.github.xzcznb.command;

import com.github.xzcznb.util.DIYMobsHelper;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class CommandReloadDIYMobs extends CommandBase {

    @Override
    public String getName() {
        return "reloadDIYmobs";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/reloadDIYmobs";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        DIYMobsHelper.reload();
        sender.sendMessage(new TextComponentString("DIYMobs config reloaded."));
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }
}
