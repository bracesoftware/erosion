package co.bracesoftware.libs.minecraft_text_formatter;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ComponentWordWrap
{
    public static List<Component> Format(Component i, int m)
    {
        List<StyledWord> a = new ArrayList<>();
        if(i.getString().isBlank() || i.getString().isEmpty())
        {
            return new ArrayList<>(List.of(Component.literal(" ")));
        }

        i.visit(
            (style, text) -> {
                String[] split = text.split("(?<=\\s)|(?=\\s)");
                for(String part : split)
                {
                    if(!part.isEmpty())
                    {
                        a.add(new StyledWord(part, style));
                    }
                }
                return Optional.empty();
            },
            Style.EMPTY
        );

        List<Component> result = new ArrayList<>();
        if(a.isEmpty())
        {
            return result;
        }

        MutableComponent cc = Component.empty();
        int w = 0;
        boolean iss = true;

        for(var sw : a)
        {
            boolean ww = sw.text.isBlank();

            if(ww && iss)
            {
                cc.append(Component.literal(sw.text).setStyle(sw.style));
                continue;
            }

            if(!ww)
            {
                iss = false;
            }
            if(!ww && w >= m)
            {
                result.add(cc);
                cc = Component.empty();
                w = 0;
                iss = true;
                if(sw.text.isBlank()) //prserve formatting 
                {
                    cc.append(Component.literal(sw.text).setStyle(sw.style));
                    continue;
                }
                if(isSymbol(sw.text)) //preserve symbols
                {
                    cc.append(Component.literal(sw.text).setStyle(sw.style));
                    continue;
                }
            }
            cc.append(Component.literal(sw.text).setStyle(sw.style));
            if(!ww) w++;
        }
        if(!cc.getString().isEmpty()) result.add(cc);

        return result;
    }

    public static final String SYMBOL_LIST = "!?.()[]{}#$*/:;&%\"\'\\";

    private static boolean isSymbol(String who)
    {
        if(who.length() != 1) return false;
        char c = who.charAt(0);
        for(int i = 0; i < SYMBOL_LIST.length(); i++)
        {
            if(c == SYMBOL_LIST.charAt(i)) return true;
        }
        return false;
    }

    private static class StyledWord
    {
        String text;
        Style style;

        //now i can preserve all the shi :D
        public StyledWord(String text, Style style)
        {
            this.text = text;
            this.style = style;
        }
    }
}