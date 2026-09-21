package co.bracesoftware.erosion.world.blocks.chemical_reactor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import co.bracesoftware.erosion.ErosionCore;
import co.bracesoftware.erosion.world.ErosionRegistry;
import co.bracesoftware.erosion.world.blocks.chemical_reactor.ChemicalReactorSystemCore.IErosionChemicalReactorSystemComponent;
import co.bracesoftware.erosion.world.custom.ErosionCustomEntitySys.Gas;
import co.bracesoftware.erosion.world.custom.ErosionCustomEntitySys.GasType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ChemicalReactorMenu extends AbstractContainerMenu implements IErosionChemicalReactorSystemComponent
{
    public List<GasType> gasesToBeEmitted;
    public Player player;
    public BlockPos position;

    private final Container reactants;
    private final Container products;

    public static final int ROWS = 3;
    public static final int COL = 2;
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

    public ChemicalReactorMenu(int cid, Inventory pinv, BlockPos p)
    {
        this(
            cid, pinv, 
            new SimpleContainer(ROWS * COL), 
            new SimpleContainer(ROWS * COL),
            p
        );
    }

    public ChemicalReactorMenu(
        int cid, Inventory pinv,
        Container r, Container p,
        BlockPos pos
    )
    {
        super(ErosionRegistry.Menus.CHEMICAL_REACTOR.get(), cid);
        
        checkContainerSize(r, 6);
        checkContainerSize(p, 6);
        
        this.reactants = r;
        this.products = p;
        this.player = pinv.player;

        this.position = pos;

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
        if(this.player.level().isClientSide()) return;

        super.slotsChanged(c);
        if(c == this.reactants)
        {
            scanRecipez();
        }
        return;
    }

    private void scanRecipez()
    {
        for(var cr : ErosionCore.BlockRecipes.ChemicalReactor.getChemicalReactions())
        {
            if(cr.getReactants().equals(this.reactantsAsItemList()))
            {
                this.fillContainer(products, cr.getProducts());
                this.gasesToBeEmitted = cr.getGasCoproducts();
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
                this.handleGasEmission();
            }
        }

        if(this.products.isEmpty()) scanRecipez();
        return;
    }

    public void handleGasEmission()
    {
        if(this.gasesToBeEmitted == null) return;
        for(var g : this.gasesToBeEmitted)
        {
            var l = (ServerLevel) this.player.level();
            if(!ChemicalReactorBlock.isFunctionalScrubberPresent(l, position))
            {
                Gas.createGas(l, this.position, g);
            }
            else ChemicalReactorBlock.damageScrubberFilter(l, position);
        }
        return;
    }

    private void clearProducts()
    {
        for(int i = 0; i < this.products.getContainerSize(); i++)
        {
            this.products.setItem(i, ItemStack.EMPTY);
        }
        return;
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
        return;
    }

    private void addPlayerHotbar(Inventory pinv)
    {
        final int y = 160;
        for(int k = 0; k < 9; ++k)
        {
            this.addSlot(new Slot(pinv, k, 8 + k * 18, y));
        }
        return;
    }

    @Override 
    public ItemStack quickMoveStack(Player p, int idx)
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
        if(this.reactants.isEmpty() && !this.products.isEmpty())
        {
            this.clearContainer(p, this.products);
        }
        else this.clearContainer(p, this.reactants);
        return;
    }
}