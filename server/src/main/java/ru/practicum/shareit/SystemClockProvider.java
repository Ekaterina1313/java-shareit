package ru.practicum.shareit;

import org.springframework.stereotype.Component;

import java.time.Clock;

@Component
public class SystemClockProvider implements ClockProvider {
    public Clock systemClock() {
        return Clock.systemDefaultZone();
    }
}