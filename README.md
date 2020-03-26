[![License: CC BY-NC-SA 4.0](https://licensebuttons.net/l/by-nc-sa/4.0/80x15.png)](https://creativecommons.org/licenses/by-nc-sa/4.0/)  
[![Language](https://img.shields.io/badge/language-kotlin-orange.svg)](https://kotlinlang.org/)
![BUILD](https://github.com/StarHeartHunt/Mooncell-Client-Kotlin/workflows/BUILD/badge.svg)
# Mooncell-Client-Kotlin
this repo contains the source code for the official [fgo.wiki android client](https://fgo.wiki/w/Mooncell:Appclient)
## build
1. move your MiPush_SDK_Client_3_7_5.jar to /app/libs
2. move your google-services.json to app/
3. build with option -PMI_PUSH_APP_ID=yourMiPushAppId -PMI_PUSH_APP_KEY=yourMiPushAppKey
