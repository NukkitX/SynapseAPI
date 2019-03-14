package org.itxtech.synapseapi.command;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import org.itxtech.synapseapi.SynapsePlayer;

public class TransferCommand extends Command {

    public TransferCommand(String name) {
        super(name);
        this.setDescription("Transfer server to another server");
        this.setUsage("/transfer <description>");
        this.setPermission("synapseapi.command.transfer");
        this.setAliases(new String[] {"transfer", "stransfer", "synapsetransfer", "proxy"});
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if (sender instanceof SynapsePlayer){
            if (args.length > 0){
                ((SynapsePlayer) sender).transferByDescription(args[0]);
                return true;
            }
            sender.sendMessage(TextFormat.RED + this.getUsage());
            return false;
        }
        sender.sendMessage(TextFormat.RED + "[SynapseAPI] This command only available for player!");
        return false;
    }
}
