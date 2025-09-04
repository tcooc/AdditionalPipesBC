package buildcraft.additionalpipes;

import java.util.Collection;

import buildcraft.additionalpipes.api.TeleportPipeType;
import buildcraft.additionalpipes.pipes.PipeBehaviorTeleport;
import buildcraft.additionalpipes.pipes.TeleportManager;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.translation.I18n;

public class CommandAdditionalPipes extends CommandBase {

	@Override
	public String getName() {
		return AdditionalPipes.MODID.toLowerCase();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		if(args.length > 1 && "teleport".equals(args[0])) 
		{
			//let's be a bit lenient with plurals
			
			TeleportPipeType type;
			
			if(args[1].equals("items") || args[1].equals("item"))
			{
				type = TeleportPipeType.ITEMS;
			}
			else if(args[1].equals("fluids") || args[1].equals("fluid"))
			{
				type = TeleportPipeType.FLUIDS;
			}
			else if(args[1].equals("power"))
			{
				type = TeleportPipeType.POWER;
			}
			else
			{
				return;
			}
			
			Collection pipes = TeleportManager.instance.getAllPipesInNetwork(type);
			
			sender.sendMessage((ITextComponent) new TextComponentTranslation("command.ap.pipelist_header"));
			for(Object pipeObject : pipes)
			{
				StringBuffer sb = new StringBuffer();
				PipeBehaviorTeleport pipe = (PipeBehaviorTeleport)pipeObject;
				sb.append('[');
				sb.append(pipe.getPosition().getX()).append(", ");
				sb.append(pipe.getPosition().getY()).append(", ");
				sb.append(pipe.getPosition().getZ()).append("] ");
				sb.append(pipe.getOwnerName());
				sender.sendMessage(new TextComponentString(sb.toString()));
			}
		}
		else
		{
			sender.sendMessage(new TextComponentTranslation("command.ap.usage"));
		}
	}


	@Override
	public String getUsage(ICommandSender sender)
	{
		return I18n.translateToLocal("command.ap.usage");
	}


}
