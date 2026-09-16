package co.bracesoftware.erosion.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import com.google.common.collect.ImmutableList;
import co.bracesoftware.erosion.Erosion;
import co.bracesoftware.erosion.ErosionExceptions.ErosionMixinException;
import net.neoforged.neoforge.internal.BrandingControl;

@Mixin(value = BrandingControl.class, remap = false)
public class ErosionBrandingText
{
    //credits modernfix dev for the @Inject,
    //tried injecting into getBranding but didnt work
    @Inject(
        method = {"computeBranding"},
        at = {@At(value = "INVOKE",
        target = "Lnet/neoforged/fml/ModList;get()Lnet/neoforged/fml/ModList;")},
        locals = LocalCapture.CAPTURE_FAILHARD, require = 1
    )
    private static void idkWhatToCallThis(
        CallbackInfo ci, ImmutableList.Builder<String> builder
    )
    {
        builder.add(Erosion.MOD_BRANDING);
    }
}