package co.bracesoftware.erosion.world.items;

import co.bracesoftware.erosion.world.ErosionRegistry;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;

public class ErosionSimpleItems
{
    public static class GasMask extends ArmorItem
    {
        public static enum Quality
        {
            LOW(20), MEDIUM(78), HIGH(100);

            private final int successRate;

            private Quality(int sr)
            {
                this.successRate = sr;
            }

            public int getSuccessRate()
            {
                return this.successRate;
            }
        }

        private final Quality quality;

        public GasMask(Item.Properties p, Quality q)
        {
            super(getGasMaskMaterial(), ArmorItem.Type.HELMET, p);
            this.quality = q;
        }


        public Quality getQuality()
        {
            return this.quality;
        }

        ////////////////// STATICZ

        public static Item.Properties getGasMaskDefaultItemProperties()
        {
            return new Item.Properties()
            .fireResistant();
        }

        public static Holder<ArmorMaterial> getGasMaskMaterial()
        {
            return ErosionRegistry.ArmorMaterials.GAS_MASK;
        }
        public static GasMask newGasMaskItem(Quality q)
        {
            return new GasMask(GasMask.getGasMaskDefaultItemProperties(),q);
        }
    }
}