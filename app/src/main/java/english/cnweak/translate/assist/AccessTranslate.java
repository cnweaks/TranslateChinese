package english.cnweak.translate.assist;
import android.accessibilityservice.AccessibilityService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.text.format.Time;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import english.cnweak.translate.util.AccessServiceUtil;
import english.cnweak.translate.util.IAccessServiceImpl;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import android.widget.Toast;
import english.cnweak.translate.util.TranslateUtils;
import java.util.logging.LogRecord;
import android.os.Message;
import android.os.Handler;
import android.os.Bundle;
import android.os.Looper;

public class AccessTranslate implements IAccessServiceImpl{

        @Override
        public void onInterrupt(){
                // Toast.makeText(services, "翻译功能已接入", Toast.LENGTH_LONG).show();                
            }
        @Override
        public boolean isCompleted(){

                return false;
            }

        public static boolean
        isok = false,//
        istimer = true,//计时器状态
        trans = false,//是否执行翻译
        tranok = true,//
        istle = false;//

        public static TranslateHander tranht;
        MsgHander  msgh ;
        public static int ci = 0;
        AccessibilityService services;
        //定时器推送消息
        private static String  TRAN_TEXT = "";
        public static Timer timer;
        public static  List<IAccessServiceImpl> impls = new ArrayList<IAccessServiceImpl>();
        public static AccessServiceUtil util = new AccessServiceUtil(); 
        public void Acest(AccessibilityEvent event , AccessibilityService service){
                if (istimer){
                        TimerTo();
                        istimer = false;
                    }
                String eventType = "";
                switch (event.getEventType()){
                        case event.TYPE_VIEW_TEXT_SELECTION_CHANGED:
                            isok = true;
                            eventType = "选择";
                            //StratTran(service);
                            break;
                        case event.TYPE_VIEW_LONG_CLICKED:
                            eventType = "长按";
                            StratTran(service);
                            break;
                        default:
                            eventType = "";
                            break;
                    } 
                if (eventType != ""){
                    }
                for (IAccessServiceImpl impl : impls){
                        impl.Acest(event, service);
                    }
                Iterator<IAccessServiceImpl> iterator = impls.iterator();
                while (iterator.hasNext()){
                        if (iterator.next().isCompleted()){
                                iterator.remove();
                            }
                    }
            }
        public static void addImpl(IAccessServiceImpl serviceImpl){
                impls.add(serviceImpl);
            }
        //剪切板内容设置
        public void SetClipData(String text , AccessibilityService services){
                ClipboardManager clipboard = (ClipboardManager)
                    services.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("message", text);
                clipboard.setPrimaryClip(clip);

            }



        /** 返回主界面事件*/
        public static void performHome(AccessibilityService service){
                if (service == null){
                        return;
                    }
                service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME);
            }



        /*
         状态更新器
         */
        public void TimerTo(){
                timer  = new Timer(true);
                istimer = true;
                timer.schedule(
                    new java.util.TimerTask(){               
                            public void run(){
                                    trans = true;                            
                                }
                        }, 0, 1 * 5 * 1000);
            }



        //翻译执行体
        public void StratTran(AccessibilityService service){

                //取得剪贴板内容
                ClipboardManager clipboard = (ClipboardManager)
                    service.getSystemService(Context.CLIPBOARD_SERVICE);  
                //如果剪切板内容为空，则直接了当
                if (clipboard.equals(null)){
                        return;
                    }
                CharSequence clitxt = clipboard.getText();
                //剪切板有内容，但长度小于5个字符，概率估计值，过滤非句式内容翻译，直接了当
                if (clitxt.length() < 3){
                        return;
                    }

                TRAN_TEXT = clitxt + "";     
                String ed =  TRAN_TEXT.substring(0, 3);                 
                Toast.makeText(service, "剪切板内容:" + ed + "…", Toast.LENGTH_LONG).show();                  
                TRAN_TEXT = TRAN_TEXT.replaceAll("#","");
                TRAN_TEXT = TRAN_TEXT.replaceAll("\n","");
                TRAN_TEXT = TRAN_TEXT.replaceAll("//","");

                if (trans){          
                        msgh = new MsgHander();
                        this.services = service;
                        tranht = new TranslateHander();
                        new Thread(tranht).start();
                        trans = false;
                    }


            }

        /*
         打开辅助工具
         */
        public void OpenAccess(int APPType , AccessibilityService server){
                Intent accessibleIntent = 
                    new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);       
                accessibleIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                server.getApplicationContext().startActivity(accessibleIntent);
            }







        /** 
         消息接收线程:
         接受子线程处理后的消息头
         根据消息头，处理线程数据
         . */
        class MsgHander extends Handler{
                public MsgHander(){}
                public MsgHander(Looper L){super(L);}
                @Override
                public void handleMessage(Message msg){
                        super.handleMessage(msg);
                        Bundle b = msg.getData();
                        switch (msg.what){
                                case 0:
                                    String results =  b.getString("result");
                                    SetClipData(" "+results , services); 
                                    break;
                                case 1:
                                    String result =  b.getString("result");
                                    String newclied = "译文:" + result + "\n\n英文:" + TRAN_TEXT;
                                    SetClipData(newclied , services);
                                    break;
                                default:
                                    break;
                            }

                    }

            };



        /*
         翻译线程
         负责翻译英文，不提供对数据的判断
         */
        class TranslateHander extends Thread{   
                public void run(){
                        try{
                                //如果翻译成功，则在内容不变的情况
                                String traned =  new TranslateUtils().BingTranslate(TRAN_TEXT);
                                Message msg = new Message();
                                Bundle bu = new Bundle();
                                bu.putString("result" , traned);
                                msg.what = 0;
                                msg.setData(bu);
                                msgh.sendMessage(msg);
                            }catch (Exception e){}
                    }


            }                    

        //循环的方法recycle():
        //回收体
        public void recycle(AccessibilityNodeInfo info){
                if (info.getChildCount() != 0){
                        for (int i = 0; i < info.getChildCount(); i++){
                                if (info.getChild(i) != null){
                                        recycle(info.getChild(i));
                                    }
                            }
                    }
            }        



        private int isBig(String s){
            int BIG = 1;//大写
            int SML = 2;//小写
            int OTH = 3;//其他字符
                for (int i= 0;i < s.length();i++){
                        if (s.charAt(i) >= 'A' && s.charAt(i) <= 'Z'){
                                return BIG;                
                            }else if (s.charAt(i) >= 'a' && s.charAt(i) <= 'z'){
                                return SML;                                
                            }                          
                    }
                    return OTH;
            }

        private boolean isEnglish(String s){
            String[] azb = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
            String[] azs = {"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"};
            String[] eng = {",",".","'","?","!"};
            for(int i=0;i<azb.length;i++){
            //  if( azb[i] == s.charAt(i)){
                  
           //   }
                
            }
                return false;
            }
    }
