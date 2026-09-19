package co.bracesoftware.erosion.world.items;

import co.bracesoftware.erosion.world.ErosionRegistry;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;

public class ErosionSimpleItems
{
    public static class GasMask
    {
        public static Holder<ArmorMaterial> getGasMaskMaterial()
        {
            return ErosionRegistry.ArmorMaterials.GAS_MASK;
        }
        public static ArmorItem newGasMaskItem()
        {
            return new ArmorItem(
                ErosionRegistry.ArmorMaterials.GAS_MASK,
                ArmorItem.Type.HELMET,
                new Item.Properties()
            );
        }
    }
}