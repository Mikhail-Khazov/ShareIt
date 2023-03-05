package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestDtoSaveTest {
    @Autowired
    JacksonTester<ItemRequestDtoSave> itemRequestDtoSaveJacksonTester;

    ItemRequestDtoSave itemRequestDtoSave = new ItemRequestDtoSave("text");

    @Test
    void serializeTest() throws IOException {
        JsonContent<ItemRequestDtoSave> json = itemRequestDtoSaveJacksonTester.write(itemRequestDtoSave);

        assertThat(json).extractingJsonPathStringValue("description").isEqualTo(itemRequestDtoSave.getDescription());
    }
}