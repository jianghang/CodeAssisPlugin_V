package com.github.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;

public class DataComparePanel extends BasePanel {

    public DataComparePanel(Project project, ToolWindow toolWindow) {
        super(project, toolWindow);
    }

    @Override
    protected BaseView createCodeAssistView() {
        return new DataCompareView(this, project, toolWindow);
    }
}
