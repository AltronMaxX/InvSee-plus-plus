package com.janboerman.invsee.spigot.perworldinventory;

import static com.janboerman.invsee.utils.Compat.listOf;

import com.janboerman.invsee.utils.Either;
import com.janboerman.invsee.utils.Out;
import com.janboerman.invsee.utils.StringHelper;
import me.ebonjaeger.perworldinventory.Group;
import org.bukkit.GameMode;
import org.bukkit.World;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PwiCommandArgs {

    private static final String formError = "Expected the following from: PWI{<property>=<value>,...} where <property> is one of [group, world, gamemode].";

    String world;
    Group group;
    GameMode gameMode;

    private PwiCommandArgs() {}

    public static Either<String, PwiCommandArgs> parse(String argument, PerWorldInventoryHook hook) {
        PwiCommandArgs result = new PwiCommandArgs();

        if (argument.isEmpty()) return Either.left(formError);
        if (!StringHelper.startsWithIgnoreCase(argument, "PWI")) return Either.left(formError);
        if (!StringHelper.startsWithIgnoreCase(argument, "PWI{") || !argument.endsWith("}")) return Either.left(formError);
        argument = argument.substring(4, argument.length() - 1);

        Optional<String> maybeError = parseProperties(result, argument, hook);
        if (maybeError.isPresent()) {
            return Either.left(maybeError.get());
        } else {
            return Either.right(result);
        }
    }

    private static Optional<String> parseProperties(@Out PwiCommandArgs result, String propertyList, PerWorldInventoryHook hook) {
        String[] properties = propertyList.split(",");
        for (String kv : properties) {
            String[] keyValue = kv.split("=", 2);
            if (keyValue.length != 2) return Optional.of("Invalid argument, expected <property>=<value>, but got " + kv + " instead.");
            String key = keyValue[0];
            String value = keyValue[1];

            if ("world".equalsIgnoreCase(key)) {
                result.world = value;

            } else if ("group".equalsIgnoreCase(key)) {
                result.group = hook.getGroupByName(value);
                if (result.group == null) return Optional.of("Invalid group: " + value + ", pick one of: " + hook.getGroupManager().getGroups().keySet());

            } else if ("gamemode".equalsIgnoreCase(key)) {
                try {
                    result.gameMode = GameMode.valueOf(value.toUpperCase(Locale.ROOT));
                } catch (IllegalArgumentException e) {
                    return Optional.of("Invalid gamemode: " + value + ", pick one of: " + Arrays.stream(GameMode.values())
                            .map(gm -> gm.name().toLowerCase(Locale.ROOT))
                            .collect(Collectors.joining(" ,", "[", "]")));
                }

            } else {
                return Optional.of("Invalid property, expected one of [group, world, gamemode] but got " + key + " instead.");
            }
        }

        return Optional.empty();
    }

    public static List<String> complete(final String argument, PerWorldInventoryHook hook) {
        //TODO this can be called asynchronously!
        //TODO I don't think this is threadsafe.
        //TODO if we are called async, then the should wait for the primary thread, execute the logic on there, and then join.

        if (argument.length() < 4) return listOf("PWI{");
        if (!StringHelper.startsWithIgnoreCase(argument, "PWI{")) {
            return listOf("PWI{group=", "PWI{world=", "PWI{gamemode=");
        }

        String propertyList = argument.substring(4);
        if (propertyList.endsWith("}")) return listOf(argument);

        final Collection<String> groupNames = hook.getGroupManager().getGroups().keySet();
        final Collection<String> worldNames = hook.plugin.getServer().getWorlds().stream().map(World::getName).collect(Collectors.toList());
        final Collection<String> gameModes = Arrays.stream(GameMode.values()).map(gm -> gm.name().toLowerCase(Locale.ROOT)).collect(Collectors.toList());

        String[] properties = propertyList.split(",");
        if (properties.length == 0 || ((properties.length == 1 && properties[0].isEmpty()))) {
            List<String> ret = new ArrayList<>(9);
            for (String groupName : groupNames) {
                ret.add("PWI{group=" + groupName + "}");
            }
            for (String worldName : worldNames) {
                ret.add("PWI{world=" + worldName + "}");
            }
            for (String gameMode : gameModes) {
                ret.add("PWI{gamemode=" + gameMode + "}");
            }
            return ret;
        }

        PwiCommandArgs result = new PwiCommandArgs();
        parseProperties(result, propertyList, hook); //ignore error message

        String lastProperty = properties[properties.length - 1];
        int stripLength = lastProperty.length();
        boolean endsWithComma = argument.endsWith(",");
        if (endsWithComma) stripLength += 1; //not sure whether this is correct.
        String bufferBeforeLastProperty = argument.substring(0, argument.length() - stripLength);

        if (endsWithComma) {
            //we end with a comma, complete a new property
            List<String> everything = new ArrayList<>(9);
            if (result.group == null) {
                groupNames.stream().map(gn -> argument + "group=" + gn).forEach(everything::add);
            } else if (result.world == null) {
                worldNames.stream().map(wn -> argument + "world=" + wn).forEach(everything::add);
            } else if (result.gameMode == null) {
                gameModes.stream().map(gm -> argument + "gamemode=" + gm).forEach(everything::add);
            } else {
                return listOf(argument.substring(argument.length() - 1) + "}");
            }
            return everything;
        }

        //we don't end with a comma - complete the property
        String[] propKeyValue = lastProperty.split("=", 2);
        if (propKeyValue.length == 0 || (propKeyValue.length == 1 && propKeyValue[0].isEmpty())) {
            if (result.group == null) {
                //tab the group
                return groupNames.stream().map(groupName -> bufferBeforeLastProperty + "group=" + groupName)
                        .flatMap(buf -> Stream.of(buf + "}", buf + ","))
                        .collect(Collectors.toList());
            } else if (result.world == null) {
                //tab the world
                return worldNames.stream().map(worldName -> bufferBeforeLastProperty + "world=" + worldName)
                        .flatMap(buf -> Stream.of(buf + "}", buf + ","))
                        .collect(Collectors.toList());
            } else if (result.gameMode == null) {
                //tab the gamemode
                return gameModes.stream().map(gameMode -> bufferBeforeLastProperty + "gamemode=" + gameMode)
                        .flatMap(buf -> Stream.of(buf + "}", buf + ","))
                        .collect(Collectors.toList());
            } else {
                //all properties are specified - we are done!
                return listOf(argument + "}");
            }
        }

        String key = propKeyValue[0];
        if (propKeyValue.length == 1) {
            if (StringHelper.startsWithIgnoreCase("group", key)) {
                return listOf(bufferBeforeLastProperty + "group=");
            } else if (StringHelper.startsWithIgnoreCase("world", key)) {
                return listOf(bufferBeforeLastProperty + "world=");
            } else if (StringHelper.startsWithIgnoreCase("gamemode", key)) {
                return listOf(bufferBeforeLastProperty + "gamemode=");
            } else {
                return listOf(bufferBeforeLastProperty + "group=", bufferBeforeLastProperty + "world=", bufferBeforeLastProperty + "gamemode=");
            }
        }

        String value = propKeyValue[1];
        switch (key.toLowerCase(Locale.ROOT)) {
            case "group":
                Collection<String> matchingGroups = groupNames.stream().filter(gn -> StringHelper.startsWithIgnoreCase(gn, value)).collect(Collectors.toList());
                if (matchingGroups.isEmpty()) matchingGroups = groupNames;
                return matchingGroups.stream().map(gn -> bufferBeforeLastProperty + "group=" + gn)
                        .flatMap(buf -> Stream.of(buf + "}", buf + ","))
                        .collect(Collectors.toList());
            case "world":
                Collection<String> matchingWorlds = worldNames.stream().filter(wn -> StringHelper.startsWithIgnoreCase(wn, value)).collect(Collectors.toList());
                if (matchingWorlds.isEmpty()) matchingWorlds = worldNames;
                return matchingWorlds.stream().map(wn -> bufferBeforeLastProperty + "world=" + wn)
                        .flatMap(buf -> Stream.of(buf + "}", buf + ","))
                        .collect(Collectors.toList());
            case "gamemode":
                Collection<String> matchingGameModes = gameModes.stream().filter(gm -> StringHelper.startsWithIgnoreCase(gm, value)).collect(Collectors.toList());
                if (matchingGameModes.isEmpty()) matchingGameModes = gameModes;
                return matchingGameModes.stream().map(gm -> bufferBeforeLastProperty + "gamemode=" + gm)
                        .flatMap(buf -> Stream.of(buf + "}", buf + ","))
                        .collect(Collectors.toList());
            default:
                //bogus property - just send every possible option.
                Collection<String> everything = new ArrayList<>(9);
                if (result.group == null) everything.addAll(groupNames.stream().map(gn -> "group=" + gn).collect(Collectors.toList()));
                if (result.world == null) everything.addAll(worldNames.stream().map(wn -> "world=" + wn).collect(Collectors.toList()));
                if (result.gameMode == null) everything.addAll(gameModes.stream().map(gm -> "gamemode=" + gm).collect(Collectors.toList()));

                Stream<String> stream = everything.stream().map(property -> bufferBeforeLastProperty + property);
                if (result.group != null && result.world != null && result.gameMode != null) {
                    stream = stream.map(buf -> buf + "}");
                } else {
                    stream = stream.flatMap(buf -> Stream.of(buf + "}", buf + ","));
                }
                return stream.collect(Collectors.toList());
        }
    }

}
