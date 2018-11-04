package com.finance.montecarlo;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

/**
 * Utils used for testing
 *
 * @author  Dean Hutton
 * @version 1.0
 * @since   2018-11-04
 */
public class TestUtils {
    /**
     * Converts a JSON string to an object
     *
     * @param json the string to convert
     * @param clazz the type of object to convert the json too
     * @return Object representation of the json string
     */
    public static <T> T convertJsonStringToObject(final String json, final Class<T> clazz)
            throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, clazz);
    }

    /**
     * Converts a object to JSON string
     *
     * @param obj the object to convert to json string
     * @return String of json
     */
    public static String convertObjectToJsonString(final Object obj) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            final String jsonContent = mapper.writeValueAsString(obj);
            return jsonContent;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Converts currency string to a big decimal
     *
     * @param amount the string to convert i.e. $10,000
     * @param locale the type of currency i.e. American
     * @return String of json
     */
    public static BigDecimal parse(final String amount, final Locale locale) throws ParseException {
        final NumberFormat format = NumberFormat.getNumberInstance(locale);
        if (format instanceof DecimalFormat) {
            ((DecimalFormat) format).setParseBigDecimal(true);
        }
        return (BigDecimal) format.parse(amount.replaceAll("[^\\d.,]",""));
    }
}
