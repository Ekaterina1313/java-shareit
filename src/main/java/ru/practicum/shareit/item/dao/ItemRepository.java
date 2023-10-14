package ru.practicum.shareit.item.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;


public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query("SELECT i FROM Item i WHERE i.available = true AND (LOWER(i.name) LIKE %:searchText% OR LOWER(i.description) LIKE %:searchText%)")
    List<Item> searchItems(String searchText);

    List<Item> findByOwnerId(Long ownerId);
}