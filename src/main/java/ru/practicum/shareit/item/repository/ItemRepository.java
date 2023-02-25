package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query("select i from Item i where i.owner.id = ?1 order by i.id asc ")
    Page<Item> getUserStuff(long userId, PageRequest pageRequest);

    @Query("select i from Item i " +
            "where (i.available = true) " +
            "and (lower(i.description) like lower(concat( '%', :text, '%')) " +
            "or lower(i.name) like lower(concat( '%', :text, '%')))")
    Page<Item> search(@Param("text") String text, PageRequest pageRequest);

    @Query("select i from Item i where (i.available = true) and i.request.id = ?1")
    List<Item> findAllByRequestIds(List<Long> collect);

    @Query("select i from Item i where (i.available = true) and i.request.id = ?1")
    List<Item> findByRequestId(Long requestId);
}
