package io.renren.common.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import io.renren.common.utils.SpringContextUtils;

import java.io.IOException;
import java.text.DecimalFormat;

/**
 * Decimal
 */
public class CustomDoubleIntSerialize extends JsonSerializer<Double> {


    @Override
    public void serialize(Double value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        RenrenProperties renrenProperties = SpringContextUtils.getBean(RenrenProperties.class);

        jgen.writeString(new DecimalFormat(renrenProperties.getDecimalFormat()).format(value));
    }
}
