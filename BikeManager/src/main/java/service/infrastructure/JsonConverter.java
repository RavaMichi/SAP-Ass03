package service.infrastructure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import service.domain.EBike;
import service.domain.Station;
import service.domain.V2d;

import java.util.List;

public class JsonConverter {
    public static String toJson(Object obj) {
        switch (obj) {
            case EBike eBike -> {
                return toJson(eBike);
            }
            case Station station -> {
                return toJson(station);
            }
            case List<?> objects -> {
                return toJson(objects);
            }
            case V2d pos -> {
                return toJson(pos);
            }
            case null, default -> {
                ObjectMapper mapper = new ObjectMapper();
                try {
                    return mapper.writeValueAsString(obj);
                } catch (JsonProcessingException e) {
                    return "{}";
                }
            }
        }
    }
    public static String toJson(EBike obj) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode rootNode = mapper.createObjectNode();
        rootNode.put("id", obj.getID());
        rootNode.put("batteryLevel", obj.getBatteryLevel());
        var pos = mapper.createObjectNode();
        pos.put("x", obj.getPosition().x());
        pos.put("y", obj.getPosition().y());
        rootNode.set("position", pos);
        return rootNode.toPrettyString();
    }
    public static String toJson(List<?> objs) {
        return "[\n" +
                objs.stream().map(JsonConverter::toJson).reduce((i,f) -> i + ",\n" + f).orElse("")
                + "\n]";
    }
    public static String toJson(V2d obj) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode rootNode = mapper.createObjectNode();
        rootNode.put("x", obj.x());
        rootNode.put("y", obj.y());
        return rootNode.toPrettyString();
    }
    public static String toJson(Station obj) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode rootNode = mapper.createObjectNode();
        rootNode.put("id", obj.id());
        var pos = mapper.createObjectNode();
        pos.put("x", obj.position().x());
        pos.put("y", obj.position().y());
        rootNode.set("position", pos);
        return rootNode.toPrettyString();
    }
}
