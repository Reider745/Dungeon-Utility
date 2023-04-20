const DebugDu = WRAP_JAVA("com.reider.Debug");
const LoggerDisable = WRAP_JAVA("com.reider.dungeonutility.api.type.LoggerDisable");
const LoggerEnable = WRAP_JAVA("com.reider.dungeonutility.api.type.LoggerEnable");

ModAPI.addAPICallback("RuntimeSetting", (api) => {
	const ConfigStorage = api.ConfigStorage;
	const BuilderConfig = api.BuilderConfig;
	const Setting = api.Setting;

	const config = new ConfigStorage(__dir__+"runtime_config.json")
		.put("debug", false)
		.put("clearing_clusters_enable", true)
        .put("chunk_manager_type", "java");

    const builder = new BuilderConfig(config)
		.addCheckBox("Debug", "debug")
		.addSectionDivider("Chunk manager")
		.addMultipleChoice("Type", "chunk_manager_type", ["java", "native"]);

	function configUpdate(cfg: ConfigType, config: ConfigStorage, builder: BuilderConfig){
        DebugDu.set(config.get("debug", false) ? new LoggerEnable() : new LoggerDisable());
        StructurePieceController.setTypeChunkManager(config.get("chunk_manager_type", "java"));
	}

    configUpdate(config.build(), config, builder);

	new Setting(__dir__)
		.setBuilderConfig(builder)
		.setChangeSetting(configUpdate);
});