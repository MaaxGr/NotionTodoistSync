package com.maaxgr.todoistnotionsync.interfaces.config

import com.charleskorn.kaml.PolymorphismStyle
import com.charleskorn.kaml.Yaml
import kotlinx.serialization.decodeFromString
import java.io.File

class ConfigLoader() {

    var customDir = ""

    companion object {
        const val CONFIG_FILE_NAME = "config.yaml"
    }

    fun loadConfig(): ConfigYaml {
        val configFileContent = loadConfigContent()
        val yamlParser = createYamlParser()

        return yamlParser.decodeFromString(configFileContent)
    }

    private fun loadConfigContent(): String {
        val file = if (customDir.isNotBlank()) {
            File(customDir, CONFIG_FILE_NAME)
        } else {
            File(CONFIG_FILE_NAME)
        }
        return file.readText()
    }

    private fun createYamlParser(): Yaml {
        val yamlConfig = Yaml.default.configuration.copy(
            polymorphismStyle = PolymorphismStyle.Property,
            polymorphismPropertyName = "type",
        )
        return Yaml(Yaml.default.serializersModule, yamlConfig)
    }


}
