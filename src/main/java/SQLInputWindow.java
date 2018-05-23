import com.github.db.DBUtils;
import com.github.db.DatabaseTypeEnum;
import com.intellij.ide.util.PackageChooserDialog;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.ide.util.TreeFileChooser;
import com.intellij.ide.util.TreeFileChooserFactory;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.psi.PsiPackage;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Objects;
import java.util.logging.Logger;

public class SQLInputWindow implements ToolWindowFactory {

    private static Logger logger = Logger.getLogger("SQLInputWindow");

    private static final String DATABASE_TYPE = "databaseType";
    private static final String DATA_URL = "dataUrl";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String PACKAGE_NAME = "packageName";
    private static final String CLASS_NAME = "className";
    private static final String SQL_CONTENT = "sqlContent";

    private Project project;

    private JPanel sqlInputContent;
    private JEditorPane sqlEditor;
    private JButton convertButton;
    private JEditorPane javaEditor;
    private JComboBox dataBaseComboBox;
    private JTextField dataBaseUrl;
    private JTextField userNameField;
    private JPasswordField passwordField;
    private JTextField packageNameField;
    private JTextField classNameField;
    private JLabel saveUri;
    private JTextField filePath;
    private JButton fileButton;
    private ToolWindow toolWindow;

    public SQLInputWindow() {

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
            String checkResult = com.github.utils.StringUtils.checkStringsEmpty(dataUrl,username,password,packageName,className);
            if(!StringUtils.isEmpty(checkResult)){
                showMessage("有必填字段为空");
                return;
            }

            loadDatabaseDriver(databaseType);
            Connection conn;
            String sqlContent = null;
            try {
                conn = DBUtils.getConnection(dataUrl,username,password);
                sqlContent = sqlEditor.getText();
                if(StringUtils.isEmpty(sqlContent)){
                    showMessage("SQL语句为空");
                    return;
                }
                ResultSetMetaData metaData = DBUtils.getResultSetMetaData(conn,sqlContent);
                String javaCode = Objects.requireNonNull(DatabaseTypeEnum.getDatabaseTypeEnumByType(databaseType)).buildJavaCode(metaData,className,packageName,"");
                javaEditor.setText(javaCode);
            } catch (ClassNotFoundException | SQLException e1) {
                e1.printStackTrace();
                showMessage(e1.getMessage());
            }

            saveCurrentState(databaseType,dataUrl,username,password,packageName,className,sqlContent);
        });

//        FileChooserDescriptor fileChooserDescriptor = new FileChooserDescriptor(true,true,true,true,true,false);
//        TextBrowseFolderListener folderListener = new TextBrowseFolderListener(fileChooserDescriptor,project);
//        fileButton.addBrowseFolderListener(folderListener);

        fileButton.addActionListener(e -> {
            PackageChooserDialog packageChooserDialog = new PackageChooserDialog("Select Package",project);
            packageChooserDialog.show();
            PsiPackage psiPackage = packageChooserDialog.getSelectedPackage();
            filePath.setText(psiPackage.getQualifiedName());
        });
    }

    private void saveCurrentState(String databaseType, String dataUrl, String username, String password, String packageName, String className,String sqlContent) {
        PropertiesComponent propertiesComponent = PropertiesComponent.getInstance(project);
        propertiesComponent.setValue(DATABASE_TYPE,databaseType);
        propertiesComponent.setValue(DATA_URL,dataUrl);
        propertiesComponent.setValue(USERNAME,username);
        propertiesComponent.setValue(PASSWORD,password);
        propertiesComponent.setValue(PACKAGE_NAME,packageName);
        propertiesComponent.setValue(CLASS_NAME,className);
        propertiesComponent.setValue(SQL_CONTENT,sqlContent);
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

    private void showMessage(String content) {
        Icon icon = new ImageIcon(getClass().getResource("/myToolWindow/plus.png"));
        Messages.showMessageDialog(content,"SQL",icon);
    }

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        this.project = project;
        this.toolWindow = toolWindow;
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(sqlInputContent, "", false);
        toolWindow.getContentManager().addContent(content);

        PropertiesComponent component = PropertiesComponent.getInstance(this.project);
        String databaseType = component.getValue(DATABASE_TYPE);
        if(StringUtils.isNotEmpty(databaseType)){
            logger.info("databaseType: " + databaseType + " " + DatabaseTypeEnum.getIndexByType(databaseType));
            dataBaseComboBox.setSelectedItem(DatabaseTypeEnum.getIndexByType(databaseType));
        }
        String dataUrl = component.getValue(DATA_URL);
        if(StringUtils.isNotEmpty(dataUrl)){
            dataBaseUrl.setText(dataUrl);
        }
        String username = component.getValue(USERNAME);
        if(StringUtils.isNotEmpty(username)){
            userNameField.setText(username);
        }
        String password = component.getValue(PASSWORD);
        if(StringUtils.isNotEmpty(password)){
            passwordField.setText(password);
        }
        String packageName = component.getValue(PACKAGE_NAME);
        if(StringUtils.isNotEmpty(packageName)){
            packageNameField.setText(packageName);
        }
        String className = component.getValue(CLASS_NAME);
        if(StringUtils.isNotEmpty(className)){
            classNameField.setText(className);
        }
        String sqlContent = component.getValue(SQL_CONTENT);
        if(StringUtils.isNotEmpty(sqlContent)){
            sqlEditor.setText(sqlContent);
        }
    }
}