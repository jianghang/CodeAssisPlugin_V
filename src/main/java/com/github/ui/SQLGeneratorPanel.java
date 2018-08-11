package com.github.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;

public class SQLGeneratorPanel extends BasePanel{

    public SQLGeneratorPanel(Project project, ToolWindow toolWindow) {
        super(project, toolWindow);
    }

    @Override
    protected BaseView createCodeAssistView() {
        return new SQLGeneratorView(this,project,toolWindow);
    }
}
