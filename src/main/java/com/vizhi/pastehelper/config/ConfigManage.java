package com.vizhi.pastehelper.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.vizhi.pastehelper.entity.PasteItem;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author vizhi
 * 描述 :
 * @create 2026-06-03 21:30
 */
@Configuration
@Slf4j
public class ConfigManage {

    @Getter
    private List<PasteItem> pasteItems;
    private final Gson gson;
    public ConfigManage() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.pasteItems = loadConfig();
    }

    private static final String CONFIG_DIR = "D:" + File.separator + "message_copy";
    private static final String CONFIG_FILE = CONFIG_DIR + File.separator + "paste_config.json";

    //加载文件内容到pasteItems
    private List<PasteItem> loadConfig() {
        Path path = Paths.get(CONFIG_FILE);

        if (Files.exists(path)) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                Type type = new TypeToken<List<PasteItem>>(){}.getType();
                List<PasteItem> items = gson.fromJson(reader, type);
                log.info("成功加载配置文件，共 " + items.size() + " 个粘贴项" + items.toString());
                return items;
            } catch (IOException e) {
               log.info("读取配置文件失败: " + e.getMessage());
                return createDefaultConfig();
            }
        } else {
            log.warn("配置文件不存在，创建默认配置");
            return createDefaultConfig();
        }
    }

    private List<PasteItem> createDefaultConfig() {

        List<PasteItem> pasteItemList = new ArrayList<>();

        PasteItem pasteItem = new PasteItem();
        pasteItem.setName("default");
        pasteItem.setText("default");
        pasteItem.setShortcut("ctrl+alt+1");
        pasteItemList.add(pasteItem);

        saveConfig(pasteItemList);

        return pasteItemList;
    }

    //一次保存pasteItems
    private void saveConfig(List<PasteItem> pasteItems) {
        try {
            Files.createDirectories(Paths.get(CONFIG_DIR));

            try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
                gson.toJson(pasteItems, writer);
                log.info("配置文件已保存");
            }
        } catch (IOException e) {
            log.error("保存配置文件失败: " + e.getMessage());
        }
    }

    @Bean
    public List<PasteItem> pasteItems(){
        return pasteItems;
    }

    public void addPasteItem(PasteItem pasteItem){
        pasteItems.add(pasteItem);
        saveConfig(pasteItems);
    }

}





















