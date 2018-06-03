package com.github.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;

public class CodeAssistVPanel extends BasePanel{

    public CodeAssistVPanel(Project project, ToolWindow toolWindow) {
        super(project, toolWindow);
    }

    @Override
    protected BaseView createCodeAssistView() {
        return new CodeAssistView(this,project,toolWindow);
    }
}
