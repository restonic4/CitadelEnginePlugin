package me.restonic4;

import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.colors.impl.DefaultColorsScheme;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.ui.JBColor;

import java.awt.*;

public class TranslationFoldingStyle {
    public static final TextAttributesKey TRANSLATION_FOLDING_KEY = TextAttributesKey.createTextAttributesKey("TRANSLATION_FOLDING");

    public static void applyTranslationFoldingStyle() {
        EditorColorsScheme scheme = EditorColorsManager.getInstance().getGlobalScheme();
        TextAttributes attributes = scheme.getAttributes(TRANSLATION_FOLDING_KEY);

        EditorColorsManager.getInstance().addColorsScheme(new DefaultColorsScheme());
        if (attributes == null) {
            attributes = new TextAttributes();
        }

        attributes.setBackgroundColor(JBColor.GREEN);
        scheme.setAttributes(TRANSLATION_FOLDING_KEY, attributes);
    }
}
