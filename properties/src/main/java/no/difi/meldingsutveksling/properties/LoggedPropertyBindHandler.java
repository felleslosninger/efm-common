package no.difi.meldingsutveksling.properties;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.context.properties.bind.AbstractBindHandler;
import org.springframework.boot.context.properties.bind.BindContext;
import org.springframework.boot.context.properties.bind.BindHandler;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;
import tools.jackson.databind.introspect.Annotated;
import tools.jackson.databind.introspect.JacksonAnnotationIntrospector;
import tools.jackson.databind.json.JsonMapper;

@Slf4j
public class LoggedPropertyBindHandler extends AbstractBindHandler {

    private final JsonMapper jsonMapper;

    public LoggedPropertyBindHandler(BindHandler parent) {
        super(parent);
        jsonMapper = JsonMapper.builder().annotationIntrospector(new JacksonAnnotationIntrospector() {
            @Override
            protected boolean _isIgnorable(Annotated a) {
                return a.getName().toLowerCase().contains("token") ||
                    a.getName().toLowerCase().contains("password");
            }
        }).build();
    }

    @Override
    public void onFinish(@NonNull ConfigurationPropertyName name, Bindable<?> target, @NonNull BindContext context, Object result) throws Exception {
        if (target.getAnnotation(LoggedProperty.class) != null) {
            log.info("Property set: {} = {}", name, jsonMapper.writeValueAsString(result));
        }

        super.onFinish(name, target, context, result);
    }
}
