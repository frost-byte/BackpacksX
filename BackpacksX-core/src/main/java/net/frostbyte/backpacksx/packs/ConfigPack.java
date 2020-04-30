package net.frostbyte.backpacksx.packs;

import java.util.List;

import net.frostbyte.backpacksx.Backpacks;

import net.frostbyte.backpacksx.util.VersionManager;
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

		initializeRecipe();
		plugin.getLogger().info("ConfigPack: Recipe " + name + " added!");
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

	private void initializeRecipe() {

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
				}
			}
		}
		VersionManager.updatePackRecipe(plugin, name, shapedRecipe);
	}
}
