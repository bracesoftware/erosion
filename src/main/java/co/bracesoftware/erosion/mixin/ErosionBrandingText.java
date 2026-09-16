package co.bracesoftware.erosion.mixin;

import java.util.ArrayList;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import com.google.common.collect.ImmutableList;
import co.bracesoftware.erosion.Erosion;
import co.bracesoftware.erosion.ErosionExceptions.ErosionMixinException;
import net.neoforged.neoforge.internal.BrandingControl;

import java.util.List;

@Mixin(value = BrandingControl.class, remap = false)
public class ErosionBrandingText
{
    private static List<String> cachedBranding = null;

    @Inject(method = "getBranding", at = @At("RETURN"), cancellable = true)
    private static void onGetBranding(CallbackInfoReturnable<List<String>> cir)
    {
        if(cachedBranding == null)
        {
            List<String> list = new ArrayList<>(cir.getReturnValue());
            list.add(Erosion.MOD_BRANDING);
            cachedBranding = List.copyOf(list);
        }
        cir.setReturnValue(cachedBranding);
    }
}