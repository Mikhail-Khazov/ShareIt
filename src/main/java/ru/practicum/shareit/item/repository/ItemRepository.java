package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query("select i from Item i where i.owner.id = ?1 order by i.id asc ")
    List<Item> getUserStuff(long userId);

    @Query("select i from Item i " +
            "where (i.available = true) " +
            "and (lower(i.description) like lower(concat( '%', :text, '%')) " +
            "or lower(i.name) like lower(concat( '%', :text, '%')))")
    List<Item> search(@Param("text") String text);
}
