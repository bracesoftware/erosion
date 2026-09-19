package co.bracesoftware.erosion;

import co.bracesoftware.erosion.world.ErosionRegistry;
import co.bracesoftware.erosion.world.blocks.chemical_reactor.ChemicalReactorMenu;
import co.bracesoftware.erosion.world.custom.ErosionCustomEntitySys.GasType;
import co.bracesoftware.erosion.world.items.ErosionSimpleItems.GasMask.Quality;
import co.bracesoftware.erosion.world.items.ErosionSimpleItems;
import co.bracesoftware.erosion.world.items.ErosionSimpleItems.GasMask;
import co.bracesoftware.erosion.api.eventbus.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Hash;
import co.bracesoftware.erosion.ErosionCore.RefinableMaterial;
import co.bracesoftware.erosion.ErosionExceptions.ErosionRecipeImplException;


import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.registries.DeferredItem;
import it.unimi.dsi.fastutil.longs.*;

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
    public static final Map<Item, Integer> CATALYST_SUCCESS_CHANCE = new HashMap<>();

    private static Map<Block, AlterableMaterial> ALTERATION_INVERTED = new HashMap<>();

    private static Integer PERFORMED = 0;
    private static Integer PERFORMED_FAST = 0;

    public static class BlockEntityRecipeRegistries
    {
        public static final Integer MATERIAL_PURIFIER = 1;
        public static final Integer CRUCIBLE = 2;
    }

    public static abstract class ErosionDynamicItem
    {
        public String name;
        private static final List<String> DO_NOT_USE = null;
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

        public void discardDuplicationPreventionSys(
            List<String> l
        ) throws ErosionRecipeImplException
        {
            String resource = this.getClass().getSimpleName() + "::Erosion.class(\"" + this.name + "\")";
            if(!l.contains(this.name))
            {
                throw new ErosionRecipeImplException("Object was never set up, cannot be discarded -> " + resource);
            }
            l.remove(this.name);
            ErosionUtils.Log("Discarding data of: " + resource);
            return;
        }

        public void preventDuplication(
            List<String> l
        ) throws ErosionRecipeImplException
        {
            String resource = this.getClass().getSimpleName() + "::Erosion.class(\"" + this.name + "\")";
            if(l.contains(this.name))
            {
                throw new ErosionRecipeImplException("Duplicate object -> " + resource);
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
        public static final AlterationRule HIGH_PRESSURE = new AlterationRule(
            "High lithostatic pressure",
            ErosionCore::highPressure
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

        public boolean checkIfAllConditionsAreMet(ServerLevel l, BlockPos p)
        {
            for(var r : this.rules)
            {
                if(!r.check(l, p))
                {
                    return false;
                }
            }
            return true;
        }
    }

    public static class ChemicalReaction extends ErosionDynamicItem
    {
        public Supplier<List<Item>> reactant;
        public Supplier<List<Item>> product;
        public Supplier<List<Item>> mainProduct;

        public List<Item> reactantItems;
        public List<Item> productItems;
        public List<Item> mainProductItems;

        public ChemicalReaction(String n, Supplier<List<Item>> r, Supplier<List<Item>> p, Supplier<List<Item>> k)
        {
            this.name = n;
            this.reactant = r;
            this.product = p;
            this.mainProduct = k;

            this.antiDuplicator = new ArrayList<>();
        }

        @Override 
        public void setup() throws ErosionRecipeImplException
        {
            ErosionUtils.Log("Setting up chemical reaction: " + this.name);
            var p = new ArrayList<>(this.product.get());
            p.sort(ChemicalReactorMenu.itemComparator);
            this.productItems = p;

            p = new ArrayList<>(this.reactant.get());
            p.sort(ChemicalReactorMenu.itemComparator);
            this.reactantItems = p;

            this.mainProductItems = this.mainProduct.get();

            this.preventDuplication(this.antiDuplicator);

            if(
                (this.reactantItems.size() <= 0 || this.reactantItems.size() > ChemicalReactorMenu.ROWS * ChemicalReactorMenu.COL) ||
                (this.productItems.size() <= 0 || this.productItems.size() > ChemicalReactorMenu.ROWS * ChemicalReactorMenu.COL)
            )
            {
                throw new ErosionRecipeImplException("Reactant and product lists have to be in range 0 < x <= 6 -> " + this.name);
            }
            return;
        }

        public List<Item> getReactants()
        {
            return this.reactantItems;
        }
        public List<Item> getProducts()
        {
            return this.productItems;
        }

        @Override 
        public void discard()
        {
            ErosionUtils.Log("Discarding chemical reaction: " + this.name);
            this.discardDuplicationPreventionSys(this.antiDuplicator);
        }
    }

    public static class CrucibleCatalyst extends ErosionDynamicItem
    {
        public Supplier<Item> catalyst;
        public Item catalystItem;
        public Integer successChance;

        public CrucibleCatalyst(String n, Supplier<Item> c, Integer s)
        {
            this.name = n;
            this.catalyst = c;

            this.antiDuplicator = new ArrayList<>();
            this.successChance = s;
        }

        @Override 
        public void setup() throws ErosionRecipeImplException
        {
            ErosionUtils.Log("Setting up crucible catalyst: " + this.name);
            this.preventDuplication(antiDuplicator);
            this.catalystItem = this.catalyst.get();

            CATALYST_SUCCESS_CHANCE.put(this.catalystItem, this.successChance);

            if(this.successChance <= 0 || this.successChance > 100)
            {
                throw new ErosionRecipeImplException("Catalyst success rate has to be greater than 0 or less or equal to 100 -> " + this.name);
            }
        }

        @Override 
        public void discard()
        {
            ErosionUtils.Log("Discarding crucible catalyst: " + this.name);
            this.discardDuplicationPreventionSys(antiDuplicator);

            CATALYST_SUCCESS_CHANCE.remove(this.catalystItem);
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

        public static Integer getCatalystSuccessRate(Item item)
        {
            for(int i = 0; i < CRUCIBLE_CATALYST_LIST.size(); ++i)
            {
                CrucibleCatalyst c = CRUCIBLE_CATALYST_LIST.get(i);
                if(c.catalystItem == item)
                {
                    return c.successChance;
                }
            }
            return 0;
        }
        public static ChatFormatting getSRColor(int sr)
        {
            if(sr <= 20)
            {
                return ChatFormatting.RED;
            }
            if((21 < sr) && (sr <= 40))
            {
                return ChatFormatting.GOLD;
            }
            if((41 < sr) && (sr <= 60))
            {
                return ChatFormatting.YELLOW;
            }
            if((61 < sr) && (sr <= 80))
            {
                return ChatFormatting.GREEN;
            }
            return ChatFormatting.DARK_PURPLE;
        }
    }

    public static class RefinableMaterial extends ErosionDynamicItem
    {
        public Supplier<Item> material;
        public Supplier<List<Item>> product;
        public Supplier<List<Item>> coproduct;
        public Integer recipeCategory = null;

        public Item materialItem = null;
        public List<Item> productItem = null;

        public List<CrucibleCatalyst> catalyst = null;
        public List<Item> coproductItem = null;
        public List<GasType> emittedGases = null;

        private RefinableMaterial() {}

        public static class MaterialPurifier extends RefinableMaterial
        {
            public MaterialPurifier(String n, Supplier<Item> m, Supplier<List<Item>> p, Integer i)
            {
                this.name = n;
                this.material = m;
                this.product = p;
                this.recipeCategory = i;

                this.antiDuplicator = new ArrayList<>();
            }
            public MaterialPurifier(String n, Supplier<Item> m, Supplier<List<Item>> p)
            {
                this.name = n;
                this.material = m;
                this.product = p;
                this.recipeCategory = BlockEntityRecipeRegistries.MATERIAL_PURIFIER;

                this.antiDuplicator = new ArrayList<>();
            }
        }

        public static class Crucible extends RefinableMaterial
        {
            public Crucible(
                String n, Supplier<Item> m, Supplier<List<Item>> p, Integer i,
                List<CrucibleCatalyst> c, Supplier<List<Item>> g,
                List<GasType> gg
            )
            {
                this.name = n;
                this.material = m;
                this.product = p;
                this.recipeCategory = i;
                this.catalyst = c;
                this.coproduct = g;
                this.emittedGases = gg;

                this.antiDuplicator = new ArrayList<>();
            }
            public Crucible(
                String n, Supplier<Item> m, Supplier<List<Item>> p,
                List<CrucibleCatalyst> c, Supplier<List<Item>> g,
                List<GasType> gg
            )
            {
                this.name = n;
                this.material = m;
                this.product = p;
                this.recipeCategory = BlockEntityRecipeRegistries.CRUCIBLE;
                this.catalyst = c;
                this.coproduct = g;
                this.emittedGases = gg;

                this.antiDuplicator = new ArrayList<>();
            }
        }

        @Override 
        public void setup() throws ErosionRecipeImplException
        {
            ErosionUtils.Log("Setting up refinable material item: " + this.name);
            this.preventDuplication(antiDuplicator);
            
            this.materialItem = this.material.get();
            this.productItem = this.product.get();

            if(this.recipeCategory == BlockEntityRecipeRegistries.CRUCIBLE)
            {
                if(this.coproduct != null)
                {
                    this.coproductItem = this.coproduct.get();
                }
            }
            
            if(
                this.recipeCategory != BlockEntityRecipeRegistries.MATERIAL_PURIFIER &&
                this.recipeCategory != BlockEntityRecipeRegistries.CRUCIBLE
            )
            {
                throw new ErosionRecipeImplException("Invalid recipe category -> " + this.name);
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
                    throw new ErosionRecipeImplException("Missing a catalyst for recipe: " + this.name);
                }
                else
                {
                    List<Item> L_ = new ArrayList<>();
                    for(var f : this.catalyst)
                    {
                        L_.add(f.catalystItem);
                    }
                    BlockEntityRecipes.Crucible.CATALYSTS.putIfAbsent(materialItem, L_);
                }

                if(this.coproduct == null || this.coproductItem == null)
                {
                    throw new ErosionRecipeImplException("Missing a list of coproducts for recipe: " + this.name);
                }
                else
                {
                    BlockEntityRecipes.Crucible.COPRODUCTS.putIfAbsent(materialItem, this.coproductItem);
                }
                
                BlockEntityRecipes.Crucible.EMITTED_GASES.putIfAbsent(materialItem, emittedGases);
                for(var f : this.emittedGases)
                {
                    ErosionUtils.Log("Successfully registered gas `" + f.name + "` for crucible process -> " + this.name);
                }
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
            BlockEntityRecipes.Crucible.COPRODUCTS.clear();
            BlockEntityRecipes.Crucible.EMITTED_GASES.clear();
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

            public void setup() throws ErosionRecipeImplException
            {
                ErosionUtils.Log("Setting up alteration path: " + this.name);
                this.product = this.productSupplier.get();
                this.productItem = this.productItemSupplier.get();

                if(this.product.size() != this.productItem.size())
                {
                    throw new ErosionRecipeImplException("this.product.size() != this.productItem.size() -> path::(\"" + this.name + "\")");
                }

                if(this.rules == null)
                {
                    throw new ErosionRecipeImplException("Alteration rules are null for path -> " + this.name);
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
        public void setup() throws ErosionRecipeImplException
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

    public static final AlterableMaterial.AlterationPath GEMSTONE_GEN = new AlterableMaterial.AlterationPath(
        ErosionRegistry.DefaultAlterationPaths.ALTERATION_BY_HEAT_AND_PRESSURE,
        () -> List.of(
            ErosionRegistry.Blocks.RUBY_ORE.get(),
            Blocks.EMERALD_ORE,
            ErosionRegistry.Blocks.SAPPHIRE_ORE.get()
        ),
        () -> List.of(
            ErosionRegistry.Items.RUBY.get(),
            Items.EMERALD,
            ErosionRegistry.Items.SAPPHIRE.get()
        ),
        new AlterationRules(List.of(
            AlterationRules.CONTACT_WITH_LAVA,
            AlterationRules.HIGH_PRESSURE
        ))
    );

    public static final AlterableMaterial.AlterationPath HYDROTHERMAL_BLOCK_GEN = new AlterableMaterial.AlterationPath(
        ErosionRegistry.DefaultAlterationPaths.HYDROTHERMAL_ALTERATION,
        () -> List.of(
            ErosionRegistry.Blocks.ARSENOPYRITE_ORE.get()
        ),
        () -> List.of(
            ErosionRegistry.Items.RAW_ARSENOPYRITE.get()
        ),
        new AlterationRules(List.of(
            AlterationRules.CONTACT_WITH_WATER,
            AlterationRules.HIGH_PRESSURE
        ))
    );

    public static final AlterableMaterial COBBLESTONE = new AlterableMaterial(
        Blocks.COBBLESTONE.getName().getString(),
        () -> Blocks.COBBLESTONE, () -> Items.COBBLESTONE,
        List.of(
            new AlterableMaterial.AlterationPath(
                ErosionRegistry.DefaultAlterationPaths.ALTERATION_BY_LAVA,
                () -> List.of(
                    ErosionRegistry.Blocks.CRACKED_STONE.get()
                ),
                () -> List.of(
                    ErosionRegistry.Items.CRACKED_STONE.get()
                ),
                new AlterationRules(List.of(
                    AlterationRules.CONTACT_WITH_LAVA
                ))
            ),
            HYDROTHERMAL_BLOCK_GEN
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
                    ErosionRegistry.Blocks.SPHALERITE_ORE.get(),
                    ErosionRegistry.Blocks.AZURITE_ORE.get(),
                    ErosionRegistry.Blocks.TETRAHEDRITE_ORE.get(),
                    ErosionRegistry.Blocks.PYRITE_ORE.get()
                ),
                () -> List.of(
                    Items.COBBLESTONE, Items.GRAVEL, Items.CALCITE,
                    ErosionRegistry.Items.RAW_LIMONITE.get(),
                    ErosionRegistry.Items.RAW_HEMATITE.get(),
                    ErosionRegistry.Items.RAW_MAGNETITE.get(),
                    ErosionRegistry.Items.NATIVE_SILVER.get(),
                    ErosionRegistry.Items.RAW_BISMUTHINITE.get(),
                    ErosionRegistry.Items.RAW_SPHALERITE.get(),
                    ErosionRegistry.Items.RAW_AZURITE.get(),
                    ErosionRegistry.Items.RAW_TETRAHEDRITE.get(),
                    ErosionRegistry.Items.RAW_PYRITE.get()
                ),
                new AlterationRules(List.of(
                    AlterationRules.CONTACT_WITH_WATER
                ))
            ), GEMSTONE_GEN,HYDROTHERMAL_BLOCK_GEN,
            new AlterableMaterial.AlterationPath(
                ErosionRegistry.DefaultAlterationPaths.ALTERATION_BY_LAVA,
                () -> List.of(
                    ErosionRegistry.Blocks.BORAX_DEPOSIT.get(),
                    ErosionRegistry.Blocks.CRACKED_STONE.get()
                ),
                () -> List.of(
                    ErosionRegistry.Items.BORAX.get(),
                    ErosionRegistry.Items.CRACKED_STONE.get()
                ), new AlterationRules(List.of(
                    AlterationRules.CONTACT_WITH_LAVA
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
                    ErosionRegistry.Blocks.SPHALERITE_ORE.get(),
                    ErosionRegistry.Blocks.AZURITE_ORE.get()
                ),
                () -> List.of(
                    Items.COBBLED_DEEPSLATE,
                    ErosionRegistry.Items.RAW_LIMONITE.get(),
                    ErosionRegistry.Items.RAW_BISMUTHINITE.get(),
                    ErosionRegistry.Items.RAW_SPHALERITE.get(),
                    ErosionRegistry.Items.RAW_AZURITE.get()
                ),
                new AlterationRules(List.of(
                    AlterationRules.CONTACT_WITH_WATER
                ))
            ), GEMSTONE_GEN,HYDROTHERMAL_BLOCK_GEN,
            new AlterableMaterial.AlterationPath(
                ErosionRegistry.DefaultAlterationPaths.ALTERATION_BY_LAVA,
                () -> List.of(
                    ErosionRegistry.Blocks.BORAX_DEPOSIT.get()
                ),
                () -> List.of(
                    ErosionRegistry.Items.BORAX.get()
                ), new AlterationRules(List.of(
                    AlterationRules.CONTACT_WITH_LAVA
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
                    ErosionRegistry.Blocks.HEMATITE_ORE.get(),
                    ErosionRegistry.Blocks.PYRITE_ORE.get()
                ),
                () -> List.of(
                    ErosionRegistry.Items.KAOLINIZED_GRANITE.get(),
                    ErosionRegistry.Items.ALBITIZED_GRANITE.get(),
                    ErosionRegistry.Items.QUARTZ_GRAVEL.get(),
                    ErosionRegistry.Items.RAW_HEMATITE.get(),
                    ErosionRegistry.Items.RAW_PYRITE.get()
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
                    ErosionRegistry.Blocks.SPHALERITE_ORE.get(),
                    ErosionRegistry.Blocks.AZURITE_ORE.get()
                ),
                () -> List.of(
                    ErosionRegistry.Items.PROPYLITIZED_DIORITE.get(),
                    ErosionRegistry.Items.RAW_MAGNETITE.get(),
                    ErosionRegistry.Items.RAW_BISMUTHINITE.get(),
                    ErosionRegistry.Items.RAW_SPHALERITE.get(),
                    ErosionRegistry.Items.RAW_AZURITE.get()
                ),
                new AlterationRules(List.of(
                    AlterationRules.CONTACT_WITH_WATER
                ))
            ),HYDROTHERMAL_BLOCK_GEN
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
            ), GEMSTONE_GEN,HYDROTHERMAL_BLOCK_GEN
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

    public static final AlterableMaterial ANDESITE = new AlterableMaterial(
        Blocks.ANDESITE.getName().getString(),
        () -> Blocks.ANDESITE, () -> Items.ANDESITE,
        List.of(
            new AlterableMaterial.AlterationPath(
                ErosionRegistry.DefaultAlterationPaths.ALTERATION_BY_WATER,
                () -> List.of(
                    ErosionRegistry.Blocks.AZURITE_ORE.get(),
                    ErosionRegistry.Blocks.TETRAHEDRITE_ORE.get()
                ),
                () -> List.of(
                    ErosionRegistry.Items.RAW_AZURITE.get(),
                    ErosionRegistry.Items.RAW_TETRAHEDRITE.get()
                ),
                new AlterationRules(List.of(
                    AlterationRules.CONTACT_WITH_WATER
                ))
            ), GEMSTONE_GEN
        )
    );

    // ============================== CRUCIBLE CATALYSTS

    public static final CrucibleCatalyst FLUX = new CrucibleCatalyst(
        ErosionRegistry.RawRegistry.FLUX.getName(),
        () -> ErosionRegistry.Items.FLUX.get(), 60
    );

    public static final CrucibleCatalyst CRUSHED_EGG_SHELL = new CrucibleCatalyst(
        ErosionRegistry.RawRegistry.CRUSHED_EGG_SHELL.getName(),
        () -> ErosionRegistry.Items.CRUSHED_EGG_SHELL.get(), 20
    );

    public static final CrucibleCatalyst DEHYDRATED_BORAX = new CrucibleCatalyst(
        ErosionRegistry.RawRegistry.DEHYDRATED_BORAX.getName(),
        () -> ErosionRegistry.Items.DEHYDRATED_BORAX.get(), 80
    );

    public static final CrucibleCatalyst BORIC_ACID_CRYSTAL = new CrucibleCatalyst(
        ErosionRegistry.RawRegistry.BORIC_ACID_CRYSTAL.getName(),
        () -> ErosionRegistry.Items.BORIC_ACID_CRYSTAL.get(), 95
    );

    // ========================== REFINABLE MATERIALS

    public static final RefinableMaterial.MaterialPurifier KAOLINIZED_GRANITE = new RefinableMaterial.MaterialPurifier(
        ErosionRegistry.RawRegistry.KAOLINIZED_GRANITE.getName(),
        () -> ErosionRegistry.Items.KAOLINIZED_GRANITE.get(),
        () -> List.of(Items.CLAY),
        BlockEntityRecipeRegistries.MATERIAL_PURIFIER
    );

    public static final RefinableMaterial.MaterialPurifier QUARTZ_GRAVEL = new RefinableMaterial.MaterialPurifier(
        ErosionRegistry.RawRegistry.QUARTZ_GRAVEL.getName(),
        () -> ErosionRegistry.Items.QUARTZ_GRAVEL.get(),
        () -> List.of(Items.QUARTZ),
        BlockEntityRecipeRegistries.MATERIAL_PURIFIER
    );

    public static final RefinableMaterial.MaterialPurifier ALBITIZED_GRANITE = new RefinableMaterial.MaterialPurifier(
        ErosionRegistry.RawRegistry.ALBITIZED_GRANITE.getName(),
        () -> ErosionRegistry.Items.ALBITIZED_GRANITE.get(),
        () -> List.of(ErosionRegistry.Items.FELDSPAR_POWDER.get()),
        BlockEntityRecipeRegistries.MATERIAL_PURIFIER
    );

    public static final RefinableMaterial.MaterialPurifier PROPYLITIZED_DIORITE = new RefinableMaterial.MaterialPurifier(
        ErosionRegistry.RawRegistry.PROPYLITIZED_DIORITE.getName(),
        () -> ErosionRegistry.Items.PROPYLITIZED_DIORITE.get(),
        () -> List.of(
            Items.CLAY_BALL,
            ErosionRegistry.Items.RAW_MAGNETITE.get(),
            ErosionRegistry.Items.RAW_MALACHITE.get(),
            ErosionRegistry.Items.CRACKED_CALCITE.get()
        ), BlockEntityRecipeRegistries.MATERIAL_PURIFIER
    );

    public static final RefinableMaterial CRACKED_STONE = new RefinableMaterial.MaterialPurifier(
        ErosionRegistry.RawRegistry.CRACKED_STONE.getName(),
        () -> ErosionRegistry.Items.CRACKED_STONE.get(),
        () -> List.of(
            ErosionRegistry.Items.BORAX.get(),
            ErosionRegistry.Items.DEBRIS.get(),
            ErosionRegistry.Items.FELDSPAR_POWDER.get(),
            
            ErosionRegistry.Items.RAW_LIMONITE.get(),
            ErosionRegistry.Items.RAW_MAGNETITE.get(),
            ErosionRegistry.Items.RAW_MAGNETITE.get(),
            ErosionRegistry.Items.RAW_MALACHITE.get(),
            ErosionRegistry.Items.RAW_AZURITE.get(),
            ErosionRegistry.Items.RAW_TETRAHEDRITE.get(),

            ErosionRegistry.Items.NATIVE_SILVER.get(),
            ErosionRegistry.Items.NATIVE_GOLD.get(),

            ErosionRegistry.Items.RAW_BISMUTHINITE.get(),
            ErosionRegistry.Items.RAW_CASSITERITE.get(),
            ErosionRegistry.Items.RAW_SPHALERITE.get()
        ), BlockEntityRecipeRegistries.MATERIAL_PURIFIER
    );

    public static final RefinableMaterial.Crucible RAW_LIMONITE = new RefinableMaterial.Crucible(
        ErosionRegistry.RawRegistry.RAW_LIMONITE.getName(),
        () -> ErosionRegistry.Items.RAW_LIMONITE.get(),
        () -> List.of(
            Items.IRON_NUGGET
        ), BlockEntityRecipeRegistries.CRUCIBLE, List.of(
            FLUX, CRUSHED_EGG_SHELL, DEHYDRATED_BORAX,BORIC_ACID_CRYSTAL
        ), () -> List.of(), List.of(
            ErosionRegistry.GasTypes.WATER_VAPOR
        )
    );
    public static final RefinableMaterial.Crucible RAW_MAGNETITE = new RefinableMaterial.Crucible(
        ErosionRegistry.RawRegistry.RAW_MAGNETITE.getName(),
        () -> ErosionRegistry.Items.RAW_MAGNETITE.get(),
        () -> List.of(
            Items.IRON_NUGGET
        ), BlockEntityRecipeRegistries.CRUCIBLE, List.of(
            FLUX, CRUSHED_EGG_SHELL, DEHYDRATED_BORAX,BORIC_ACID_CRYSTAL
        ), () -> List.of(), List.of()
    );
    public static final RefinableMaterial.Crucible RAW_HEMATITE = new RefinableMaterial.Crucible(
        ErosionRegistry.RawRegistry.RAW_HEMATITE.getName(),
        () -> ErosionRegistry.Items.RAW_HEMATITE.get(),
        () -> List.of(
            Items.IRON_NUGGET
        ), BlockEntityRecipeRegistries.CRUCIBLE, List.of(
            FLUX, CRUSHED_EGG_SHELL, DEHYDRATED_BORAX,BORIC_ACID_CRYSTAL
        ), () -> List.of(), List.of()
    );
    public static final RefinableMaterial.Crucible RAW_MALACHITE = new RefinableMaterial.Crucible(
        ErosionRegistry.RawRegistry.RAW_MALACHITE.getName(),
        () -> ErosionRegistry.Items.RAW_MALACHITE.get(),
        () -> List.of(
            Items.RAW_COPPER
        ), BlockEntityRecipeRegistries.CRUCIBLE, List.of(
            FLUX, CRUSHED_EGG_SHELL,DEHYDRATED_BORAX,BORIC_ACID_CRYSTAL
        ), () -> List.of(), List.of()
    );

    public static final RefinableMaterial.Crucible NATIVE_GOLD = new RefinableMaterial.Crucible(
        ErosionRegistry.RawRegistry.NATIVE_GOLD.getName(),
        () -> ErosionRegistry.Items.NATIVE_GOLD.get(),
        () -> List.of(
            Items.GOLD_NUGGET
        ), BlockEntityRecipeRegistries.CRUCIBLE, List.of(
            FLUX, CRUSHED_EGG_SHELL,DEHYDRATED_BORAX,BORIC_ACID_CRYSTAL
        ), () -> List.of(), List.of()
    );

    public static final RefinableMaterial NATIVE_GOLD_DEPOSIT = new RefinableMaterial.MaterialPurifier(
        ErosionRegistry.RawRegistry.NATIVE_GOLD_DEPOSIT.getName(),
        () -> ErosionRegistry.Items.NATIVE_GOLD_DEPOSIT.get(),
        () -> List.of(
            ErosionRegistry.Items.NATIVE_GOLD.get()
        ), BlockEntityRecipeRegistries.MATERIAL_PURIFIER
    );

    public static final RefinableMaterial CALCITE_MALACHITE_ORE = new RefinableMaterial.MaterialPurifier(
        ErosionRegistry.RawRegistry.CALCITE_MALACHITE_ORE.getName(),
        () -> ErosionRegistry.Items.CALCITE_MALACHITE_ORE.get(),
        () -> List.of(
            ErosionRegistry.Items.RAW_MALACHITE.get()
        ), BlockEntityRecipeRegistries.MATERIAL_PURIFIER
    );

    public static final RefinableMaterial MAGNETITE_ORE = new RefinableMaterial.MaterialPurifier(
        ErosionRegistry.RawRegistry.MAGNETITE_ORE.getName(),
        () -> ErosionRegistry.Items.MAGNETITE_ORE.get(),
        () -> List.of(
            ErosionRegistry.Items.RAW_MAGNETITE.get()
        ), BlockEntityRecipeRegistries.MATERIAL_PURIFIER
    );

    public static final RefinableMaterial HEMATITE_ORE = new RefinableMaterial.MaterialPurifier(
        ErosionRegistry.RawRegistry.HEMATITE_ORE.getName(),
        () -> ErosionRegistry.Items.HEMATITE_ORE.get(),
        () -> List.of(
            ErosionRegistry.Items.RAW_HEMATITE.get()
        ), BlockEntityRecipeRegistries.MATERIAL_PURIFIER
    );

    public static final RefinableMaterial LIMONITE_ORE = new RefinableMaterial.MaterialPurifier(
        ErosionRegistry.RawRegistry.LIMONITE_ORE.getName(),
        () -> ErosionRegistry.Items.LIMONITE_ORE.get(),
        () -> List.of(
            ErosionRegistry.Items.RAW_LIMONITE.get()
        ), BlockEntityRecipeRegistries.MATERIAL_PURIFIER
    );
    public static final RefinableMaterial CASSITERITE_DEPOSIT = new RefinableMaterial.MaterialPurifier(
        ErosionRegistry.RawRegistry.CASSITERITE_DEPOSIT.getName(),
        () -> ErosionRegistry.Items.CASSITERITE_DEPOSIT.get(),
        () -> List.of(
            ErosionRegistry.Items.RAW_CASSITERITE.get()
        ), BlockEntityRecipeRegistries.MATERIAL_PURIFIER
    );

    public static final RefinableMaterial.Crucible RAW_CASSITERITE = new RefinableMaterial.Crucible(
        ErosionRegistry.RawRegistry.RAW_CASSITERITE.getName(),
        () -> ErosionRegistry.Items.RAW_CASSITERITE.get(),
        () -> List.of(
            ErosionRegistry.Items.TIN_CHUNK.get()
        ), BlockEntityRecipeRegistries.CRUCIBLE, List.of(
            FLUX,DEHYDRATED_BORAX,BORIC_ACID_CRYSTAL
        ), () -> List.of(), List.of()
    );
    public static final RefinableMaterial.Crucible NATIVE_SILVER = new RefinableMaterial.Crucible(
        ErosionRegistry.RawRegistry.NATIVE_SILVER.getName(),
        () -> ErosionRegistry.Items.NATIVE_SILVER.get(),
        () -> List.of(
            ErosionRegistry.Items.SILVER_CHUNK.get()
        ), BlockEntityRecipeRegistries.CRUCIBLE, List.of(
            FLUX,DEHYDRATED_BORAX,BORIC_ACID_CRYSTAL
        ), () -> List.of(), List.of()
    );

    public static final RefinableMaterial NATIVE_SILVER_DEPOSIT = new RefinableMaterial.MaterialPurifier(
        ErosionRegistry.RawRegistry.NATIVE_SILVER_DEPOSIT.getName(),
        () -> ErosionRegistry.Items.NATIVE_SILVER_DEPOSIT.get(),
        () -> List.of(
            ErosionRegistry.Items.NATIVE_SILVER.get()
        ), BlockEntityRecipeRegistries.MATERIAL_PURIFIER
    );

    //turn mined ore into pure ore
    public static final RefinableMaterial.Crucible RAW_BISMUTHINITE = new RefinableMaterial.Crucible(
        ErosionRegistry.RawRegistry.RAW_BISMUTHINITE.getName(),
        () -> ErosionRegistry.Items.RAW_BISMUTHINITE.get(),
        () -> List.of(
            ErosionRegistry.Items.BISMUTH_CHUNK.get()
        ), BlockEntityRecipeRegistries.CRUCIBLE, List.of(
            FLUX, CRUSHED_EGG_SHELL,DEHYDRATED_BORAX,BORIC_ACID_CRYSTAL
        ), () -> List.of(
            ErosionRegistry.Items.SULFUR_SLAG.get()
        ), List.of(
            ErosionRegistry.GasTypes.SULFUR_DIOXIDE
        )
    );

    //turn block into its raw ore if mined with silk touch
    public static final RefinableMaterial BISMUTHINITE_ORE = new RefinableMaterial.MaterialPurifier(
        ErosionRegistry.RawRegistry.BISMUTHINITE_ORE.getName(),
        () -> ErosionRegistry.Items.BISMUTHINITE_ORE.get(),
        () -> List.of(
            ErosionRegistry.Items.RAW_BISMUTHINITE.get()
        ), BlockEntityRecipeRegistries.MATERIAL_PURIFIER
    );

    //turn mined ore into pure ore
    public static final RefinableMaterial RAW_SPHALERITE = new RefinableMaterial.Crucible(
        ErosionRegistry.RawRegistry.RAW_SPHALERITE.getName(),
        () -> ErosionRegistry.Items.RAW_SPHALERITE.get(),
        () -> List.of(
            ErosionRegistry.Items.ZINC_CHUNK.get()
        ), BlockEntityRecipeRegistries.CRUCIBLE, List.of(
            FLUX, CRUSHED_EGG_SHELL,DEHYDRATED_BORAX,BORIC_ACID_CRYSTAL
        ), () -> List.of(), List.of(
            ErosionRegistry.GasTypes.SULFUR_DIOXIDE
        )
    );

    //turn block into its raw ore if mined with silk touch
    public static final RefinableMaterial SPHALERITE_ORE = new RefinableMaterial.MaterialPurifier(
        ErosionRegistry.RawRegistry.SPHALERITE_ORE.getName(),
        () -> ErosionRegistry.Items.SPHALERITE_ORE.get(),
        () -> List.of(
            ErosionRegistry.Items.RAW_SPHALERITE.get()
        ), BlockEntityRecipeRegistries.MATERIAL_PURIFIER
    );

    //turn mined ore into pure ore
    public static final RefinableMaterial.Crucible RAW_AZURITE = new RefinableMaterial.Crucible(
        ErosionRegistry.RawRegistry.RAW_AZURITE.getName(),
        () -> ErosionRegistry.Items.RAW_AZURITE.get(),
        () -> List.of(
            Items.RAW_COPPER
        ), BlockEntityRecipeRegistries.CRUCIBLE, List.of(
            FLUX, CRUSHED_EGG_SHELL,DEHYDRATED_BORAX,BORIC_ACID_CRYSTAL
        ), () -> List.of(), List.of()
    );

    //turn block into its raw ore if mined with silk touch
    public static final RefinableMaterial AZURITE_ORE = new RefinableMaterial.MaterialPurifier(
        ErosionRegistry.RawRegistry.AZURITE_ORE.getName(),
        () -> ErosionRegistry.Items.AZURITE_ORE.get(),
        () -> List.of(
            ErosionRegistry.Items.RAW_AZURITE.get()
        ), BlockEntityRecipeRegistries.MATERIAL_PURIFIER
    );

    //turn mined ore into pure ore
    public static final RefinableMaterial.Crucible RAW_TETRAHEDRITE = new RefinableMaterial.Crucible(
        ErosionRegistry.RawRegistry.RAW_TETRAHEDRITE.getName(),
        () -> ErosionRegistry.Items.RAW_TETRAHEDRITE.get(),
        () -> List.of(
            Items.RAW_COPPER
        ), BlockEntityRecipeRegistries.CRUCIBLE, List.of(
            FLUX, CRUSHED_EGG_SHELL,DEHYDRATED_BORAX,BORIC_ACID_CRYSTAL
        ), () -> List.of(
            ErosionRegistry.Items.SULFUR_SLAG.get(),
            ErosionRegistry.Items.ANTIMONY_SLAG.get()
        ), List.of(
            ErosionRegistry.GasTypes.SULFUR_DIOXIDE
        )
    );

    //turn block into its raw ore if mined with silk touch
    public static final RefinableMaterial TETRAHEDRITE_ORE = new RefinableMaterial.MaterialPurifier(
        ErosionRegistry.RawRegistry.TETRAHEDRITE_ORE.getName(),
        () -> ErosionRegistry.Items.TETRAHEDRITE_ORE.get(),
        () -> List.of(
            ErosionRegistry.Items.RAW_TETRAHEDRITE.get()
        ), BlockEntityRecipeRegistries.MATERIAL_PURIFIER
    );

    //turn mined ore into pure ore
    public static final RefinableMaterial.Crucible RAW_ARSENOPYRITE = new RefinableMaterial.Crucible(
        ErosionRegistry.RawRegistry.RAW_ARSENOPYRITE.getName(),
        () -> ErosionRegistry.Items.RAW_ARSENOPYRITE.get(),
        () -> List.of(
            Items.IRON_NUGGET
        ), List.of(
            FLUX,DEHYDRATED_BORAX,BORIC_ACID_CRYSTAL
        ), () -> List.of(
            ErosionRegistry.Items.SULFUR_SLAG.get()
        ), List.of(
            ErosionRegistry.GasTypes.SULFUR_DIOXIDE,
            ErosionRegistry.GasTypes.ARSENIC_TRIOXIDE
        )
    );

    //turn block into its raw ore if mined with silk touch
    public static final RefinableMaterial ARSENOPYRITE_ORE = new RefinableMaterial.MaterialPurifier(
        ErosionRegistry.RawRegistry.ARSENOPYRITE_ORE.getName(),
        () -> ErosionRegistry.Items.ARSENOPYRITE_ORE.get(),
        () -> List.of(
            ErosionRegistry.Items.RAW_ARSENOPYRITE.get()
        )
    );

    //turn mined ore into pure ore
    public static final RefinableMaterial.Crucible RAW_PYRITE = new RefinableMaterial.Crucible(
        ErosionRegistry.RawRegistry.RAW_PYRITE.getName(),
        () -> ErosionRegistry.Items.RAW_PYRITE.get(),
        () -> List.of(
            Items.IRON_NUGGET
        ), List.of(
            FLUX,DEHYDRATED_BORAX,BORIC_ACID_CRYSTAL
        ), () -> List.of(
            ErosionRegistry.Items.SULFUR_SLAG.get()
        ), List.of(
            ErosionRegistry.GasTypes.SULFUR_DIOXIDE
        )
    );

    //turn block into its raw ore if mined with silk touch
    public static final RefinableMaterial PYRITE_ORE = new RefinableMaterial.MaterialPurifier(
        ErosionRegistry.RawRegistry.PYRITE_ORE.getName(),
        () -> ErosionRegistry.Items.PYRITE_ORE.get(),
        () -> List.of(
            ErosionRegistry.Items.RAW_PYRITE.get()
        )
    );

    public static final RefinableMaterial RUBY_ORE = new RefinableMaterial.MaterialPurifier(
        ErosionRegistry.RawRegistry.RUBY_ORE.getName(),
        () -> ErosionRegistry.Items.RUBY_ORE.get(),
        () -> List.of(
            ErosionRegistry.Items.RUBY.get()
        ), BlockEntityRecipeRegistries.MATERIAL_PURIFIER
    );
    public static final RefinableMaterial SAPPHIRE_ORE = new RefinableMaterial.MaterialPurifier(
        ErosionRegistry.RawRegistry.SAPPHIRE_ORE.getName(),
        () -> ErosionRegistry.Items.SAPPHIRE_ORE.get(),
        () -> List.of(
            ErosionRegistry.Items.SAPPHIRE.get()
        ), BlockEntityRecipeRegistries.MATERIAL_PURIFIER
    );

    public static final RefinableMaterial BORAX = new RefinableMaterial.MaterialPurifier(
        ErosionRegistry.RawRegistry.BORAX.getName(),
        () -> ErosionRegistry.Items.BORAX.get(),
        () -> List.of(
            ErosionRegistry.Items.DEHYDRATED_BORAX.get()
        ), BlockEntityRecipeRegistries.MATERIAL_PURIFIER
    );
    // ========================== CHEMICAL REACTIONS

    public static final ChemicalReaction DIRT_HYDRATION = new ChemicalReaction(
        ErosionRegistry.RawRegistry.ChemicalReactions.DIRT_HYDRATION.getName(),
        () -> List.of(
            Items.DIRT, Items.WATER_BUCKET
        ), () -> List.of(
            Items.MUD, Items.BUCKET
        ),
        () -> List.of(Items.MUD)
    );

    public static final ChemicalReaction SULFURIC_ACID_SYNTHESIS = new ChemicalReaction(
        ErosionRegistry.RawRegistry.ChemicalReactions.SULFURIC_ACID_SYNTHESIS.getName(),
        () -> List.of(
            ErosionRegistry.Items.SULFUR_SLAG.get(),
            Items.WATER_BUCKET
        ), () -> List.of(
            ErosionRegistry.Items.BUCKET_OF_SULFURIC_ACID.get()
        ),
        () -> List.of(ErosionRegistry.Items.BUCKET_OF_SULFURIC_ACID.get())
    );

    public static final ChemicalReaction BORIC_ACID_SYNTHESIS = new ChemicalReaction(
        ErosionRegistry.RawRegistry.ChemicalReactions.BORIC_ACID_SYNTHESIS.getName(),
        () -> List.of(
            ErosionRegistry.Items.BORAX.get(),
            ErosionRegistry.Items.BUCKET_OF_SULFURIC_ACID.get()
        ), () -> List.of(
            ErosionRegistry.Items.BORIC_ACID_CRYSTAL.get(),
            Items.BUCKET
        ), () -> List.of(ErosionRegistry.Items.BORIC_ACID_CRYSTAL.get())
    );

    public static final ChemicalReaction ANHYDROUS_BORAX_HYDRATION = new ChemicalReaction(
        ErosionRegistry.RawRegistry.ChemicalReactions.ANHYDROUS_BORAX_HYDRATION.getName(),
        () -> List.of(
            Items.WATER_BUCKET, ErosionRegistry.Items.DEHYDRATED_BORAX.get()
        ), () -> List.of(
            ErosionRegistry.Items.BORAX.get(),
            Items.BUCKET
        ), () -> List.of(ErosionRegistry.Items.BORAX.get())
    );

    // ========================== REGISTRY

    private static final List<AlterableMaterial> ALTERABLE_MATERIALS_LIST_ORIGINAL = List.of(
        GRASS_BLOCK, DIRT, SAND, COARSE_DIRT,
        STONE, DEEPSLATE, GRANITE, DIORITE,
        TUFF, CALCITE, GRAVEL, MUD, ANDESITE,
        COBBLESTONE
    );
    private static final List<RefinableMaterial> REFINABLE_MATERIALS_LIST_ORIGINAL = List.of(
        KAOLINIZED_GRANITE, QUARTZ_GRAVEL, ALBITIZED_GRANITE,
        PROPYLITIZED_DIORITE, RAW_LIMONITE, RAW_HEMATITE,
        RAW_MAGNETITE, RAW_MALACHITE, NATIVE_GOLD, NATIVE_GOLD_DEPOSIT,
        CALCITE_MALACHITE_ORE, MAGNETITE_ORE, HEMATITE_ORE, LIMONITE_ORE,
        CASSITERITE_DEPOSIT, RAW_CASSITERITE, NATIVE_SILVER,
        NATIVE_SILVER_DEPOSIT, RAW_BISMUTHINITE, BISMUTHINITE_ORE,
        RAW_SPHALERITE, SPHALERITE_ORE, RAW_AZURITE, AZURITE_ORE,
        RAW_TETRAHEDRITE, TETRAHEDRITE_ORE, RUBY_ORE, SAPPHIRE_ORE,
        BORAX, CRACKED_STONE, ARSENOPYRITE_ORE, RAW_ARSENOPYRITE,
        PYRITE_ORE, RAW_PYRITE
    );
    private static final List<CrucibleCatalyst> CRUCIBLE_CATALYST_LIST_ORIGINAL = List.of(
        FLUX, CRUSHED_EGG_SHELL, DEHYDRATED_BORAX,BORIC_ACID_CRYSTAL
    );

    private static final List<ChemicalReaction> CHEMICAL_REACTION_LIST_ORIGINAL = List.of(
        DIRT_HYDRATION, BORIC_ACID_SYNTHESIS, SULFURIC_ACID_SYNTHESIS,
        ANHYDROUS_BORAX_HYDRATION
    );

    private static final List<RefinableMaterial> REFINABLE_MATERIALS_LIST = new ArrayList<>();
    private static final List<AlterableMaterial> ALTERABLE_MATERIALS_LIST = new ArrayList<>();
    private static final List<CrucibleCatalyst> CRUCIBLE_CATALYST_LIST = new ArrayList<>();
    private static final List<ChemicalReaction> CHEMICAL_REACTION_LIST = new ArrayList<>();

    public static void add(RefinableMaterial e)
    {
        REFINABLE_MATERIALS_LIST.add(e);
    }
    public static void add(AlterableMaterial e)
    {
        ALTERABLE_MATERIALS_LIST.add(e);
    }
    public static void add(CrucibleCatalyst e)
    {
        CRUCIBLE_CATALYST_LIST.add(e);
    }
    public static void add(ChemicalReaction e)
    {
        CHEMICAL_REACTION_LIST.add(e);
    }

    public static final List<ChemicalReaction> getChemicalReactions()
    {
        return Collections.unmodifiableList(CHEMICAL_REACTION_LIST);
    }

    // =====================================

    @ErosionEvents.ErosionEventSubscribe
    public static void BE(ErosionEvents.ErosionBlockEntityRecipeRegistration e)
    {
        ErosionUtils.Log("Event called -> " + e.getClass().getName());
        
        return;
    }

    public static void Load()
    {
        PENDING.clear();
        PENDING_FAST.clear();

        PERFORMED = 0;
        PERFORMED_FAST = 0;

        REFINABLE_MATERIALS_LIST.clear();
        ALTERABLE_MATERIALS_LIST.clear();
        CRUCIBLE_CATALYST_LIST.clear();
        CHEMICAL_REACTION_LIST.clear();

        REFINABLE_MATERIALS_LIST.addAll(REFINABLE_MATERIALS_LIST_ORIGINAL);
        ALTERABLE_MATERIALS_LIST.addAll(ALTERABLE_MATERIALS_LIST_ORIGINAL);
        CRUCIBLE_CATALYST_LIST.addAll(CRUCIBLE_CATALYST_LIST_ORIGINAL);
        CHEMICAL_REACTION_LIST.addAll(CHEMICAL_REACTION_LIST_ORIGINAL);

        for(int i = 0; i < ErosionModCompat.COMPATIBLE_MODS.size(); ++i)
        {
            var m = ErosionModCompat.COMPATIBLE_MODS.get(i);
            m.setupCompat();
        }

        ErosionEventBus.ErosionEventInvocation.CALL_EVENT_LISTENERS(
            new ErosionEvents.ErosionModLoading()
        );

        ErosionEventBus.ErosionEventInvocation.CALL_EVENT_LISTENERS(
            new ErosionEvents.ErosionBlockEntityRecipeRegistration()
        );

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

        for(int i = 0; i < CHEMICAL_REACTION_LIST.size(); ++i)
        {
            var m = CHEMICAL_REACTION_LIST.get(i);
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

        for(int i = 0; i < CHEMICAL_REACTION_LIST.size(); ++i)
        {
            var m = CHEMICAL_REACTION_LIST.get(i);
            m.discard();
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
        else if(currentItem == ErosionRegistry.Items.CRUCIBLE.get())
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
        else if(currentItem == ErosionRegistry.Items.DEBRIS.get())
        {
            desc.add(
                Component.literal("Product of a failed material modification process")
                .withStyle(ChatFormatting.DARK_RED)
            );
        }
        else if(currentItem == ErosionRegistry.Items.CHEMICAL_REACTOR.get())
        {
            desc.add(
                Component.literal("A versatile container designed to safely sustain chemical reactions.")
                .withStyle(ChatFormatting.DARK_PURPLE)
            );
        }

        if(currentItem instanceof ErosionSimpleItems.GasMask git)
        {
            desc.add(
                Component.literal("A head accessory designed to protect the wearer from toxic gases emitted during melting items in a crucible.")
                .withStyle(ChatFormatting.DARK_GREEN, ChatFormatting.BOLD)
            );

            Component qc = switch (git.getQuality()) {
                case HIGH -> Component.literal("High").withStyle(ChatFormatting.DARK_PURPLE);
                case MEDIUM -> Component.literal("Medium").withStyle(ChatFormatting.BLUE);
                case LOW -> Component.literal("Low").withStyle(ChatFormatting.DARK_RED);
            };

            desc.add(
                Component.literal("- Quality: ").withStyle(ChatFormatting.DARK_AQUA)
                .append(qc)
            );
        }
        
        final class ChemicalInfo
        {
            private List<String> info;

            public ChemicalInfo(List<String> l)
            {
                this.info = l;
            }

            public ChemicalInfo()
            {
                this.info = List.of(
                    "No geochemical information"
                );
            }

            public List<String> getInfo()
            {
                return this.info;
            }
        }

        final Map<Item, ChemicalInfo> CHEMICAL_ITEM_INFO = Map.ofEntries(
            //ванила ајтеми хаахахаха
            Map.entry(Items.RAW_COPPER, new ChemicalInfo(List.of(
                "Elemental copper chunk (Cu)"
            ))),
            Map.entry(Items.IRON_NUGGET, new ChemicalInfo(List.of(
                "Elemental iron chunk (Fe)"
            ))),
            Map.entry(Items.GOLD_NUGGET, new ChemicalInfo(List.of(
                "Elemental gold chunk (Au)"
            ))),
            //мод ајтемс
            Map.entry(ErosionRegistry.Items.BORAX.get(), new ChemicalInfo(List.of(
                "Sodium-tetraborate decahydrate"
            ))),
            Map.entry(ErosionRegistry.Items.BORIC_ACID_CRYSTAL.get(), new ChemicalInfo(List.of(
                "Crystallised boric acid"
            ))),
            Map.entry(ErosionRegistry.Items.DEHYDRATED_BORAX.get(), new ChemicalInfo(List.of(
                "Anhydrous sodium-tetraborate"
            ))),
            Map.entry(ErosionRegistry.Items.RAW_MAGNETITE.get(), new ChemicalInfo(List.of(
                "Iron(II, III)-oxide"
            ))),
            Map.entry(ErosionRegistry.Items.RAW_HEMATITE.get(), new ChemicalInfo(List.of(
                "Iron(III)-oxide"
            ))),
            Map.entry(ErosionRegistry.Items.RAW_LIMONITE.get(), new ChemicalInfo(List.of(
                "Hydrated iron(III) oxide-hydroxide"
            ))),
            Map.entry(ErosionRegistry.Items.RAW_MALACHITE.get(), new ChemicalInfo(List.of(
                "Copper(II) carbonate-hydroxide",
                "Basic copper(II)-carbonate"
            ))),
            Map.entry(ErosionRegistry.Items.RAW_AZURITE.get(), new ChemicalInfo(List.of(
                "Basic copper(II)-carbonate",
                "Dicopper(II)-carbonate dihydroxi-copper(II)-carbonate"
            ))),
            Map.entry(ErosionRegistry.Items.RAW_TETRAHEDRITE.get(), new ChemicalInfo(List.of(
                "Copper-antimony thioantimonite",
                "Copper-antimony sulfide"
            ))),
            Map.entry(ErosionRegistry.Items.RAW_ARSENOPYRITE.get(), new ChemicalInfo(List.of(
                "Iron arsenic sulfide",
                "Iron(III)-sulfoarsenide"
            ))),
            Map.entry(ErosionRegistry.Items.RAW_PYRITE.get(), new ChemicalInfo(List.of(
                "Iron(II)-sulfide",
                "Also known as \"fool's gold\""
            ))),
            Map.entry(ErosionRegistry.Items.RAW_CASSITERITE.get(), new ChemicalInfo(List.of(
                "Tin(IV)-oxide"
            ))),
            Map.entry(ErosionRegistry.Items.RAW_SPHALERITE.get(), new ChemicalInfo(List.of(
                "Zinc-sulfide"
            ))),
            Map.entry(ErosionRegistry.Items.RAW_BISMUTHINITE.get(), new ChemicalInfo(List.of(
                "Bismuth(II)-sulfide"
            ))),
            Map.entry(ErosionRegistry.Items.FLUX.get(), new ChemicalInfo(List.of(
                "Natural silicate flux rich in alkali and alkaline earth metal oxides"
            ))),
            Map.entry(ErosionRegistry.Items.CRUSHED_EGG_SHELL.get(), new ChemicalInfo(List.of(
                "Rich in calcium-carbonate"
            ))),
            Map.entry(ErosionRegistry.Items.FELDSPAR_POWDER.get(), new ChemicalInfo(List.of(
                "Potassium-sodium-aluminum silicate",
                "May contain traces of calcium"
            ))),
            Map.entry(ErosionRegistry.Items.SULFUR_SLAG.get(), new ChemicalInfo(List.of(
                "Iron sulfide matrix rich in sulfur impurities",
                "Industrial metallurgical byproduct"
            ))),
            Map.entry(ErosionRegistry.Items.ANTIMONY_SLAG.get(), new ChemicalInfo(List.of(
                "Contains antimonic oxides",
                "Industrial metallurgical byproduct"
            ))),
            Map.entry(ErosionRegistry.Items.RUBY.get(), new ChemicalInfo(List.of(
                "Aluminium-oxide",
                "Chromium-doped corundum"
            ))),
            Map.entry(ErosionRegistry.Items.SAPPHIRE.get(), new ChemicalInfo(List.of(
                "Aluminium-oxide",
                "Titanium and iron-doped corundum"
            ))),
            Map.entry(ErosionRegistry.Items.NATIVE_GOLD.get(), new ChemicalInfo(List.of(
                "Native elemental gold (Au)",
                "Contains impurities"
            ))),
            Map.entry(ErosionRegistry.Items.NATIVE_SILVER.get(), new ChemicalInfo(List.of(
                "Native elemental silver (Ag)",
                "Contains impurities"
            ))),
            Map.entry(ErosionRegistry.Items.TIN_CHUNK.get(), new ChemicalInfo(List.of(
                "Elemental tin chunk (Sn)"
            ))),
            Map.entry(ErosionRegistry.Items.BISMUTH_CHUNK.get(), new ChemicalInfo(List.of(
                "Elemental bismuth chunk (Bi)"
            ))),
            Map.entry(ErosionRegistry.Items.ZINC_CHUNK.get(), new ChemicalInfo(List.of(
                "Elemental zinc chunk (Zn)"
            )))
        );

        for(var h : CHEMICAL_ITEM_INFO.entrySet())
        {
            var it = h.getKey();
            var ci = h.getValue();

            if(currentItem == it)
            {
                for(var op : ci.getInfo()) desc.add(
                    Component.literal(op)
                    .withStyle(ChatFormatting.BOLD, ChatFormatting.DARK_AQUA)
                );
            }
        }

        for(var c : CRUCIBLE_CATALYST_LIST)
        {
            if(currentItem == c.catalystItem)
            {
                desc.add(Component.literal(""));
                desc.add(
                    Component.literal("Used as a crucible catalyst").withStyle(ChatFormatting.GOLD, ChatFormatting.UNDERLINE)
                );
                Integer sr = CrucibleCatalyst.getCatalystSuccessRate(currentItem);
                Integer cteg = 100 - sr;
                desc.add(
                    Component.literal("- Has ").withStyle(ChatFormatting.GRAY)
                    .append(Component.literal(sr.toString() + "%")
                    .withStyle(CrucibleCatalyst.getSRColor(sr)))
                    .append(Component.literal(" success rate.").withStyle(ChatFormatting.GRAY))
                );
                desc.add(
                    Component.literal("- Has ").withStyle(ChatFormatting.GRAY)
                    .append(Component.literal(cteg.toString() + "%")
                    .withStyle(CrucibleCatalyst.getSRColor(cteg)))
                    .append(Component.literal(" chance to emit coproduct gases.").withStyle(ChatFormatting.GRAY))
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
        for(var m : ALTERABLE_MATERIALS_LIST)
        {
            if(m.materialItem == currentItem)
            {
                for(int i = 0; i < m.paths.size(); i++)
                {
                    var p = m.paths.get(i);
                    StringBuilder ruleNames = new StringBuilder();
                    List<String> productNames = new ArrayList<>();

                    var ll = p.rules.rules;
                    for(int j = 0; j < ll.size(); j++)
                    {
                        ruleNames.append(ll.get(j).name);
                        if(j < ll.size() - 1)
                        {
                            if(j == ll.size() - 2) ruleNames.append(" combined with ");
                            else ruleNames.append(", ");
                        }
                    }

                    for(int j = 0; j < p.productItem.size(); j++)
                    {
                        productNames.add(p.productItem.get(j).getDescription().getString());
                    }
                    
                    alterationInfo.add(new AlterationInfo(ruleNames.toString(), productNames));
                }
            }
        }

        List<String> refinesIntoNames = new java.util.ArrayList<>();
        for(var m : REFINABLE_MATERIALS_LIST)
        {
            if(m.recipeCategory == BlockEntityRecipeRegistries.MATERIAL_PURIFIER) if(m.materialItem == currentItem)
            {
                for (Item prodItem : m.productItem)
                { 
                    refinesIntoNames.add(prodItem.getDescription().getString());
                }
            }
        }

        List<String> meltsIntoNames = new ArrayList<>();
        List<String> catalystListLmao = new ArrayList<>();
        List<String> coproductOfNames = new ArrayList<>();
        Boolean hasCoproducts = false;
        List<GasType> emitsGases = new ArrayList<>();
        for(var m : REFINABLE_MATERIALS_LIST)
        {
            if(m.recipeCategory == BlockEntityRecipeRegistries.CRUCIBLE)
            {
                for(var cpl : m.coproductItem)
                {
                    if(cpl == currentItem)
                    {
                        coproductOfNames.add(m.materialItem.getDescription().getString());
                    }
                }
                if(m.materialItem == currentItem)
                {
                    for(var c : m.catalyst)
                    {
                        catalystListLmao.add(c.name);
                    }
                    for(Item prodItem : m.productItem)
                    { 
                        meltsIntoNames.add(prodItem.getDescription().getString());
                    }
                    if(!m.emittedGases.isEmpty()) emitsGases = m.emittedGases;
                    if(!m.coproductItem.isEmpty())
                    {
                        hasCoproducts = true;
                    }
                }
            }
        }

        final class SynthFromData
        {
            public String reactionName;
            public List<String> from;

            public SynthFromData(String n, List<String> f)
            {
                this.reactionName = n;
                this.from = f;
            }
        }
    
        List<SynthFromData> synthFrom = new ArrayList<>();
        List<String> usedIn = new ArrayList<>();
        for(var p : CHEMICAL_REACTION_LIST)
        {
            if(p.reactantItems.contains(currentItem))
            {
                usedIn.add(p.name);
            }
            if(p.mainProductItems.contains(currentItem))
            {
                var l = new ArrayList<String>();
                for(var k : p.reactantItems)
                {
                    l.add(k.getDescription().getString());
                }
                synthFrom.add(new SynthFromData(p.name, l));
            }
        }

        final String TAB = "  * ";

        if(
            !(usedIn.isEmpty()) ||
            !(synthFrom.isEmpty())
        )
        {
            desc.add(Component.literal(""));
            desc.add(
                Component.literal("Chemical reactor information").
                withStyle(ChatFormatting.DARK_GREEN, ChatFormatting.UNDERLINE)
            );
        }
        if(!usedIn.isEmpty())
        {
            desc.add(
                Component.literal("- Used as a reactant in following chemical reactions:")
                .withStyle(ChatFormatting.GRAY)
            );
            for(var s : usedIn)
            {
                desc.add(
                    Component.literal(TAB).withStyle(ChatFormatting.GRAY)
                    .append(
                        Component.literal(s).withStyle(ChatFormatting.BLUE, ChatFormatting.BOLD)
                    )
                );
            }
        }
        if(!synthFrom.isEmpty())
        {
            desc.add(
                Component.literal("- Can be obtained in following chemical reactions:")
                .withStyle(ChatFormatting.GRAY)
            );
            for(var s : synthFrom)
            {
                desc.add(
                    Component.literal(TAB).withStyle(ChatFormatting.GRAY)
                    .append(
                        Component.literal(s.reactionName).withStyle(ChatFormatting.YELLOW)
                    ).append(
                        Component.literal(" using:").withStyle(ChatFormatting.GRAY)
                    )
                );
                String v = new String("      ");
                var ll = s.from;
                for(int i = 0; i < ll.size(); i++)
                {
                    v += ll.get(i);
                    if(i < ll.size() - 1)
                    {
                        if(i == ll.size() - 2) v += " and ";
                        else v += ", ";
                    }
                }
                desc.add(
                    Component.literal(v).withStyle(ChatFormatting.YELLOW)
                );
            }
        }

        if(!coproductOfNames.isEmpty())
        {
            desc.add(Component.literal(""));
            desc.add(Component.literal("Coproduct of crucible melting").withStyle(ChatFormatting.DARK_AQUA, ChatFormatting.UNDERLINE));
            List<Component> list = new ArrayList<>();
            for(int i = 0; i < coproductOfNames.size(); i++)
            {
                list.add(
                    Component.literal(TAB).withStyle(ChatFormatting.GRAY)
                    .append(
                        Component.literal(coproductOfNames.get(i)).withStyle(ChatFormatting.DARK_GREEN, ChatFormatting.ITALIC)
                    )
                );
            }
            desc.add(
                Component.literal("- Can be obtained by crucible-melting: ").withStyle(ChatFormatting.GRAY)
            );
            for(int i = 0; i < list.size(); i++)
            {
                desc.add(list.get(i));
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
            MutableComponent catalysts = Component.literal("None");
            if(!catalystListLmao.isEmpty())
            {
                catalysts = Component.literal("");
            }
            for(int i = 0; i < catalystListLmao.size(); i++)
            {
                var s = catalystListLmao.get(i);
                catalysts.append(
                    Component.literal(s).withStyle(ChatFormatting.DARK_PURPLE)
                );
                if(!(i + 1 >= catalystListLmao.size()))
                {
                    catalysts.append(Component.literal(", ").withStyle(ChatFormatting.GRAY));
                }
            }
            desc.add(
                Component.literal("- Compatible catalyst(s): ").withStyle(ChatFormatting.GRAY)
                .append(
                    catalysts
                )
            );
            if(hasCoproducts)
            {
                desc.add(
                    Component.literal("- Has potential solid coproduct(s)!")
                    .withStyle(ChatFormatting.GRAY)
                );
            }
            if(!emitsGases.isEmpty())
            {
                desc.add(
                    Component.literal("- Melting this material emits potential gas coproduct(s): ")
                    .withStyle(ChatFormatting.GRAY)
                );
                for(var y : emitsGases)
                {
                    desc.add(
                        Component.literal("  * ").withStyle(ChatFormatting.GRAY)
                        .append(
                            Component.literal(y.name).withStyle(ChatFormatting.BOLD)
                            .withStyle(
                                y.isToxic() ?
                                ChatFormatting.DARK_RED :
                                ChatFormatting.GRAY
                            )
                        )
                    );
                }
            }
        }

        List<String> madeByErodingNames = new ArrayList<>();
        for(AlterableMaterial m : ALTERABLE_MATERIALS_LIST)
        {
            for(int i = 0; i < m.paths.size(); i++)
            {
                var p = m.paths.get(i);
                if(p.productItem.contains(currentItem))
                {
                    String f = m.materialItem.getDescription().getString();
                    if(!madeByErodingNames.contains(f)) madeByErodingNames.add(f);
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

        var l = new ArrayList<Component>();
        var e = new ErosionEvents.ErosionItemDescription(currentItem, l);
        ErosionEventBus.ErosionEventInvocation.CALL_EVENT_LISTENERS(e);
        desc.addAll(l);

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

        if(Pending.contains(pos.asLong()))
        {
            //processPendingCore(level, ErosionMod.RANDOM.nextInt(ErosionConfig.MAX_EROSIONS_PER_TICK), priority);
            return;
        }
        BlockState state = level.getBlockState(pos);

        if(!isAlterable(state)) return;
        var l = ALTERATION_INVERTED.get(state.getBlock());
        var ALTERATION_PATHS = new ArrayList<>(l.paths);

        Collections.shuffle(ALTERATION_PATHS);
        for(var p : ALTERATION_PATHS)
        {
            if(p.rules.checkIfAllConditionsAreMet(level, pos))
            {
                if(Pending.size() >= (priority ? ErosionConfig.MAX_PENDING_FAST_SIZE : ErosionConfig.MAX_PENDING_SIZE))
                {
                    Pending.remove(0);
                }
                Pending.add(pos, p.product.get(ErosionMod.RANDOM.nextInt(p.product.size())));
                if(ErosionConfig.isDebugOn()) ErosionUtils.Log("Added candidate: " + pos);
                return;
            }
        }
        return;
    }
    
    public static void addCandidate(ServerLevel l, BlockPos p)
    {
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
            gpos = Pending.getAndRemove(i);
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

    public static boolean highPressure(ServerLevel l, BlockPos p)
    {
        //simple as that lmao
        if(p.getY() < 20) return true;
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

    public static class BlockEntityRecipes
    {
        public static class MaterialPurifier
        {
            public static Map<Item, List<Item>> RECIPES = new HashMap<>();
        }
        public static class Crucible
        {
            public static Map<Item, List<Item>> RECIPES = new HashMap<>();
            public static Map<Item, List<Item>> CATALYSTS = new HashMap<>();
            public static Map<Item, List<Item>> COPRODUCTS = new HashMap<>();
            public static Map<Item, List<GasType>> EMITTED_GASES = new HashMap<>();
        }
        public static class ChemicalReactor
        {
            //nvm
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