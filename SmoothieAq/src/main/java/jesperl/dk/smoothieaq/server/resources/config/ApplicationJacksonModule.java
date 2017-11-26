package jesperl.dk.smoothieaq.server.resources.config;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.*;
import com.fasterxml.jackson.databind.deser.std.*;
import com.fasterxml.jackson.databind.ser.*;
import com.fasterxml.jackson.databind.ser.std.*;
import com.fasterxml.jackson.databind.type.*;
import com.fasterxml.jackson.databind.util.*;

public class ApplicationJacksonModule extends Module {
	
    public static class IdEnumDeserializers extends Deserializers.Base {
    	@Override public JsonDeserializer<?> findEnumDeserializer(Class<?> type, DeserializationConfig config, BeanDescription beanDesc) throws JsonMappingException {
    		return new JsInterOpAwareEnumDeserializer(type);
    	}
    }
    
    public static class JsInterOpAwareEnumDeserializer extends StdDeserializer<Enum<?>> {
		private static final long serialVersionUID = -7004258190341723444L;
		private Method enumValuOfs;

		public JsInterOpAwareEnumDeserializer(Class<?> vc) {
			super(vc);
			try {
				enumValuOfs = vc.getMethod("valueOf",String.class);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		@Override public Enum<?> deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException {
			String enumName = "error";
			if (parser.getCurrentToken() == JsonToken.VALUE_STRING) {
				enumName = parser.getText();
			} else {
				JsonNode node = parser.getCodec().readTree(parser);
				Iterator<String> it = node.fieldNames();
				while (it.hasNext()) {
					String fieldName = it.next();
					if (fieldName.startsWith("name_"))
						enumName = node.get(fieldName).asText();
				}
			}
			try {
				return (Enum<?>) enumValuOfs.invoke(null, enumName);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
    	
    }
    
    public static class RxJavaJacksonSerializers extends Serializers.Base {
        @Override public JsonSerializer<?> findSerializer(SerializationConfig sc, JavaType jt, BeanDescription bd) {
            Class<?> raw = jt.getRawClass();
            if (rx.Observable.class.isAssignableFrom(raw)) {
                JavaType vt = jt.containedType(0);
                if (vt == null) vt = TypeFactory.unknownType();
                JavaType delegate = sc.getTypeFactory().constructParametrizedType(Iterable.class, Iterable.class, vt);
                return new StdDelegatingSerializer(ObservableConverter.instance, delegate, null);
            }
            if (rx.Single.class.isAssignableFrom(raw)) {
                JavaType delegate = jt.containedType(0);
                if (delegate == null) delegate = TypeFactory.unknownType();
                return new StdDelegatingSerializer(SingleConverter.instance, delegate, null);
            }
            return super.findSerializer(sc, jt, bd);
        }

        static class ObservableConverter extends StdConverter<Object, Iterable<?>> {
            static final Converter<Object, Iterable<?>> instance = new ObservableConverter();
            @Override public Iterable<?> convert(Object v) { return ((rx.Observable<?>) v).toBlocking().toIterable(); }
        }

        static class SingleConverter extends StdConverter<Object, Object> {
            static final Converter<Object, Object> instance = new SingleConverter();
            @Override public Object convert(Object v) { return ((rx.Single<?>) v).toObservable().toBlocking().single(); }
        }
    }
	
	@Override public String getModuleName() { return "Application Module"; }
    @Override public Version version() { return Version.unknownVersion(); }
    
    @Override public void setupModule(SetupContext c) {
        c.addSerializers(new RxJavaJacksonSerializers());
        c.addDeserializers(new IdEnumDeserializers());
    }
}