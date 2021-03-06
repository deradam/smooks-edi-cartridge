package org.smooks.edi.edg.template;

import com.github.mustachejava.reflect.ReflectionObjectHandler;

import java.util.List;
import java.util.Set;

public class InterchangeTemplate extends Template {

    public InterchangeTemplate(String version, List<String> messageTypes) {
        super(version);
        mustacheFactory.setObjectHandler(new ReflectionObjectHandler());
        scope.put("schemaLocation", "EDIFACT-Messages.dfdl.xsd");
        scope.put("messageTypes", messageTypes);
    }

    @Override
    public String getName() {
        return "EDIFACT-Common/EDIFACT-Interchange.dfdl.xsd.mustache";
    }
}
