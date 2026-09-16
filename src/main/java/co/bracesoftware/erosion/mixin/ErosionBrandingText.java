package co.bracesoftware.erosion.mixin;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import co.bracesoftware.erosion.Erosion;
import net.neoforged.neoforge.internal.BrandingControl;

@Mixin(value = BrandingControl.class, remap = false)
public class ErosionBrandingText
{
    @Inject(
        method = "computeBranding",
        at = @At("HEAD")
    )
    private static void test(CallbackInfo ci) {
        System.out.println("================================");
        System.out.println("EROSION MIXIN IS LOADED!");
        System.out.println("================================");
    }
    
    @Inject(
        method = "getBrandings(ZZ)Ljava/util/List;",
        at = @At("RETURN"),
        cancellable = true
    )
    private static void idkWhatToCallThis(
        boolean inc,
        boolean r,
        CallbackInfoReturnable<List<String>> cir
    ) throws RuntimeException
    {
        System.out.println("Yo wassup");
        List<String> original = cir.getReturnValue();

        if(original == null || original.contains("Erosion build " + Erosion.BUILD)) return;

        List<String> brandings = new ArrayList<>(original);

        if(r) brandings.add(0, "Erosion build " + Erosion.BUILD);
        else brandings.add("Erosion build " + Erosion.BUILD);

        cir.setReturnValue(brandings);
        return;
    }
}