package lk.rc07.ten_years.touchdown.utils;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Sabri on 1/14/2017. Date formatter for Gson seralizer
 */

public class JsonDateSerializer implements JsonDeserializer<Date> {

    private String dateFormatter;

    public JsonDateSerializer(String dateFormatter) {
        this.dateFormatter = dateFormatter;
    }

    @Override
    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        SimpleDateFormat format = new SimpleDateFormat(dateFormatter, Locale.getDefault());
        String date = json.getAsString();
        try {
            if (!date.equals("")) {
                return format.parse(date);
            } else {
                return null;
            }

        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
