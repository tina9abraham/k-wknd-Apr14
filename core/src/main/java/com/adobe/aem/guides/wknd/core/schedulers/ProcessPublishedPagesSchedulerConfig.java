package com.adobe.aem.guides.wknd.core.schedulers;

import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.osgi.service.metatype.annotations.AttributeDefinition;

@ObjectClassDefinition(name = "Process Published Pages Scheduler Configuration")
public @interface ProcessPublishedPagesSchedulerConfig {

    @AttributeDefinition(name = "CRON Expression", description = "CRON schedule for this job")
    String scheduler_expression() default "0 0/2 * * * ?";
}