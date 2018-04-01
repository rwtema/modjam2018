package com.rwtema.monkmod.debug;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonWriter;
import com.rwtema.monkmod.MonkMod;
import org.apache.commons.lang3.Validate;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class CreateJSons {
	public static void create() {
		createAdvancements();
		createItemModels();

		ArrayList<String> strings = new ArrayList<>();
		for (int i = 0; i <= 20; i++) {
			strings.add("monk.advancements.level." + i + "=Monk Level " + i );
			strings.add("monk.advancements.level." + i + ".desc=" );
			strings.add("monk.advancements.reward." + i + "=Awakened Skills: " + i);
			strings.add("monk.advancements.reward." + i + ".desc=" );
		}
		MonkMod.logger.info("\n" +  strings.stream().collect(Collectors.joining("\n")));

	}

	private static void createItemModels() {
		File dir = new File("C:\\extrautils\\modjam2018\\src\\main\\resources\\assets\\monk\\models\\item");

		for (int level = 0; level <= 20; level++) {
			writeJSon(new File(dir, Validate.notNull(MonkMod.ITEM_MONK_BASE.getRegistryName()).getResourcePath() + "_" + level + ".json"), json()
					.add("parent", "item/generated")
					.add("textures", json()
							.add("layer0", MonkMod.MODID + ":monk_level_icon_" + level)
					)
					.build()
			);
		}
	}

	private static void createAdvancements() {
		File dir = new File("C:\\extrautils\\modjam2018\\src\\main\\resources\\assets\\monk\\advancements\\monk");

		writeJSon(new File(dir, "root.json"), json()
				.add("display", json()
						.add("icon", json()
								.add("item", Validate.notNull(MonkMod.ITEM_MONK_BASE.getRegistryName()).toString())
								.add("data", 0))
						.add("title", json()
								.add("translate", "monk.advancements.level.0"))
						.add("description", json()
								.add("translate", "monk.advancements.level.0.desc"))
						.add("background", "minecraft:textures/gui/advancements/backgrounds/stone.png")
						.add("show_toast", true)
						.add("announce_to_chat", false)
						.add("hidden", false)
				)
				.add("criteria", json()
						.add("level", json()
								.add("trigger", "monk:levelup")
								.add("conditions", json()
										.add("level", 0))
						)
				).build());

		for (int level = 1; level <= 20; level++) {
			writeJSon(new File(dir, "level_" + level + ".json"), json()
					.add("display", json()
							.add("icon", json()
									.add("item", Validate.notNull(MonkMod.ITEM_MONK_BASE.getRegistryName()).toString())
									.add("data", level))
							.add("title", json()
									.add("translate", "monk.advancements.level." + level))
							.add("description", json()
									.add("translate", "monk.advancements.level." + level + ".desc"))

							.add("show_toast", true)
							.add("announce_to_chat", true)
							.add("hidden", false)
					)
					.add("parent", level == 1 ? "monk:monk/root" : "monk:monk/level_" + (level-1))
					.add("criteria", json()
							.add("level", json()
									.add("trigger", "monk:levelup")
									.add("conditions", json()
											.add("level", level))
							)
					).build());

			writeJSon(new File(dir, "level_reward_" + level + ".json"), json()
					.add("display", json()
							.add("icon", json()
									.add("item", Validate.notNull(MonkMod.ITEM_MONK_BASE.getRegistryName()).toString())
									.add("data", level))
							.add("title", json()
									.add("translate", "monk.advancements.reward." + level))
							.add("description", json()
									.add("translate", "monk.advancements.reward." + level + ".desc"))

							.add("show_toast", false)
							.add("announce_to_chat", false)
							.add("hidden", true)

					)
					.add("parent", level == 1 ? "monk:monk/root" : "monk:monk/level_" + (level-1))

					.add("criteria", json()
							.add("level", json()
									.add("trigger", "monk:levelup")
									.add("conditions", json()
											.add("level", level))
							)
					).build());
		}
	}


	private static void writeJSon(@Nonnull File file, JsonObject model) {
		try (FileWriter writer = new FileWriter(file.getPath())) {
			JsonWriter jsonWriter = new JsonWriter(writer);
			jsonWriter.setIndent("  ");
			jsonWriter.setLenient(true);
			Streams.write(model, jsonWriter);
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static JSonObjBuilder json() {
		return new JSonObjBuilder();
	}

	public static class JSonObjBuilder {
		JsonObject jsonObject = new JsonObject();

		public JsonObject build() {
			return jsonObject;
		}

		public JSonObjBuilder add(String property, JsonElement value) {
			jsonObject.add(property, value);
			return this;
		}

		public JSonObjBuilder add(String property, JSonObjBuilder value) {
			jsonObject.add(property, value.build());
			return this;
		}

		public JSonObjBuilder add(String property, String value) {
			jsonObject.addProperty(property, value);
			return this;
		}

		public JSonObjBuilder add(String property, Number value) {
			jsonObject.addProperty(property, value);
			return this;
		}

		public JSonObjBuilder add(String property, Boolean value) {
			jsonObject.addProperty(property, value);
			return this;
		}

		public JSonObjBuilder add(String property, Character value) {
			jsonObject.addProperty(property, value);
			return this;
		}
	}

	public class AdvDummy {
		DisplayDummy display;
		HashMap<String, CriteriaDummy> criteria = new HashMap<>();

		public AdvDummy(int level) {
			this.display = new DisplayDummy();
			display.icon.data = level;
			display.title.translate = "monk.advancements.level." + level;
			display.description.translate = "monk.advancements.level." + level + ".desc";
			display.parent = level == 1 ? "monk:monk/root" : "monk:monk/level_" + level;
			CriteriaDummy criteriaDummy = new CriteriaDummy();
			criteriaDummy.level = level;
			this.criteria = Maps.newHashMap();
			criteria.put("level", criteriaDummy);
		}

		public class DisplayDummy {
			ItemDummy icon = new ItemDummy();
			StringDummy title = new StringDummy();
			StringDummy description = new StringDummy();
			String parent;
			boolean show_toast = true;
			boolean announce_to_chat = true;

			public class ItemDummy {
				String icon = Validate.notNull(MonkMod.ITEM_MONK_BASE.getRegistryName()).toString();
				int data;
			}

			public class StringDummy {
				String translate;
			}
		}

		public class CriteriaDummy {
			String trigger = "monk:levelup";
			int level;
		}
	}


}
