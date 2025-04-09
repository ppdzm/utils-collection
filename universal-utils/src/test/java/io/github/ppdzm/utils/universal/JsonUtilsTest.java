package io.github.ppdzm.utils.universal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import io.github.ppdzm.utils.universal.base.Person;
import io.github.ppdzm.utils.universal.formats.json.JacksonJsonUtils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by Stuart Alex on 2024/3/13.
 */
public class JsonUtilsTest {
    @Test
    public void test() throws JsonProcessingException {
        List<Person> personList = new ArrayList<>();
        personList.add(Person.builder().name("张三").age(30).build());
        personList.add(Person.builder().name("李四").age(30).build());
        String json = JacksonJsonUtils.serialize(personList);
        System.out.println(json);
//        final List<Person> parse =(List<Person>) JacksonJsonUtils.parse(json, List.class);
        personList.clear();
        ArrayNode arrayNode = (ArrayNode) JacksonJsonUtils.parse(json);
        for (JsonNode jsonNode : arrayNode) {
            Person person = JacksonJsonUtils.deserialize(JacksonJsonUtils.serialize(jsonNode), Person.class);
            personList.add(person);
        }
        System.out.println(JacksonJsonUtils.serialize(personList));
    }
}
