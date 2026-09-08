package co.bracesoftware.erosion;

import co.bracesoftware.erosion.blocks.ErosionRegistry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Hash;
import co.bracesoftware.erosion.ErosionCore.AlterableMaterial;
import co.bracesoftware.erosion.ErosionCore.RefinableMaterial;

import java.util.ArrayList;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.level.ChunkEvent.Unload;
import net.minecraft.core.BlockPos;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.minecraft.server.level.ServerLevel;

import it.unimi.dsi.fastutil.longs.*;

@EventBusSubscriber(modid = Erosion.MODID, value = Dist.CLIENT)
public class ErosionCore
{
    public static String CACHED_STATUS_STRING = "";

    private static AlterationPacket gpos = null;

    private static class AlterationPacket
    {
        public BlockPos pos = null;
        public Block block = null;

        public AlterationPacket(BlockPos a, Block b)
        {
            this.pos = a;
            this.block = b;
        }
    }

    public static class AlterationPacketList
    {
        private static class BlockConverter
        {
            public static long blockToLong(Block block)
            {
                if (block == null) return 0L;
                return BuiltInRegistries.BLOCK.getId(block);
            }

            public static Block longToBlock(long id)
            {
                return BuiltInRegistries.BLOCK.byId((int) id);
            }
        }

        private static final long FIXED_OVERHEAD_BYTES = 160L;
        private final LongArrayList pos;
        private final LongArrayList block;
        private final LongOpenHashSet posHash;

        public AlterationPacketList(int capacity)
        {
            this.posHash = new LongOpenHashSet(capacity);
            this.pos = new LongArrayList(capacity);
            this.block = new LongArrayList(capacity);
        }

        public void add(AlterationPacket p)
        {
            this.pos.add(p.pos.asLong());
            this.posHash.add(p.pos.asLong());
            this.block.add(BlockConverter.blockToLong(p.block));
        }

        public void add(BlockPos p, Block b)
        {
            this.pos.add(p.asLong());
            this.posHash.add(p.asLong());
            this.block.add(BlockConverter.blockToLong(b));
        }

        public AlterationPacket get(int idx)
        {
            return new AlterationPacket(BlockPos.of(this.pos.getLong(idx)), BlockConverter.longToBlock(this.block.getLong(idx)));
        }

        public AlterationPacket getAndRemove(int idx)
        {
            this.posHash.remove(this.pos.getLong(idx));
            return new AlterationPacket(BlockPos.of(this.pos.removeLong(idx)), BlockConverter.longToBlock(this.block.removeLong(idx)));
        }

        public void remove(int idx)
        {
            this.posHash.remove(this.pos.getLong(idx));
            this.block.removeLong(idx);
            this.pos.removeLong(idx);
        }

        public boolean isEmpty()
        {
            return this.pos.isEmpty();
        }

        public int size()
        {
            return this.pos.size();
        }

        public void clear()
        {
            this.posHash.clear();
            this.pos.clear();
            this.block.clear();
        }
        public void removeUnordered(int idx)
        {
            this.posHash.remove(this.pos.getLong(idx));

            int lastIdx = size() - 1;
            if(idx != lastIdx)
            {
                this.pos.set(idx, this.pos.getLong(lastIdx));
                this.block.set(idx, this.block.getLong(lastIdx));
            }

            this.pos.removeLong(lastIdx);
            this.block.removeLong(lastIdx);
        }

        public boolean contains(long key)
        {
            return this.posHash.contains(key);
        }

        public long getUsedMemory()
        {
            long activeElements = this.size();
            long activeHashElements = this.posHash.size();

            return FIXED_OVERHEAD_BYTES + (activeElements * 8L) + (activeElements * 8L) + (activeHashElements * 8L);
        }
        public long getMaxAllocatedMemory()
        {
            long posCapacity = this.pos.elements().length;
            long blockCapacity = this.block.elements().length;

            int hashCount = this.posHash.size();
            long hashCapacity = hashCount > 0
            ? HashCommon.arraySize(hashCount, Hash.DEFAULT_LOAD_FACTOR)
            : 16L;

            return FIXED_OVERHEAD_BYTES + (posCapacity * 8L) + (blockCapacity * 8L) + (hashCapacity * 8L);
        }
    }

    public static final AlterationPacketList PENDING = new AlterationPacketList(ErosionConfig.MAX_PENDING_SIZE);
    public static final AlterationPacketList PENDING_FAST = new AlterationPacketList(ErosionConfig.MAX_PENDING_FAST_SIZE);

    public static final Map<Item, List<Component>> ITEM_DESCRIPTIONS = new HashMap<>();

    private static Map<Block, AlterableMaterial> ALTERATION_INVERTED = new HashMap<>();

    private static Integer PERFORMED = 0;
    private static Integer PERFORMED_FAST = 0;

    public static class BlockEntityRecipeRegistries
    {
        public static final Integer MATERIAL_PURIFIER = 1;
        public static final Integer CRUCIBLE = 2;
    }

    public static class ErosionDynamicItem
    {
        public String name;
        private static List<String> DO_NOT_USE = null;
        public List<String> antiDuplicator;

        public void setup()
        {
            ErosionUtils.Log("Setting up simple dynamic item: " + this.name);
            return;
        }

        public void discard()
        {
            ErosionUtils.Log("Discarding simple dynamic item: " + this.name);
            return;
        }

        public void discardDuplicationPreventionSys(List<String> l)
        {
            String resource = this.getClass().getSimpleName() + "::Erosion.class(\"" + this.name + "\")";
            if(!l.contains(this.name))
            {
                throw new RuntimeException("Object was never set up, cannot be discarded -> " + resource);
            }
            l.remove(this.name);
            ErosionUtils.Log("Discarding data of: " + resource);
            return;
        }

        public void preventDuplication(List<String> l)
        {
            String resource = this.getClass().getSimpleName() + "::Erosion.class(\"" + this.name + "\")";
            if(l.contains(this.name))
            {
                throw new RuntimeException("Duplicate object -> " + resource);
            }
            l.add(this.name);
            ErosionUtils.Log("Preventing duplication of: " + resource);
            return;
        }
    }

    public static class AlterationRules
    {
        @FunctionalInterface 
        private interface CheckAlterationRule
        {
            boolean check(ServerLevel l, BlockPos p);
        }

        private static class AlterationRule
        {
            private String name;
            private final CheckAlterationRule RuleCheck;

            public AlterationRule(String n, CheckAlterationRule c)
            {
                this.name = n;
                this.RuleCheck = c;
            }

            public boolean check(ServerLevel l, BlockPos p)
            {
                return RuleCheck.check(l, p);
            }

            public String getName()
            {
                return this.name;
            }
        }
        public static final AlterationRule CONTACT_WITH_WATER = new AlterationRule(
            "Contact with water or steam",
            ErosionCore::hasWaterNearby
        );
        public static final AlterationRule CONTACT_WITH_LAVA = new AlterationRule(
            "Contact with lava",
            ErosionCore::hasLavaNearby
        );
        
        // =========================================== //
        private List<AlterationRule> rules;

        public AlterationRules(List<AlterationRule> r)
        {
            this.rules = r;
        }

        public List<AlterationRule> getRules()
        {
            return this.rules;
        }
    }

    public static class CrucibleCatalyst extends ErosionDynamicItem
    {
        public Supplier<Item> catalyst;
        public Item catalystItem;

        public CrucibleCatalyst(String n, Supplier<Item> c)
        {
            this.name = n;
            this.catalyst = c;

            this.antiDuplicator = new ArrayList<>();
        }

        @Override 
        public void setup()
        {
            ErosionUtils.Log("Setting up crucible catalyst: " + this.name);
            this.preventDuplication(antiDuplicator);
            

            this.catalystItem = this.catalyst.get();
        }

        @Override 
        public void discard()
        {
            ErosionUtils.Log("Discarding crucible catalyst: " + this.name);
            this.discardDuplicationPreventionSys(antiDuplicator);
            return;
        }

        public static Boolean isItemCrucibleCatalyst(Item item)
        {
            for(int i = 0; i < CRUCIBLE_CATALYST_LIST.size(); ++i)
            {
                CrucibleCatalyst c = CRUCIBLE_CATALYST_LIST.get(i);
                if(c.catalystItem == item)
                {
                    return true;
                }
            }
            return false;
        }
    }

    public static class RefinableMaterial extends ErosionDynamicItem
    {
        public Supplier<Item> material;
        public Supplier<List<Item>> product;
        public Integer recipeCategory = null;

        public Item materialItem = null;
        public List<Item> productItem = null;

        public CrucibleCatalyst catalyst = null;

        public RefinableMaterial(String n, Supplier<Item> m, Supplier<List<Item>> p, Integer i)
        {
            this.name = n;
            this.material = m;
            this.product = p;
            this.recipeCategory = i;

            this.antiDuplicator = new ArrayList<>();
        }

        public RefinableMaterial(String n, Supplier<Item> m, Supplier<List<Item>> p, Integer i, CrucibleCatalyst c)
        {
            this.name = n;
            this.material = m;
            this.product = p;
            this.recipeCategory = i;
            this.catalyst = c;

            this.antiDuplicator = new ArrayList<>();
        }

        @Override 
        public void setup()
        {
            ErosionUtils.Log("Setting up refinable material item: " + this.name);
            this.preventDuplication(antiDuplicator);
            
            this.materialItem = this.material.get();
            this.productItem = this.product.get();
            
            if(
                this.recipeCategory < BlockEntityRecipeRegistries.MATERIAL_PURIFIER ||
                this.recipeCategory > BlockEntityRecipeRegistries.CRUCIBLE
            )
            {
                throw new RuntimeException("Invalid recipe category -> " + this.name);
            }
            
            if(this.recipeCategory == BlockEntityRecipeRegistries.MATERIAL_PURIFIER)
            {
                BlockEntityRecipes.MaterialPurifier.RECIPES.putIfAbsent(materialItem, productItem);
            }
            else if(this.recipeCategory == BlockEntityRecipeRegistries.CRUCIBLE)
            {
                BlockEntityRecipes.Crucible.RECIPES.putIfAbsent(materialItem, productItem);
                if(this.catalyst == null)
                {
                    throw new RuntimeException("Missing a catalyst for recipe: " + this.name);
                }
                else BlockEntityRecipes.Crucible.CATALYSTS.putIfAbsent(materialItem, this.catalyst);
            }
            return;
        }

        @Override 
        public void discard()
        {
            ErosionUtils.Log("Unloading refinable material item: " + this.name);
            BlockEntityRecipes.MaterialPurifier.RECIPES.clear();
            BlockEntityRecipes.Crucible.RECIPES.clear();
            BlockEntityRecipes.Crucible.CATALYSTS.clear();
            this.discardDuplicationPreventionSys(antiDuplicator);
            return;
        }
    }

    public static class AlterableMaterial extends ErosionDynamicItem
    {
        public Supplier<Block> materialSupplier;
        public Supplier<Item> materialItemSupplier;

        public Block material = null;
        public Item materialItem = null;//for tooltips

        public static class AlterationPath
        {
            public String name;
            public Supplier<List<Block>> productSupplier;
            public Supplier<List<Item>> productItemSupplier;

            public AlterationRules rules = null;
            public List<Block> product = null;
            public List<Item> productItem = null; //for tooltips

            public AlterationPath(
                String n,
                Supplier<List<Block>> a,
                Supplier<List<Item>> b,
                AlterationRules c
            )
            {
                this.name = n;
                this.rules = c;
                this.productSupplier = a;
                this.productItemSupplier = b;
            }

            public void setup()
            {
                ErosionUtils.Log("Setting up alteration path: " + this.name);
                this.product = this.productSupplier.get();
                this.productItem = this.productItemSupplier.get();

                if(this.product.size() != this.productItem.size())
                {
                    throw new RuntimeException("this.product.size() != this.productItem.size() -> path::(\"" + this.name + "\")");
                }

                if(this.rules == null)
                {
                    throw new RuntimeException("Alteration rules are null for path -> " + this.name);
                }
                return;
            }
        }

        public List<AlterationPath> paths = null;
        
        public AlterableMaterial(
            String n, Supplier<Block> a,
            Supplier<Item> b,
            List<AlterationPath> c
        )
        {
            this.name = n;
           
            this.materialSupplier = a;
            this.materialItemSupplier = b;
            this.paths = c;

            this.antiDuplicator = new ArrayList<>();
        }

        @Override 
        public void setup()
        {
            ErosionUtils.Log("Setting up erodable material: " + this.name);
            this.preventDuplication(antiDuplicator);

            this.material = this.materialSupplier.get();
            this.materialItem = this.materialItemSupplier.get();

            for(int i = 0; i < this.paths.size(); ++i)
            {
                this.paths.get(i).setup();
            }

            ALTERATION_INVERTED.putIfAbsent(this.material, this);
            return;
        }

        @Override 
        public void discard()
        {
            ErosionUtils.Log("Discarding erodable material: " + this.name);
            
            ALTERATION_INVERTED.clear();
            this.discardDuplicationPreventionSys(antiDuplicator);
            return;
        }
    }

    // ======================= ERODABLE MATERIALS

    public static final AlterableMaterial GRASS_BLOCK = new AlterableMaterial(
        Blocks.GRASS_BLOCK.getName().getString(),
        () -> Blocks.GRASS_BLOCK, () -> Items.GRASS_BLOCK,
        List.of(
            new AlterableMaterial.AlterationPath(
                ErosionRegistry.DefaultAlterationPaths.ALTERATION_BY_WATER,
                () -> List.of(
                    Blocks.MUD,
                    ErosionRegistry.Blocks.NATIVE_GOLD_DEPOSIT.get(),
                    ErosionRegistry.Blocks.CASSITERITE_DEPOSIT.get(),
                    ErosionRegistry.Blocks.NATIVE_SILVER_DEPOSIT.get(),
                    ErosionRegistry.Blocks.MINERAL_RICH_DIRT.get()
                ),
                () -> List.of(
                    Items.MUD,
                    ErosionRegistry.Items.NATIVE_GOLD.get(),
                    ErosionRegistry.Items.RAW_CASSITERITE.get(),
                    ErosionRegistry.Items.NATIVE_SILVER.get(),
                    ErosionRegistry.Items.MINERAL_RICH_DIRT.get()
                ),
                new AlterationRules(List.of(
                    AlterationRules.CONTACT_WITH_WATER
                ))
            ), new AlterableMaterial.AlterationPath(
                ErosionRegistry.DefaultAlterationPaths.ALTERATION_BY_LAVA,
                () -> List.of(
                    ErosionRegistry.Blocks.DRIED_DIRT.get(),
                    Blocks.COARSE_DIRT
                ),
                () -> List.of(
                    ErosionRegistry.Items.DRIED_DIRT.get(),
                    Items.COARSE_DIRT
                ),
                new AlterationRules(List.of(
                    AlterationRules.CONTACT_WITH_LAVA
                ))
            )
        )
    );
    public static final AlterableMaterial MUD = new AlterableMaterial(
        Blocks.MUD.getName().getString(),
        () -> Blocks.MUD, () -> Items.MUD,
        List.of(
            new AlterableMaterial.AlterationPath(
                ErosionRegistry.DefaultAlterationPaths.ALTERATION_BY_WATER,
                () -> List.of(
                    Blocks.MUD,
                    ErosionRegistry.Blocks.NATIVE_GOLD_DEPOSIT.get(),
                    ErosionRegistry.Blocks.CASSITERITE_DEPOSIT.get(),
                    ErosionRegistry.Blocks.NATIVE_SILVER_DEPOSIT.get(),
                    ErosionRegistry.Blocks.MINERAL_RICH_DIRT.get()
                ),
                () -> List.of(
                    Items.MUD,
                    ErosionRegistry.Items.NATIVE_SILVER.get(),
                    ErosionRegistry.Items.RAW_CASSITERITE.get(),
                    ErosionRegistry.Items.NATIVE_GOLD.get(),
                    ErosionRegistry.Items.MINERAL_RICH_DIRT.get()
                ),
                new AlterationRules(List.of(
                    AlterationRules.CONTACT_WITH_WATER
                ))
            ), new AlterableMaterial.AlterationPath(
                ErosionRegistry.DefaultAlterationPaths.ALTERATION_BY_LAVA,
                () -> List.of(
                    ErosionRegistry.Blocks.DRIED_DIRT.get()
                ),
                () -> List.of(
                    ErosionRegistry.Items.DRIED_DIRT.get()
                ),
                new AlterationRules(List.of(
                    AlterationRules.CONTACT_WITH_LAVA
                ))
            )
        )
    );
    public static final AlterableMaterial DIRT = new AlterableMaterial(
        Blocks.DIRT.getName().getString(),
        () -> Blocks.DIRT, () -> Items.DIRT,
        List.of(
            new AlterableMaterial.AlterationPath(
                ErosionRegistry.DefaultAlterationPaths.ALTERATION_BY_WATER,
                () -> List.of(
                    Blocks.MUD,
                    ErosionRegistry.Blocks.NATIVE_GOLD_DEPOSIT.get(),
                    ErosionRegistry.Blocks.CASSITERITE_DEPOSIT.get(),
                    ErosionRegistry.Blocks.NATIVE_SILVER_DEPOSIT.get(),
                    ErosionRegistry.Blocks.MINERAL_RICH_DIRT.get()
                ),
                () -> List.of(
                    Items.MUD,
                    ErosionRegistry.Items.NATIVE_GOLD.get(),
                    ErosionRegistry.Items.RAW_CASSITERITE.get(),
                    ErosionRegistry.Items.NATIVE_SILVER.get(),
                    ErosionRegistry.Items.MINERAL_RICH_DIRT.get()
                ),
                new AlterationRules(List.of(
                    AlterationRules.CONTACT_WITH_WATER
                ))
            ), new AlterableMaterial.AlterationPath(
                ErosionRegistry.DefaultAlterationPaths.ALTERATION_BY_LAVA,
                () -> List.of(
                    ErosionRegistry.Blocks.DRIED_DIRT.get(),
                    Blocks.COARSE_DIRT
                ),
                () -> List.of(
                    ErosionRegistry.Items.DRIED_DIRT.get(),
                    Items.COARSE_DIRT
                ),
                new AlterationRules(List.of(
                    AlterationRules.CONTACT_WITH_LAVA
                ))
            )
        )
    );
    public static final AlterableMaterial GRAVEL = new AlterableMaterial(
        Blocks.GRAVEL.getName().getString(),
        () -> Blocks.GRAVEL, () -> Items.GRAVEL,
        List.of(
            new AlterableMaterial.AlterationPath(
                ErosionRegistry.DefaultAlterationPaths.ALTERATION_BY_WATER,
                () -> List.of(
                    ErosionRegistry.Blocks.QUARTZ_GRAVEL.get(),
                    ErosionRegistry.Blocks.CASSITERITE_DEPOSIT.get(),
                    ErosionRegistry.Blocks.NATIVE_SILVER_DEPOSIT.get(),
                    ErosionRegistry.Blocks.MINERAL_RICH_DIRT.get()
                ),
                () -> List.of(
                    ErosionRegistry.Items.QUARTZ_GRAVEL.get(),
                    ErosionRegistry.Items.RAW_CASSITERITE.get(),
                    ErosionRegistry.Items.NATIVE_SILVER.get(),
                    ErosionRegistry.Items.MINERAL_RICH_DIRT.get()
                ),
                new AlterationRules(List.of(
                    AlterationRules.CONTACT_WITH_WATER
                ))
            )
        )
    );

    public static final AlterableMaterial SAND = new AlterableMaterial(
        Blocks.SAND.getName().getString(),
        () -> Blocks.SAND, () -> Items.SAND,
        List.of(
            new AlterableMaterial.AlterationPath(
                ErosionRegistry.DefaultAlterationPaths.ALTERATION_BY_WATER,
                () -> List.of(
                    ErosionRegistry.Blocks.QUARTZ_GRAVEL.get(),
                    ErosionRegistry.Blocks.CASSITERITE_DEPOSIT.get(),
                    Blocks.GRAVEL,
                    ErosionRegistry.Blocks.NATIVE_SILVER_DEPOSIT.get()
                ),
                () -> List.of(
                    ErosionRegistry.Items.QUARTZ_GRAVEL.get(),
                    ErosionRegistry.Items.RAW_CASSITERITE.get(),
                    Items.GRAVEL,
                    ErosionRegistry.Items.NATIVE_SILVER.get()
                ),
                new AlterationRules(List.of(
                    AlterationRules.CONTACT_WITH_WATER
                ))
            )
        )
    );

    public static final AlterableMaterial COARSE_DIRT = new AlterableMaterial(
        Blocks.COARSE_DIRT.getName().getString(),
        () -> Blocks.COARSE_DIRT, () -> Items.COARSE_DIRT,
        List.of(
            new AlterableMaterial.AlterationPath(
                ErosionRegistry.DefaultAlterationPaths.ALTERATION_BY_WATER,
                () -> List.of(
                    Blocks.MUD,
                    ErosionRegistry.Blocks.NATIVE_GOLD_DEPOSIT.get(),
                    ErosionRegistry.Blocks.CASSITERITE_DEPOSIT.get(),
                    ErosionRegistry.Blocks.NATIVE_SILVER_DEPOSIT.get()
                ),
                () -> List.of(
                    Items.MUD,
                    ErosionRegistry.Items.NATIVE_GOLD.get(),
                    ErosionRegistry.Items.RAW_CASSITERITE.get(),
                    ErosionRegistry.Items.NATIVE_SILVER.get()
                ),
                new AlterationRules(List.of(
                    AlterationRules.CONTACT_WITH_WATER
                ))
            ), new AlterableMaterial.AlterationPath(
                ErosionRegistry.DefaultAlterationPaths.ALTERATION_BY_LAVA,
                () -> List.of(
                    ErosionRegistry.Blocks.DRIED_DIRT.get()
                ),
                () -> List.of(
                    ErosionRegistry.Items.DRIED_DIRT.get()
                ),
                new AlterationRules(List.of(
                    AlterationRules.CONTACT_WITH_LAVA
                ))
            )
        )
    );

    public static final AlterableMaterial STONE = new AlterableMaterial(
        Blocks.STONE.getName().getString(),
        () -> Blocks.STONE, () -> Items.STONE,
        List.of(
            new AlterableMaterial.AlterationPath(
                ErosionRegistry.DefaultAlterationPaths.ALTERATION_BY_WATER,
                () -> List.of(
                    Blocks.COBBLESTONE, Blocks.GRAVEL, Blocks.CALCITE,
                    ErosionRegistry.Blocks.LIMONITE_ORE.get(),
                    ErosionRegistry.Blocks.HEMATITE_ORE.get(),
                    ErosionRegistry.Blocks.MAGNETITE_ORE.get(),
                    ErosionRegistry.Blocks.NATIVE_SILVER_DEPOSIT.get(),
                    ErosionRegistry.Blocks.BISMUTHINITE_ORE.get(),
                    ErosionRegistry.Blocks.SPHALERITE_ORE.get()
                ),
                () -> List.of(
                    Items.COBBLESTONE, Items.GRAVEL, Items.CALCITE,
                    ErosionRegistry.Items.RAW_LIMONITE.get(),
                    ErosionRegistry.Items.RAW_HEMATITE.get(),
                    ErosionRegistry.Items.RAW_MAGNETITE.get(),
                    ErosionRegistry.Items.NATIVE_SILVER.get(),
                    ErosionRegistry.Items.RAW_BISMUTHINITE.get(),
                    ErosionRegistry.Items.RAW_SPHALERITE.get()
                ),
                new AlterationRules(List.of(
                    AlterationRules.CONTACT_WITH_WATER
                ))
            )
        )
    );

    public static final AlterableMaterial DEEPSLATE = new AlterableMaterial(
        Blocks.DEEPSLATE.getName().getString(),
        () -> Blocks.DEEPSLATE, () -> Items.DEEPSLATE,
        List.of(
            new AlterableMaterial.AlterationPath(
                ErosionRegistry.DefaultAlterationPaths.ALTERATION_BY_WATER,
                () -> List.of(
                    Blocks.COBBLED_DEEPSLATE,
                    ErosionRegistry.Blocks.LIMONITE_ORE.get(),
                    ErosionRegistry.Blocks.BISMUTHINITE_ORE.get(),
                    ErosionRegistry.Blocks.SPHALERITE_ORE.get()
                ),
                () -> List.of(
                    Items.COBBLED_DEEPSLATE,
                    ErosionRegistry.Items.RAW_LIMONITE.get(),
                    ErosionRegistry.Items.RAW_BISMUTHINITE.get(),
                    ErosionRegistry.Items.RAW_SPHALERITE.get()
                ),
                new AlterationRules(List.of(
                    AlterationRules.CONTACT_WITH_WATER
                ))
            )
        )
    );

    public static final AlterableMaterial GRANITE = new AlterableMaterial(
        Blocks.GRANITE.getName().getString(),
        () -> Blocks.GRANITE, () -> Items.GRANITE,
        List.of(
            new AlterableMaterial.AlterationPath(
                ErosionRegistry.DefaultAlterationPaths.ALTERATION_BY_WATER,
                () -> List.of(
                    ErosionRegistry.Blocks.KAOLINIZED_GRANITE.get(),
                    ErosionRegistry.Blocks.ALBITIZED_GRANITE.get(),
                    ErosionRegistry.Blocks.QUARTZ_GRAVEL.get(),
                    ErosionRegistry.Blocks.HEMATITE_ORE.get()
                ),
                () -> List.of(
                    ErosionRegistry.Items.KAOLINIZED_GRANITE.get(),
                    ErosionRegistry.Items.ALBITIZED_GRANITE.get(),
                    ErosionRegistry.Items.QUARTZ_GRAVEL.get(),
                    ErosionRegistry.Items.RAW_HEMATITE.get()
                ),
                new AlterationRules(List.of(
                    AlterationRules.CONTACT_WITH_WATER
                ))
            )
        )
    );

    public static final AlterableMaterial DIORITE = new AlterableMaterial(
        Blocks.DIORITE.getName().getString(),
        () -> Blocks.DIORITE, () -> Items.DIORITE,
        List.of(
            new AlterableMaterial.AlterationPath(
                ErosionRegistry.DefaultAlterationPaths.ALTERATION_BY_WATER,
                () -> List.of(
                    ErosionRegistry.Blocks.PROPYLITIZED_DIORITE.get(),
                    ErosionRegistry.Blocks.MAGNETITE_ORE.get(),
                    ErosionRegistry.Blocks.BISMUTHINITE_ORE.get(),
                    ErosionRegistry.Blocks.SPHALERITE_ORE.get()
                ),
                () -> List.of(
                    ErosionRegistry.Items.PROPYLITIZED_DIORITE.get(),
                    ErosionRegistry.Items.RAW_MAGNETITE.get(),
                    ErosionRegistry.Items.RAW_BISMUTHINITE.get(),
                    ErosionRegistry.Items.RAW_SPHALERITE.get()
                ),
                new AlterationRules(List.of(
                    AlterationRules.CONTACT_WITH_WATER
                ))
            )
        )
    );

    public static final AlterableMaterial TUFF = new AlterableMaterial(
        Blocks.TUFF.getName().getString(),
        () -> Blocks.TUFF, () -> Items.TUFF,
        List.of(
            new AlterableMaterial.AlterationPath(
                ErosionRegistry.DefaultAlterationPaths.ALTERATION_BY_WATER,
                () -> List.of(
                    ErosionRegistry.Blocks.LIMONITE_ORE.get(),
                    ErosionRegistry.Blocks.SPHALERITE_ORE.get()
                ),
                () -> List.of(
                    ErosionRegistry.Items.RAW_LIMONITE.get(),
                    ErosionRegistry.Items.RAW_SPHALERITE.get()
                ),
                new AlterationRules(List.of(
                    AlterationRules.CONTACT_WITH_WATER
                ))
            )
        )
    );

    public static final AlterableMaterial CALCITE = new AlterableMaterial(
        Blocks.CALCITE.getName().getString(),
        () -> Blocks.CALCITE, () -> Items.CALCITE,
        List.of(
            new AlterableMaterial.AlterationPath(
                ErosionRegistry.DefaultAlterationPaths.ALTERATION_BY_WATER,
                () -> List.of(
                    ErosionRegistry.Blocks.CALCITE_MALACHITE_ORE.get(),
                    ErosionRegistry.Blocks.CRACKED_CALCITE.get()
                ),
                () -> List.of(
                    ErosionRegistry.Items.RAW_MALACHITE.get(),
                    ErosionRegistry.Items.CRACKED_CALCITE.get()
                ),
                new AlterationRules(List.of(
                    AlterationRules.CONTACT_WITH_WATER
                ))
            )
        )
    );

    public static final List<AlterableMaterial> ALTERABLE_MATERIALS_LIST = List.of(
        GRASS_BLOCK, DIRT, SAND, COARSE_DIRT,
        STONE, DEEPSLATE, GRANITE, DIORITE,
        TUFF, CALCITE, GRAVEL, MUD
    );

    // ============================== CRUCIBLE CATALYSTS

    public static final CrucibleCatalyst FLUX = new CrucibleCatalyst(
        ErosionRegistry.RawRegistry.FLUX.getName(),
        () -> ErosionRegistry.Items.FLUX.get()
    );

    public static final List<CrucibleCatalyst> CRUCIBLE_CATALYST_LIST = List.of(
        FLUX
    );

    // ========================== REFINABLE MATERIALS

    public static final RefinableMaterial KAOLINIZED_GRANITE = new RefinableMaterial(
        ErosionRegistry.RawRegistry.KAOLINIZED_GRANITE.getName(),
        () -> ErosionRegistry.Items.KAOLINIZED_GRANITE.get(),
        () -> List.of(Items.CLAY),
        BlockEntityRecipeRegistries.MATERIAL_PURIFIER
    );

    public static final RefinableMaterial QUARTZ_GRAVEL = new RefinableMaterial(
        ErosionRegistry.RawRegistry.QUARTZ_GRAVEL.getName(),
        () -> ErosionRegistry.Items.QUARTZ_GRAVEL.get(),
        () -> List.of(Items.QUARTZ),
        BlockEntityRecipeRegistries.MATERIAL_PURIFIER
    );

    public static final RefinableMaterial ALBITIZED_GRANITE = new RefinableMaterial(
        ErosionRegistry.RawRegistry.ALBITIZED_GRANITE.getName(),
        () -> ErosionRegistry.Items.ALBITIZED_GRANITE.get(),
        () -> List.of(ErosionRegistry.Items.FELDSPAR_POWDER.get()),
        BlockEntityRecipeRegistries.MATERIAL_PURIFIER
    );

    public static final RefinableMaterial PROPYLITIZED_DIORITE = new RefinableMaterial(
        ErosionRegistry.RawRegistry.PROPYLITIZED_DIORITE.getName(),
        () -> ErosionRegistry.Items.PROPYLITIZED_DIORITE.get(),
        () -> List.of(
            Items.CLAY_BALL,
            ErosionRegistry.Items.RAW_MAGNETITE.get(),
            ErosionRegistry.Items.RAW_MALACHITE.get(),
            ErosionRegistry.Items.CRACKED_CALCITE.get()
        ), BlockEntityRecipeRegistries.MATERIAL_PURIFIER
    );

    public static final RefinableMaterial RAW_LIMONITE = new RefinableMaterial(
        ErosionRegistry.RawRegistry.RAW_LIMONITE.getName(),
        () -> ErosionRegistry.Items.RAW_LIMONITE.get(),
        () -> List.of(
            Items.IRON_NUGGET
        ), BlockEntityRecipeRegistries.CRUCIBLE, FLUX
    );
    public static final RefinableMaterial RAW_MAGNETITE = new RefinableMaterial(
        ErosionRegistry.RawRegistry.RAW_MAGNETITE.getName(),
        () -> ErosionRegistry.Items.RAW_MAGNETITE.get(),
        () -> List.of(
            Items.IRON_NUGGET
        ), BlockEntityRecipeRegistries.CRUCIBLE, FLUX
    );
    public static final RefinableMaterial RAW_HEMATITE = new RefinableMaterial(
        ErosionRegistry.RawRegistry.RAW_HEMATITE.getName(),
        () -> ErosionRegistry.Items.RAW_HEMATITE.get(),
        () -> List.of(
            Items.IRON_NUGGET
        ), BlockEntityRecipeRegistries.CRUCIBLE, FLUX
    );
    public static final RefinableMaterial RAW_MALACHITE = new RefinableMaterial(
        ErosionRegistry.RawRegistry.RAW_MALACHITE.getName(),
        () -> ErosionRegistry.Items.RAW_MALACHITE.get(),
        () -> List.of(
            Items.RAW_COPPER
        ), BlockEntityRecipeRegistries.CRUCIBLE, FLUX
    );

    public static final RefinableMaterial NATIVE_GOLD = new RefinableMaterial(
        ErosionRegistry.RawRegistry.NATIVE_GOLD.getName(),
        () -> ErosionRegistry.Items.NATIVE_GOLD.get(),
        () -> List.of(
            Items.GOLD_NUGGET
        ), BlockEntityRecipeRegistries.CRUCIBLE, FLUX
    );

    public static final RefinableMaterial NATIVE_GOLD_DEPOSIT = new RefinableMaterial(
        ErosionRegistry.RawRegistry.NATIVE_GOLD_DEPOSIT.getName(),
        () -> ErosionRegistry.Items.NATIVE_GOLD_DEPOSIT.get(),
        () -> List.of(
            ErosionRegistry.Items.NATIVE_GOLD.get()
        ), BlockEntityRecipeRegistries.MATERIAL_PURIFIER
    );

    public static final RefinableMaterial CALCITE_MALACHITE_ORE = new RefinableMaterial(
        ErosionRegistry.RawRegistry.CALCITE_MALACHITE_ORE.getName(),
        () -> ErosionRegistry.Items.CALCITE_MALACHITE_ORE.get(),
        () -> List.of(
            ErosionRegistry.Items.RAW_MALACHITE.get()
        ), BlockEntityRecipeRegistries.MATERIAL_PURIFIER
    );

    public static final RefinableMaterial MAGNETITE_ORE = new RefinableMaterial(
        ErosionRegistry.RawRegistry.MAGNETITE_ORE.getName(),
        () -> ErosionRegistry.Items.MAGNETITE_ORE.get(),
        () -> List.of(
            ErosionRegistry.Items.RAW_MAGNETITE.get()
        ), BlockEntityRecipeRegistries.MATERIAL_PURIFIER
    );

    public static final RefinableMaterial HEMATITE_ORE = new RefinableMaterial(
        ErosionRegistry.RawRegistry.HEMATITE_ORE.getName(),
        () -> ErosionRegistry.Items.HEMATITE_ORE.get(),
        () -> List.of(
            ErosionRegistry.Items.RAW_HEMATITE.get()
        ), BlockEntityRecipeRegistries.MATERIAL_PURIFIER
    );

    public static final RefinableMaterial LIMONITE_ORE = new RefinableMaterial(
        ErosionRegistry.RawRegistry.LIMONITE_ORE.getName(),
        () -> ErosionRegistry.Items.LIMONITE_ORE.get(),
        () -> List.of(
            ErosionRegistry.Items.RAW_LIMONITE.get()
        ), BlockEntityRecipeRegistries.MATERIAL_PURIFIER
    );
    public static final RefinableMaterial CASSITERITE_DEPOSIT = new RefinableMaterial(
        ErosionRegistry.RawRegistry.CASSITERITE_DEPOSIT.getName(),
        () -> ErosionRegistry.Items.CASSITERITE_DEPOSIT.get(),
        () -> List.of(
            ErosionRegistry.Items.RAW_CASSITERITE.get()
        ), BlockEntityRecipeRegistries.MATERIAL_PURIFIER
    );

    public static final RefinableMaterial RAW_CASSITERITE = new RefinableMaterial(
        ErosionRegistry.RawRegistry.RAW_CASSITERITE.getName(),
        () -> ErosionRegistry.Items.RAW_CASSITERITE.get(),
        () -> List.of(
            ErosionRegistry.Items.TIN_CHUNK.get()
        ), BlockEntityRecipeRegistries.CRUCIBLE, FLUX
    );
    public static final RefinableMaterial NATIVE_SILVER = new RefinableMaterial(
        ErosionRegistry.RawRegistry.NATIVE_SILVER.getName(),
        () -> ErosionRegistry.Items.NATIVE_SILVER.get(),
        () -> List.of(
            ErosionRegistry.Items.SILVER_CHUNK.get()
        ), BlockEntityRecipeRegistries.CRUCIBLE, FLUX
    );

    public static final RefinableMaterial NATIVE_SILVER_DEPOSIT = new RefinableMaterial(
        ErosionRegistry.RawRegistry.NATIVE_SILVER_DEPOSIT.getName(),
        () -> ErosionRegistry.Items.NATIVE_SILVER_DEPOSIT.get(),
        () -> List.of(
            ErosionRegistry.Items.NATIVE_SILVER.get()
        ), BlockEntityRecipeRegistries.MATERIAL_PURIFIER
    );

    //turn mined ore into pure ore
    public static final RefinableMaterial RAW_BISMUTHINITE = new RefinableMaterial(
        ErosionRegistry.RawRegistry.RAW_BISMUTHINITE.getName(),
        () -> ErosionRegistry.Items.RAW_BISMUTHINITE.get(),
        () -> List.of(
            ErosionRegistry.Items.BISMUTH_CHUNK.get()
        ), BlockEntityRecipeRegistries.CRUCIBLE, FLUX
    );

    //turn block into its raw ore if mined with silk touch
    public static final RefinableMaterial BISMUTHINITE_ORE = new RefinableMaterial(
        ErosionRegistry.RawRegistry.BISMUTHINITE_ORE.getName(),
        () -> ErosionRegistry.Items.BISMUTHINITE_ORE.get(),
        () -> List.of(
            ErosionRegistry.Items.RAW_BISMUTHINITE.get()
        ), BlockEntityRecipeRegistries.MATERIAL_PURIFIER
    );

    //turn mined ore into pure ore
    public static final RefinableMaterial RAW_SPHALERITE = new RefinableMaterial(
        ErosionRegistry.RawRegistry.RAW_SPHALERITE.getName(),
        () -> ErosionRegistry.Items.RAW_SPHALERITE.get(),
        () -> List.of(
            ErosionRegistry.Items.ZINC_CHUNK.get()
        ), BlockEntityRecipeRegistries.CRUCIBLE, FLUX
    );

    //turn block into its raw ore if mined with silk touch
    public static final RefinableMaterial SPHALERITE_ORE = new RefinableMaterial(
        ErosionRegistry.RawRegistry.SPHALERITE_ORE.getName(),
        () -> ErosionRegistry.Items.SPHALERITE_ORE.get(),
        () -> List.of(
            ErosionRegistry.Items.RAW_SPHALERITE.get()
        ), BlockEntityRecipeRegistries.MATERIAL_PURIFIER
    );

    public static final List<RefinableMaterial> REFINABLE_MATERIALS_LIST = List.of(
        KAOLINIZED_GRANITE, QUARTZ_GRAVEL, ALBITIZED_GRANITE,
        PROPYLITIZED_DIORITE, RAW_LIMONITE, RAW_HEMATITE,
        RAW_MAGNETITE, RAW_MALACHITE, NATIVE_GOLD, NATIVE_GOLD_DEPOSIT,
        CALCITE_MALACHITE_ORE, MAGNETITE_ORE, HEMATITE_ORE, LIMONITE_ORE,
        CASSITERITE_DEPOSIT, RAW_CASSITERITE, NATIVE_SILVER,
        NATIVE_SILVER_DEPOSIT, RAW_BISMUTHINITE, BISMUTHINITE_ORE,
        RAW_SPHALERITE, SPHALERITE_ORE
    );

    // =====================================

    public static void Load()
    {
        PENDING.clear();
        PENDING_FAST.clear();

        PERFORMED = 0;
        PERFORMED_FAST = 0;

        for(int i = 0; i < ErosionModCompat.COMPATIBLE_MODS.size(); ++i)
        {
            var m = ErosionModCompat.COMPATIBLE_MODS.get(i);
            m.setupCompat();
        }

        for(int i = 0; i < CRUCIBLE_CATALYST_LIST.size(); ++i)
        {
            var m = CRUCIBLE_CATALYST_LIST.get(i);
            m.setup();
        }

        for(int i = 0; i < ALTERABLE_MATERIALS_LIST.size(); ++i)
        {
            var m = ALTERABLE_MATERIALS_LIST.get(i);
            m.setup();
        }

        for(int i = 0; i < REFINABLE_MATERIALS_LIST.size(); ++i)
        {
            var m = REFINABLE_MATERIALS_LIST.get(i);
            m.setup();
        }
        return;
    }

    public static void Unload()
    {
        for(int i = 0; i < ErosionModCompat.COMPATIBLE_MODS.size(); ++i)
        {
            var m = ErosionModCompat.COMPATIBLE_MODS.get(i);
            m.discardCompat();
        }

        for(int i = 0; i < CRUCIBLE_CATALYST_LIST.size(); ++i)
        {
            var m = CRUCIBLE_CATALYST_LIST.get(i);
            m.discard();
        }

        for(int i = 0; i < ALTERABLE_MATERIALS_LIST.size(); ++i)
        {
            var m = ALTERABLE_MATERIALS_LIST.get(i);
            m.discard();
        }

        for(int i = 0; i < REFINABLE_MATERIALS_LIST.size(); ++i)
        {
            var m = REFINABLE_MATERIALS_LIST.get(i);
            m.discard();
        }
        return;
    }

    @SubscribeEvent 
    public static void onTooltip(ItemTooltipEvent e)
    {
        Item currentItem = e.getItemStack().getItem();
        var tooltip = e.getToolTip();

        if(ITEM_DESCRIPTIONS.containsKey(currentItem))
        {
            var a = ITEM_DESCRIPTIONS.get(currentItem);
            for(int i = 0; i < a.size(); i++)
            {
                Component f = a.get(i).copy().withStyle(s -> s.withFont(ErosionConfig.MINI_FONT));
                tooltip.add(f);
            }
            return;
        }

        var b = setupItemDescription(currentItem);
        for(var c : b)
        {
            Component f = c.copy().withStyle(s -> s.withFont(ErosionConfig.MINI_FONT));
            tooltip.add(f);
        }
        return;
    }

    public static List<Component> setupItemDescription(Item currentItem)
    {
        List<Component> desc = new ArrayList<>();

        if(currentItem == ErosionRegistry.Items.MATERIAL_PURIFIER.get())
        {
            desc.add(
                Component.literal("A machine fueled with redstone powder.").withStyle(ChatFormatting.BLUE)
            );
            desc.add(
                Component.literal("Used to refine eroded or chemically modified materials.")
                .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC)
            );
        }
        if(currentItem == ErosionRegistry.Items.CRUCIBLE.get())
        {
            desc.add(
                Component.literal("A piece of pottery used to melt eligible materials into their raw forms.")
                .withStyle(ChatFormatting.GOLD)
            );
            desc.add(
                Component.literal("Can be placed only if there is a heat source under it such as Lava or Campfire.")
                .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC)
            );
        }

        for(CrucibleCatalyst c : CRUCIBLE_CATALYST_LIST)
        {
            if(currentItem == c.catalystItem)
            {
                desc.add(
                    Component.literal("Used as a crucible catalyst").withStyle(ChatFormatting.GOLD)
                );
            }
        }

        final class AlterationInfo
        {
            public String ruleNames = "";
            public List<String> productNames = new ArrayList<>();

            public AlterationInfo(String n, List<String> l)
            {
                this.ruleNames = n;
                this.productNames = l;
            }
        }
        
        List<AlterationInfo> alterationInfo = new ArrayList<>();
        for (AlterableMaterial m : ALTERABLE_MATERIALS_LIST)
        {
            if(m.materialItem == currentItem)
            {
                for(int i = 0; i < m.paths.size(); i++)
                {
                    var p = m.paths.get(i);
                    String ruleNames = "";
                    List<String> productNames = new ArrayList<>();

                    for(int j = 0; j < p.rules.rules.size(); j++)
                    {
                        ruleNames += p.rules.rules.get(j).name;
                        if(!(j + 1 >= p.rules.rules.size()))
                        {
                            ruleNames += ", ";
                        }
                    }

                    for(int j = 0; j < p.productItem.size(); j++)
                    {
                        productNames.add(p.productItem.get(j).getDescription().getString());
                    }
                    
                    alterationInfo.add(new AlterationInfo(ruleNames, productNames));
                }
            }
        }

        List<String> refinesIntoNames = new java.util.ArrayList<>();
        for (RefinableMaterial m : REFINABLE_MATERIALS_LIST)
        {
            if(m.recipeCategory == BlockEntityRecipeRegistries.MATERIAL_PURIFIER) if(m.materialItem == currentItem)
            {
                for (Item prodItem : m.productItem)
                { 
                    refinesIntoNames.add(prodItem.getDescription().getString());
                }
            }
        }

        List<String> meltsIntoNames = new java.util.ArrayList<>();
        String catalyst = null;
        for(RefinableMaterial m : REFINABLE_MATERIALS_LIST)
        {
            if(m.recipeCategory == BlockEntityRecipeRegistries.CRUCIBLE) if(m.materialItem == currentItem)
            {
                catalyst = m.catalyst.name;
                for (Item prodItem : m.productItem)
                { 
                    meltsIntoNames.add(prodItem.getDescription().getString());
                }
            }
        }

        if(!alterationInfo.isEmpty())
        {
            desc.add(Component.literal(""));
            desc.add(Component.literal("Geochemically alterable material").withStyle(ChatFormatting.AQUA, ChatFormatting.UNDERLINE));
            //String combinedProducts = String.join(", ", erodesIntoNames);
            for(int k = 0; k < alterationInfo.size(); k++)
            {
                String factor = alterationInfo.get(k).ruleNames;
                List<String> products = alterationInfo.get(k).productNames;

                desc.add(
                    Component.literal("- ").withStyle(ChatFormatting.GRAY).append(
                        Component.literal(factor).withStyle(ChatFormatting.BLUE)
                    ).append(
                        Component.literal(" can alter this block’s composition and transform it into:").withStyle(ChatFormatting.GRAY)
                    )
                );
                for(int i = 0; i < products.size(); i++)
                {
                    desc.add(
                        Component.literal("  * ").withStyle(ChatFormatting.GRAY)
                        .append(
                            Component.literal(products.get(i)).withStyle(ChatFormatting.YELLOW)
                        )
                    );
                }
            }
        }
        if(!refinesIntoNames.isEmpty())
        {
            desc.add(Component.literal(""));
            desc.add(Component.literal("Refinable material").withStyle(ChatFormatting.GREEN, ChatFormatting.UNDERLINE));
            //String combinedProducts = String.join(", ", refinesIntoNames);
            List<Component> list = new ArrayList<>();
            for(int i = 0; i < refinesIntoNames.size(); i++)
            {
                list.add(
                    Component.literal("  * ").withStyle(ChatFormatting.GRAY)
                    .append(
                        Component.literal(refinesIntoNames.get(i)).withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC)
                    )
                );
            }
            desc.add(
                Component.literal("- Can be refined into: ").withStyle(ChatFormatting.GRAY)
            );
            for(int i = 0; i < list.size(); i++)
            {
                desc.add(list.get(i));
            }
        }

        if(!meltsIntoNames.isEmpty())
        {
            desc.add(Component.literal(""));
            desc.add(Component.literal("Contains pure materials").withStyle(ChatFormatting.GOLD, ChatFormatting.UNDERLINE));
            //String combinedProducts = String.join(", ", meltsIntoNames);
            List<Component> list = new ArrayList<>();
            for(int i = 0; i < meltsIntoNames.size(); i++)
            {
                list.add(
                    Component.literal("  * ").withStyle(ChatFormatting.GRAY)
                    .append(
                        Component.literal(meltsIntoNames.get(i)).withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC)
                    )
                );
            }
            desc.add(
                Component.literal("- Can be crucible-molten into:")
                .withStyle(ChatFormatting.GRAY)
            );
            for(int i = 0; i < list.size(); i++)
            {
                desc.add(list.get(i));
            }
            desc.add(
                Component.literal("- Compatible catalyst: ").withStyle(ChatFormatting.GRAY)
                .append(
                    Component.literal(catalyst).withStyle(ChatFormatting.DARK_PURPLE)
                )
            );
        }

        List<String> madeByErodingNames = new ArrayList<>();
        for(AlterableMaterial m : ALTERABLE_MATERIALS_LIST)
        {
            for(int i = 0; i < m.paths.size(); i++)
            {
                var p = m.paths.get(i);
                if(p.productItem.contains(currentItem))
                {
                    madeByErodingNames.add(m.materialItem.getDescription().getString());
                }
            }
        }

        List<String> madeByRefiningNames = new ArrayList<>();
        for(RefinableMaterial m : REFINABLE_MATERIALS_LIST)
        {
            if(m.recipeCategory == BlockEntityRecipeRegistries.MATERIAL_PURIFIER) if(m.productItem.contains(currentItem))
            {
                madeByRefiningNames.add(m.materialItem.getDescription().getString());
            }
        }

        List<String> madeByMelting = new ArrayList<>();
        for(RefinableMaterial m : REFINABLE_MATERIALS_LIST)
        {
            if(m.recipeCategory == BlockEntityRecipeRegistries.CRUCIBLE) if(m.productItem.contains(currentItem))
            {
                madeByMelting.add(m.materialItem.getDescription().getString());
            }
        }

        if(!madeByErodingNames.isEmpty())
        {
            desc.add(Component.literal(""));
            desc.add(Component.literal("Product of geochemical alteration").withStyle(ChatFormatting.AQUA, ChatFormatting.UNDERLINE));
            //String combinedParents = String.join(", ", madeByErodingNames);
            List<Component> list = new ArrayList<>();
            for(int i = 0; i < madeByErodingNames.size(); i++)
            {
                list.add(
                    Component.literal("  * ").withStyle(ChatFormatting.GRAY)
                    .append(
                        Component.literal(madeByErodingNames.get(i)).withStyle(ChatFormatting.YELLOW)
                    )
                );
            }
            desc.add(
                Component.literal("- Made when environmental factors alter the composition of:")
                .withStyle(ChatFormatting.GRAY)
            );
            for(int i = 0; i < list.size(); i++)
            {
                desc.add(list.get(i));
            }
        }

        if(!madeByRefiningNames.isEmpty())
        {
            desc.add(Component.literal(""));
            desc.add(Component.literal("Refined material").withStyle(ChatFormatting.GREEN, ChatFormatting.UNDERLINE));
            //String combinedParents = String.join(", ", madeByRefiningNames);
            List<Component> list = new ArrayList<>();
            for(int i = 0; i < madeByRefiningNames.size(); i++)
            {
                list.add(
                    Component.literal("  * ").withStyle(ChatFormatting.GRAY)
                    .append(
                        Component.literal(madeByRefiningNames.get(i)).withStyle(ChatFormatting.YELLOW, ChatFormatting.ITALIC)
                    )
                );
            }
            desc.add(
                Component.literal("- Made by refining:").withStyle(ChatFormatting.GRAY)
                
            );
            for(int i = 0; i < list.size(); i++)
            {
                desc.add(list.get(i));
            }
        }
        if(!madeByMelting.isEmpty())
        {
            desc.add(Component.literal(""));
            desc.add(Component.literal("Pure material").withStyle(ChatFormatting.GOLD, ChatFormatting.UNDERLINE));
            //String combinedParents = String.join(", ", madeByMelting);
            List<Component> list = new ArrayList<>();
            for(int i = 0; i < madeByMelting.size(); i++)
            {
                list.add(
                    Component.literal("  * ").withStyle(ChatFormatting.GRAY)
                    .append(
                        Component.literal(madeByMelting.get(i)).withStyle(ChatFormatting.RED)
                    )
                );
            }
            desc.add(
                Component.literal("- Made by crucible-melting: ").withStyle(ChatFormatting.GRAY)
                
            );
            for(int i = 0; i < list.size(); i++)
            {
                desc.add(list.get(i));
            }
        }

        //cache item descriptions
        if(!desc.isEmpty())
        {
            ITEM_DESCRIPTIONS.put(currentItem, desc);
            ErosionUtils.Log("Successfully set description of item: " + currentItem.getDescription().getString());
        }
        return desc;
    }

    // =================================================== //

    private static void addCandidateMain(ServerLevel level, BlockPos pos, Boolean priority)
    {
        var Pending = PENDING;
        if(priority)
        {
            Pending = PENDING_FAST;
        }

        if(Pending.contains(pos.asLong())) return;
        BlockState state = level.getBlockState(pos);

        if(!isAlterable(state)) return;
        var l = ALTERATION_INVERTED.get(state.getBlock());
        for(int i = 0; i < l.paths.size(); ++i)
        {
            var p = l.paths.get(i);
            for(int j = 0; j < p.rules.getRules().size(); ++j)
            {
                var r = p.rules.getRules().get(j);
                if(r.check(level, pos))
                {
                    Pending.add(pos, p.product.get(ErosionMod.RANDOM.nextInt(p.product.size())));

                    if(Pending.size() >= (priority ? ErosionConfig.MAX_PENDING_FAST_SIZE : ErosionConfig.MAX_PENDING_SIZE))
                    {
                        Pending.remove(0);
                    }
                    if(ErosionConfig.isDebugOn()) ErosionUtils.Log("Added candidate: " + pos);
                    return;
                }
            }
        }
        return;
    }
    
    public static void addCandidate(ServerLevel l, BlockPos p)
    {
        if(PENDING.size() >= ErosionConfig.MAX_PENDING_SIZE)
        {
            return;
        }
        addCandidateMain(l, p, false);
    }

    public static void addCandidatePriority(ServerLevel l, BlockPos p)
    {
        addCandidateMain(l, p, true);
    }

    // =================================================== //
        
    private static void processPendingCore(ServerLevel level, Integer count, Boolean priority)
    {
        var Pending = PENDING;
        if(priority)
        {
            Pending = PENDING_FAST;
        }

        if(Pending.isEmpty()) return;
        int processedThisTick = 0;

        for(int i = Pending.size() - 1; i >= 0 && processedThisTick < count; i--)
        {
            gpos = Pending.get(i);
            tryAlterBlock(level, gpos, priority);
            processedThisTick++;
        }
        return;
    }

    public static void processPending(ServerLevel l)
    {
        processPendingCore(l, ErosionConfig.MAX_EROSIONS_PER_TICK, false);
    }

    public static void processPendingPriority(ServerLevel l)
    {
        processPendingCore(l, ErosionConfig.MAX_EROSIONS_PER_TICK + 1, true);//lmao
    }

    // =================================================== //

    private static void tryAlterBlock(ServerLevel level, AlterationPacket p, Boolean priority)
    {
        BlockState state = level.getBlockState(p.pos);
        if(!isAlterable(state)) return;
        
        var s = p.block.defaultBlockState();

        level.setBlock(p.pos, s, 3);
        level.sendBlockUpdated(p.pos, state, s, 3);

        if(ErosionConfig.isDebugOn()) ErosionUtils.Log(
            "Erosion performed: "
            + p.pos
        );

        if(priority) PERFORMED_FAST++;
        else PERFORMED++;
        return;
    }

    // =================================================== //

    public static boolean isAlterable(BlockState state)
    {
        return ALTERATION_INVERTED.containsKey(state.getBlock());
    }

    // =================================================== //

    public static boolean hasWaterNearbyOLD(ServerLevel level, BlockPos pos)
    {
        for (Direction direction : Direction.values())
        {
            BlockPos nearby = pos.relative(direction);

            if (level.getFluidState(nearby).is(Fluids.WATER))
            {
                return true;
            }
        }

        return false;
    }

    public static boolean hasWaterNearby(ServerLevel level, BlockPos pos)
    {
        int radius = 4;
        int radiusSq = radius * radius;

        for (int x = -radius; x <= radius; x++)
        {
            for (int y = -radius; y <= radius; y++)
            {
                for (int z = -radius; z <= radius; z++)
                {
                    if (x * x + y * y + z * z > radiusSq) continue;

                    BlockPos nearby = pos.offset(x, y, z);

                    if (level.getFluidState(nearby).is(Fluids.WATER))
                    {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public static boolean hasLavaNearby(ServerLevel level, BlockPos pos)
    {
        int radius = 4;
        int radiusSq = radius * radius;

        for (int x = -radius; x <= radius; x++)
        {
            for (int y = -radius; y <= radius; y++)
            {
                for (int z = -radius; z <= radius; z++)
                {
                    if (x * x + y * y + z * z > radiusSq) continue;

                    BlockPos nearby = pos.offset(x, y, z);

                    if (level.getFluidState(nearby).is(Fluids.LAVA))
                    {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    // =================================================== //

    public static Integer getPendingSize()
    {
        return PENDING.size();
    }

    public static Integer getPendingFastSize()
    {
        return PENDING_FAST.size();
    }

    public static Integer getPerformedAlterations()
    {
        return PERFORMED;
    }
    public static Integer getPerformedAlterationsPriority()
    {
        return PERFORMED_FAST;
    }

    // =================================================== //

    public static class Command
    {
        public String name = "";
        public String subcmd = "";
        public Consumer<CommandSourceStack> action = null;

        public Command(String n, String s, Consumer<CommandSourceStack> c)
        {
            this.name = n;
            this.subcmd = s;
            this.action = c;
        }

        public void execute(CommandSourceStack s)
        {
            action.accept(s);
            return;
        }
    }

    public static class CommandRegistry
    {
        public static final String EROSION_STATUS_SUBCMD = "status";
        public static final String EROSION_CONFIG_SUBCMD = "reload_config";

        public static final List<Command> COMMANDS = List.of(
            new Command(Erosion.MODID, EROSION_STATUS_SUBCMD, ErosionCommands::handleStatus),
            new Command(Erosion.MODID, EROSION_CONFIG_SUBCMD, ErosionCommands::reloadCfg)
        );
    }

    // =================================================== //

    public static class BlockEntityRecipes
    {
        public static class MaterialPurifier
        {
            public static Map<Item, List<Item>> RECIPES = new HashMap<>();
        }
        public static class Crucible
        {
            public static Map<Item, List<Item>> RECIPES = new HashMap<>();
            public static Map<Item, CrucibleCatalyst> CATALYSTS = new HashMap<>();
        }
    }
    public static class Extra
    {
        public static void bulkProcess(ServerLevel level)
        {
            for(int i = 0; i < ErosionConfig.MAX_EROSIONS_PER_TICK / 2; ++i)
            {
                ErosionCore.processPending(level);
                ErosionCore.processPendingPriority(level);
            }
        }
    } 
}