# BoM Weather

The weather from Australia's Bureau of Meteorology in the launcher's top-right corner (`top_weather`): the
temperature now, an icon for today's forecast, and today's max, min and chance of rain. The launcher has no
internet access, so this app reads BoM for it. It has no screen of its own.

The contract is in [`docs/modules.md`](../../docs/modules.md#weather-in-the-top-bar-comgathintvhomeweather).

## Choose your place

Set three values in `app/src/main/res/values/config.xml` before building. While they're empty, the add-on
fetches nothing and the launcher shows no weather.

| Value | What | Example (Melbourne) |
|---|---|---|
| `observations_url` | The JSON feed of the nearest station. Open your state's observations page on bom.gov.au (for example `https://www.bom.gov.au/vic/observations/melbourne.shtml`), open the station, and take its number from the address: `IDV60901.95936.shtml` becomes `https://www.bom.gov.au/fwo/IDV60901/IDV60901.95936.json`. | `https://www.bom.gov.au/fwo/IDV60901/IDV60901.95936.json` |
| `forecast_url` | Your state's town forecasts: `IDN11060` (NSW and ACT), `IDV10753` (VIC), `IDQ11295` (QLD), `IDW14199` (WA), `IDS10044` (SA), `IDT16710` (TAS), `IDD10207` (NT), as `https://www.bom.gov.au/fwo/<id>.xml`. | `https://www.bom.gov.au/fwo/IDV10753.xml` |
| `forecast_aac` | Your town's `aac` code in that file: search it for `description="<town>"`. | `VIC_PT042` |

A suburb without its own forecast takes the nearest town that has both a station and a forecast.

## What it fetches, and when

- Both feeds over `https://`, at most every 30 minutes, and only while the launcher asks, which it does when Home
  appears and every 10 minutes while Home stays on screen. A failed read is tried again after 5 minutes.
- Late in the day BoM drops today's max (and sometimes the min) from the forecast; the corner shows what is left.
- The data is © Commonwealth of Australia, Bureau of Meteorology, from its published feeds. The add-on doesn't
  use BoM's app API (`api.weather.bom.gov.au`), which forbids use by others.

## Permissions

| Permission | Why |
|---|---|
| `INTERNET` | Reads the two BoM feeds you set, over `https://`. Nothing else goes online. |

It answers only apps signed with its own key, which means the launcher when both are built the same way (both
in Docker, or both natively). No background service, nothing at boot, no telemetry.

## Build and install

Set your place, build it the same way as the launcher, then install it like any add-on
([`addons/README.md`](../README.md)). Turn on `top_weather` in the launcher's `config.xml` and rebuild it as
well. Uninstall the add-on and the corner is empty.

Tested on a Sony BRAVIA X90L (Sony BRAVIA 4K VH22), Google TV 12.
