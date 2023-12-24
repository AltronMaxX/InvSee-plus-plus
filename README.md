# InvSee++

A bukkit plugin for manipulating player inventories.

![Logo](https://github.com/Jannyboy11/InvSee-plus-plus/blob/master/img/invsee6.png?raw=true)

This plugin will still work when target players are offline, even when they have never been on the server.

Do you like this plugin? Then please leave a rating anda review on [SpigotMC](https://www.spigotmc.org/resources/invsee.82342/)!

# IMPORTANT NOTE!
## This branch of InvSee++ is a one-off release in order to support the Mohist and Arclight 1.20.1 server software.

### Commands
- `/invsee <userName>|<uniqueId> [PWI{...}]`
- `/endersee <userName>|<uniequeId> [PWI{...}]`
Note that for integration with PerWorldInventory, `load-data-on-join` needs to be set to `true` in its config.

### Permissions

###### Base permissions:
- `invseeplusplus.invsee.view` allows access to `/invsee`. By default only for server operators.
- `invseeplusplus.invsee.edit` allows the player to manipulate the target player's inventory. By default only for server operators.
- `invseeplusplus.endersee.view` allows access to `/endersee`. By default only for server operators.
- `invseeplusplus.endersee.edit` allows the player to manipulate the target player's enderchest. By default only for server operators.
- `invseeplusplus.exempt.invsee` makes it impossible to spectate the inventory of the owner of this permission.
- `invseeplusplus.exempt.endersee` makes it impossible to spectate the enderchest of the owner of this permission.
- `invseeplusplus.bypass-exempt.invsee` ignore whether target players are exempted from having their inventory spectated
- `invseeplusplus.bypass-exempt.endersee` ignore whether target players are exempted from having their enderchest spectated

###### Aggregate permissions:
- `invseeplusplus.view` provides `invseeplusplus.invsee.view` and `invseeplusplus.endersee.view`.
- `invseeplusplus.edit` provides `invseeplusplus.invsee.edit` and `invseeplusplus.endersee.edit`.
- `invseeplusplus.exempt` provides `invseeplusplus.exempt.invsee` and `invseeplusplus.exempt.endersee`.
- `invseeplusplus.bypass-exempt` provides `invseeplusplus.bypass-exempt.invsee` and `invseeplusplus.bypass-exempt.endersee`.
- `invseeplusplus.*` provides all eight of the base permissions as well as all of the addon permissions.

## Addons

#### InvSee++_Give
##### Commands:
- `/invgive <target player> <item type> [<amount>] [<nbt tag>]`
- `/endergive <target player> <item type> [<amount>] [<nbt tag>]`
###### Examples:
- `/invgive Notch diamond 1 {"foo":"bar"}`
- `/endergive Jannyboy11 wool:14`
##### Permissions:
- `invseeplusplus.give.*` provides `invseeplusplus.give.inventory` and `invseeplusplus.give.enderchest`.
- `invseeplusplus.give.inventory` allows access to `/invgive`.
- `invseeplusplus.give.enderchest` allows access to `/endergive`.


#### InvSee++_Clear
##### Commands:
- `/invclear <player> <item type>? <amount>?`
- `/enderclear <player> <item type>? <amount>?`
###### Examples:
- `/invclear Notch diamond 1`
- `/enderclear Jannyboy11 wool:14`
##### Permissions:
- `invseeplusplus.clear.*` provides `invseeplusplus.clear.inventory` and `invseeplusplus.clear.enderchest`.
- `invseeplusplus.clear.inventory` allows access to `/invclear`.
- `invseeplusplus.clear.enderchest` allows access to `/enderclear`.

### Contact

Bugs & Feature requests: [GitHub issues](https://github.com/Jannyboy11/InvSee-plus-plus/issues)
Anything else can be discussed via the [discussion thread on SpigotMC](https://www.spigotmc.org/threads/invsee.456148/) or via
[Discord](https://discord.gg/Z8WCDHHcdJ).

### Compiling

###### Prerequisites: [JDK-17](https://jdk.java.net/) or newer, [BuildTools](https://www.spigotmc.org/wiki/buildtools/) and [Maven](https://maven.apache.org).

1. Install CraftBukkit into your local repository first by running BuildTools with
    - `java -jar BuildTools.jar --rev 1.20.1 --compile craftbukkit --remapped`
2. In the root directory of this project run `mvn clean package`.
You can find the plugin jar at InvSee++_plugin/target/InvSee++.jar.

### Developers API
Documentation available on the [wiki](https://github.com/Jannyboy11/InvSee-plus-plus/wiki)!

### License
LGPLv2.1. See the LICENSE.txt file.

### Credits
Special thanks to Icodak ([Discord](https://discordapp.com/users/345308025331908619)) ([SpigotMC](https://www.spigotmc.org/members/icodak.473813/)) for creating the logo!

### Supported server software

CraftBukkit 1.20.1
