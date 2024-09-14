package me.restonic4;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;

public class NotRecommendedAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (element instanceof PsiMethod) {
            PsiMethod method = (PsiMethod) element;
            if (method.hasAnnotation("me.restonic4.NotRecommended")) {
                holder.newAnnotation(HighlightSeverity.WARNING, "This method is not recommended")
                        .range(method.getNameIdentifier().getTextRange())
                        .create();
            }
        }
    }
}
