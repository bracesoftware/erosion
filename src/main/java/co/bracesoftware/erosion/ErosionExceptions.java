package co.bracesoftware.erosion;

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

    public static class ErosionAPIExceptions
    {
        public static class ErosionDisplayMessageException extends ErosionException
        {
            public ErosionDisplayMessageException(String e)
            {
                super(e);
            }
        }
    }

    public static class ErosionCommandExceptions
    {
        public static class ErosionCommandParserException extends ErosionException
        {
            public ErosionCommandParserException(String e)
            {
                super(e);
            }
        }
        public static class ErosionCommandSetupException extends ErosionException
        {
            public ErosionCommandSetupException(String e)
            {
                super(e);
            }
        }
    }

    public static class ErosionCustomEntityExceptions
    {
        public static class ErosionGasInitException extends ErosionException
        {
            public ErosionGasInitException(String e)
            {
                super(e);
            }
        }
    }
    public static class ErosionBlockExceptions
    {
        public static class ErosionBlockWithTipImpl extends ErosionException
        {
            public ErosionBlockWithTipImpl(String e)
            {
                super(e);
            }
        }

        public static class ErosionNetworkSafeBlockException extends ErosionException
        {
            public ErosionNetworkSafeBlockException(String e)
            {
                super(e);
            }
        }

        public static class ErosionChemicalReactorException extends ErosionException
        {
            public ErosionChemicalReactorException(String e)
            {
                super(e);
            }
        }
    }

    public static class ErosionItemExceptions
    {
        public static class ErosionGasMaskInitException extends ErosionException
        {
            public ErosionGasMaskInitException(String e)
            {
                super(e);
            }
        }
    }

    public static class ErosionConfigException
    {
        public static class ErosionWrongConfigGetterOrSetterMethodCalledException extends ErosionException
        {
            public ErosionWrongConfigGetterOrSetterMethodCalledException(String e)
            {
                super(e);
            }
        }
    }
}