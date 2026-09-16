package co.bracesoftware.erosion.mixin;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import co.bracesoftware.erosion.Erosion;
import co.bracesoftware.erosion.ErosionUtils;
import net.neoforged.neoforge.internal.BrandingControl;

@Mixin(value = BrandingControl.class, remap = false)
public class ErosionBrandingText
{
    @Inject(
        method = "computeBranding",
        at = @At("HEAD")
    )
    private static void test(CallbackInfo ci)
    {
        ErosionUtils.Log("Erosion Mixin loaded!");
        return;
    }

    @Inject(
        method = "getBrandings",
        at = @At("RETURN")
    )
    private static void test(
        boolean includeMC,
        boolean reverse,
        CallbackInfoReturnable<List<String>> cir
    ) {
        System.out.println("=== GET BRANDINGS CALLED ===");
        System.out.println("includeMC = " + includeMC);
        System.out.println("reverse = " + reverse);
        System.out.println("result = " + cir.getReturnValue());

        ErosionInjectBranding(includeMC, reverse, cir);
    }

    private static void ErosionInjectBranding(
        boolean inc,
        boolean r,
        CallbackInfoReturnable<List<String>> cir
    ) throws RuntimeException
    {
        String verstr = "Erosion build " + Erosion.BUILD;
        System.out.println("Yo wassup");
        List<String> original = cir.getReturnValue();

        if(original == null || original.contains(verstr)) return;

        List<String> brandings = new ArrayList<>(original);

        if(r) brandings.add(0, verstr);
        else brandings.add(verstr);

        cir.setReturnValue(brandings);
        return;
    }
}