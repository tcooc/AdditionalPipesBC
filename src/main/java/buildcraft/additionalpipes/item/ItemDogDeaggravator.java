package buildcraft.additionalpipes.item;

import java.util.Iterator;
import java.util.List;

import javax.annotation.Nullable;

import buildcraft.additionalpipes.AdditionalPipes;
import buildcraft.additionalpipes.sound.APSounds;
import buildcraft.additionalpipes.utils.Log;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemDogDeaggravator extends Item
{	
	public static final String NAME = "dog_deaggravator";
	
	public ItemDogDeaggravator()
	{
		setRegistryName(NAME);
		setUnlocalizedName(NAME);
		setCreativeTab(AdditionalPipes.instance.creativeTab);
		setMaxStackSize(1);
	}
	
	@Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
    {
		//this code adapted from EntityAIHurtByTarget.startExecuting()
		double horizontalRange = 16;
        List<EntityWolf> list = world.getEntitiesWithinAABB(EntityWolf.class, new AxisAlignedBB(player.posX, player.posY + 1.0D, player.posZ,
        		player.posX, player.posY + 1.0D, player.posZ).grow(horizontalRange, 10.0D, horizontalRange));
        Iterator<EntityWolf> iterator = list.iterator();
        int wolfCounter = 0;

        while(iterator.hasNext())
        {
            EntityWolf wolf = iterator.next();

            if(wolf.isTamed() && wolf.isOnSameTeam(player))
            {
            	++wolfCounter;
                wolf.setAttackTarget(null);
            }
        }
        
        world.playSound(player, player.getPosition(), APSounds.dogDeaggravatorBell, SoundCategory.PLAYERS, 1, 1);
        Log.debug("Cleared attack target on " + wolfCounter + " wolves.");
        
        return ActionResult.<ItemStack>newResult(EnumActionResult.SUCCESS, hand == EnumHand.MAIN_HAND ? player.getHeldItemMainhand() : player.getHeldItemOffhand());

	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> list, ITooltipFlag flagIn)
	{
		list.add(I18n.format("tooltip.dog_deaggravator"));
	}

}