package english.cnweak.translate;
import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.KeyguardManager;
import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.PowerManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;
import english.cnweak.translate.assist.AccessTranslate;
import english.cnweak.translate.util.AccessServiceUtil;
import english.cnweak.translate.util.IAccessServiceImpl;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import android.os.Handler;
import android.os.Message;
import english.cnweak.translate.util.TranslateUtils;
/*
 * Created by Ecvit Cnweaks@ecvit.com
 */
public class AccessService extends AccessibilityService{
        //打开系统无障碍设置界面
        /*定时器部
         修饰词         类内部     本包     子类     外部包 
         public          √        √        √        √
         protected       √        √        √        ×
         default         √        √        ×        ×
         private     	 √	      ×        ×	    ×
         */
        
        public static boolean tran = false;
        public static  final String TAG = "Translate";
        public AccessTranslate translate;
        public static  List<IAccessServiceImpl> impls = new ArrayList<IAccessServiceImpl>(); 
        
        protected void onServiceConnected(){
                Log.i(TAG, "服务接入成功");
                AccessibilityServiceInfo info = getServiceInfo();
                info.notificationTimeout = 600;     
             
                setServiceInfo(info);
              
               String toaststr = "[" , addstr = "]辅助服务已连接";
                tran = true;         
                if (tran){
                    translate = new AccessTranslate();
                        addstr=" 英译汉"+addstr;
                    }
                Toast.makeText(this, toaststr+addstr, Toast.LENGTH_LONG).show();          
                }


        //绑定线程
        public boolean OnBind(Intent intent){
                return true;
            }


        public void onAccessibilityEvent(AccessibilityEvent event){
            translate.Acest(event , this);
            }
            
            
            //中断时调用
        public void onInterrupt(){
                Toast.makeText(this, "翻译服务失去连接", Toast.LENGTH_LONG).show();
                //如果计时器处在运行状态，则停止
                translate.timer.cancel();
            }


        @Override
        public void onDestroy(){
                super.onDestroy();
                Toast.makeText(this, "翻译服务失去连接", Toast.LENGTH_LONG).show();
            }
            
            
            
           
       
    }
  
