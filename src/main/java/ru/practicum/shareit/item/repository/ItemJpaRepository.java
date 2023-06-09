package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemJpaRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByOwnerId(Long ownerId);

    List<Item> findAllByNameOrDescriptionContainingIgnoreCaseAndAvailable(String name, String description, boolean available);

    List<Item> findAllByRequestId(Long requestId);

}
