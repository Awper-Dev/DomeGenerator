package me.Marni.CoolaxDomeGen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class gsonFunc {
    private static CoolaxDomeGen plugin = CoolaxDomeGen.getInstance();

    private Gson gson = new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create();
    private File file = new File(plugin.getDataFolder(), "playerdata.json");

    public void refreshGson() {
        UUID[] arraydata = PlayerManager.players.toArray(new UUID[0]);
        try {
            FileUtils.writeStringToFile(file, gson.toJson(arraydata), Charset.forName("UTF-8"), false);
        } catch (IOException e) {
            plugin.getLogger().warning(e.toString());
        }
    }
    public void loadGson() {
        File file = new File(plugin.getDataFolder(), "playerdata.json");
        String jsonstring = null;
        try {
            jsonstring = FileUtils.readFileToString(file);

        } catch (IOException e) {
            Bukkit.getLogger().info("Couldn't load playerdata.json");
            return;
        }
        UUID[] playerChallengeData = gson.fromJson(jsonstring, UUID[].class);
        if (!(playerChallengeData == null)) {
            Bukkit.getLogger().info("Loaded all disabled items!");
            List<UUID> uuids = new ArrayList<>(Arrays.asList(playerChallengeData));
            for (UUID di : uuids) {
                PlayerManager.players.add(di);
            }
        }
    }

}
