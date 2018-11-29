package me.Marni.CoolaxDomeGen;

import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.ps.PS;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class CoolaxDomeGen
        extends JavaPlugin
        implements Listener {

    private static CoolaxDomeGen instance;
    private gsonFunc gf = new gsonFunc();

    public static CoolaxDomeGen getInstance() {
        return instance;
    }

    public static List<Location> generateSphere(Location centerblock, int radius, boolean hollow, MPlayer player) {
        List<Location> circleBlocks = new ArrayList<>();
        int bX = centerblock.getBlockX();
        int bY = centerblock.getBlockY();
        int bZ = centerblock.getBlockZ();
        for (int x = bX - radius; x <= bX + radius; x++) {
            for (int y = bY - radius; y <= bY + radius; y++) {
                for (int z = bZ - radius; z <= bZ + radius; z++) {
                    double distance = (bX - x) * (bX - x) + (bZ - z) * (bZ - z) + (bY - y) * (bY - y);
                    if ((distance < radius * radius) && ((!hollow) || (distance >= (radius - 1) * (radius - 1)))) {
                        Location l = new Location(centerblock.getWorld(), x, y, z);
                        Faction blockfac = BoardColl.get().getFactionAt(PS.valueOf(l));
                        if ((blockfac.isNone()) || (blockfac.equals(player.getFaction()))) {
                            circleBlocks.add(l);
                        } else {
                            return new ArrayList<>();
                        }
                    }
                }
            }
        }
        return circleBlocks;
    }

    @Override
    public void onEnable() {
        instance = this;
        Bukkit.getServer().getLogger().info("CoolaxDomeGen enabled!");
        Bukkit.getServer().getPluginManager().registerEvents(this, this);
        FileManager fm = new FileManager();
        fm.setupFiles();
        new PlayerManager();
        gf.loadGson();

    }

    @EventHandler
    public void join(PlayerJoinEvent e) {
        if (!PlayerManager.players.contains(e.getPlayer().getUniqueId())) {
            ItemStack newItem = new ItemStack(Material.CHEST);
            ItemMeta nIM = newItem.getItemMeta();
            nIM.setDisplayName(ChatColor.RED + "Dome Generator");
            List<String> lore = new ArrayList<>();
            lore.add("Place this chest down to generate a dome!");
            nIM.setLore(lore);
            newItem.setItemMeta(nIM);
            e.getPlayer().getInventory().addItem(newItem);
            PlayerManager.players.add(e.getPlayer().getUniqueId());
            gf.refreshGson();
        }
    }

    @EventHandler
    public void onClick(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (e.getItem().getItemMeta().getDisplayName() == null) {
                return;
            }
            if ((e.getItem().getType().equals(Material.CHEST)) && (e.getItem().getItemMeta().getDisplayName().equals(ChatColor.RED + "Dome Generator"))) {
                e.getPlayer().setMetadata(e.getPlayer().getName(), new FixedMetadataValue(this, "placing"));
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void blockPlace(BlockPlaceEvent e) {
        Player p = e.getPlayer();
        MPlayer player = MPlayer.get(p.getUniqueId());
        if ((e.getBlock().getType().equals(Material.CHEST)) &&
                (e.getPlayer().hasMetadata(e.getPlayer().getName()))) {
            e.getPlayer().removeMetadata(e.getPlayer().getName(), this);
            if (generateSphere(p.getLocation(), 20, true, player) == null) {
                p.sendMessage(ChatColor.RED + "You are too close to another faction!");
                e.setCancelled(true);
            } else {
                try {
                    for (Location l : generateSphere(p.getLocation(), 15, true, player)) {
                        l.getBlock().setType(Material.GLASS);
                        e.getBlock().setType(Material.AIR);
                    }
                } catch (Exception ex) {
                    Bukkit.getLogger().info("Please inform marni!");
                }
            }
        }
    }
}
