package co.bracesoftware.erosion.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.include.com.google.common.collect.ImmutableList;

import co.bracesoftware.erosion.Erosion;
import co.bracesoftware.erosion.ErosionExceptions.ErosionMixinException;
import net.neoforged.neoforge.internal.BrandingControl;

@Mixin(value = BrandingControl.class, remap = false)
public class ErosionBrandingText
{
    @Inject(
        method = {"computeBranding"},
        at = {@At(value = "INVOKE",
        target = "Lnet/neoforged/fml/ModList;get()Lnet/neoforged/fml/ModList;")},
        locals = LocalCapture.CAPTURE_FAILHARD, require = 0
    )
    private static void addModernFixBranding(
        CallbackInfo ci, ImmutableList.Builder<String> builder
    ) throws ErosionMixinException
    {
        builder.add(Erosion.MOD_BRANDING);
    }
}