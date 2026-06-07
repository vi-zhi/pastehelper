package com.vizhi.pastehelper.ui;

import com.vizhi.pastehelper.config.ConfigManage;
import com.vizhi.pastehelper.entity.PasteItem;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AddEditDialog extends JDialog {

    private final ConfigManage configManage;
    private final PasteItem existingItem;
    private boolean confirmed = false;

    private JTextField nameField;
    private JTextArea textArea;
    // 快捷键选择框
    private JComboBox<String> shortcutCombo;

    // 构造函数
    public AddEditDialog(Frame parent, PasteItem existingItem, ConfigManage configManage) {

        super(parent, existingItem == null ? "添加新粘贴项" : "编辑粘贴项", true);
        this.existingItem = existingItem;
        this.configManage = configManage;

        initUI();

        if (existingItem != null) {
            fillData();
        }
    }

    private void initUI() {
        setSize(500, 400);
        setLocationRelativeTo(getParent());
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        mainPanel.add(createInputPanel(), BorderLayout.CENTER);
        mainPanel.add(createButtonPanel(), BorderLayout.SOUTH);

        add(mainPanel);
    }

    // 创建输入面板
    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        // 创建网格布局
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        panel.add(new JLabel("名称："), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        nameField = new JTextField(20);
        panel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        panel.add(new JLabel("文本内容："), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;
        textArea = new JTextArea(5, 20);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(textArea);
        panel.add(scrollPane, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(new JLabel("快捷键："), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        String[] shortcuts = {
                "ctrl+alt+1", "ctrl+alt+2", "ctrl+alt+3", "ctrl+alt+4", "ctrl+alt+5",
                "ctrl+shift+1", "ctrl+shift+2", "ctrl+shift+3", "ctrl+shift+4", "ctrl+shift+5"
        };
        shortcutCombo = new JComboBox<>(shortcuts);
        panel.add(shortcutCombo, gbc);

        return panel;
    }

    // 创建按钮面板
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));

        JButton saveButton = new JButton("保存");
        saveButton.setPreferredSize(new Dimension(80, 30));
        saveButton.addActionListener(e -> saveAndClose());

        JButton cancelButton = new JButton("取消");
        cancelButton.setPreferredSize(new Dimension(80, 30));
        cancelButton.addActionListener(e -> dispose());

        panel.add(saveButton);
        panel.add(cancelButton);

        return panel;
    }

    // 填充数据
    private void fillData() {
        nameField.setText(existingItem.getName());
        textArea.setText(existingItem.getText());
        shortcutCombo.setSelectedItem(existingItem.getShortcut());
    }

    private void saveAndClose() {
        String name = nameField.getText().trim();
        String text = textArea.getText().trim();
        String shortcut = (String) shortcutCombo.getSelectedItem();

        if (name.isEmpty() || text.isEmpty()) {
            JOptionPane.showMessageDialog(this, "名称和文本内容不能为空！", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (existingItem == null) {
            PasteItem newItem = new PasteItem(name, text, shortcut);
            configManage.addPasteItem(newItem);
        } else {
            existingItem.setName(name);
            existingItem.setText(text);
            existingItem.setShortcut(shortcut);

            List<PasteItem> pasteItems = configManage.getPasteItems();

            for(PasteItem item :pasteItems){
                configManage.addPasteItem(item);
            }
        }

        confirmed = true;
        dispose();
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}
