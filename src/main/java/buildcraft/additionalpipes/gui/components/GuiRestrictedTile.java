/**
 * Additional Pipes is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */

package buildcraft.additionalpipes.gui.components;

import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import buildcraft.additionalpipes.AdditionalPipes;
import buildcraft.additionalpipes.access.ContainerRestrictedTile;
import buildcraft.additionalpipes.api.AccessRule;
import buildcraft.additionalpipes.gui.GuiAdditionalPipes;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class GuiRestrictedTile<T extends ContainerRestrictedTile> extends GuiAdditionalPipes<T> {

	protected final class AccessLedger extends Ledger {

		public AccessLedger() {
			maxWidth = 96;
			maxHeight = 72;
			overlayColor = 0xffffff;
			headerTitle = "gui.restriction";
			icon = Items.name_tag.getIconFromDamage(0);
			if (!AdditionalPipes.remoteDisablePermissions) {
				addButton(0, 14, 52);
			}
		}

		@Override
		protected void drawLedger(int x, int y) {
			super.drawLedger(x, y);
			if (!AdditionalPipes.remoteDisablePermissions) {
				String owner = getPropOwner();
				drawSubheader(x + 8, y + 20, "gui.owner", ":");
				drawText(x + 18, y + 32, owner);

				drawSubheader(x + 8, y + 44, "gui.access", ":");
				int texture;
				AccessRule accessRule = getPropAccess();
				switch (accessRule) {
					case SHARED: texture = GuiIconProvider.SHARED; break;
					case RESTRICTED: texture = GuiIconProvider.RESTRICTED; break;
					case PRIVATE: texture = GuiIconProvider.PRIVATE; break;
					default: throw new IllegalStateException("AccessRule out of bounds");
				}
				drawIcon(0, GuiIconProvider.INSTANCE.getIcon(texture), x, y);
				drawText(x + 34, y + 56, null, accessRule.toString());
			}
		}

		@Override
		protected void buttonPressed(int id) {
			if (id == 0) {
				setAccessRule(AccessRule.values()[(getPropAccess().ordinal() + 1) % AccessRule.values().length]);
			}
		}

	}

	public GuiRestrictedTile(T container, IInventory inventory) {
		super(container, inventory);
	}

	@Override
	protected void initLedgers(IInventory inventory) {
		super.initLedgers(inventory);
		ledgerManager.add(new AccessLedger());
	}

	protected void setAccessRule(AccessRule newAccess) {
		clientProps.pushProperty(clientProps.propAccess.index, newAccess.ordinal());
	}

	protected String getPropOwner() {
		return clientProps.propOwner.value;
	}

	protected AccessRule getPropAccess() {
		return AccessRule.values()[clientProps.propAccess.value];
	}

}
