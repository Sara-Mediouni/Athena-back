package com.example.demo.API.Entity;

import java.time.LocalDate;

class MonthSummary {
    LocalDate minDate;
    LocalDate maxDate;
    double total;

    void update(LocalDate date, double ttc) {
        if (minDate == null || date.isBefore(minDate)) {
            minDate = date;
        }
        if (maxDate == null || date.isAfter(maxDate)) {
            maxDate = date;
        }
        total += ttc;
    }
}
