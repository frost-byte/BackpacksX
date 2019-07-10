package me.tade.backpacks.packs;

import java.util.List;

import me.tade.backpacks.Backpacks;

import me.tade.backpacks.util.VersionManager;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

@SuppressWarnings("WeakerAccess")
public class ConfigPack {
	private String name;
	private int size;
	private List<String> recipe;
	private ItemStack itemStack;
	private ShapedRecipe shapedRecipe;
	@SuppressWarnings({"FieldCanBeLocal", "unused"})
	private final Backpacks plugin;


	public ConfigPack(Backpacks plugin, String name, int size, List<String> recipe, ItemStack itemStack) {
		this.plugin = plugin;
		this.name = name;
		this.size = size;
		this.recipe = recipe;
		this.itemStack = itemStack;

		if (initializeRecipe())
		{
			plugin.getLogger().info("Pack Recipe " + name + " added!");
		}
		else {
			plugin.getLogger().info("Pack Recipe " + name + " was not added!");
		}
	}

	public String getName() {
		return name;
	}

	public int getSize() {
		return size;
	}

	public List<String> getRecipe() {
		return recipe;
	}

	public ItemStack getItemStack() {
		return itemStack;
	}

	@SuppressWarnings("unused")
	public ShapedRecipe getShapedRecipe()
	{
		return shapedRecipe;
	}

	public String packInfo()
	{
		StringBuilder builder = new StringBuilder();
		builder
			.append("\n§a- Name: ")
			.append(name)
			.append("; Size: ")
			.append(size)
			.append("\n§a- Key: ")
			.append(plugin.getName().toLowerCase())
			.append(".")
			.append(name)
			.append("; Type: ")
			.append(itemStack.getType().name())
			.append("\n§a- Recipe:\n");

		for (String line : this.getRecipe())
		{
			builder
				.append("§a- ")
				.append(line)
				.append("\n");
		}
		builder.append("\n--------------\n");
		return builder.toString();
	}

	private boolean initializeRecipe() {

		shapedRecipe = VersionManager.createShapedRecipe(plugin, name, itemStack);
		shapedRecipe.shape("ABC", "DEF", "GHI");

		int number = 0;
		for (String line : recipe)
		{
			String[] derived = line.split(" ");

			for (String materialName : derived) {
				Material material;

				try
				{
					material = Material.valueOf(materialName);
					shapedRecipe.setIngredient((char)('A' + number), material);
					++number;
				}
				catch (Exception e)
				{
					e.printStackTrace();
					return false;
				}
			}
		}

		return plugin.getServer().addRecipe(shapedRecipe);
	}
}
