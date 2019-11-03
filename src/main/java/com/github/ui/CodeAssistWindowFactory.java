package com.github.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.openapi.wm.ex.ToolWindowManagerEx;
import com.intellij.openapi.wm.ex.ToolWindowManagerListener;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

public class CodeAssistWindowFactory implements ToolWindowFactory {

    public static final String TOOL_WINDOW_ID = "AssistV";

    private Project project;

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        this.project = project;
        toolWindow.setToHideOnEmptyContent(true);

        createContents(project, toolWindow);
        ToolWindowManager manager = ToolWindowManager.getInstance(project);
        if (manager instanceof ToolWindowManagerEx) {
            ToolWindowManagerEx managerEx = (ToolWindowManagerEx) manager;
            managerEx.addToolWindowManagerListener(new ToolWindowManagerListener() {
                @Override
                public void toolWindowRegistered(@NotNull String id) {

                }

                @Override
                public void stateChanged() {
                    ToolWindow window = ToolWindowManager.getInstance(project).getToolWindow(CodeAssistWindowFactory.TOOL_WINDOW_ID);
                    if (window != null) {
                        boolean visible = window.isVisible();
                        if (visible && toolWindow.getContentManager().getContentCount() == 0) {
                            createContents(project, window);
                        }
                    }
                }
            });
        }
    }

    private void createContents(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        CodeAssistVPanel codeAssistPanel = new CodeAssistVPanel(project, toolWindow);
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(codeAssistPanel, "CodeAssistV", false);
        toolWindow.getContentManager().addContent(content, 0);

//        SQLGeneratorPanel sqlGeneratorPanel = new SQLGeneratorPanel(project, toolWindow);
//        content = contentFactory.createContent(sqlGeneratorPanel, "SQLGenerator", false);
//        toolWindow.getContentManager().addContent(content, 1);

        DataComparePanel dataComparePanel = new DataComparePanel(project, toolWindow);
        content = contentFactory.createContent(dataComparePanel, "DataCompare", false);
        toolWindow.getContentManager().addContent(content, 1);
    }
}
