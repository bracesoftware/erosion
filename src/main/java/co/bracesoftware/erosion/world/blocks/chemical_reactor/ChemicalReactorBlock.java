package co.bracesoftware.erosion.world.blocks.chemical_reactor;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class ChemicalReactorBlock extends Block
{
    public ChemicalReactorBlock(Properties p)
    {
        super(p);
    }

    @Override 
    protected InteractionResult useWithoutItem(
        BlockState bs, Level l, BlockPos bp,
        Player p, BlockHitResult hr
    )
    {
        if(!l.isClientSide())
        {
            p.openMenu(
                new SimpleMenuProvider(
                    (cid, pinv, pid) -> new ChemicalReactorMenu(cid, pinv),
                    Component.literal("Chemical Reactor")
                )
            );
        }
        return InteractionResult.sidedSuccess(l.isClientSide());
    }
}