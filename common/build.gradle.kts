val mc_version: String by extra
val geckolib_version: String by extra

common()

dependencies {
    compileOnly("software.bernie.geckolib:geckolib-common-${mc_version}:${geckolib_version}")
}