package app.habit.service.gpt;

import app.habit.dto.HabitPreQuestionRs;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;
import java.util.List;

public class PreQuestionDeserializer extends StdDeserializer<List<HabitPreQuestionRs>> {

    public PreQuestionDeserializer() {
        super(List.class);
    }

    @Override
    public List<HabitPreQuestionRs> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(p.getText(), new TypeReference<List<HabitPreQuestionRs>>(){});
    }
}
