package com.didan.archetype.config.scheduling;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.context.scope.refresh.RefreshScopeRefreshedEvent;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass({RefreshScopeRefreshedEvent.class}) // Khi RefreshScopeRefreshedEvent có mặt trong classpath thì mới kích hoạt
@RequiredArgsConstructor
public class RefreshScopeListener {
  private final List<RefreshScheduler> refreshSchedulers; // Danh sách các RefreshScheduler

  public void onApplicationEvent(RefreshScopeRefreshedEvent event) {
    // Khi có sự kiện RefreshScopeRefreshedEvent xảy ra, gọi phương thức materializeAfterRefresh() của từng RefreshScheduler
    refreshSchedulers.forEach(RefreshScheduler::materializeAfterRefresh);
  }
}

/*
Class RefreshScopeListener lắng nghe sự kiện RefreshScopeRefreshedEvent và gọi phương thức materializeAfterRefresh() của từng RefreshScheduler trong danh sách refreshSchedulers.
Điều này cho phép các RefreshScheduler thực hiện các hành động cần thiết sau khi có sự kiện làm mới xảy ra.
Ví dụ, nếu bạn có một RefreshScheduler để làm mới một số tác vụ định kỳ hoặc cập nhật cấu hình, bạn có thể thêm nó vào danh sách refreshSchedulers và nó sẽ tự động được gọi khi có sự kiện làm mới xảy ra.
Tóm lại, RefreshScopeListener là một lớp cấu hình trong Spring Boot giúp lắng nghe sự kiện làm mới và gọi các phương thức tương ứng của các RefreshScheduler đã được định nghĩa.

VD:
```java
@Scheduled(fixedRate = 5000)
public void refresh() {
    // Code để làm mới dữ liệu hoặc thực hiện tác vụ định kỳ
    // ...
    // Gọi sự kiện RefreshScopeRefreshedEvent
    applicationEventPublisher.publishEvent(new RefreshScopeRefreshedEvent(this));
    // ...
    // Gọi phương thức materializeAfterRefresh() của từng RefreshScheduler
    refreshSchedulers.forEach(RefreshScheduler::materializeAfterRefresh);
}
*/
