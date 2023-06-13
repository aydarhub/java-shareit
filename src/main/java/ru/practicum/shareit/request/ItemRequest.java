package ru.practicum.shareit.request;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@Getter
@Setter
@Entity
@Table(name = "requests")
public class ItemRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "description", nullable = false)
    private String description;

    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "requestor_id")
    private User requestor;

    private LocalDateTime created;

}
