package co.bracesoftware.erosion.blocks.crucible;

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

public class CrucibleBlockEntity extends BlockEntity
{
    public ItemStack catalyst = ItemStack.EMPTY;
    public boolean working = false;
    public boolean finished = true;
    public ItemStack storedItem = ItemStack.EMPTY;
    public int progress = 0;

    public static class DataRawName
    {
        public static final String CATALYST = "catalyst";
        public static final String WORKING = "working";
        public static final String FINISHED = "finished";
        public static final String STORED_ITEM = "stored";
        public static final String PROGRESS = "progress"; 
        public static final String HEAT = "heat"; 
    }

    public CrucibleBlockEntity(BlockPos pos, BlockState state)
    {
        super(ErosionRegistry.BlockEntities.CRUCIBLE.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, CrucibleBlockEntity be)
    {
        if(level.isClientSide()) return;

        int calculatedHeat = Math.min(ErosionConfig.CRUCIBLE_SECONDS, be.progress / 20);
        int currentHeat = state.getValue(CrucibleBlock.HEAT);

        if(currentHeat != calculatedHeat)
        {
            level.setBlock(pos, state.setValue(CrucibleBlock.HEAT, calculatedHeat), Block.UPDATE_CLIENTS);
        }


        if(be.working)
        {
            if(ErosionConfig.CRUCIBLE_SECONDS < 1)
            {
                throw new RuntimeException("Invalid `ErosionConfig.CRUCIBLE_SECONDS` value; must be 1 or bigger.");
            }
            be.progress++;
            if(be.progress >= 20 * ErosionConfig.CRUCIBLE_SECONDS)
            {
                be.working = false;
                be.finished = true;
                be.progress = 0;

                var l = ErosionCore.BlockEntityRecipes.Crucible.RECIPES.get(be.storedItem.getItem());
                be.storedItem = new ItemStack(l.get(ErosionMod.RANDOM.nextInt(l.size())));

                be.catalyst = ItemStack.EMPTY;

                level.getLightEngine().checkBlock(pos);
                level.setBlock(pos,
                    state.setValue(
                        CrucibleBlock.FINISHED, be.finished
                    ).setValue(
                        CrucibleBlock.WORKING, be.working
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
        t.putBoolean(DataRawName.WORKING, working);
        t.putBoolean(DataRawName.FINISHED, finished);
        t.putInt(DataRawName.PROGRESS, progress);
        if(!this.storedItem.isEmpty())
        {
            t.put(DataRawName.STORED_ITEM, this.storedItem.save(r));
        }
        if(!this.catalyst.isEmpty())
        {
            t.put(DataRawName.CATALYST, this.catalyst.save(r));
        }
        return;
    }

    @Override 
    public void loadAdditional(CompoundTag t, HolderLookup.Provider r)
    {
        super.loadAdditional(t, r);
        this.working = t.getBoolean(DataRawName.WORKING);
        this.finished = t.getBoolean(DataRawName.FINISHED);
        this.progress = t.getInt(DataRawName.PROGRESS);
        if(t.contains(DataRawName.STORED_ITEM))
        {
            this.storedItem = ItemStack.parse(r, t.getCompound(DataRawName.STORED_ITEM)).orElse(ItemStack.EMPTY);
        }
        else this.storedItem = ItemStack.EMPTY;

        if(t.contains(DataRawName.CATALYST))
        {
            this.catalyst = ItemStack.parse(r, t.getCompound(DataRawName.CATALYST)).orElse(ItemStack.EMPTY);
        }
        else this.catalyst = ItemStack.EMPTY;
        return;
    }
}