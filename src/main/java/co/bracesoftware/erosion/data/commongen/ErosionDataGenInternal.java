package co.bracesoftware.erosion.data.commongen;

import co.bracesoftware.erosion.Erosion;
import co.bracesoftware.erosion.ErosionConfig;
import co.bracesoftware.erosion.ErosionUtils;
import co.bracesoftware.erosion.world.ErosionRegistry;

import java.util.ArrayList;
import java.util.List;

public class ErosionDataGenInternal
{
    private static final String assetsBlock = "assets/" + Erosion.MODID + "/textures/block/";
    private static final String assetsGenerated = assetsBlock + ErosionUtils.getGeneratedFolder();
    public static void generateChemicalReactorAnims()
    {
        String where;
        ErosionTextureGen.createAnimatedTexture(
            ErosionUtils.getResourcesFolder() +
            assetsGenerated + ErosionRegistry.RawRegistry.CHEMICAL_REACTOR.getId() + "_top." +
            ErosionConfig.ErosionDataGen.ErosionTextureGen.OUTPUT_FORMAT,
            generateNameList(assetsBlock + ErosionRegistry.RawRegistry.CHEMICAL_REACTOR.getId(), 2, "top"),
            20, true
        );

        ErosionTextureGen.createAnimatedTexture(
            ErosionUtils.getResourcesFolder() +
            assetsGenerated + ErosionRegistry.RawRegistry.CHEMICAL_REACTOR_MODULE.getId() + "_bottom." +
            ErosionConfig.ErosionDataGen.ErosionTextureGen.OUTPUT_FORMAT,
            generateNameList(assetsGenerated + ErosionRegistry.RawRegistry.CHEMICAL_REACTOR_MODULE.getId(), 12, "bottom"),
            3, true
        );
        ErosionTextureGen.createAnimatedTexture(
            ErosionUtils.getResourcesFolder() +
            assetsGenerated + ErosionRegistry.RawRegistry.CHEMICAL_REACTOR_MODULE.getId() + "_top." +
            ErosionConfig.ErosionDataGen.ErosionTextureGen.OUTPUT_FORMAT,
            generateNameList(assetsGenerated + ErosionRegistry.RawRegistry.CHEMICAL_REACTOR_MODULE.getId(), 4, "top"),
            5, true
        );

        where = "side";
        ErosionTextureGen.createAnimatedTexture(
            ErosionUtils.getResourcesFolder() +
            assetsGenerated + ErosionRegistry.RawRegistry.CHEMICAL_REACTOR_COOLING_SYSTEM.getId() + "_" + where + "." +
            ErosionConfig.ErosionDataGen.ErosionTextureGen.OUTPUT_FORMAT,
            generateNameList(assetsGenerated + ErosionRegistry.RawRegistry.CHEMICAL_REACTOR_COOLING_SYSTEM.getId(), 4, where),
            15, true
        );

        where = "top";
        ErosionTextureGen.createAnimatedTexture(
            ErosionUtils.getResourcesFolder() +
            assetsGenerated + ErosionRegistry.RawRegistry.CHEMICAL_REACTOR_COOLING_SYSTEM.getId() + "_" + where + "." +
            ErosionConfig.ErosionDataGen.ErosionTextureGen.OUTPUT_FORMAT,
            generateNameList(assetsGenerated + ErosionRegistry.RawRegistry.CHEMICAL_REACTOR_COOLING_SYSTEM.getId(), 36, where),
            2, false
        );
        return;
    }

    public static List<String> generateNameList(String id, int count, String orientation)
    {
        var l = new ArrayList<String>();
        for(int i = 0; i < count; i++)
        {
            int idx = i + 1;
            l.add(
                ErosionUtils.getResourcesFolder() +
                id + "_" + orientation + "_" + idx + "." +
                ErosionConfig.ErosionDataGen.ErosionTextureGen.OUTPUT_FORMAT
            );
        }
        return l;
    }
}