![BackpacksX][logo]
##### Advanced Backpack plugin for Spigot/Paper!

### Features
- Free!
- Store any item!
- Automatically saves backpacks to custom files!
- Unlimited backpacks!
- Custom backpack configuration, recipes, items and more!
- Compatible with Multiple Minecraft Versions: 1.8.3, 1.12.2, 1.14.4 and 1.15.2

### Permissions

##### LuckPerms
I recommend using [LuckPerms][1] to manage your permissions.
Install the LuckPerms plugin then use the following commands from your server console:
```
lp creategroup admins
lp group admins parent set default
lp group admins permission set backpacksx.*
lp group default permission set backpacksx.craft.teenagepack
lp group default permission set backpacksx.craft.babypack
```
**Add yourself to the admins group**

`lp user your_username parent set admins`

**(Optional) OR create a Track for easy promotion and demotion**
```
lp createtrack roles
lp track roles append default
lp track roles append admins
lp user your_username promote roles
```

**To give your admin group the ability to use luckperms' commands in game**

`lp group admins permission set luckperms.*`

### Commands
> All parameters in **\<brackets\>** are required
> Parameters in **\[square brackets\]** are optional.
> If the optional parameter is a player, then if the command issuer is a player, the action will be applied
> to them.

|Name               | Parameters                            | Aliases   | Permission            | Description   |
|:-----------       |:-----------------                     |:--------- |:-------------         |:---               |
| _/bpx_            | _None_                                | _None_    | _None_                | _Shows all commands and summaries for each_ |
| _/bpx reload_     | _None_                                | _rl_      | _backpacksx.reload_   | _Reload the configuration files for BackpacksX._ |
| _/bpx info_       | _\<backpack\>_                        | _i_       | _None_                | _View Info about a specific backpack._ |
| _/bpx give_       | _\<backpack\> \[player\]_             | _g_       | _backpacksx.give_     | _Give the specified pack to a player._ |
| _/bpx list_       | _None_                                | _ls_      | _backpacksx.info_     | _View a list of all available backpacks._ |

### Configuration

```yaml
backpacks:
  babypack:
    size: 9
    recipe:
    - 'CHEST CHEST CHEST'
    - 'CHEST COBBLESTONE CHEST'
    - 'CHEST CHEST CHEST'
    item:
      material: CHEST
      data: 0
      amount: 1
      name: '&a&lBabypack'
      lore:
      - ' '
      - '&79 slots available '
  teenagepack:
    size: 18
    recipe:
    - 'CHEST CHEST CHEST'
    - 'CHEST ENDER_CHEST CHEST'
    - 'CHEST CHEST CHEST'
    item:
      material: CHEST
      data: 0
      amount: 1
      name: '&b&lTeenagepack'
      lore:
      - ' '
      - '&718 slots available '
```

New backpacks can be added by adding a section to the `config.yml` file and then reloading the plugin via `/bpx rl`

##### Example Pack
- **size**: _I suggest using a factor of 9, but other options may work depending upon the container sizes available._
- **recipe**: _A list of 3 rows of 3 Item Materials, each Item in a row separated using a space
- **item**: _The Item Stack for the backpack, as it will appear in player's inventories._ 
```yaml
  sorting_hat:
    size: 54
    recipe:
    - 'IRON_BLOCK IRON_BLOCK IRON_BLOCK'
    - 'IRON_BLOCK HOPPER IRON_BLOCK'
    - 'IRON_BLOCK IRON_BLOCK IRON_BLOCK'
    item:
      material: HOPPER
      data: 0
      amount: 1
      name: '&b&lSorting Hat'
      lore:
      - ' '
      - '&754 magical slots '
```

### Links
[1]: https://luckperms.net/
[2]: https://helpch.at/discord
[3]: https://github.com/frost-byte/BackpacksX
[4]: https://frostbyte.mycloudrepo.io/public/repositories/releases 
[5]: https://www.spigotmc.org/resources/quickboard-free-scoreboard-plugin-scroller-changeable-text-placeholderapi-anti-flicker.15057/
[6]: https://discord.gg/MZNYhTA
[7]: https://frostbyte.mycloudrepo.io/public/repositories/snapshots
[logo]: https://github.com/frost-byte/BackpacksX/blob/master/images/Layer-BackPacksX.png

### Source
[Github Repository][3]  
[Github Maven Repository - Releases][4]  
[Github Maven Repository - Snapshots][7]

#### Maven

##### Artifact
```xml
<!--BackpacksX-->
<dependency>
    <groupId>net.frostbyte.backpacksx</groupId>
    <artifactId>quickboardx-core</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

##### Maven Repositories
```xml
<!-- frost-byte snapshots -->
<repository>
    <id>io.cloudrepo.snapshots</id>
    <url>https://frostbyte.mycloudrepo.io/public/repositories/snapshots</url>
</repository>
<!-- frost-byte releases -->
<repository>
    <id>io.cloudrepo</id>
    <url>https://frostbyte.mycloudrepo.io/public/repositories/releases</url>
</repository>
```

#### Discord
For support please contact me on [the frost-byte central Discord!][6]