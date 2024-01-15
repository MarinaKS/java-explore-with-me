package ru.practicum.server.event.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "event", schema = "public")
public class Event {
}
