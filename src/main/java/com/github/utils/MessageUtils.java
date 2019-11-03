package com.github.utils;

import com.github.common.CodeAssistIcons;
import com.github.common.CommonConstant;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;

import javax.swing.*;

public class MessageUtils {

    public static void showMessage(String content, Project project) {
        Icon icon = CodeAssistIcons.message;
        Messages.showMessageDialog(project, content, CommonConstant.MESSAGE_TITLE, icon);
    }
}
