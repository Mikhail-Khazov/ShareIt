package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemDtoTest {
    @Autowired
    JacksonTester<ItemDto> itemDtoJacksonTester;

    ItemDto itemDto = ItemDto.builder()
            .id(1L)
            .name("Pen")
            .description("Blue")
            .available(true)
            .requestId(1L)
            .build();

    @Test
    void serializeTest() throws IOException {
        JsonContent<ItemDto> json = itemDtoJacksonTester.write(itemDto);

        assertThat(json).extractingJsonPathNumberValue("id").isEqualTo(1);
        assertThat(json).extractingJsonPathStringValue("name").isEqualTo("Pen");
        assertThat(json).extractingJsonPathStringValue("description").isEqualTo("Blue");
        assertThat(json).extractingJsonPathBooleanValue("available").isEqualTo(true);
        assertThat(json).extractingJsonPathNumberValue("requestId").isEqualTo(1);
    }
}