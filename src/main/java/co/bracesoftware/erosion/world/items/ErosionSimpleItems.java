package co.bracesoftware.erosion.world.items;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import co.bracesoftware.erosion.Erosion;
import co.bracesoftware.erosion.ErosionConfig;
import co.bracesoftware.erosion.ErosionUtils;
import co.bracesoftware.erosion.ErosionExceptions.ErosionItemExceptions.ErosionGasMaskInitException;
import co.bracesoftware.erosion.world.ErosionModContentManager;
import co.bracesoftware.erosion.world.ErosionModContentManager.ErosionModContent;
import co.bracesoftware.erosion.world.ErosionModContentManager.ErosionModContentResourceLocation;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

public class ErosionSimpleItems
{
    public static final class ErosionRockBlockItem extends BlockItem
    {
        public static final int STACKS_TO = 32;
        public ErosionRockBlockItem(Block id, Item.Properties p)
        {
            super(id, p);
        }
        public ErosionRockBlockItem(Block id)
        {
            super(id, new Item.Properties().stacksTo(STACKS_TO));
        }
    }
    public static final class GasMask extends ArmorItem
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
        private final Holder<ArmorMaterial> material;

        public GasMask(ErosionModContentResourceLocation id, Item.Properties p, Quality q)
        {
            super(createMaterial(id), ArmorItem.Type.HELMET, p);
            this.quality = q;
            this.material = createMaterial(id);
        }

        public Quality getQuality()
        {
            return this.quality;
        }

        public Holder<ArmorMaterial> getGasMaskMaterial()
        {
            return this.material;
        }

        ////////////////// STATICZ
        private static final Map<String, Holder<ArmorMaterial>> MATERIALS = new HashMap<>();

        private static Holder<ArmorMaterial> createMaterial(
            ErosionModContentResourceLocation id
        ) throws ErosionGasMaskInitException
        {
            if(MATERIALS.containsKey(id.getId()))
            {
                if(ErosionConfig.SUPER_SAFE_MODE)
                {
                    throw new ErosionGasMaskInitException("Already created such material -> " + id.getId());
                }
                return MATERIALS.get(id.getId());
            }
            var m = ErosionModContentManager.EROSION_MOD_ARMOR_MATERIALS.register(
                id.getId(),() -> new ArmorMaterial(
                    Map.of(
                        ArmorItem.Type.HELMET, 2,
                        ArmorItem.Type.CHESTPLATE, 0,
                        ArmorItem.Type.LEGGINGS, 0,
                        ArmorItem.Type.BOOTS, 0,
                        ArmorItem.Type.BODY, 0
                    ),
                    10, SoundEvents.ARMOR_EQUIP_LEATHER,
                    () -> Ingredient.EMPTY,
                    List.of(
                        new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(
                            Erosion.MODID, id.getId()
                        ))
                    ), 0f,0f
                )
            );
            MATERIALS.put(id.getId(), m);
            ErosionUtils.Log("Created armor material -> " + id.getId());
            return m;
        } 

        public static Item.Properties getGasMaskDefaultItemProperties()
        {
            return new Item.Properties()
            .fireResistant();
        }

        public static ErosionModContent.ErosionItem newGasMaskItem(ErosionModContentResourceLocation id, Quality q)
        {
            createMaterial(id);
            return new ErosionModContentManager.ErosionModContent.ErosionItem(
                id,
                () -> new GasMask(id, GasMask.getGasMaskDefaultItemProperties(),q)
            );
        }
    }
}