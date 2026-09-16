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
}