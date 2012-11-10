package cam.listener;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import cam.Likeaboss;

import com.nisovin.magicspells.events.SpellCastEvent;

public class MagicSpellsListener implements Listener
{
    private Likeaboss plugin;
    private List<String> disabled, disabledOnBoss, disabledOnSwarm;
    
    public MagicSpellsListener(Likeaboss plugin)
    {
        this.plugin = plugin;
        
        // Set up the MagicSpells config-file.
        //File spellFile = FileUtils.extractResource(plugin.getDataFolder(), "magicspells.yml");
        //Config spellConfig = new Config(spellFile);
        spellConfig.load();
        setupSpells(spellConfig);
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onSpellCast(SpellCastEvent event)
    {
        
    }
    
    private void setupSpells(Config config)
    {
        
    }
}