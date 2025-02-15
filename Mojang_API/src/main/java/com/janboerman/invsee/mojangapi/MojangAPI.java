package com.janboerman.invsee.mojangapi;

import static com.janboerman.invsee.mojangapi.ResponseUtils.readJSONObject;
import static com.janboerman.invsee.mojangapi.ResponseUtils.charsetFromHeader;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import com.janboerman.invsee.utils.Compat;
import com.janboerman.invsee.utils.Rethrow;
import com.janboerman.invsee.utils.UUIDHelper;

import org.json.simple.JSONObject;

public class MojangAPI {

    private final Executor asyncExecutor;

    public MojangAPI(Executor asyncExecutor) {
        this.asyncExecutor = asyncExecutor;
    }

    public CompletableFuture<Optional<UUID>> lookupUniqueId(String userName) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + userName);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5 * 1000); //5 seconds
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestProperty("User-Agent", "InvSee++/MojangAPI");

                int statusCode = connection.getResponseCode();

                if (statusCode == HttpURLConnection.HTTP_OK) {
                    JSONObject json = readJSONObject(connection);
                    String id = (String) json.get("id");
                    UUID uuid = UUIDHelper.dashed(id);
                    return Optional.of(uuid);
                }

                else {
                    return handleNotOk(statusCode, connection);
                }

            } catch (IOException e) {
                return Rethrow.unchecked(e);
            }
        }, asyncExecutor);
    }

    public CompletableFuture<Optional<String>> lookupUserName(UUID uniqueId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + UUIDHelper.unDashed(uniqueId));
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5 * 1000); //5 seconds
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestProperty("User-Agent", "InvSee++/MojangAPI");

                int statusCode = connection.getResponseCode();

                if (statusCode == HttpURLConnection.HTTP_OK) {
                    JSONObject profileJson = readJSONObject(connection);
                    String userName = (String) profileJson.get("name");
                    return Optional.of(userName);
                }

                else {
                    return handleNotOk(statusCode, connection);
                }

            } catch (IOException e) {
                return Rethrow.unchecked(e);
            }
        }, asyncExecutor);
    }

    private static <T> Optional<T> handleNotOk(int statusCode, HttpURLConnection connection) throws IOException {
        assert statusCode == connection.getResponseCode();

        switch (statusCode) {
            case ResponseUtils.HTTP_NO_CONTENT: return handleNoContent(connection);
            case ResponseUtils.HTTP_BAD_REQUEST: return handleBadRequest(connection);
            case ResponseUtils.HTTP_TOO_MANY_REQUESTS: return handleTooManyRequests(connection);
            default: return handleUnknownStatusCode(statusCode);
        }
    }

    private static <T> Optional<T> handleNoContent(HttpURLConnection connection) {
        return Optional.empty();
    }

    private static <T> Optional<T> handleBadRequest(HttpURLConnection connection) {
        JSONObject jsonObject = readJSONObject(connection);

        String error = (String) jsonObject.get("error");
        String errorMessage = (String) jsonObject.get("errorMessage");

        throw new RuntimeException("We sent a bad request to Mojang. We got a(n) " + error + " with the following message: " + errorMessage);
    }

    private static <T> Optional<T> handleTooManyRequests(HttpURLConnection connection) {
        try (InputStream inputStream = connection.getInputStream()) {
            byte[] bytes = Compat.readAllBytes(inputStream);
            Charset charset = charsetFromHeader(connection.getHeaderField("Content-Type"));
            String errorMessage = new String(bytes, charset);
            throw new RuntimeException("We were rate limited by Mojang. Error message: " + errorMessage);
        } catch (IOException e) {
            throw new RuntimeException("Exception occurred when processing 429 (rate limited) response.", e);
        }
    }

    private static <T> Optional<T> handleUnknownStatusCode(int statusCode) {
        throw new RuntimeException("Unexpected status code from Mojang API: " + statusCode);
    }
}
