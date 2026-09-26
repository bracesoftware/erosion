package co.bracesoftware.erosion.world;

import java.util.List;
import java.util.function.Supplier;

import com.mojang.serialization.Codec;

import co.bracesoftware.erosion.Erosion;
import co.bracesoftware.erosion.network.server.ErosionNetworkSafeVariants.ErosionNetworkSafeBlockEntity;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityType.BlockEntitySupplier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
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
    public static class ErosionModContent<T> implements Supplier<T>
    {
        protected DeferredBlock<Block> blockHolder;
        protected DeferredItem<Item> itemHolder;
        protected Supplier<BlockEntityType<? extends ErosionNetworkSafeBlockEntity<?>>> blockEntityHolder;
        protected Supplier<SoundEvent> soundHolder;
        protected DeferredHolder<MenuType<?>, MenuType<? extends AbstractContainerMenu>> menuHolder;
        protected Object serializerHolder;
        protected DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> booleanDataComponentHolder;

        @Override public T get()
        {
            return null;
        }

        public static final class ErosionBooleanDataComponent extends ErosionModContent<DataComponentType<Boolean>>
        {
            public ErosionBooleanDataComponent(ErosionModContentResourceLocation loc)
            {
                this.booleanDataComponentHolder = EROSION_MOD_DATA_COMPONENTS.registerComponentType(
                    loc.getId(),
                    ComponentBuilder -> ComponentBuilder
                    .persistent(Codec.BOOL) 
                    .networkSynchronized(ByteBufCodecs.BOOL)
                );
            }

            @Override public DataComponentType<Boolean> get()
            {
                return this.booleanDataComponentHolder.get();
            }
        }

        public static final class ErosionBlock extends ErosionModContent<Block>
        {
            public ErosionBlock(ErosionModContentResourceLocation id, Supplier<? extends Block> s)
            {
                this.blockHolder = EROSION_MOD_BLOCKS.register(id.getId(), s);
            }

            @Override public Block get()
            {
                return this.blockHolder.get();
            }
        }
        public static final class ErosionItem extends ErosionModContent<Item>
        {
            public ErosionItem(ErosionModContentResourceLocation id, Supplier<? extends Item> s)
            {
                this.itemHolder = EROSION_MOD_ITEMS.register(id.getId(), s);
            }
            
            @Override public Item get()
            {
                return this.itemHolder.get();
            }
        }

        public static final class ErosionBlockEntity<T> extends ErosionModContent<BlockEntityType<?>>
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

            @Override public BlockEntityType<?> get()
            {
                return this.blockEntityHolder.get();
            }
        }
        public static final class ErosionSound extends ErosionModContent<SoundEvent>
        {
            public ErosionSound(
                ErosionModContentResourceLocation loc,
                Supplier<SoundEvent> s
            )
            {
                this.soundHolder = EROSION_MOD_SOUND_EVENTS.register(
                    loc.getId(), s
                );
            }

            @Override public SoundEvent get()
            {
                return this.soundHolder.get();
            }
        }

        public static final class ErosionMenu<G extends AbstractContainerMenu> extends ErosionModContent<MenuType<?>>
        {
            protected DeferredHolder<MenuType<?>, MenuType<G>> menuHolderSpecific;
            public ErosionMenu(
                ErosionModContentResourceLocation loc,
                Supplier<? extends MenuType<G>> s
            )
            {
                this.menuHolderSpecific = EROSION_MOD_MENUS.register(
                    loc.getId(), s
                );
                //super.menuHolder = this.menuHolderSpecific;
            }

            @Override public MenuType<G> get()
            {
                return this.menuHolderSpecific.get();
            }
        }

        public static final class ErosionSerializer<G extends CustomRecipe> extends ErosionModContent<SimpleCraftingRecipeSerializer<?>>
        {
            protected DeferredHolder<
                RecipeSerializer<?>, SimpleCraftingRecipeSerializer<G>
            > serializerHolderSpecific;
            public ErosionSerializer(
                ErosionModContentResourceLocation loc,
                Supplier<? extends SimpleCraftingRecipeSerializer<G>> s
            )
            {
                this.serializerHolderSpecific = EROSION_MOD_SERIALIZERS.register(
                    loc.getId(), s
                );
            }

            @Override public SimpleCraftingRecipeSerializer<G> get()
            {
                return this.serializerHolderSpecific.get();
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

        public final Supplier<SoundEvent> getSoundHolder()
        {
            return this.soundHolder;
        }

        public final DeferredHolder<MenuType<?>, MenuType<?>> getMenuHolder()
        {
            return this.menuHolder;
        }

        public final Object getSerializerHolder()
        {
            return this.serializerHolder;
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
        ErosionModContentManager.EROSION_MOD_ARMOR_MATERIALS.register(b);
        ErosionModContentManager.EROSION_MOD_BLOCKS.register(b);
        ErosionModContentManager.EROSION_MOD_ITEMS.register(b);
        ErosionModContentManager.EROSION_MOD_CREATIVE_MODE_TABS.register(b);
        ErosionModContentManager.EROSION_MOD_BLOCK_ENTITY_TYPES.register(b);
        ErosionModContentManager.EROSION_MOD_MENUS.register(b);
        ErosionModContentManager.EROSION_MOD_SOUND_EVENTS.register(b);
        ErosionModContentManager.EROSION_MOD_SERIALIZERS.register(b);
        ErosionModContentManager.EROSION_MOD_DATA_COMPONENTS.register(b);

        what.run();
    }
}