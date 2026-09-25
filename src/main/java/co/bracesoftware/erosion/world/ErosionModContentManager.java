package co.bracesoftware.erosion.world;

import java.util.List;
import java.util.function.Supplier;

import co.bracesoftware.erosion.Erosion;
import co.bracesoftware.erosion.network.server.ErosionNetworkSafeVariants.ErosionNetworkSafeBlockEntity;
import co.bracesoftware.erosion.world.ErosionModContentManager.ErosionModContent;
import co.bracesoftware.erosion.world.ErosionModContentManager.ErosionModContentResourceLocation;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityType.BlockEntitySupplier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ErosionModContentManager
{
    public static final class ErosionModContentResourceLocation
    {
        private String id = null;
        private String name = null;

        public ErosionModContentResourceLocation(String id, String name)
        {
            this.id = id;
            this.name = name;
        }

        public String getId()
        {
            return this.id;
        }
        public String getName()
        {
            return this.name;
        }
    }
    public static final DeferredRegister<MenuType<?>> EROSION_MOD_MENUS = DeferredRegister.create(Registries.MENU, Erosion.MODID);
    public static final DeferredRegister.Blocks EROSION_MOD_BLOCKS = DeferredRegister.createBlocks(Erosion.MODID);
    public static final DeferredRegister.Items EROSION_MOD_ITEMS = DeferredRegister.createItems(Erosion.MODID);
    public static final DeferredRegister<BlockEntityType<?>> EROSION_MOD_BLOCK_ENTITY_TYPES = DeferredRegister.create(
        Registries.BLOCK_ENTITY_TYPE, Erosion.MODID
    );
    public static final DeferredRegister<CreativeModeTab> EROSION_MOD_CREATIVE_MODE_TABS = DeferredRegister.create(
        Registries.CREATIVE_MODE_TAB, Erosion.MODID
    );
    public static final DeferredRegister<ArmorMaterial> EROSION_MOD_ARMOR_MATERIALS = DeferredRegister.create(
        Registries.ARMOR_MATERIAL, Erosion.MODID
    );
    public static final DeferredRegister<SoundEvent> EROSION_MOD_SOUND_EVENTS = DeferredRegister.create(
        Registries.SOUND_EVENT, Erosion.MODID
    );
    public static final DeferredRegister.DataComponents EROSION_MOD_DATA_COMPONENTS = DeferredRegister.createDataComponents(
        Registries.DATA_COMPONENT_TYPE, Erosion.MODID
    );
    public static final DeferredRegister<RecipeSerializer<?>> EROSION_MOD_SERIALIZERS = DeferredRegister.create(
        Registries.RECIPE_SERIALIZER, Erosion.MODID
    );
    public static class ErosionModContent
    {
        protected DeferredBlock<Block> blockHolder;
        protected DeferredItem<Item> itemHolder;
        protected Supplier<BlockEntityType<? extends ErosionNetworkSafeBlockEntity<?>>> blockEntityHolder;

        public static final class ErosionBlock extends ErosionModContent
        {
            public ErosionBlock(ErosionModContentResourceLocation id, Supplier<? extends Block> s)
            {
                this.blockHolder = EROSION_MOD_BLOCKS.register(id.getId(), s);
            }
        }
        public static final class ErosionItem extends ErosionModContent
        {
            public ErosionItem(ErosionModContentResourceLocation id, Supplier<? extends Item> s)
            {
                this.itemHolder = EROSION_MOD_ITEMS.register(id.getId(), s);
            }
        }

        public static final class ErosionBlockEntity<T> extends ErosionModContent
        {
            public ErosionBlockEntity(
                ErosionModContentResourceLocation loc,
                BlockEntitySupplier<? extends ErosionNetworkSafeBlockEntity<T>> s,
                Block... b
            )
            {
                this.blockEntityHolder = EROSION_MOD_BLOCK_ENTITY_TYPES.register(
                    loc.getId(), () -> BlockEntityType.Builder.of(s, b).build(null)
                );
            }
        }

        public final DeferredItem<Item> getItemHolder()
        {
            return this.itemHolder;
        }

        public final DeferredBlock<Block> getBlockHolder()
        {
            return this.blockHolder;
        }

        public final Supplier<BlockEntityType<? extends ErosionNetworkSafeBlockEntity<?>>> getBlockEntityHolder()
        {
            return this.blockEntityHolder;
        }
    }

    public static final void loadClasses(List<Class<?>> l)
    {
        for(var c : l)
        {
            c.getName();
        }
    }

    public static final void loadClasses(Class<?>... l)
    {
        loadClasses(List.of(l));
    }

    public static final void registerContent(IEventBus b, Runnable what)
    {
        ErosionModContentManager.EROSION_MOD_BLOCKS.register(b);
        ErosionModContentManager.EROSION_MOD_ITEMS.register(b);
        ErosionModContentManager.EROSION_MOD_BLOCK_ENTITY_TYPES.register(b);

        what.run();
    }
}