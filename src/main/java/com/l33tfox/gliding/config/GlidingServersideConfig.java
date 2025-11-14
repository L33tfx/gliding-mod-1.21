package com.l33tfox.gliding.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

// Config screen set up using AutoConfig which is bundled with ClothConfig API
// Is set up on the server side to control whether holding gliders in off hand allows gliding
@Config(name = "gliding")
public class GlidingServersideConfig implements ConfigData {

    @ConfigEntry.Gui.Tooltip
    public boolean offHandEnabled = true;
}
