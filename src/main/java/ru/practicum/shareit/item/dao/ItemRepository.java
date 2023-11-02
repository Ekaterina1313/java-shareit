package ru.practicum.shareit.item.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;


public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query("SELECT i FROM Item i WHERE i.available = true AND (LOWER(i.name) LIKE %:searchText% OR LOWER(i.description) LIKE %:searchText%)")
    Page<Item> searchItems(String searchText, Pageable pageable);

    Page<Item> findByOwnerId(Long ownerId, Pageable pageable);

    @Query("SELECT i FROM Item i WHERE i.requestId IN :requestIds")
    List<Item> findByRequestIds(List<Long> requestIds);

    List<Item> findByRequestId(Long requestId);
}