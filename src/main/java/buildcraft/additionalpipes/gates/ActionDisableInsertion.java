/**
 * Additional Pipes is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */

package buildcraft.additionalpipes.triggers;

import additionalpipes.client.texture.ActionTriggerIconProvider;

public class ActionDisableInsertion extends APAction {

	public ActionDisableInsertion(int legacyId) {
		super(legacyId, "additionalpipes.pipe.insertion");
	}

	@Override
	public int getIconIndex() {
		return ActionTriggerIconProvider.Action_DisableInsertion;
	}

	@Override
	public String getDescription() {
		return "Disable Insertion";
	}

}
