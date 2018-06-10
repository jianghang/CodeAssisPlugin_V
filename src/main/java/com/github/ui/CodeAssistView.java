package com.github.ui;

import com.github.db.DBUtils;
import com.github.db.DatabaseTypeEnum;
import com.github.utils.CodeStringUtils;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.ide.util.PackageChooserDialog;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.fileTypes.PlainTextFileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.PsiPackage;
import com.intellij.ui.EditorTextField;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Objects;
import java.util.logging.Logger;

public class CodeAssistView extends BaseView {

    private static Logger logger = Logger.getLogger("CodeAssistView");

    private static final String DATABASE_TYPE = "databaseType";
    private static final String DATA_URL = "dataUrl";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String PACKAGE_NAME = "packageName";
    private static final String CLASS_NAME = "className";
    private static final String SQL_CONTENT = "sqlContent";

    private static String annotationStr = "";

    private PsiPackage psiPackage;

    private JPanel sqlInputContent;
    private EditorTextField sqlEditor;
    private JButton convertButton;
    private EditorTextField javaEditor;
    private JComboBox dataBaseComboBox;
    private JTextField dataBaseUrl;
    private JTextField userNameField;
    private JPasswordField passwordField;
    private JTextField packageNameField;
    private JTextField classNameField;
    private JTextField filePath;
    private JButton fileButton;
    private JRadioButton xmlRadioButton;
    private JRadioButton jsonRadioButton;
    private JTabbedPane tabbedPane1;
    private JTabbedPane tabbedPane2;
    private EditorTextField mapperEditor;
    private JRadioButton unwantedRadioButton;

    public CodeAssistView(BasePanel basePanel, Project project, ToolWindow toolWindow) {
        super(basePanel, project, toolWindow);
        initListener();
        initState();
    }

    private void initState() {
        PropertiesComponent component = PropertiesComponent.getInstance(this.project);
        String databaseType = component.getValue(DATABASE_TYPE);
        if (StringUtils.isNotEmpty(databaseType)) {
            logger.info("databaseType: " + databaseType + " " + DatabaseTypeEnum.getIndexByType(databaseType));
            dataBaseComboBox.setSelectedIndex(DatabaseTypeEnum.getIndexByType(databaseType));
        }
        String dataUrl = component.getValue(DATA_URL);
        if (StringUtils.isNotEmpty(dataUrl)) {
            dataBaseUrl.setText(dataUrl);
        }
        String username = component.getValue(USERNAME);
        if (StringUtils.isNotEmpty(username)) {
            userNameField.setText(username);
        }
        String password = component.getValue(PASSWORD);
        if (StringUtils.isNotEmpty(password)) {
            passwordField.setText(password);
        }
        String packageName = component.getValue(PACKAGE_NAME);
        if (StringUtils.isNotEmpty(packageName)) {
            packageNameField.setText(packageName);
        }
        String className = component.getValue(CLASS_NAME);
        if (StringUtils.isNotEmpty(className)) {
            classNameField.setText(className);
        }
        String sqlContent = component.getValue(SQL_CONTENT);
        if (StringUtils.isNotEmpty(sqlContent)) {
            sqlEditor.setText(sqlContent);
        }
    }

    private void initListener() {
        convertButton.addActionListener(e -> {
            String databaseType = Objects.requireNonNull(dataBaseComboBox.getSelectedItem()).toString();
            String dataUrl = dataBaseUrl.getText();
            String username = userNameField.getText();
            char[] passwordChar = passwordField.getPassword();
            String password = String.valueOf(passwordChar);
            String packageName = packageNameField.getText();
            String className = classNameField.getText();

            logger.info("databaseType: " + databaseType);
            logger.info("dataUrl: " + dataUrl + " username: " + username + " password: " + password);
            logger.info("packageName: " + packageName + " className: " + className);
            logger.info("dataBaseComboBox Index: " + dataBaseComboBox.getSelectedIndex());
            logger.info("Annotation: " + annotationStr);
            boolean checkResult = CodeStringUtils.checkStringsEmpty(dataUrl, username, password, packageName, className);
            if (checkResult) {
                showMessage("有必填字段为空");
                return;
            }

            loadDatabaseDriver(databaseType);
            Connection conn = null;
            String sqlContent = null;
            try {
                conn = DBUtils.getConnection(project.getName(), dataUrl, username, password);
                sqlContent = sqlEditor.getText();
                if (StringUtils.isEmpty(sqlContent)) {
                    showMessage("SQL语句为空");
                    return;
                }
                ResultSetMetaData metaData = DBUtils.getResultSetMetaData(conn, sqlContent);
                String javaCode = Objects.requireNonNull(DatabaseTypeEnum.getDatabaseTypeEnumByType(databaseType))
                        .buildJavaCode(metaData, className, packageName, annotationStr);
                javaEditor.setText(javaCode);

                if (StringUtils.isNotEmpty(filePath.getText())) {
                    createFileInWriteCommandAction(psiPackage, className, javaCode);
                }
            } catch (ClassNotFoundException | SQLException e1) {
                e1.printStackTrace();
                showMessage(e1.getMessage());
            } finally {
                try {
                    conn.close();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }

            saveCurrentState(databaseType, dataUrl, username, password, packageName, className, sqlContent);
        });

//        FileChooserDescriptor fileChooserDescriptor = new FileChooserDescriptor(true,true,true,true,true,false);
//        TextBrowseFolderListener folderListener = new TextBrowseFolderListener(fileChooserDescriptor,project);
//        fileButton.addBrowseFolderListener(folderListener);

        fileButton.addActionListener(e -> {
            PackageChooserDialog packageChooserDialog = new PackageChooserDialog("Select Package Path", project);
            packageChooserDialog.show();
            psiPackage = packageChooserDialog.getSelectedPackage();
            if (Objects.nonNull(psiPackage)) {
                filePath.setText(psiPackage.getQualifiedName());
            }
        });

        xmlRadioButton.addActionListener(e -> annotationStr = xmlRadioButton.getText());

        jsonRadioButton.addActionListener(e -> annotationStr = jsonRadioButton.getText());

        unwantedRadioButton.addActionListener(e -> annotationStr = unwantedRadioButton.getText());
    }

    @Override
    public JPanel getPanel() {
        return sqlInputContent;
    }

    private void createFileInWriteCommandAction(PsiPackage psiPackage, String className, String javaSource) {
        final String fileName = className + JavaFileType.DOT_DEFAULT_EXTENSION;
        PsiDirectory psiDirectory = psiPackage.getDirectories()[0];
        PsiFile psiFile = psiDirectory.findFile(fileName);
        if (Objects.nonNull(psiFile)) {
            showMessage(fileName + " file is exist!");
            return;
        }

        WriteCommandAction.runWriteCommandAction(project, () -> {
            PsiFileFactory psiFileFactory = PsiFileFactory.getInstance(project);
            PsiFile psiFileContent = psiFileFactory.createFileFromText(fileName, JavaFileType.INSTANCE, javaSource);
            PsiDirectory[] psiDirectories = psiPackage.getDirectories();
            logger.info("directory name: " + psiDirectories[0].getName());

            psiDirectories[0].add(psiFileContent);
        });
    }

    private void showMessage(String content) {
        Icon icon = new ImageIcon(getClass().getResource("/myToolWindow/plus.png"));
        Messages.showMessageDialog(project, content, "CodeAssist", icon);
    }

    private void saveCurrentState(String databaseType, String dataUrl, String username, String password, String packageName, String className, String sqlContent) {
        PropertiesComponent propertiesComponent = PropertiesComponent.getInstance(project);
        propertiesComponent.setValue(DATABASE_TYPE, databaseType);
        propertiesComponent.setValue(DATA_URL, dataUrl);
        propertiesComponent.setValue(USERNAME, username);
        propertiesComponent.setValue(PASSWORD, password);
        propertiesComponent.setValue(PACKAGE_NAME, packageName);
        propertiesComponent.setValue(CLASS_NAME, className);
        propertiesComponent.setValue(SQL_CONTENT, sqlContent);
    }

    private void loadDatabaseDriver(String dataType) {
        String databaseDriver = DatabaseTypeEnum.getDriverByType(dataType);
        try {
            Class.forName(databaseDriver);
        } catch (ClassNotFoundException e) {
            showMessage("加载驱动失败");
            e.printStackTrace();
        }
    }

    private void createUIComponents() {
        javaEditor = new EditorTextField("", project, JavaFileType.INSTANCE) {
            @Override
            protected EditorEx createEditor() {
                EditorEx editorEx = super.createEditor();
                editorEx.setVerticalScrollbarVisible(true);
                editorEx.setHorizontalScrollbarVisible(true);
                return editorEx;
            }
        };
        javaEditor.setOneLineMode(false);

        sqlEditor = new EditorTextField("", project, PlainTextFileType.INSTANCE) {
            @Override
            protected EditorEx createEditor() {
                EditorEx editorEx = super.createEditor();
                editorEx.setVerticalScrollbarVisible(true);
                editorEx.setHorizontalScrollbarVisible(true);
                return editorEx;
            }
        };
        sqlEditor.setOneLineMode(false);

        mapperEditor = new EditorTextField("", project, XmlFileType.INSTANCE) {
            @Override
            protected EditorEx createEditor() {
                EditorEx editorEx = super.createEditor();
                editorEx.setVerticalScrollbarVisible(true);
                editorEx.setHorizontalScrollbarVisible(true);
                return editorEx;
            }
        };
        mapperEditor.setOneLineMode(false);
    }
}
