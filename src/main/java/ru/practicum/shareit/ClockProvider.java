package ru.practicum.shareit;

import java.time.Clock;

public interface ClockProvider {
    Clock systemClock();
}