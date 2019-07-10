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

	public Backpack(String owner, int size, String configName, List<HashMap<Map<String, Object>, Map<String, Object>>> items) {
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