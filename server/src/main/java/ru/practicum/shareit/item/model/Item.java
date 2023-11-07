package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "items")

public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String name;
    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private Boolean available;

    @Column
    @JoinColumn(name = "request_id", nullable = true)
    private Long requestId;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;
}