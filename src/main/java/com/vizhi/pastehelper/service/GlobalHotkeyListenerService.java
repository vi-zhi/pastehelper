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

        log.info("按键: " + NativeKeyEvent.getKeyText(nativeEvent.getKeyCode()));

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

            log.info(sb.toString());
            String text = shortcutMap.get(sb.toString());
            log.info("文本: " + text);
            if (text != null && !text.isEmpty()) {
                log.info("检测到快捷键: {}, 执行粘贴", sb);
                copyAndPaste(text);
            }
        }
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent nativeEvent) {

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

        log.debug("按键释放");
    }
    private void copyAndPaste(String text) {
        try {
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

            log.info("粘贴完成");
        } catch (AWTException e) {
            log.error("创建 Robot 失败", e);
        } catch (InterruptedException e) {
            log.error("操作被中断", e);
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            log.error("粘贴操作失败", e);
        }
    }


}
