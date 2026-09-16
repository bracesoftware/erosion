package co.bracesoftware.erosion.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.include.com.google.common.collect.ImmutableList;

import co.bracesoftware.erosion.Erosion;
import net.neoforged.neoforge.internal.BrandingControl;

@Mixin(value = BrandingControl.class, remap = false)
public class ErosionBrandingText
{
    public static final String MOD_BRANDING = "Erosion build " + Erosion.BUILD;

    @Inject(
        method = {"computeBranding"},
        at = {@At(value = "INVOKE",
        target = "Lnet/neoforged/fml/ModList;get()Lnet/neoforged/fml/ModList;")},
        locals = LocalCapture.CAPTURE_FAILHARD, require = 0
    )
    private static void addModernFixBranding(
        CallbackInfo ci, ImmutableList.Builder<String> builder
    ) throws RuntimeException
    {
        builder.add(MOD_BRANDING);
    }
}