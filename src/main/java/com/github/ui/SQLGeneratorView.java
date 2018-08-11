package com.github.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;

import javax.swing.*;

public class SQLGeneratorView extends BaseView{

    private JPanel sqlGeneratorPanel;

    public SQLGeneratorView(BasePanel basePanel, Project project, ToolWindow toolWindow) {
        super(basePanel, project, toolWindow);
    }

    @Override
    public JPanel getPanel() {
        return sqlGeneratorPanel;
    }
}
