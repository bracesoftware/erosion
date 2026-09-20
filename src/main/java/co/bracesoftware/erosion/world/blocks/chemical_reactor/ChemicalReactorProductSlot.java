package co.bracesoftware.erosion.world.blocks.chemical_reactor;

import co.bracesoftware.erosion.world.blocks.chemical_reactor.ChemicalReactorSystemCore.ChemicalReactorSystemComponent;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ChemicalReactorProductSlot extends Slot implements ChemicalReactorSystemComponent
{
    private final Runnable watToDo;

    public ChemicalReactorProductSlot(
        Container container, int slotIndex, int x, int y,
        Runnable onTakeAction
    )
    {
        super(container, slotIndex, x, y);
        this.watToDo = onTakeAction;
    }

    @Override
    public boolean mayPlace(ItemStack stack)
    {
        return false;
    }

    @Override
    public void onTake(Player player, ItemStack stack)
    {
        if(player.level().isClientSide()) return;
        this.watToDo.run();
        super.onTake(player, stack);
    }
}