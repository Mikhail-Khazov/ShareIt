package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoSaveTest {
    @Autowired
    JacksonTester<BookingDtoSave> bookingDtoSaveJacksonTester;
    LocalDateTime dateStart = LocalDateTime.of(2022, 11, 11, 22, 31, 9);
    LocalDateTime dateEnd = LocalDateTime.of(2022, 11, 16, 15, 57, 6);

    BookingDtoSave bookingDtoSave = BookingDtoSave.builder()
            .itemId(1L)
            .start(dateStart)
            .end(dateEnd)
            .build();

    @Test
    void serializeTest() throws IOException {
        JsonContent<BookingDtoSave> json = bookingDtoSaveJacksonTester.write(bookingDtoSave);

        assertThat(json).extractingJsonPathNumberValue("itemId").isEqualTo(1);
        assertThat(json).extractingJsonPathValue("start").isEqualTo(dateStart.toString());
        assertThat(json).extractingJsonPathValue("end").isEqualTo(dateEnd.toString());
    }
}