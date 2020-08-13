package com.reason.restapi.accounts;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.yaml.snakeyaml.serializer.Serializer;

import java.io.IOException;

// json component로 등록하면 항상 아래와 같이 시리얼라이즈 된다. 필요에 따라 선택하도록 등록하지 않는다.
public class AccountSerializer extends JsonSerializer<Account> {
    @Override
    public void serialize(Account account, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("id", account.getId());
        jsonGenerator.writeEndObject();
    }
}
