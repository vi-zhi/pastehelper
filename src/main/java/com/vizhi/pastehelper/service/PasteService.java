package com.vizhi.pastehelper.service;

import com.vizhi.pastehelper.config.ConfigManage;
import com.vizhi.pastehelper.entity.PasteItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author vizhi
 * 描述 :
 * @create 2026-06-04 20:11
 */
@Service
public class PasteService {

    @Autowired
    public List<PasteItem> pasteItems;
    @Autowired
    public ConfigManage configManage;

    public void add(){
        List<PasteItem> pasteItemList = new ArrayList<>();

        PasteItem pasteItem = new PasteItem();
        pasteItem.setName("default");
        pasteItem.setText("default");
        pasteItem.setShortcut("ctrl+alt+1");

        configManage.addPasteItem(pasteItem);
    }

}
