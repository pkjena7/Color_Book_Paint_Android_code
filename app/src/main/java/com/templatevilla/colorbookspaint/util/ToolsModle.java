package com.templatevilla.colorbookspaint.util;

public class ToolsModle {

  private String action;
 public    int icon,unSelectedIcon;
    public int action_code;

    public ToolsModle(String action,int icon,int unSelectedIcon,int action_code){
        this.action = action;
        this.icon = icon;
        this.unSelectedIcon = unSelectedIcon;
        this.action_code = action_code;
    }
}
