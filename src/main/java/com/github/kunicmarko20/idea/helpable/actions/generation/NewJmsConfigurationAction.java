package com.github.kunicmarko20.idea.helpable.actions.generation;

import com.github.kunicmarko20.idea.helpable.dialog.NewJmsDialog;
import com.github.kunicmarko20.idea.helpable.service.ClassFieldFinder;
import com.github.kunicmarko20.idea.helpable.service.PropertyTypeFinder;
import com.intellij.icons.AllIcons;
import com.intellij.ide.IdeView;
import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFileFactory;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.actions.PhpNamedElementNode;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import org.jetbrains.annotations.NotNull;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import java.util.HashMap;

public class NewJmsConfigurationAction extends AnAction {
    public NewJmsConfigurationAction() {
        super("JMS Configuration", "JMS configuration", AllIcons.FileTypes.Xml);
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
        PhpNamedElementNode[] classProperties = ClassFieldFinder.allProperties(phpClass);
        HashMap<String, String> properties = new HashMap<>();

        for (PhpNamedElementNode property: classProperties) {
            properties.put(
                property.getText(),
                PropertyTypeFinder.findFQCWithoutNull(property.getPsiElement())
            );
        }

        JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/jms_configuration.twig.xml");
        JtwigModel model = JtwigModel.newModel()
                .with("properties", properties)
                .with("class", selectedClass);

        ApplicationManager.getApplication().runWriteAction(() -> {
            directory.add(
                PsiFileFactory.getInstance(project).createFileFromText(
                    selectedClass.replace("\\", ".") + ".xml",
                    XmlFileType.INSTANCE,
                    template.render(model)
                )
            );
        });
    }
}
