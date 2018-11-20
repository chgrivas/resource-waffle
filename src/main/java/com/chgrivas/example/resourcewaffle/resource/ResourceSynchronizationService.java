package com.chgrivas.example.resourcewaffle.resource;

import org.springframework.core.io.Resource;

import java.util.List;

public interface ResourceSynchronizationService {
  void sync(List<Resource> resources);
}
