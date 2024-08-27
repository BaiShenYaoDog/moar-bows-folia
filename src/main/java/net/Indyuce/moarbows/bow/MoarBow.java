package net.Indyuce.moarbows.bow;

import net.Indyuce.moarbows.MoarBows;
import net.Indyuce.moarbows.bow.modifier.BooleanModifier;
import net.Indyuce.moarbows.bow.modifier.DoubleModifier;
import net.Indyuce.moarbows.bow.modifier.Modifier;
import net.Indyuce.moarbows.bow.modifier.StringModifier;
import net.Indyuce.moarbows.bow.particle.ParticleData;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public abstract class MoarBow {
    protected static final Random random = new Random();
    private final String id;
    private final Map<String, Modifier> mods = new HashMap<>();
    private String name;
    private String[] craft;
    private List<String> lore;
    private int customModelData;
    private ParticleData particles;
    private boolean craftEnabled;

    public MoarBow(String[] lore, ParticleData particles, String[] craft) {
        this.id = getClass().getSimpleName().toUpperCase();
        this.name = "&f" + getClass().getSimpleName().replace("_", " ");

        this.lore = lore == null ? new ArrayList<>() : Arrays.asList(lore);
        this.particles = particles;
        this.craft = craft;
    }

    public MoarBow(String id, String name, String[] lore, int customModelData, ParticleData particles, String[] craft) {
        this.id = id;
        this.name = name;

        this.lore = lore == null ? new ArrayList<>() : Arrays.asList(lore);
        this.customModelData = customModelData;
        this.particles = particles;
        this.craft = craft;
        this.craftEnabled = false;
    }

    /**
     * Called before the arrow is summoned to apply specific restrictions like
     * the Railgun bow which needs the player to be in a minecart. This can also
     * be used to create bows which don't actually throw arrows but eggs or snowballs.
     *
     * @param event The bukkit shoot event
     * @param data  Generated arrow data
     * @return If the player is allowed to fire the bow.
     */
    public abstract boolean canShoot(EntityShootBowEvent event, ArrowData data);

    /**
     * When an arrow fired by that bow hits another entity. Does NOT
     * get called when the arrow lands on the ground/on a block.
     * <p>
     * For bows like the Fire Bow that method does the same as {@link #whenLand(ArrowData)}
     *
     * @param event  The bukkit damage event
     * @param data   Generated arrow data
     * @param target The entity being hit (same as {@link EntityDamageByEntityEvent#getEntity()}
     */
    public abstract void whenHit(EntityDamageByEntityEvent event, ArrowData data, Entity target);

    /**
     * When an arrow fired by that bow lands on the ground. Does NOT get
     * called when the arrow hits another entity.
     * <p>
     * For bows like the Fire Bow that method does the same as {@link #whenHit(EntityDamageByEntityEvent, ArrowData, Entity)}
     *
     * @param data Generated arrow data
     */
    public abstract void whenLand(ArrowData data);

    public String getId() {
        return id;
    }

    public String getLowerCaseId() {
        return id.toLowerCase().replace("_", "-");
    }

    public String getUncoloredName() {
        return name;
    }

    public String getName() {
        return ChatColor.translateAlternateColorCodes('&', name);
    }

    public List<String> getLore() {
        return lore;
    }

    public int getCustomModelData() {
        return customModelData;
    }

    public Collection<Modifier> getModifiers() {
        return mods.values();
    }

    public Set<String> modifierKeys() {
        return mods.keySet();
    }

    public void addModifier(Modifier... modifiers) {
        for (Modifier modifier : modifiers)
            mods.put(modifier.getPath(), modifier);
    }

    public Modifier getModifier(String path) {
        return mods.get(path);
    }

    public boolean hasModifier(String path) {
        return mods.containsKey(path);
    }

    public double getDouble(String path, int x) {
        return ((DoubleModifier) getModifier(path)).calculate(x);
    }

    public boolean getBoolean(String path) {
        return ((BooleanModifier) getModifier(path)).getValue();
    }

    public String getString(String path) {
        return ((StringModifier) getModifier(path)).getValue();
    }

    public String[] getFormattedCraftingRecipe() {
        return craft == null ? new String[0] : craft;
    }

    public boolean isCraftEnabled() {
        return craftEnabled;
    }

    public boolean hasParticles() {
        return particles != null;
    }

    public ParticleData getParticles() {
        return particles;
    }

    public void update(ConfigurationSection config) {
        name = config.getString("name");
        lore = config.getStringList("lore");
        try {
            particles = config.contains("particle") ? new ParticleData(config.getConfigurationSection("particle")) : null;
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Could not load bow particle effect: " + exception.getMessage());
        }
        customModelData = config.getInt("custom-model-data");
        craft = config.getStringList("craft").toArray(new String[0]);
        craftEnabled = config.getBoolean("craft-enabled");

        // reload modifiers
        mods.forEach((key, modifier) -> modifier.load(config.get(key)));
    }

    public ItemStack getItem() {
        return getItem(1);
    }

    public ItemStack getItem(int level) {
        level = Math.max(1, level);

        ItemStack item = new ItemStack(Material.BOW);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(getName());

        if (customModelData > 0)
            meta.setCustomModelData(customModelData);

        if (MoarBows.plugin.getLanguage().hideUnbreakable)
            meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);

        if (MoarBows.plugin.getLanguage().hideEnchants)
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        if (this.lore != null) {
            List<String> lore = new ArrayList<>();
            for (String str : this.lore)
                lore.add(ChatColor.GRAY + applyPlaceholders(str, level));
            meta.setLore(lore);
        }

        if (MoarBows.plugin.getLanguage().unbreakable)
            meta.setUnbreakable(true);

        meta.getPersistentDataContainer().set(new NamespacedKey(MoarBows.plugin, "MoarBow"), PersistentDataType.STRING, getId());
        meta.getPersistentDataContainer().set(new NamespacedKey(MoarBows.plugin, "MoarBowLevel"), PersistentDataType.INTEGER, level);

        item.setItemMeta(meta);

        return item;
    }

    private String applyPlaceholders(String str, int x) {

        // Apply bow level placeholder
        str = str.replace("{level}", String.valueOf(x));

        Modifier modifier;
        while (str.contains("{") && str.substring(str.indexOf("{")).contains("}")) {
            String holder = str.substring(str.indexOf("{") + 1, str.indexOf("}")).replace("_", "-");
            str = str.replace("{" + holder + "}",
                    hasModifier(holder) && (modifier = getModifier(holder)) instanceof DoubleModifier ? ((DoubleModifier) modifier).getDisplay(x)
                            : "PHE");
        }

        return ChatColor.translateAlternateColorCodes('&', str);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MoarBow moarBow = (MoarBow) o;
        return Objects.equals(id, moarBow.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
