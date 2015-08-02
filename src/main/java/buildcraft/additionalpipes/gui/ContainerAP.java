/**
 * Additional Pipes is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */

package buildcraft.additionalpipes.gui;

import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ICrafting;
import buildcraft.additionalpipes.access.Property;
import buildcraft.core.lib.gui.BuildCraftContainer;
import buildcraft.core.proxy.CoreProxy;

import com.google.common.collect.Lists;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class ContainerAP extends BuildCraftContainer {

	public static class ContainerModifyEvent extends Event {
		public final ContainerAP source;
		public final Object obj;
		public ContainerModifyEvent(ContainerAP source, Object obj) {
			this.source = source;
			this.obj = obj;
		}
	}

	// Shared properties
	public List<Property> clientProps = Lists.<Property>newArrayList();

	public ContainerAP(int inventorySize) {
		super(inventorySize);
	}

	protected <T extends Property> T addPropertyToContainer(T prop) {
		prop.index = clientProps.size();
		clientProps.add(prop);
		return prop;
	}

	@SuppressWarnings("unchecked")
	public <T extends Property> T getProperty(int index) {
		return (T) clientProps.get(index);
	}

	@Override
	public void addCraftingToCrafters(ICrafting par1iCrafting) {
		if (crafters.isEmpty()) {
			// initialize. should be server side
			for (Property prop : clientProps) {
				prop.setValue(getPropertyValue(prop.index));
			}
		}

		super.addCraftingToCrafters(par1iCrafting);
		if (par1iCrafting instanceof EntityPlayerMP) {
			EntityPlayerMP player = (EntityPlayerMP) par1iCrafting;
			for (int i = 0; i < clientProps.size(); i++) {
				sendPropContents(player, i, clientProps.get(i));
			}
		}
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
			return;
		}

		for (int i = 0; i < clientProps.size(); i++) {
			Property prop = clientProps.get(i);
			Object value = getPropertyValue(i);
			if (!prop.equalsValue(value)) {
				prop.setValue(value);
				for (Object crafter : crafters) {
					// crafter is ICrafting
					if (crafter instanceof EntityPlayerMP) {
						sendPropContents((EntityPlayerMP) crafter, i, prop);
					}
				}
			}
		}
	}

	protected void sendPropContents(EntityPlayerMP player, int index, Property prop) {
		PacketProperty packet = new PacketProperty(APPacketIds.BROADCAST_PROPERTY, windowId, index, prop);
		CoreProxy.proxy.sendToPlayer(player, packet);
	}

	// push (client) -> change (server) -> detectAndSendChanges (server) -> set (client)

	@SideOnly(Side.CLIENT)
	public void pushProperty(int index, Object value) {
		Property prop = clientProps.get(index);
		prop.setValue(value);
		PacketProperty packet = new PacketProperty(APPacketIds.PUSH_PROPERTY, windowId, index, prop);
		CoreProxy.proxy.sendToServer(packet.getPacket());
	}

	public void changeProperty(int index, Property prop, EntityPlayer player) {
		if (onChangeProperty(index, prop, player)) {
			clientProps.set(index, prop);
		}
		detectAndSendChanges();
	}

	@SideOnly(Side.CLIENT)
	public void setProperty(int index, ByteBuf data) throws IOException {
		Property prop = clientProps.get(index);
		if (prop == null) {
			clientProps.set(index, Property.readPacket(data));
		} else {
			data.readByte();
			prop.readData(data);
		}
	}

	protected boolean onChangeProperty(int index, Property prop, EntityPlayer player) {
		return true;
	}

	protected abstract Object getPropertyValue(int index);

}
