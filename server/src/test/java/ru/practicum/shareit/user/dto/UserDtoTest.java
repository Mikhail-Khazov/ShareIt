package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class UserDtoTest {
    @Autowired
    JacksonTester<UserDto> userDtoJacksonTester;

    UserDto userDto = new UserDto(1L, "John", "doe@ya.ru");

    @Test
    void serializeTest() throws IOException {
        JsonContent<UserDto> json = userDtoJacksonTester.write(userDto);

        assertThat(json).extractingJsonPathNumberValue("id").isEqualTo(1);
        assertThat(json).extractingJsonPathStringValue("name").isEqualTo("John");
        assertThat(json).extractingJsonPathStringValue("email").isEqualTo("doe@ya.ru");
    }
}