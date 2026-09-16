package co.bracesoftware.erosion.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import co.bracesoftware.erosion.Erosion;

import java.util.ArrayList;
import java.util.List;

import net.neoforged.neoforge.internal.BrandingControl;

@Mixin(value = BrandingControl.class, remap = false)
public class ErosionBrandingText
{
    @Inject(method = "getBrandings", at = @At("RETURN"), cancellable = true)
    private static void onGetBrandings(boolean i, boolean o, CallbackInfoReturnable<List<String>> cir)
    {
        List<String> brandings = new ArrayList<>(cir.getReturnValue());
        brandings.add(0, "Erosion build " + Erosion.BUILD.toString());
        cir.setReturnValue(brandings);
    }
}