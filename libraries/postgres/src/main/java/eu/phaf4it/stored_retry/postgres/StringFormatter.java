package eu.phaf4it.stored_retry.postgres;

import org.slf4j.helpers.MessageFormatter;

public final class StringFormatter {
    private StringFormatter() {
    }

    public static String format(String format, Object... params) {
        return MessageFormatter.arrayFormat(format, params).getMessage();
    }
}
