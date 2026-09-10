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
                if(sw.text.isBlank())
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