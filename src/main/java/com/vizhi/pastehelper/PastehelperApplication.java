package com.vizhi.pastehelper;

import com.vizhi.pastehelper.config.ConfigManage;
import com.vizhi.pastehelper.ui.PasteManagerFrame;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class PastehelperApplication {

    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "false");

        ConfigurableApplicationContext context = SpringApplication.run(PastehelperApplication.class, args);

        ConfigManage configManage = context.getBean(ConfigManage.class);

        javax.swing.SwingUtilities.invokeLater(() -> {
            PasteManagerFrame frame = new PasteManagerFrame(configManage);
            frame.setVisible(true);
        });
    }

}
