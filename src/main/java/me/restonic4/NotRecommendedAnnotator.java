package me.restonic4;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;

public class NotRecommendedAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (element instanceof PsiMethod) {
            PsiMethod method = (PsiMethod) element;
            if (method.hasAnnotation("me.restonic4.NotRecommended")) {
                String message = getNotRecommendedMessage(method);
                holder.newAnnotation(HighlightSeverity.WARNING, message)
                        .range(method.getNameIdentifier().getTextRange())
                        .create();
            }
        } else if (element instanceof PsiMethodCallExpression) {
            PsiMethodCallExpression methodCall = (PsiMethodCallExpression) element;
            PsiMethod method = methodCall.resolveMethod();
            if (method != null && method.hasAnnotation("me.restonic4.NotRecommended")) {
                String message = getNotRecommendedMessage(method);
                holder.newAnnotation(HighlightSeverity.WARNING, message)
                        .range(methodCall.getTextRange())
                        .create();
            }
        }
    }

    private String getNotRecommendedMessage(PsiMethod method) {
        PsiAnnotation annotation = findAnnotation(method, "me.restonic4.NotRecommended");
        String message = "This method is not recommended";
        if (annotation != null) {
            PsiAnnotationMemberValue value = annotation.findAttributeValue("value");
            if (value instanceof PsiLiteralExpression) {
                String customMessage = ((PsiLiteralExpression) value).getValue().toString();
                if (!customMessage.isEmpty()) {
                    message += " because: " + customMessage;
                }
            }
        }
        return message;
    }

    private PsiAnnotation findAnnotation(PsiMethod method, String annotationFqName) {
        for (PsiAnnotation annotation : method.getAnnotations()) {
            if (annotation.getQualifiedName() != null && annotation.getQualifiedName().equals(annotationFqName)) {
                return annotation;
            }
        }
        return null;
    }
}
