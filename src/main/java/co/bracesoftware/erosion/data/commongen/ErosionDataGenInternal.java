package co.bracesoftware.erosion.data.commongen;

import co.bracesoftware.erosion.Erosion;
import co.bracesoftware.erosion.ErosionConfig;
import co.bracesoftware.erosion.ErosionUtils;

import java.util.List;

public class ErosionDataGenInternal
{
    public static void generateChemicalReactorAnim()
    {
        final String assetsBlock = "assets/" + Erosion.MODID + "/textures/block/";

        ErosionTextureGen.createAnimatedTexture(
            ErosionUtils.getResourcesFolder() +
            assetsBlock + "generated/chemical_reactor_top." +
            ErosionConfig.ErosionDataGen.ErosionTextureGen.OUTPUT_FORMAT,
            List.of(
                ErosionUtils.getResourcesFolder() + assetsBlock +
                "chemical_reactor_top_1." + ErosionConfig.ErosionDataGen.ErosionTextureGen.OUTPUT_FORMAT,
                ErosionUtils.getResourcesFolder() + assetsBlock +
                "chemical_reactor_top_2." + ErosionConfig.ErosionDataGen.ErosionTextureGen.OUTPUT_FORMAT
            ), 20, false
        );
        return;
    }
}