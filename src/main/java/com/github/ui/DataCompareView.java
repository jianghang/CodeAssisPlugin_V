package com.github.ui;

import com.github.db.DBUtils;
import com.github.db.DatabaseTypeEnum;
import com.github.model.DbColumnInfo;
import com.github.param.GlobalParameter;
import com.github.utils.MessageUtils;
import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.fileTypes.PlainTextFileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.EditorTextField;
import com.sun.jna.platform.win32.GL;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class DataCompareView extends BaseView {

    private JPanel dataComparePanel;
    private JComboBox dataBaseComboBox;
    private JTextField sourceDataLinkField;
    private JTextField targetDataLinkField;
    private JTextField targetUserNameField;
    private JTextField sourceUserNameField;
    private JPasswordField targetPasswordField;
    private JPasswordField sourcePasswordField;
    private EditorTextField compareInfoEditor;
    private JButton beginCompareButton;

    public DataCompareView(BasePanel basePanel, Project project, ToolWindow toolWindow) {
        super(basePanel, project, toolWindow);
        initState();
        initListener();
    }

    private void initListener() {
        beginCompareButton.addActionListener(e -> {
            compareInfoEditor.setText("开始比对....");
            String databaseType = Objects.requireNonNull(dataBaseComboBox.getSelectedItem()).toString();
            String sourceDataLink = sourceDataLinkField.getText();
            String sourceUserName = sourceUserNameField.getText();
            String sourcePassword = String.valueOf(sourcePasswordField.getPassword());

            String targetDataLink = targetDataLinkField.getText();
            String targetUserName = targetUserNameField.getText();
            String targetPassword = String.valueOf(targetPasswordField.getPassword());

            String sourceDatabaseName = Splitter.on("/").splitToList(sourceDataLink).get(1);
            String targetDatabaseName = Splitter.on("/").splitToList(targetDataLink).get(1);
            DbUtils.loadDriver(DatabaseTypeEnum.getDriverByType(databaseType));
            String sourceDataLinkStr = String.format(GlobalParameter.MYSQL_LINK_URL, sourceDataLink);
            String targetDataLinkStr = String.format(GlobalParameter.MYSQL_LINK_URL, targetDataLink);

            try (Connection targetConn = DriverManager.getConnection(targetDataLinkStr, targetUserName, targetPassword);
                 Connection sourceConn = DriverManager.getConnection(sourceDataLinkStr, sourceUserName, sourcePassword)) {
                QueryRunner runner = new QueryRunner();
                List<DbColumnInfo> dbColumnInfoList = runner.query(targetConn, GlobalParameter.SELECT_TABLE_INFO_SQL,
                        new BeanListHandler<>(DbColumnInfo.class), targetDatabaseName);
                Table<String, String, DbColumnInfo> targetTable = HashBasedTable.create();
                DBUtils.handleColumnInfoList(dbColumnInfoList, targetTable);

                dbColumnInfoList = runner.query(sourceConn, GlobalParameter.SELECT_TABLE_INFO_SQL,
                        new BeanListHandler<>(DbColumnInfo.class), sourceDatabaseName);
                Table<String, String, DbColumnInfo> sourceTable = HashBasedTable.create();
                DBUtils.handleColumnInfoList(dbColumnInfoList, sourceTable);

                Set<String> sourceTableNameSet = sourceTable.rowKeySet();
                sourceTableNameSet.forEach(tableName -> {
                    if (targetTable.containsRow(tableName)) {
                        List<String> messageList = DBUtils.compareColumnInfo(sourceTable, targetTable, tableName);
                        if (!CollectionUtils.isEmpty(messageList)) {
                            String messageStr = Joiner.on("\n").join(messageList);
                            messageStr = Joiner.on("\n").join(compareInfoEditor.getText(), messageStr);
                            compareInfoEditor.setText(messageStr);
                        }
                    } else {
                        String str = "源数据库[%s]表[%s]存在，目标数据库[%s]不存在";
                        str = String.format(str, sourceDatabaseName, tableName, targetDatabaseName);
                        str = Joiner.on("\n").join(compareInfoEditor.getText(), str);
//                        str = new String(str.getBytes(), Charsets.UTF_8);
                        compareInfoEditor.setText(str);
                    }
                });
            } catch (SQLException ex) {
                MessageUtils.showMessage(ex.getMessage(), project);
                ex.printStackTrace();
            }
            saveCurrentState(sourceDataLink, sourceUserName, sourcePassword,
                    targetDataLink, targetUserName, targetPassword);
        });
    }

    private void saveCurrentState(String sourceDataLinkStr, String sourceUserName, String sourcePassword, String targetDataLinkStr, String targetUserName, String targetPassword) {
        PropertiesComponent propertiesComponent = PropertiesComponent.getInstance(project);
        propertiesComponent.setValue(GlobalParameter.SOURCE_DATA_LINK_KEY, sourceDataLinkStr);
        propertiesComponent.setValue(GlobalParameter.SOURCE_USER_NAME_KEY, sourceUserName);
        propertiesComponent.setValue(GlobalParameter.SOURCE_PASSWORD_KEY, sourcePassword);

        propertiesComponent.setValue(GlobalParameter.TARGET_DATA_LINK_KEY, targetDataLinkStr);
        propertiesComponent.setValue(GlobalParameter.TARGET_USER_NAME_KEY, targetUserName);
        propertiesComponent.setValue(GlobalParameter.TARGET_PASSWORD_KEY, targetPassword);
    }

    private void initState() {
        PropertiesComponent component = PropertiesComponent.getInstance(this.project);
        String sourceDataLinkStr = component.getValue(GlobalParameter.SOURCE_DATA_LINK_KEY);
        sourceDataLinkField.setText(sourceDataLinkStr);
        sourceUserNameField.setText(component.getValue(GlobalParameter.SOURCE_USER_NAME_KEY));
        sourcePasswordField.setText(component.getValue(GlobalParameter.SOURCE_PASSWORD_KEY));

        targetDataLinkField.setText(component.getValue(GlobalParameter.TARGET_DATA_LINK_KEY));
        targetUserNameField.setText(component.getValue(GlobalParameter.TARGET_USER_NAME_KEY));
        targetPasswordField.setText(component.getValue(GlobalParameter.TARGET_PASSWORD_KEY));
    }

    @Override
    public JPanel getPanel() {
        return dataComparePanel;
    }

    private void createUIComponents() {
        compareInfoEditor = new EditorTextField("compareInfo", project, PlainTextFileType.INSTANCE) {
            @Override
            protected EditorEx createEditor() {
                EditorEx editorEx = super.createEditor();
                editorEx.setVerticalScrollbarVisible(true);
                editorEx.setHorizontalScrollbarVisible(true);
                return editorEx;
            }
        };
        compareInfoEditor.setOneLineMode(false);
    }
}
