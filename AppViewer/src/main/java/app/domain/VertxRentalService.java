package app.domain;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.codec.BodyCodec;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class VertxRentalService implements RentalService {

    private WebClient webClient;
    private final int port = 8080;
    private final String host = "localhost";
    private final String token = "AUTHORIZED";

    public VertxRentalService() {
        Vertx vertx = Vertx.vertx();
        this.webClient = WebClient.create(vertx);
    }

    @Override
    public List<EBike> getBikes() {
        var bikes = webClient.get(port, host, "/bikes")
                .as(BodyCodec.jsonArray())
                .putHeader("Authorization", token)
                .send()
                .map(res -> {
                    JsonArray jsonArray = res.body();
                    List<EBike> bikeList = new ArrayList<>();
                    for (int i = 0; i < jsonArray.size(); i++) {
                        JsonObject jsonBike = jsonArray.getJsonObject(i);
                        bikeList.add(new EBike(
                                jsonBike.getString("id"),
                                jsonBike.getInteger("batteryLevel"),
                                jsonBike.getJsonObject("position").getDouble("x"),
                                jsonBike.getJsonObject("position").getDouble("y")
                        ));
                    }
                    return bikeList;
                })
                .toCompletionStage()
                .toCompletableFuture();

        try {
            return bikes.get();
        } catch (InterruptedException | ExecutionException e) {
            return List.of();
        }
    }

    @Override
    public List<User> getUsers() {
        var users = webClient.get(port, host, "/users")
                .as(BodyCodec.jsonArray())
                .putHeader("Authorization", token)
                .send()
                .map(res -> {
                    JsonArray jsonArray = res.body();
                    List<User> userList = new ArrayList<>();
                    for (int i = 0; i < jsonArray.size(); i++) {
                        JsonObject jsonUser = jsonArray.getJsonObject(i);
                        userList.add(new User(
                                jsonUser.getString("username"),
                                jsonUser.getInteger("credits")
                        ));
                    }
                    return userList;
                })
                .toCompletionStage()
                .toCompletableFuture();

        try {
            return users.get();
        } catch (InterruptedException | ExecutionException e) {
            return List.of();
        }
    }
}
