package com.adobe.aem.guides.wknd.core.models;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.day.cq.wcm.api.Page;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * JUnit test verifying the FormSearchModel
 */
@ExtendWith(AemContextExtension.class)
class FormSearchModelTest {

    private FormSearchModel formSearchModel;

    private Page page;
    private Resource resource;

    @BeforeEach
    public void setup(AemContext context) throws Exception {
        // Create a page in the test context
        page = context.create().page("/content/mypage");

        // Create a resource and set its resource type to match the Sling model's expected resource type
        resource = context.create().resource(page, "formsearch",
            "sling:resourceType", "wknd/components/formsearch");

        // Log and check if the resource type is correctly assigned
        System.out.println("Resource type: " + resource.getResourceType());

        // Adapt the resource to the FormSearchModel
        formSearchModel = resource.adaptTo(FormSearchModel.class);

        // Check if the model is successfully adapted
        assertNotNull(formSearchModel, "FormSearchModel should not be null after adaptation");
    }

	/*
	 * @Test void testButtonText() throws Exception { // Ensure buttonText is
	 * initialized String buttonText = formSearchModel.getButtonText();
	 * assertNotNull(buttonText, "Button text should not be null");
	 * assertTrue(StringUtils.isNotBlank(buttonText),
	 * "Button text should not be blank");
	 * 
	 * // Additional check: if there is a default value, assert it
	 * assertEquals("Submit", buttonText,
	 * "Button text should match the expected value"); }
	 * 
	 * @Test void testInputLabel() throws Exception { // Ensure inputLabel is
	 * initialized String inputLabel = formSearchModel.getInputLabel();
	 * assertNotNull(inputLabel, "Input label should not be null");
	 * assertTrue(StringUtils.isNotBlank(inputLabel),
	 * "Input label should not be blank");
	 * 
	 * // Additional check: if there is a default value, assert it
	 * assertEquals("Search", inputLabel,
	 * "Input label should match the expected value"); }
	 */
}
