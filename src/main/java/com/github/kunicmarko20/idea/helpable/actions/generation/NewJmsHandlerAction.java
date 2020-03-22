package com.github.kunicmarko20.idea.helpable.actions.generation;

import com.github.kunicmarko20.idea.helpable.dialog.NewJmsDialog;
import com.intellij.ide.IdeView;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFileFactory;
import com.jetbrains.php.PhpIcons;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.PhpFileType;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.impl.MethodImpl;
import com.jetbrains.php.roots.PhpNamespaceCompositeProvider;
import org.apache.commons.lang.WordUtils;
import org.jetbrains.annotations.NotNull;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;
import java.util.List;

public class NewJmsHandlerAction extends AnAction {
    public NewJmsHandlerAction() {
        super("JMS Handler", "JMS handler", PhpIcons.CLASS);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        DataContext dataContext = event.getDataContext();
        IdeView view = LangDataKeys.IDE_VIEW.getData(dataContext);

        this.invoke(CommonDataKeys.PROJECT.getData(dataContext), view.getOrChooseDirectory());
    }

    public void invoke(@NotNull Project project, @NotNull PsiDirectory directory) {
        NewJmsDialog dialog = new NewJmsDialog(project);

        if (!dialog.showAndGet()) {
            return;
        }

        String selectedClass = dialog.getSelectedClass();

        PhpIndex instance = PhpIndex.getInstance(project);
        PhpClass phpClass = instance.getAnyByFQN(selectedClass).iterator().next();
        List<String> namespaces = PhpNamespaceCompositeProvider.INSTANCE.suggestNamespaces(directory);

        String handlerName = phpClass.getName() + "Handler";

        JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/jms_handler.twig.php");
        MethodImpl toMethod = (MethodImpl) this.toMethod(phpClass);
        JtwigModel model = JtwigModel.newModel()
            .with("namespace", namespaces.isEmpty() ? "" : namespaces.get(0))
            .with("handling_type_fqcn", phpClass.getFQN().substring(1))
            .with("handling_type_name", phpClass.getName())
            .with("name", handlerName)
            .with("factory_method", this.factoryMethod(phpClass))
            .with("to_method", toMethod.getName())
            .with("to_method_type", toMethod.getDeclaredType());

        ApplicationManager.getApplication().runWriteAction(() -> {
            directory.add(
                PsiFileFactory.getInstance(project).createFileFromText(
                    handlerName + ".php",
                    PhpFileType.INSTANCE,
                    template.render(model)
                )
            );
        });
    }

    private Method toMethod(PhpClass phpClass) {
        return phpClass.getMethods()
                .stream()
                .filter(method -> method.getName().startsWith("to"))
                .findFirst()
                .get();
    }

    private String factoryMethod(PhpClass phpClass) {
        return phpClass.getMethods()
                .stream()
                .filter(method -> method.getName().equals("for") || method.getName().equals("with") || method.getName().startsWith("from"))
                .findFirst()
                .get()
                .getName();
    }
}
