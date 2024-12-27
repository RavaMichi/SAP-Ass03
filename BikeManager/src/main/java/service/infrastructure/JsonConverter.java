package service.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import service.domain.EBike;

import java.util.List;

public class JsonConverter {
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
    public static String toJson(List<EBike> objs) {
        return "[\n" +
                objs.stream().map(JsonConverter::toJson).reduce((i,f) -> i + ",\n" + f).orElse("")
                + "\n]";
    }
}
