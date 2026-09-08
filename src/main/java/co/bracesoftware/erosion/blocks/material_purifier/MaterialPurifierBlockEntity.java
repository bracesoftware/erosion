package co.bracesoftware.erosion.blocks.material_purifier;

import java.util.List;

import javax.annotation.Nullable;

import co.bracesoftware.erosion.ErosionConfig;
import co.bracesoftware.erosion.ErosionCore;
import co.bracesoftware.erosion.ErosionMod;
import co.bracesoftware.erosion.ErosionUtils;
import co.bracesoftware.erosion.blocks.ErosionRegistry;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.ReloadableServerRegistries.Holder;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.NotNull;

public class MaterialPurifierBlockEntity extends BlockEntity
{
    public int fuel = 0;
    public boolean working = false;
    public boolean finished = true;
    public ItemStack storedItem = ItemStack.EMPTY;
    public int progress = 0;

    public static class DataRawName
    {
        public static final String FUEL = "fuel";
        public static final String WORKING = "working";
        public static final String FINISHED = "finished";
        public static final String STORED_ITEM = "stored";
        public static final String PROGRESS = "progress"; 
    }

    public MaterialPurifierBlockEntity(BlockPos pos, BlockState state)
    {
        super(ErosionRegistry.BlockEntities.MATERIAL_PURIFIER.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, MaterialPurifierBlockEntity be)
    {
        if(level.isClientSide()) return;

        AABB s = new AABB(pos).inflate(0.5, 1.0, 0.5);
        List<ItemEntity> i = level.getEntitiesOfClass(ItemEntity.class, s);

        if(ErosionConfig.isDebugOn()) if(!i.isEmpty()) {
            System.out.println("Items: " + i.size());
            System.out.println("Item: " + i.get(0).getItem().getItem());
            System.out.println("Map keys: " + ErosionCore.BlockEntityRecipes.MaterialPurifier.RECIPES.keySet());
            System.out.println("Working: " + be.working + " | Finished: " + be.finished);
        }

        if(level.hasNeighborSignal(pos))
        {
            if(be.fuel < ErosionConfig.MAX_PURIFIER_FUEL)
            {
                be.fuel = ErosionConfig.MAX_PURIFIER_FUEL;

                if(!be.finished && !be.working && !be.storedItem.isEmpty())
                {
                    be.fuel--;
                    be.working = true;
                }

                be.setChanged();
                level.setBlock(pos, state.setValue(MaterialPurifierBlock.FUEL, be.fuel)
                    .setValue(MaterialPurifierBlock.FINISHED, be.finished)
                    .setValue(MaterialPurifierBlock.WORKING, be.working),
                    Block.UPDATE_ALL
                );
            }
        }

        if (!be.working && be.finished && be.storedItem.isEmpty())
        {    
            AABB searchBox = s;
            List<ItemEntity> items = i;

            for(ItemEntity itemEntity : items)
            {
                ItemStack stack = itemEntity.getItem();
                if(ErosionCore.BlockEntityRecipes.MaterialPurifier.RECIPES.containsKey(stack.getItem()))
                {
                    ItemStack singleItem = stack.split(1);
                    be.storedItem = singleItem;
                    be.finished = false;

                    if(stack.isEmpty())
                    {
                        itemEntity.discard();
                    }

                    if(be.fuel > 0)
                    {
                        be.fuel--;
                        be.working = true;
                    }

                    be.setChanged();
                    level.setBlock(pos, state.setValue(MaterialPurifierBlock.FUEL, be.fuel)
                        .setValue(MaterialPurifierBlock.FINISHED, be.finished)
                        .setValue(MaterialPurifierBlock.WORKING, be.working),
                        Block.UPDATE_ALL
                    );
                    break;
                }
            }
        }

        if(be.working)
        {
            if(ErosionConfig.PURIFIER_SECONDS < 1)
            {
                throw new RuntimeException("Invalid `ErosionConfig.PURIFIER_SECONDS` value; must be 1 or bigger.");
            }
            be.progress++;
            if(be.progress >= 20 * ErosionConfig.PURIFIER_SECONDS)
            {
                be.working = false;
                be.finished = true;
                be.progress = 0;

                var l = ErosionCore.BlockEntityRecipes.MaterialPurifier.RECIPES.get(be.storedItem.getItem());
                be.storedItem = new ItemStack(l.get(ErosionMod.RANDOM.nextInt(l.size())));

                level.setBlock(pos,
                    state.setValue(
                        MaterialPurifierBlock.FUEL, be.fuel
                    ).setValue(
                        MaterialPurifierBlock.FINISHED, be.finished
                    ).setValue(
                        MaterialPurifierBlock.WORKING, be.working
                    ),
                    Block.UPDATE_ALL
                );

                be.setChanged();
            }
        }
        return;
    }

    @Override 
    protected void saveAdditional(CompoundTag t, HolderLookup.Provider r)
    {
        super.saveAdditional(t, r);
        t.putInt(DataRawName.FUEL, fuel);
        t.putBoolean(DataRawName.WORKING, working);
        t.putBoolean(DataRawName.FINISHED, finished);
        t.putInt(DataRawName.PROGRESS, progress);
        if(!this.storedItem.isEmpty())
        {
            t.put(DataRawName.STORED_ITEM, this.storedItem.save(r));
        }
        return;
    }

    @Override 
    public void loadAdditional(CompoundTag t, HolderLookup.Provider r)
    {
        super.loadAdditional(t, r);
        this.fuel = t.getInt(DataRawName.FUEL);
        this.working = t.getBoolean(DataRawName.WORKING);
        this.finished = t.getBoolean(DataRawName.FINISHED);
        this.progress = t.getInt(DataRawName.PROGRESS);
        if(t.contains(DataRawName.STORED_ITEM))
        {
            this.storedItem = ItemStack.parse(r, t.getCompound(DataRawName.STORED_ITEM)).orElse(ItemStack.EMPTY);
        }
        else this.storedItem = ItemStack.EMPTY;
        return;
    }
    // =================================== //
    private final IItemHandler itemHandler = new IItemHandler() {
        @Override
        public int getSlots()
        {
            return 1;
        }

        @Override
        public @NotNull ItemStack getStackInSlot(int slot)
        {
            return storedItem;
        }

        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate)
        {
            if (storedItem.isEmpty() && !working && finished && !stack.isEmpty())
            {
                if(ErosionCore.BlockEntityRecipes.MaterialPurifier.RECIPES.containsKey(stack.getItem()))
                {
                    if (!simulate)
                    {
                        storedItem = stack.split(1);
                        finished = false;

                        if(fuel > 0)
                        {
                            fuel--;
                            working = true;
                        }

                        setChanged();
                        if(level != null)
                        {
                            level.setBlock(getBlockPos(), getBlockState()
                                    .setValue(MaterialPurifierBlock.FUEL, fuel)
                                    .setValue(MaterialPurifierBlock.FINISHED, finished), Block.UPDATE_ALL);
                        }
                    }
                    else
                    {
                        ItemStack remainder = stack.copy();
                        remainder.shrink(1);
                        return remainder;
                    }
                }
            }
            return stack;
        }

        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate)
        {
            if(finished && !storedItem.isEmpty())
            {
                int count = Math.min(amount, storedItem.getCount());
                ItemStack extracted = storedItem.copyWithCount(count);
                if(!simulate)
                {
                    storedItem.shrink(count);
                    if (storedItem.isEmpty())
                    {
                        storedItem = ItemStack.EMPTY;
                    }
                    setChanged();
                }
                return extracted;
            }
            return ItemStack.EMPTY;
        }

        @Override
        public int getSlotLimit(int slot)
        {
            return 1;
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack)
        {
            return ErosionCore.BlockEntityRecipes.MaterialPurifier.RECIPES.containsKey(stack.getItem());
        }
    };

    public IItemHandler getItemHandler(@Nullable Direction side)
    {
        return itemHandler;
    }
}