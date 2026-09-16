package co.bracesoftware.erosion;

import co.bracesoftware.erosion.ErosionExceptions.ErosionException;

public class ErosionExceptions
{
    public static abstract class ErosionException extends RuntimeException
    {
        public ErosionException(String e)
        {
            super(e);
        }
    }

    public static class ErosionMixinException extends ErosionException
    {
        public ErosionMixinException(String e)
        {
            super(e);
        }
    }

    public static class ErosionRecipeImplException extends ErosionException
    {
        public ErosionRecipeImplException(String e)
        {
            super(e);
        }
    }

    public static class ErosionDataGenException extends ErosionException
    {
        public ErosionDataGenException(String e)
        {
            super(e);
        }
    }

    public static class ErosionEventBusException extends ErosionException
    {
        public ErosionEventBusException(String e)
        {
            super(e);
        }
    }

    public static class ErosionBlockEntityExceptions
    {
        public static class ErosionMaterialPurifierException extends ErosionException
        {
            public ErosionMaterialPurifierException(String e)
            {
                super(e);
            }
        }
        public static class ErosionCrucibleException extends ErosionException
        {
            public ErosionCrucibleException(String e)
            {
                super(e);
            }
        }
    }
    public static class ErosionBlockExceptions
    {
        public static class ErosionChemicalReactorException extends ErosionException
        {
            public ErosionChemicalReactorException(String e)
            {
                super(e);
            }
        }
    }
}