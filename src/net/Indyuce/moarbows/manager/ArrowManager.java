package net.Indyuce.moarbows.manager;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.bukkit.entity.Arrow;

import net.Indyuce.moarbows.api.ArrowData;

public class ArrowManager {
	private final Map<Integer, ArrowData> map = new HashMap<>();

	public void registerArrow(ArrowData data) {
		map.put(data.getArrow().getEntityId(), data);
	}

	public void unregisterArrow(Arrow arrow) {
		map.remove(arrow.getEntityId());
	}

	public Collection<ArrowData> getActive() {
		return map.values();
	}

	@Deprecated
	public boolean isCustomArrow(Arrow arrow) {
		return map.containsKey(arrow.getEntityId());
	}

	public Optional<ArrowData> getArrowData(Arrow arrow) {
		return map.containsKey(arrow.getEntityId()) ? Optional.of(map.get(arrow.getEntityId())) : Optional.empty();
	}

	@Deprecated
	public ArrowData safeGetArrowData(Arrow arrow) {
		return map.containsKey(arrow.getEntityId()) ? map.get(arrow.getEntityId()) : null;
	}
}
