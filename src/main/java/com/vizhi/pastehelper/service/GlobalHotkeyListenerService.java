package com.vizhi.pastehelper.service;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.vizhi.pastehelper.config.ConfigManage;
import com.vizhi.pastehelper.entity.PasteItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author vizhi
 * 描述 :
 * @create 2026-06-05 20:25
 */
@Service
@Slf4j
public class GlobalHotkeyListenerService implements NativeKeyListener {

    @Autowired
    public List<PasteItem> pasteItems;

    private final Map<String, String> shortcutMap = new ConcurrentHashMap<>(); // 快捷键 -> 文本
    private boolean isCtrlPressed = false;
    private boolean isShiftPressed = false;
    private boolean isAltPressed = false;
    private boolean isRobotOperating = false;
    private String pendingText = null; // 待执行的粘贴文本

    /**
     * 初始化
     */
    @PostConstruct
    public void init() {
        try {
            GlobalScreen.registerNativeHook();
            GlobalScreen.addNativeKeyListener(this);
            List2Map();
            log.info("全局键盘监听器启动成功");
        } catch (NativeHookException e) {
            log.error("全局键盘监听器启动失败", e);
            throw new RuntimeException(e);
        }
    }

    public void List2Map() {
        for (PasteItem pasteItem : pasteItems) {
            shortcutMap.put(pasteItem.getShortcut(), pasteItem.getText());
        }
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent nativeEvent) {

        if (isRobotOperating) {
            return;
        }

        log.info("按键按下: " + NativeKeyEvent.getKeyText(nativeEvent.getKeyCode()));

        switch (nativeEvent.getKeyCode()) {
            case NativeKeyEvent.VC_CONTROL:
                isCtrlPressed = true;
                break;
            case NativeKeyEvent.VC_SHIFT:
                isShiftPressed = true;
                break;
            case NativeKeyEvent.VC_ALT:
                isAltPressed = true;
                break;
        }

        if (isShiftPressed && isCtrlPressed) {
            extracted(nativeEvent, "ctrl+shift+");
        } else if (isAltPressed && isCtrlPressed) {
            extracted(nativeEvent, "ctrl+alt+");
        } else{
            log.info("");
        }

    }

    private void extracted(NativeKeyEvent nativeEvent, String prem) {

        int keyCode = nativeEvent.getKeyCode();

        if (keyCode >= NativeKeyEvent.VC_1 && keyCode <= NativeKeyEvent.VC_9) {

            StringBuilder sb = new StringBuilder();
            sb.append(prem);
            sb.append(keyCode - NativeKeyEvent.VC_1 + 1);

            String text = shortcutMap.get(sb.toString());

            if (text != null && !text.isEmpty()) {
                log.info("检测到快捷键: {}, 准备执行粘贴", sb);
                pendingText = text; // 记录待粘贴文本，等待按键释放后执行
            }
        }
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent nativeEvent) {
        
        if (isRobotOperating) {
            return;
        }
        
        log.info("按键松开: " + NativeKeyEvent.getKeyText(nativeEvent.getKeyCode()));

        switch (nativeEvent.getKeyCode()) {
            case NativeKeyEvent.VC_CONTROL:
                isCtrlPressed = false;
                break;
            case NativeKeyEvent.VC_SHIFT:
                isShiftPressed = false;
                break;
            case NativeKeyEvent.VC_ALT:
                isAltPressed = false;
                break;
        }

        // 当所有修饰键都释放后，执行待处理的粘贴操作
        if (!isCtrlPressed && !isShiftPressed && !isAltPressed && pendingText != null) {
            String textToPaste = pendingText;
            pendingText = null; // 清空待处理文本
            
            // 延迟一小段时间确保系统完全稳定
            new Thread(() -> {
                try {
                    Thread.sleep(50);
                    copyAndPaste(textToPaste);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        }

        log.debug("按键释放");
    }
    private void copyAndPaste(String text) {
        try {
            isRobotOperating = true;
            
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

            StringSelection selection = new StringSelection(text);
            clipboard.setContents(selection, null);

            Thread.sleep(100);

            Robot robot = new Robot();
            robot.delay(50);

            robot.keyPress(KeyEvent.VK_CONTROL);
            robot.keyPress(KeyEvent.VK_V);

            robot.delay(50);

            robot.keyRelease(KeyEvent.VK_V);
            robot.keyRelease(KeyEvent.VK_CONTROL);

            robot.delay(100);
            
            log.info("粘贴完成");
        } catch (AWTException e) {
            log.error("创建 Robot 失败", e);
        } catch (InterruptedException e) {
            log.error("操作被中断", e);
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            log.error("粘贴操作失败", e);
        } finally {
            isRobotOperating = false;
        }
    }


}
