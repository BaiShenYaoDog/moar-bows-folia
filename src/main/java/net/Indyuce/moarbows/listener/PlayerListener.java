package net.Indyuce.moarbows.listener;

import net.Indyuce.moarbows.player.PlayerData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {
    @EventHandler
    public void a(PlayerJoinEvent event) {
        PlayerData.setup(event.getPlayer());
    }

    @EventHandler
    public void b(PlayerQuitEvent event) {
        PlayerData.get(event.getPlayer()).logOff();
    }
}
