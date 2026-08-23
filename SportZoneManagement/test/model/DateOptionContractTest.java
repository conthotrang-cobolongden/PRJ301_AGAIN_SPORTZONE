package model;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Kiem tra contract cua DateOption dung cho day pill chon ngay
 * tren trang chi tiet san.
 *
 * JSP contract:
 * - ${d.value}
 * - ${d.weekday}
 * - ${d.dayMonth}
 * - ${d.selected}
 *
 * Tag: U9
 */
public final class DateOptionContractTest {

    private DateOptionContractTest() {
    }

    public static void main(String[] args) throws Exception {
        Class<?> dateOptionType = loadDateOptionType();

        Constructor<?> constructor = dateOptionType.getConstructor(
                String.class,
                String.class,
                String.class,
                boolean.class
        );

        Object option = constructor.newInstance(
                "2026-08-23",
                "CN",
                "23/08",
                true
        );

        requireGetterValue(
                dateOptionType,
                option,
                "getValue",
                "2026-08-23"
        );

        requireGetterValue(
                dateOptionType,
                option,
                "getWeekday",
                "CN"
        );

        requireGetterValue(
                dateOptionType,
                option,
                "getDayMonth",
                "23/08"
        );

        requireGetterValue(
                dateOptionType,
                option,
                "isSelected",
                Boolean.TRUE
        );

        System.out.println("DateOptionContractTest PASSED");
    }

    /**
     * Dung reflection de test van compile duoc khi DateOption
     * chua ton tai trong vong RED.
     */
    private static Class<?> loadDateOptionType() {
        try {
            return Class.forName("model.DateOption");
        } catch (ClassNotFoundException exception) {
            throw new AssertionError(
                    "DateOption must exist for court detail date pills",
                    exception
            );
        }
    }

    /**
     * Goi getter bang reflection va so sanh gia tri tra ve.
     */
    private static void requireGetterValue(
            Class<?> type,
            Object target,
            String getterName,
            Object expected
    ) throws Exception {
        Method getter = type.getMethod(getterName);
        Object actual = getter.invoke(target);

        if (!expected.equals(actual)) {
            throw new AssertionError(
                    getterName
                    + " expected <"
                    + expected
                    + "> but was <"
                    + actual
                    + ">"
            );
        }
    }
}