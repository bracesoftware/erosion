package co.bracesoftware.libs.minecraft_text_formatter;

public class Emojis
{
    public static class StringConstants
    {
        public static final String COOLING_FLUID = "❄";
        public static final String FIRE = "🔥";
        public static final String CLOUD = "☁";

        public static final String FULL_BOX = "█";
        public static final String EMPTY_BOX_3 = "▓";
        public static final String EMPTY_BOX_2 = "▒";
        public static final String EMPTY_BOX = "░";
    }
    
    public static class Utils
    {
        public static void repeat(int times, Runnable action)
        {
            for(int i = 0; i < times; i++)
            {
                action.run();
            }
        }
        public static final int MAX_SIZE = 20;
        public static String formatLoadingBar(int progress, int max)
        {
            return formatLoadingBar(progress, max, "");
        }

        public static String formatLoadingBar(int progress, int max, String extra)
        {
            var b = new StringBuilder();
            if(progress > max)
            {
                b.append("100% ");
                repeat(MAX_SIZE, () -> {
                    b.append(Emojis.StringConstants.FULL_BOX);
                });
                b.append(" " + extra);
                return b.toString();
            }
            if(progress <= 0)
            {
                b.append("0% ");
                repeat(MAX_SIZE, () -> {
                    b.append(Emojis.StringConstants.EMPTY_BOX);
                });
                b.append(" " + extra);
                return b.toString();
            }

            double procent = (double) progress / max;
            b.append(procent * 100);
            b.append("% ");

            int prog = (int) (procent * MAX_SIZE);
            repeat(prog, () -> {
                b.append(Emojis.StringConstants.FULL_BOX);
            });

            int remaining = MAX_SIZE - prog;
            for(int i = 0; i < remaining; i++)
            {
                if(i == 0) b.append(Emojis.StringConstants.EMPTY_BOX_3);
                else if(i == 1) b.append(Emojis.StringConstants.EMPTY_BOX_2);
                else b.append(Emojis.StringConstants.EMPTY_BOX);
            }

            b.append(" " + extra);

            return b.toString();
        }
    }
}
