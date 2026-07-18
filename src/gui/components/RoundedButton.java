package gui.components;

/** Variante nominal mantida para telas que pedem explicitamente um botão arredondado. */
public final class RoundedButton extends ModernButton {
    public RoundedButton(String text, AppIcon.Kind icon, Variant variant) { super(text, icon, variant); }
    public RoundedButton(String text, AppIcon.Kind icon, Variant variant, boolean pill) { super(text, icon, variant, pill); }
}
