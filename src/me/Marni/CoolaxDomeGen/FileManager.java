package me.Marni.CoolaxDomeGen;

import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;

public class FileManager {
    private CoolaxDomeGen plugin = CoolaxDomeGen.getInstance();
    private File datFile;

    public void setupFiles(){
        if(!plugin.getDataFolder().exists()){
            plugin.getDataFolder().mkdir();
        }
        datFile = new File(plugin.getDataFolder(), "playerdata.json");

        if(!datFile.exists()){
            try{
                datFile.createNewFile();
            }catch (IOException e){
                Bukkit.getLogger().warning("Failed to create playerdata.json !");
            }
        }

    }
}
