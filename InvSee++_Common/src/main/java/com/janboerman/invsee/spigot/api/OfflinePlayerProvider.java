package com.janboerman.invsee.spigot.api;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.function.Consumer;

public interface OfflinePlayerProvider {

    /** Gets all usernames of players that are known to the server. Usernames will be passed to the consumer as they are discovered. */
    public void getAll(Consumer<String> consumer);

    @Deprecated//(forRemoval = true, since = "0.20.0") //TODO delete in 1.0
    public default Set<String> getAll() {
        Set<String> result = new ConcurrentSkipListSet<>(String.CASE_INSENSITIVE_ORDER);
        getAll(result::add);
        return result;
    }

    /** Gets all usernames that starts with the specified prefix of players that are known to the server. Usernames will be passed to the consumer as they are discovered. */
    public void getWithPrefix(String prefix, Consumer<String> consumer);

    @Deprecated//(forRemoval = true, since = "0.20.0")  //TODO delete in 1.0
    public default Set<String> getWithPrefix(String prefix) {
        Set<String> result = new ConcurrentSkipListSet<>(String.CASE_INSENSITIVE_ORDER);
        getWithPrefix(prefix, result::add);
        return result;
    }

    public static class Dummy implements OfflinePlayerProvider {

        public static final Dummy INSTANCE = new Dummy();

        private Dummy() {
        }

        @Override
        public void getAll(Consumer<String> consumer) {
        }

        @Override
        public void getWithPrefix(String prefix, Consumer<String> consumer) {
        }

        @Override
        public Set<String> getAll() {
            return Collections.emptySet();
        }

        @Override
        public Set<String> getWithPrefix(String prefix) {
            return Collections.emptySet();
        }
    }

}
