package co.bracesoftware.libs.minecraft_text_formatter;

import java.util.ArrayList;
import java.util.List;

public class Text
{
    private static class Const
    {
        public static class Misc
        {
            public static final String FORMAT_SYMBOL = "§";
        }
    }
    public static class InternalClass
    {
        public Integer Code = 0;
        public Character Char = '?';

        public InternalClass(Integer c, char c2)
        {
            this.Code = c;
            this.Char = c2;
        }

        public Integer get()
        {
            return this.Code;
        }
    }
    public class Col
    {
        public static final InternalClass BLACK = new InternalClass(1, '0');//
        public static final InternalClass DARK_BLUE = new InternalClass(2, '1');//2;//'1';
        public static final InternalClass DARK_GREEN = new InternalClass(3, '2');//'2';
        public static final InternalClass DARK_AQUA = new InternalClass(4, '3');//4;//'3';
        public static final InternalClass DARK_RED = new InternalClass(5,'4');//5;//'4';
        public static final InternalClass DARK_PURPLE = new InternalClass(6,'5');//6;//'5';
        public static final InternalClass GOLD = new InternalClass(7,'6');//7;//'6';
        public static final InternalClass GRAY = new InternalClass(8,'7');//8;//'7';
        public static final InternalClass DARK_GRAY = new InternalClass(9,'8');//9;//'8';
        public static final InternalClass BLUE = new InternalClass(10,'9');//10;//'9';
        public static final InternalClass GREEN = new InternalClass(11,'a');//11;//'a';
        public static final InternalClass AQUA = new InternalClass(12,'b');//12;//'b';
        public static final InternalClass RED = new InternalClass(13,'c');//13;//'c';
        public static final InternalClass LIGHT_PURPLE = new InternalClass(14,'d');//14;//'d';
        public static final InternalClass YELLOW = new InternalClass(15,'e');//15;//'e';
        public static final InternalClass WHITE = new InternalClass(16,'f');//16;//'f';

        public static final List<InternalClass> LIST = List.of(
            BLACK, DARK_BLUE, DARK_GREEN, DARK_AQUA, DARK_RED, DARK_PURPLE,
            GOLD, GRAY, DARK_GRAY, BLUE, GREEN, AQUA, RED, LIGHT_PURPLE,
            YELLOW, WHITE
        );
    }
    public class Style
    {
        public static final InternalClass OBFUSCATED = new InternalClass(17,'k');//17;//'k';
        public static final InternalClass BOLD = new InternalClass(18,'l');//18;//'l';
        public static final InternalClass STRIKETHROUGH = new InternalClass(19,'m');//19;//'m';
        public static final InternalClass UNDERLINE = new InternalClass(20,'n');//20;//'n';
        public static final InternalClass ITALIC = new InternalClass(21,'o');//21;//'o';
        public static final InternalClass RESET = new InternalClass(22, 'r');//22;//'r';

        public static final List<InternalClass> LIST = List.of(
            OBFUSCATED, BOLD, STRIKETHROUGH, UNDERLINE, ITALIC, RESET
        );
    }
    public static String Format()
    {
        return "";
    }
    public static String Format(InternalClass l)
    {
        return Const.Misc.FORMAT_SYMBOL + l.Char;
    }
    public static String Format(Integer l)
    {
        String result = "";
        for(int k = 0; k < Style.LIST.size(); ++k)
        {
            if(l == Style.LIST.get(k).Code)
            {
                result = Const.Misc.FORMAT_SYMBOL + Style.LIST.get(k).Char;
                return result;
            }
        }
        for(int k = 0; k < Col.LIST.size(); ++k)
        {
            if(l == Col.LIST.get(k).Code)
            {
                result = Const.Misc.FORMAT_SYMBOL + Col.LIST.get(k).Char;
                return result;
            }
        }
        return result;
    }
    public static String Format(InternalClass... l)
    {
        List<Integer> g = new ArrayList<>();
        for(InternalClass item : l)
        {
            g.add(item.Code);
        }
        return Format(g);
    }
    public static String Format(Integer... l)
    {
        return Format(List.of(l));
    }
    public static String Format(List<Integer> l)
    {
        String result = "";
        if(l.isEmpty())
        {
            return result;
        }
        //firstly colors
        for(int i = 0; i < l.size(); ++i)
        {
            Integer p = l.get(i);
            for(int k = 0; k < Col.LIST.size(); ++k)
            {
                if(p == Col.LIST.get(k).Code)
                {
                    result += Const.Misc.FORMAT_SYMBOL + Col.LIST.get(k).Char;
                }
            }
        }
        //then styles
        for(int i = 0; i < l.size(); ++i)
        {
            Integer p = l.get(i);
            for(int k = 0; k < Style.LIST.size(); ++k)
            {
                if(p == Style.LIST.get(k).Code)
                {
                    result += Const.Misc.FORMAT_SYMBOL + Style.LIST.get(k).Char;
                }
            }
        }
        return result;
    }
}
