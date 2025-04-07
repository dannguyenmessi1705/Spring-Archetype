package com.didan.archetype.config.scheduling;

public interface RefreshScheduler {
  default void materializeAfterRefresh() {
    // Default implementation does nothing
  }
}
