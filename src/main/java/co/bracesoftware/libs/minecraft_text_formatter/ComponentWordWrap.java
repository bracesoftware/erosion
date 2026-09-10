package co.bracesoftware.libs.minecraft_text_formatter;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ComponentWordWrap
{
    public static Component Format(Component i, int m)
    {
        List<StyledWord> ALL_WORDS = new ArrayList<>();
        
        i.visit(
            (style, text) -> {
                String[] split = text.split("(?<=\\s)|(?=\\s)");
                for(String part : split)
                {
                    if(!part.isEmpty())
                    {
                        ALL_WORDS.add(new StyledWord(part, style));
                    }
                }
                return Optional.empty();
            },
            Style.EMPTY
        );

        MutableComponent result = Component.empty();
        int w = 0;
        boolean f = true;

        for(StyledWord sw : ALL_WORDS)
        {
            if(w == 0 && sw.text.trim().isEmpty())
            {
                continue;
            }

            if(w >= m && !sw.text.trim().isEmpty())
            {
                if(!f)
                {
                    result.append(Component.literal("\n"));
                }
                w = 0;
                f = false;
            }

            result.append(Component.literal(sw.text).setStyle(sw.style));
            
            if(!sw.text.trim().isEmpty())
            {
                w++;
            }
        }

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