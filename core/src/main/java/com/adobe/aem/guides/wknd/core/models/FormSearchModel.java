package com.adobe.aem.guides.wknd.core.models;

import javax.annotation.PostConstruct;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;


import java.util.Optional;

@Model(adaptables = Resource.class, resourceType = "wknd/components/formsearch")
public class FormSearchModel {

    @SlingObject
    private ResourceResolver resourceResolver;

    @ValueMapValue
    private String buttonText;

    @ValueMapValue
    private String inputLabel;

    @PostConstruct
    protected void init() {
        // Initialize model properties
        buttonText = Optional.ofNullable(buttonText).orElse("Submit");
        inputLabel = Optional.ofNullable(inputLabel).orElse("Search");
    }

    public String getButtonText() {
        return buttonText;
    }

    public String getInputLabel() {
        return inputLabel;
    }
}
