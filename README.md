# Bukloit

Utility to automatically implement a backdoor in Bukkit and Spigot plugins.

![Bukloit Logo](https://i.imgur.com/4mqGWoQ.png)

> *Readme text translated by Google Translator, grammar errors possible.*

[![Ready to use release](https://img.shields.io/badge/-DOWNLOAD_READY_TO_USE_RELEASE-blue?style=for-the-badge)](https://repo.rikonardo.now.sh/#/MC_Security_testing_kit/Bukloit) 

[![GitHub](https://img.shields.io/badge/license-GPL--3.0-orange)](https://github.com/Rikonardo/Bukloit/blob/master/LICENSE.txt) [![GitHub release (latest by date)](https://img.shields.io/github/v/release/Rikonardo/Bukloit)](https://github.com/Rikonardo/Bukloit/releases) [![GitHub issues](https://img.shields.io/github/issues/Rikonardo/Bukloit)](https://github.com/Rikonardo/Bukloit/issues) ![GitHub issues](https://img.shields.io/badge/java_version-1.8.0-orange) 

---

## About

Bukloit is the first of its kind universal backdoor injector compatible with all Bukkit/Spigot plugins. Its feature is the ability to integrate with absolutely any plugin, and the implemented backdoor will work the same in all plugins. Bukloit was developed to test the security systems of Minecraft servers.

### Features

- **Compatible with all plugins**

- **Supports all versions of the game starting from <ins>1.8</ins>**\* **(subject to the availability of a newest version of the Bukkit/Spigot core)**

- **Activation by key chat message**

- **PermissionsEx Integration**

  \**Older versions are also supported, but there may be some problems with injection or backdoor execution.*

---

## How to use

### Arguments

| Short Argument | Long Argument | Description                                                  | Type  |
| -------------- | ------------- | ------------------------------------------------------------ | ----- |
| -m             | --mode        | Mode. Can be single/multiple.<br />Default: <ins>multiple</ins>.<br />In multiple mode, modifies all files in the specified folder. In single - only the specified file. | Value |
| -k             | --key         | The text to be used to activate the backdoor.<br />Default: <ins>"-opme"</ins>. | Value |
| -i             | --input       | Path to input folder/file (mode dependent).<br />Default: <ins>in</ins> (<ins>in.jar</ins> if mode is single). | Value |
| -o             | --output      | Path to output folder/file (mode dependent).<br />Default: <ins>out</ins> (<ins>out.jar</ins> if mode is single). | Value |
| -r             | --replace     | Replace output file if it already exists.                    | Flag  |

### Examples

1. Patch all files with "-opme" key from "in" folder and save them into "out" folder without replacement.

   ```bash
   java -jar bukloit.jar -m multiple -i "in" -o "out" -k "\-opme"
   ```

2. Patch all files with "hacktheserver" key from "in" folder and save them into "out" folder with replacement.

   ```bash
   java -jar bukloit.jar -m multiple -i "in" -o "out" -k "hacktheserver" -r
   ```

3. Patch single file "PluginName.jar" with "-opme" key and save it as "Output.jar" file with replacement.

   ```bash
   java -jar bukloit.jar -m single -i "PluginName.jar" -o "Output.jar" -k "\-opme" -r
   ```

---

Planned features: *stealth mode*, *hard mode*.