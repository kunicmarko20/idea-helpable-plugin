package com.github.kunicmarko20.idea.helpable.dialog;

import com.intellij.openapi.fileTypes.FileTypes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.EditorTextField;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.completion.PhpCompletionUtil;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import javax.swing.*;
import java.awt.*;
import java.util.Collection;

public class NewJmsDialog extends DialogWrapper {

    private final EditorTextField classSelectionTextField;
    private final Project project;

    public NewJmsDialog(Project project) {
        super(true);
        this.project = project;
        this.classSelectionTextField = new EditorTextField("", project, FileTypes.PLAIN_TEXT);
        PhpCompletionUtil.installClassCompletion(this.classSelectionTextField, (String) null, this.getDisposable(), null, new String[0]);

        init();
        setTitle("Helpable - JMS Configuration");
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(700, 30));

        JLabel label = new JLabel("Class:");
        label.setPreferredSize(new Dimension(100, 30));

        panel.add(label, BorderLayout.LINE_START);
        panel.add(this.classSelectionTextField, BorderLayout.CENTER);

        return panel;
    }

    @NotNull
    public String getSelectedClass() {
        return this.classSelectionTextField.getText();
    }

    @Nullable
    public JComponent getPreferredFocusedComponent() {
        return this.classSelectionTextField;
    }

    protected ValidationInfo doValidate() {
        PhpIndex instance = PhpIndex.getInstance(this.project);
        Collection<PhpClass> classes = instance.getAnyByFQN(this.getSelectedClass());

        if (classes.isEmpty()) {
            return new ValidationInfo(
                String.format(
                    "Class \"%s\" not found. Please select an existing class.",
                    this.getSelectedClass()
                )
            );
        }

        return null;
    }
}
