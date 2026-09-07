# terraWorld

`terraWorld` provides TerraFirmaCraft 4.2.9 compatibility for Toroidal World on
Minecraft 1.21.1 / NeoForge. It keeps TFC terrain, biomes, climate, rainfall,
groundwater, grass colors, caves, rivers, and client climate data continuous
across wrapped world boundaries.

## Modules

- `tfc-toroidal-fork`: the topology-aware TerraFirmaCraft fork.
- `tfc-toroidal-compat`: the Toroidal World integration and client HUD.

## Development

Use Java 21. Build the fork first, followed by the compatibility module:

```powershell
cd tfc-toroidal-fork
.\gradlew.bat jar
cd ..
.\gradlew.bat -p tfc-toroidal-compat build writeMinecraftClasspathClient
```

Import the repository root into IntelliJ IDEA and use the included
`TFC Toroidal Client` run configuration. Test world-generation changes in a new
world because existing chunks are not regenerated.

## Client HUD

The bottom-left HUD displays TFC latitude and toroidal longitude. Its visibility,
scale, and offsets are configured in `tfc_toroidal_compat-client.toml`.

Verbose diagnostics are disabled by default. Set `enableDebugLogging=true` in
`tfc_toroidal_compat-common.toml` to restore seam reports, crossing traces, and
the `/tfc-toroidal-debug` commands.
