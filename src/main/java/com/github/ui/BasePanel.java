package com.github.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.wm.ToolWindow;

import javax.swing.*;

public abstract class BasePanel extends SimpleToolWindowPanel {

    protected ToolWindow toolWindow;
    protected Project project;

    protected JPanel jPanel;
    protected BaseView baseView;

    public BasePanel(boolean vertical) {
        super(vertical);
        initUI();
    }

    public BasePanel(boolean vertical, boolean borderless) {
        super(vertical, borderless);
        initUI();
    }

    public BasePanel(Project project,ToolWindow toolWindow){
        super(false,true);
        this.project = project;
        this.toolWindow = toolWindow;
        initUI();
    }

    private void initUI() {
        baseView = createCodeAssistView();
        setContent(baseView.getPanel());
    }

    protected abstract BaseView createCodeAssistView();
}
