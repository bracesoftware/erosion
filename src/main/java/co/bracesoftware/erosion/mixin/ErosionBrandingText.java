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

public class ErosionBrandingText
{
    public static final Boolean FKTHIS = true;
}