package gui;

import gui.components.ModernButton;
import java.awt.Color;

/** @deprecated Use ModernButton em gui.components. Mantém compatibilidade de código legado. */
@Deprecated
class RoundedButton extends ModernButton {
    RoundedButton(String text,Color background,Color foreground){this(text,background,foreground,false);}
    RoundedButton(String text,Color background,Color foreground,boolean pill){super(text,null,variant(background),pill);setForeground(foreground);}
    private static Variant variant(Color color){
        if(UiTheme.DANGER.equals(color))return Variant.DANGER;
        if(UiTheme.ACCENT.equals(color))return Variant.SUCCESS;
        if(UiTheme.PRIMARY.equals(color)||UiTheme.PRIMARY_DARK.equals(color))return Variant.PRIMARY;
        return Variant.SECONDARY;
    }
}
