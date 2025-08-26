val mod_version: String by extra
val mc_version: String by extra

plugins {
    id("com.possible-triangle.gradle") version ("0.2.18")
}

mod {
    version = "$mc_version-$mod_version"
}

subprojects {
    repositories {
        modrinthMaven()
        mavenLocal()

        nexus {
            content {
                includeGroup("com.possible-triangle")
                includeGroup("io.github.fabricators_of_create.Porting-Lib")
            }
        }

        maven {
            url = uri("https://dl.cloudsmith.io/public/geckolib3/geckolib/maven/")
            content {
                includeGroup("software.bernie.geckolib")
                includeGroup("com.eliotlash.mclib")
            }
        }

        //maven {
        //    url = uri("https://mvn.devos.one/snapshots/")
        //    content {
        //        includeGroup("io.github.fabricators_of_create.Porting-Lib")
        //    }
        //}

        maven {
            url = uri("https://maven.jamieswhiteshirt.com/libs-release")
            content {
                includeGroup("com.jamieswhiteshirt")
            }
        }

        maven {
            url = uri("https://maven.blamejared.com/")
            content {
                includeGroup("mezz.jei")
            }
        }

        maven {
            url = uri("https://maven.theillusivec4.top/")
            content {
                includeGroup("top.theillusivec4.curios")
            }
        }

        maven {
            url = uri("https://maven.terraformersmc.com/")
            content {
                includeGroup("dev.emi")
            }
        }

        maven {
            url = uri("https://maven.ladysnake.org/releases")
            content {
                includeGroup("org.ladysnake.cardinal-components-api")
            }
        }

        maven {
            url = uri("https://jitpack.io")
            content {
                includeGroup("com.github.Chocohead")
            }
        }

        maven {
            url = uri("https://raw.githubusercontent.com/Fuzss/modresources/main/maven/")
            content {
                includeGroup("net.minecraftforge")
                includeGroup("fuzs.forgeconfigapiport")
            }
        }
    }
}

enableSpotless()