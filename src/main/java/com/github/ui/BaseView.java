package com.github.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;

import javax.swing.*;

public abstract class BaseView {
    protected BasePanel basePanel;
    protected Project project;
    protected ToolWindow toolWindow;

    public BaseView(BasePanel basePanel, Project project, ToolWindow toolWindow){
        this.basePanel = basePanel;
        this.project = project;
        this.toolWindow = toolWindow;
    }

    public abstract JPanel getPanel();
}
