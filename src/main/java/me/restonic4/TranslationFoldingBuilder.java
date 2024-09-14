package me.restonic4;


import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilderEx;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.components.ServiceBean;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.FoldingGroup;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.JavaRecursiveElementWalkingVisitor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLiteralExpression;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class TranslationFoldingBuilder extends FoldingBuilderEx {
    private static final String TRANSLATION_FILE_PATH = "resources/data/language/en_us.json";
    private Map<String, String> translations = new HashMap<>();

    public TranslationFoldingBuilder() {
        loadTranslations();
    }

    private void loadTranslations() {
        System.out.println("Loading translations...");
        Project project = ProjectManager.getInstance().getOpenProjects()[0];
        File file = new File(project.getBasePath(), TRANSLATION_FILE_PATH);
        if (!file.exists()) {
            System.out.println("Translation file not found: " + file.getAbsolutePath());
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            Gson gson = new Gson();
            Type type = new TypeToken<Map<String, String>>() {}.getType();
            translations = gson.fromJson(reader, type);
            if (translations != null) {
                for (Map.Entry<String, String> entry : translations.entrySet()) {
                    System.out.println("Loaded translation: " + entry);
                }
            } else {
                System.out.println("Translation file is empty or not properly formatted.");
            }
        } catch (IOException e) {
            System.out.println("Error reading translation file: " + e.getMessage());
        }
    }

    @Override
    public FoldingDescriptor @NotNull [] buildFoldRegions(@NotNull PsiElement root, @NotNull Document document, boolean quick) {
        FoldingGroup group = FoldingGroup.newGroup("TranslationFoldingGroup");
        List<FoldingDescriptor> descriptors = new ArrayList<>();

        //TranslationFoldingStyle.applyTranslationFoldingStyle();

        root.accept(new JavaRecursiveElementWalkingVisitor() {
            @Override
            public void visitElement(@NotNull PsiElement element) {
                super.visitElement(element);

                if (element instanceof PsiLiteralExpression) {
                    PsiLiteralExpression literalExpression = (PsiLiteralExpression) element;
                    String text = literalExpression.getText();

                    if (text.startsWith("\"") && text.endsWith("\"")) {
                        String key = text.substring(1, text.length() - 1);

                        if (key.contains(":")) {
                            String translation = translations.get(key);
                            if (translation != null) {
                                TextRange range = new TextRange(
                                        literalExpression.getTextRange().getStartOffset(),
                                        literalExpression.getTextRange().getEndOffset()
                                );

                                descriptors.add(new FoldingDescriptor(literalExpression.getNode(), range, group));
                            }
                        }
                    }
                }
            }
        });

        return descriptors.toArray(FoldingDescriptor.EMPTY_ARRAY);
    }

    @Override
    public @Nullable String getPlaceholderText(@NotNull ASTNode node) {
        PsiElement element = node.getPsi();
        if (element instanceof PsiLiteralExpression) {
            String text = ((PsiLiteralExpression) element).getText();
            if (text.startsWith("\"") && text.endsWith("\"")) {
                String key = text.substring(1, text.length() - 1);
                if (key.contains(":")) {
                    return "\"" + translations.getOrDefault(key, key) + "\"";
                }
            }
        }
        return "...";
    }

    @Override
    public boolean isCollapsedByDefault(@NotNull ASTNode node) {
        return true;
    }

    @Override
    public boolean isCollapsedByDefault(@NotNull FoldingDescriptor foldingDescriptor) {
        return super.isCollapsedByDefault(foldingDescriptor);
    }
}
