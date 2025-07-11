val mc_version: String by extra
val geckolib_version: String by extra
val easy_villager_version: String by extra
val jei_version: String by extra
val curios_version: String by extra
val pathfinding_renderer_version: String by extra

neoforge {
    dependOn(project(":common"))

    enableMixins()
}

dependencies {
    modImplementation("software.bernie.geckolib:geckolib-neoforge-${mc_version}:${geckolib_version}")
    modImplementation("top.theillusivec4.curios:curios-neoforge:${curios_version}")

    modRuntimeOnly("maven.modrinth:easy-villagers:${easy_villager_version}")
    modRuntimeOnly("mezz.jei:jei-${mc_version}-neoforge:${jei_version}")

    modRuntimeOnly("com.possible-triangle:pathfinding_renderer-neoforge:${mc_version}-${pathfinding_renderer_version}")
}