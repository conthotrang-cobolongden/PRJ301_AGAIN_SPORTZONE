package com.sportzone.model;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Kiem tra model Phase 1 bam dung chin bang cua ERD v3.
 */
public final class ModelContractTest {

    private ModelContractTest() {
    }

    public static void main(String[] args) throws Exception {
        requireDefaultConstructor(Role.class);
        requireDefaultConstructor(Account.class);
        requireDefaultConstructor(CourtType.class);
        requireDefaultConstructor(Court.class);
        requireDefaultConstructor(TimeSlot.class);
        requireDefaultConstructor(Service.class);
        requireDefaultConstructor(Booking.class);
        requireDefaultConstructor(BookingService.class);
        requireDefaultConstructor(Invoice.class);
        requireFullConstructor(Role.class);
        requireFullConstructor(Account.class);
        requireFullConstructor(CourtType.class);
        requireFullConstructor(Court.class);
        requireFullConstructor(TimeSlot.class);
        requireFullConstructor(Service.class);
        requireFullConstructor(Booking.class);
        requireFullConstructor(BookingService.class);
        requireFullConstructor(Invoice.class);

        Set<String> bookingFields = fieldNames(Booking.class);
        require(bookingFields.contains("timeSlotID"), "Booking must reference TimeSlotID");
        require(!bookingFields.contains("courtID"), "ERD v3 Booking must not contain CourtID");
        require(fieldNames(BookingService.class).containsAll(Arrays.asList(
                "bookingID", "serviceID", "quantity", "priceAtOrder", "lineTotal")),
                "BookingService must map the composite key and computed total");

        System.out.println("ModelContractTest PASSED");
    }

    private static void requireDefaultConstructor(Class<?> type) throws Exception {
        require(type.getDeclaredConstructor().newInstance() != null,
                type.getSimpleName() + " must have a default constructor");
    }

    private static Set<String> fieldNames(Class<?> type) {
        Set<String> names = new HashSet<>();
        for (Field field : type.getDeclaredFields()) {
            names.add(field.getName());
        }
        return names;
    }

    private static void requireFullConstructor(Class<?> type) {
        require(type.getDeclaredConstructors().length >= 2,
                type.getSimpleName() + " must have a full constructor");
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
