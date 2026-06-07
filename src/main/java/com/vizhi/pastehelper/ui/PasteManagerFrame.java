package com.vizhi.pastehelper.ui;

import com.vizhi.pastehelper.config.ConfigManage;
import com.vizhi.pastehelper.entity.PasteItem;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

@Slf4j
public class PasteManagerFrame extends JFrame {

    private final ConfigManage configManage;
    private JTable pasteTable;
    private DefaultTableModel tableModel;

    public PasteManagerFrame(ConfigManage configManage) {
        this.configManage = configManage;
        initUI();
    }

    private void initUI() {
        setTitle("粘贴助手 - 管理面板");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        mainPanel.add(createTablePanel(), BorderLayout.CENTER);
        mainPanel.add(createButtonPanel(), BorderLayout.SOUTH);

        add(mainPanel);
        refreshTable();
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columns = {"名称", "文本内容", "快捷键"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        pasteTable = new JTable(tableModel);
        pasteTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        pasteTable.setRowHeight(25);
        pasteTable.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(pasteTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JLabel label = new JLabel("当前粘贴项列表：");
        label.setFont(new Font("微软雅黑", Font.BOLD, 14));
        panel.add(label, BorderLayout.NORTH);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));

        JButton addButton = new JButton("添加新项");
        addButton.setPreferredSize(new Dimension(100, 35));
        addButton.addActionListener(e -> showAddDialog());

        JButton editButton = new JButton("编辑选中项");
        editButton.setPreferredSize(new Dimension(100, 35));
        editButton.addActionListener(e -> showEditDialog());

        JButton deleteButton = new JButton("删除选中项");
        deleteButton.setPreferredSize(new Dimension(100, 35));
        deleteButton.addActionListener(e -> deleteSelectedItem());

        panel.add(addButton);
        panel.add(editButton);
        panel.add(deleteButton);

        return panel;
    }

    private void showAddDialog() {
        AddEditDialog dialog = new AddEditDialog(this, null, configManage);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            refreshTable();
            JOptionPane.showMessageDialog(this, "添加成功！", "提示", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void showEditDialog() {
        int selectedRow = pasteTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请先选择要编辑的项！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<PasteItem> items = configManage.getPasteItems();
        if (selectedRow < items.size()) {
            PasteItem selectedItem = items.get(selectedRow);
            AddEditDialog dialog = new AddEditDialog(this, selectedItem, configManage);
            dialog.setVisible(true);

            if (dialog.isConfirmed()) {
                refreshTable();
                JOptionPane.showMessageDialog(this, "编辑成功！", "提示", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void deleteSelectedItem() {
        int selectedRow = pasteTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请先选择要删除的项！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "确定要删除选中的项吗？",
                "确认删除",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            List<PasteItem> items = configManage.getPasteItems();
            if (selectedRow < items.size()) {
                items.remove(selectedRow);
                configManage.saveConfig(items);
                refreshTable();
                JOptionPane.showMessageDialog(this, "删除成功！", "提示", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        List<PasteItem> items = configManage.getPasteItems();

        for (PasteItem item : items) {
            Object[] row = {
                    item.getName(),
                    item.getText(),
                    item.getShortcut()
            };
            tableModel.addRow(row);
        }
    }
}
