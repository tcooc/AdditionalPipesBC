/**
 * Additional Pipes is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */

package buildcraft.additionalpipes.utils;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;
import cpw.mods.fml.common.FMLCommonHandler;

public class WaseiUtils {

	public static int sum(short[] array) {
		int sum = 0;
		for (short a : array) {
			sum += a;
		}
		return sum;
	}

	public static int sum(int[] array) {
		int sum = 0;
		for (int a : array) {
			sum += a;
		}
		return sum;
	}

	public static double sum(double[] array) {
		double sum = 0;
		for (double a : array) {
			sum += a;
		}
		return sum;
	}

	public static float sum(float[] array) {
		float sum = 0;
		for (float a : array) {
			sum += a;
		}
		return sum;
	}

	public static int divideAndCeil(int dividend, int divisor) {
		return (dividend + divisor - 1) / divisor;
	}

	public static void checkIllegalClientAccess() {
		if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
			throw new IllegalStateException("Illegal access at client side.");
			//AdditionalPipes.logger.severe("Illegal access at client side.");
			//new Throwable().printStackTrace();
		}
	}

	public static boolean isValidMap(ItemStack stack, World world) {
		return stack != null && stack.getUnlocalizedName().equals(Items.map.getUnlocalizedName()) && world.loadItemData(MapData.class, "map_" + stack.getItemDamage()) instanceof MapData;
	}

	public static ItemStack createMapStack(int id, World world) {
		ItemStack result = new ItemStack(Items.map, 1, id);
		if (getMapData(result, world).scale > 0) {
			result.setTagCompound(new NBTTagCompound());
			result.stackTagCompound.setBoolean("map_is_scaling", true);
		}
		return result;
	}
	
	public static MapData getMapData(ItemStack map, World world)
	{
		if(isValidMap(map, world))
		{
			return (MapData) world.loadItemData(MapData.class, "map_" + map.getItemDamage());
		}
		else
		{
			Log.error("getMapData() called on invalid map!");
			return null;
		}
	}

}
