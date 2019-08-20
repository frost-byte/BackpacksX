package me.tade.backpacks.packs;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class Backpack implements Serializable {
	private String owner;
	private int size;
	private String configName;
	private List<HashMap<Map<String, Object>, Map<String, Object>>> items;

	public Backpack(
		String owner,
		int size,
		String configName,
		List<HashMap<Map<String, Object>, Map<String, Object>>> items
	) {
		this.owner = owner;
		this.size = size;
		this.configName = configName;
		this.items = items;
	}

	public String getOwner() {
		return owner;
	}

	public int getSize() {
		return size;
	}

	@SuppressWarnings("EqualsReplaceableByObjectsCall")
	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Backpack backpack = (Backpack) o;

		if (owner != null ? !owner.equals(backpack.owner) : backpack.owner != null) return false;
		if (size != backpack.size) return false;
		return configName != null ? configName.equals(backpack.configName) : backpack.configName == null;
	}

	@Override
	public int hashCode()
	{
		int result = size;
		result = 31 * result + (owner != null ? owner.hashCode() : 0);
		result = 31 * result + (configName != null ? configName.hashCode() : 0);

		return result;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public String getConfigName() {
		return configName;
	}

	public List<HashMap<Map<String, Object>, Map<String, Object>>> getItems() {
		return items;
	}

	public void setItems(List<HashMap<Map<String, Object>, Map<String, Object>>> items) {
		this.items = items;
	}
}