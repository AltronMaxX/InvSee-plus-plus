package com.janboerman.invsee.spigot.internal;

public class PlayerFileHelper {

    private PlayerFileHelper() {}

    public static boolean isPlayerSaveFile(String fileName) {
        return fileName.length() == 40 && fileName.endsWith(".dat");
    }
}
