package english.cnweak.translate.util;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;

//服务处理接口accessibility_service_config
public interface IAccessServiceImpl {
  void Acest(AccessibilityEvent event, AccessibilityService service);

  void onInterrupt();

  boolean isCompleted();
}
