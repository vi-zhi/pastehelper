package com.vizhi.pastehelper.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author vizhi
 * 描述 :
 * @create 2026-06-03 21:22
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasteItem {

    private String name;

    private String text;

    private String shortcut;
}
