# terraWorld

`terraWorld` provides TerraFirmaCraft 4.2.9 compatibility for Toroidal World on
Minecraft 1.21.1 / NeoForge. It keeps TFC terrain, biomes, climate, rainfall,
groundwater, grass colors, caves, rivers, and client climate data continuous
across wrapped world boundaries.

## Modules

- `tfc-toroidal-fork`: the topology-aware TerraFirmaCraft fork.
- `tfc-toroidal-compat`: the Toroidal World integration and client HUD.

## JourneyMap compatibility

JourneyMap is included in the development client as an optional integration.
When it is installed, Toroidal World's map integration keeps explored tiles and
waypoints folded into the playable world, repeats the world on the fullscreen
map, and marks its seams. The compatibility mod still loads normally when
JourneyMap is absent.

## Toroidal climate

TFC's latitude bands form one mirrored northern hemisphere: the joined
top/bottom map seam is the 90 degrees north polar line and the middle row is the
equator. Climate warms from either edge toward the center; there is no southern
hemisphere or inverted southern season. Temperature, seasons, daylight,
celestial rendering, vegetation, and the location HUD all use the same
latitude phase.

The HUD retains the familiar Latitude and Longitude labels while reporting
torus-native angles. Longitude is the toroidal angle from 0 to 360 degrees,
with 0 at the east/west seam. Latitude is the signed poloidal angle: 0 at the
equator, +180 at the upper pole edge, and -180 at the lower pole edge. The two
180-degree edges are the same pole line, so the sign swaps when it is crossed.

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
