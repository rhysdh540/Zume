import xyz.wagyourtail.unimined.api.minecraft.task.RemapJarTask

operator fun String.invoke(): String {
	return (rootProject.properties[this] as String?)!!
}

unimined.minecraft {
	version("lexforge_minecraft_version"())

	runs {
		config("server") {
			disabled = true
		}
	}

	minecraftForge {
		loader("lexforge_version"())
		mixinConfig("zume-lexforge.mixins.json")
	}

	mappings {
		mojmap()
		parchment(mcVersion = "lexforge_minecraft_version"(), version = "lexforge_parchment_version"())
	}

	defaultRemapJar = true
}

//tasks.withType<RemapJarTask> {
//	mixinRemap {
//		enableMixinExtra()
//	}
//}

dependencies {
	"minecraftLibraries"("blue.endless:jankson:${"jankson_version"()}")
	
//	val mixinExtrasCommon = "io.github.llamalad7:mixinextras-common:${"mixinextras_version"()}"
//	compileOnly(mixinExtrasCommon)
//	annotationProcessor(mixinExtrasCommon)
//	val mixinExtrasForge = "io.github.llamalad7:mixinextras-forge:${"mixinextras_version"()}"
//	"minecraftLibraries"(mixinExtrasForge)

	"minecraftLibraries"(project(":common"))
}

tasks.processResources {
	from("common/src/main/resources")

	filteringCharset = "UTF-8"

	filesMatching("META-INF/mods.toml") {
		expand(rootProject.properties)
	}
}