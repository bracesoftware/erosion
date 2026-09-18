package co.bracesoftware.erosion.world.blocks.chemical_reactor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import co.bracesoftware.erosion.ErosionCore;
import co.bracesoftware.erosion.world.ErosionRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ChemicalReactorMenu extends AbstractContainerMenu
{
    private final Container reactants;
    private final Container products;

    public static final Integer ROWS = 3;
    public static final Integer COL = 2;
    public static final Comparator<Item> itemComparator = Comparator.comparing(
        i -> BuiltInRegistries.ITEM.getKey(i)
    );

    public List<Item> reactantsAsItemList()
    {
        List<Item> l = new ArrayList<>();
        for(int i = 0; i < this.reactants.getContainerSize(); i++)
        {
            var s = this.reactants.getItem(i);
            if(!s.isEmpty())
            {
                l.add(s.getItem());
            }
        }
        l.sort(ChemicalReactorMenu.itemComparator);
        return l;
    }
    public List<Item> productsAsItemList()
    {
        List<Item> l = new ArrayList<>();
        for(int i = 0; i < this.products.getContainerSize(); i++)
        {
            var s = this.products.getItem(i);
            if(!s.isEmpty())
            {
                l.add(s.getItem());
            }
        }
        l.sort(ChemicalReactorMenu.itemComparator);
        return l;
    }

    public void fillContainer(Container c, List<Item> it)
    {
        for(int i = 0; i < c.getContainerSize(); i++)
        {
            c.setItem(i, ItemStack.EMPTY);
        }

        for(int i = 0; i < it.size() && i < c.getContainerSize(); i++)
        {
            Item item = it.get(i);
            if(item != null && item != Items.AIR)
            {
                c.setItem(i, new ItemStack(item, 1));
            }
        }
    }

    public ChemicalReactorMenu(int cid, Inventory pinv)
    {
        this(
            cid, pinv, 
            new SimpleContainer(ROWS * COL), 
            new SimpleContainer(ROWS * COL)
        );
    }

    public ChemicalReactorMenu(
        int cid, Inventory pinv,
        Container r, Container p
    )
    {
        super(ErosionRegistry.Menus.CHEMICAL_REACTOR.get(), cid);
        
        checkContainerSize(r, 6);
        checkContainerSize(p, 6);
        
        this.reactants = r;
        this.products = p;

        if(r instanceof SimpleContainer sc)
        {
            sc.addListener(this::slotsChanged);
        }

        reactants.startOpen(pinv.player);
        products.startOpen(pinv.player);

        for(int row = 0; row < ROWS; ++row)
        {
            for(int col = 0; col < COL; ++col)
            {
                int index = col + row * 2;
                this.addSlot(new Slot(reactants, index, 30 + col * 18, 17 + row * 18));
            }
        }

        for(int row = 0; row < 3; ++row)
        {
            for(int col = 0; col < 2; ++col)
            {
                int index = col + row * 2;
                this.addSlot(new ChemicalReactorProductSlot(products, index, 124 + col * 18, 17 + row * 18, this::onProductTaken));
            }
        }

        addPlayerInventory(pinv);
        addPlayerHotbar(pinv);
        return;
    }

    @Override
    public void slotsChanged(Container c)
    {
        super.slotsChanged(c);
        if(c == this.reactants)
        {
            scanRecipez();
        }
        return;
    }

    private void scanRecipez()
    {
        for(var cr : ErosionCore.getChemicalReactions())
        {
            if(cr.getReactants().equals(this.reactantsAsItemList()))
            {
                this.fillContainer(products, cr.getProducts());
                return;
            }
        }
        this.clearProducts();
        return;
    }

    private void onProductTaken()
    {
        for(int i = 0; i < this.reactants.getContainerSize(); i++)
        {
            ItemStack stack = this.reactants.getItem(i);
            if(!stack.isEmpty())
            {
                stack.shrink(1);
            }
        }

        if(this.products.isEmpty()) scanRecipez();
        return;
    }

    private void clearProducts()
    {
        for(int i = 0; i < this.products.getContainerSize(); i++)
        {
            this.products.setItem(i, ItemStack.EMPTY);
        }
    }

    private void addPlayerInventory(Inventory pinv)
    {
        final int y = 102;
        for(int i = 0; i < 3; ++i)
        {
            for(int j = 0; j < 9; ++j)
            {
                this.addSlot(new Slot(pinv, j + i * 9 + 9, 8 + j * 18, y + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory pinv)
    {
        final int y = 160;
        for(int k = 0; k < 9; ++k)
        {
            this.addSlot(new Slot(pinv, k, 8 + k * 18, y));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player p, int i)
    {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player p)
    {
        return this.reactants.stillValid(p) && this.products.stillValid(p);
    }

    @Override 
    public void removed(Player p)
    {
        super.removed(p);
        this.clearContainer(p, this.reactants);
    }
}