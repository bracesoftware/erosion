package co.bracesoftware.erosion.data.commongen;

import co.bracesoftware.erosion.Erosion;
import co.bracesoftware.erosion.ErosionConfig;
import co.bracesoftware.erosion.ErosionExceptions.ErosionDataGenException;
import co.bracesoftware.erosion.ErosionUtils;
import co.bracesoftware.erosion.world.ErosionRegistry;

import java.util.ArrayList;
import java.util.List;

public class ErosionBlockAnimGen
{
    private static final String assetsBlock = "assets/" + Erosion.MODID + "/textures/block/";
    private static final String assetsGenerated = assetsBlock + ErosionUtils.getGeneratedFolder();

    private static final List<String> generatedAnims = new ArrayList<>();

    public static void generateChemicalReactorAnims()
    {
        generateAnim(
            ErosionRegistry.RawRegistry.CHEMICAL_REACTOR.getId(), "top",
            2, 20, false,
            assetsGenerated, assetsBlock
        );
      
        generateAnim(
            ErosionRegistry.RawRegistry.CHEMICAL_REACTOR_MODULE.getId(), "bottom",
            12, 3, true,
            assetsGenerated,assetsGenerated
        );

        generateAnim(
            ErosionRegistry.RawRegistry.CHEMICAL_REACTOR_MODULE.getId(), "top",
            4, 5, true,
            assetsGenerated,assetsGenerated
        );
       
        generateAnim(
            ErosionRegistry.RawRegistry.CHEMICAL_REACTOR_COOLING_SYSTEM.getId(), "side",
            4, 15, true,
            assetsGenerated,assetsGenerated
        );

        generateAnim(
            ErosionRegistry.RawRegistry.CHEMICAL_REACTOR_COOLING_SYSTEM.getId(), "top",
            36, 2, false,
            assetsGenerated,assetsGenerated
        );
        return;
    }

    public static void generateAnim(
        String id, String where, int count,
        int time, boolean interpolate,
        String to, String from
    ) throws ErosionDataGenException
    {
        var lmao = id + "::" + where;
        if(generatedAnims.contains(lmao))
        {
            throw new ErosionDataGenException("Duplicate animation -> " + lmao);
        }
        generatedAnims.add(lmao);
        ErosionTextureGen.createAnimatedTexture(
            ErosionUtils.getResourcesFolder() +
            to + id + "_" + where + "." +
            ErosionConfig.ErosionDataGen.ErosionTextureGen.OUTPUT_FORMAT,
            generateNameList(from + id, count, where),
            time, interpolate
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