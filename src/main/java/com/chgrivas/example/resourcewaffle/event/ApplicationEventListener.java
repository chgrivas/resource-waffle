package com.chgrivas.example.resourcewaffle.event;

import com.chgrivas.example.resourcewaffle.resource.ResourceSynchronizationService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;

@Component
public class ApplicationEventListener {

  public static final String CLASSPATH_PHOTOS_PATTERN = "classpath:photos/*";

  private final ResourcePatternResolver resourcePatternResolver;
  private final ResourceSynchronizationService resourceSynchronizationService;

  public ApplicationEventListener(
      ResourcePatternResolver resourcePatternResolver,
      ResourceSynchronizationService resourceSynchronizationService) {
    this.resourcePatternResolver = resourcePatternResolver;
    this.resourceSynchronizationService = resourceSynchronizationService;
  }

  @EventListener
  public void onApplicationEvent(ApplicationReadyEvent event) throws IOException {
    Resource[] resources = resourcePatternResolver.getResources(CLASSPATH_PHOTOS_PATTERN);
    resourceSynchronizationService.sync(Arrays.asList(resources));
  }
}
