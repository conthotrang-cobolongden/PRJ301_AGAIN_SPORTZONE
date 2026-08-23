package model;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
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

        // U7-U9: camel-case JavaBeans properties used by court JSP EL.
        requireReadableProperty(Court.class, "courtId");
        requireReadableProperty(Court.class, "imageUrl");
        requireReadableProperty(TimeSlot.class, "timeSlotId");

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

    private static void requireReadableProperty(Class<?> type, String propertyName)
            throws IntrospectionException {
        for (PropertyDescriptor property
                : Introspector.getBeanInfo(type).getPropertyDescriptors()) {
            if (propertyName.equals(property.getName())
                    && property.getReadMethod() != null) {
                return;
            }
        }
        throw new AssertionError(type.getSimpleName()
                + " must expose readable JavaBeans property '"
                + propertyName + "' for JSP EL");
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
