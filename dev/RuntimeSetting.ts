const DebugDu = WRAP_JAVA("com.reider.dungeonutility.logger.Debug");
const LoggerDisable = WRAP_JAVA("com.reider.dungeonutility.logger.LoggerDisable");
const LoggerEnable = WRAP_JAVA("com.reider.dungeonutility.logger.LoggerEnable");
const ChunkClearMembory = WRAP_JAVA("com.reider.dungeonutility.struct.generation.thread.ChunkClearMembory");

ModAPI.addAPICallback("RuntimeSetting", (api) => {
	const ConfigStorage = api.ConfigStorage;
	const BuilderConfig = api.BuilderConfig;
	const Setting = api.Setting;

	const config = new ConfigStorage(__dir__+"runtime_config.json")
        // .put("chunk_manager_type", "native")

		// .put("chunk_clear", true)
		// .put("chunk_clear_time", 60)
		// .put("chunk_clear_limit", 60)
		// .put("chunk_clear_pace", 64)
		
		.put("debug_enable", false)
		.put("debug_text_size", 15)
		.put("debug_chart_info", 30)
		.put("chart_height", 45)
		.put("chart_width", 100)
		.put("algorithms_debug", true)
		// .put("chunk_clear_debug", true)
		// .put("chunks_debug", true)
		.put("structures_debug", true)
		.put("generation_debug", true)
		.put("structures_queue", true);

    const builder = new BuilderConfig(config)
		// .addSectionDivider("Chunk manager")
		// .addMultipleChoice("Type", "chunk_manager_type", ["java", "native"])

		//.addSectionDivider("ChunkClear")
		//.addCheckBox("Enable", "chunk_clear")
		//.addSlider("Time", "chunk_clear_time", 20, 400, 1)
		//.addSlider("Limit time", "chunk_clear_limit", 30, 120, 1)
		//.addSlider("Pace", "chunk_clear_pace", 8, 128, 1)
		
		.addSectionDivider("Debug")
		.addCheckBox("Enable", "debug_enable")
		.addSlider("Text size", "debug_text_size", 10, 30, 1)
		.addSlider("Chart info storage", "debug_chart_info", 20, 50, 1)
		.addSlider("Chart height", "chart_height", 40, 150, 1)
		.addSlider("Chart width", "chart_width", 80, 200, 1)
		.addCheckBox("Algorithms", "algorithms_debug")
		//.addCheckBox("Chunk clear", "chunk_clear_debug")
		//.addCheckBox("Chunks", "chunks_debug")
		.addCheckBox("Structures", "structures_debug")
		.addCheckBox("Generation", "generation_debug")
		.addCheckBox("Structures queue", "structures_queue");

	function configUpdate(cfg: ConfigType, config: ConfigStorage, builder: BuilderConfig){
		let debug = config.get("debug_enable", false) ? new LoggerEnable() : new LoggerDisable();
        
		debug.setEnable("algorithms", config.get("algorithms_debug", true));
		//debug.setEnable("chunk_clear_manager", config.get("chunk_clear_debug", true));
		//debug.setEnable("chunk_clear_manager_chunks", config.get("chunks_debug", true));
		debug.setEnable("structures", config.get("structures_debug", true));
		debug.setEnable("generation", config.get("generation_debug", true));
		debug.setEnable("structures_queue", config.get("structures_queue_debug", true));

		debug.setAdditionSetting({
			text_size: config.get("debug_text_size", 15),
			chart_info: config.get("debug_chart_info", 30),
			chart_height: config.get("chart_height", 45),
			chart_width: config.get("chart_width", 100)
		});

		DebugDu.set(debug);

		// ChunkClearMembory.enable = config.get("chunk_clear", true);
		// ChunkClearMembory.time = config.get("chunk_clear_time", 60)/20*1000;
		// ChunkClearMembory.limit = config.get("chunk_clear_limit", 60)*1000;
		// ChunkClearMembory.pace = config.get("chunk_clear_pace", 64);

        //StructurePieceController.setTypeChunkManager(config.get("chunk_manager_type", "native"));
	}

    configUpdate(config.build(), config, builder);

	const setting = new Setting(__dir__)
		.setBuilderConfig(builder)
		.setChangeSetting(configUpdate);
});