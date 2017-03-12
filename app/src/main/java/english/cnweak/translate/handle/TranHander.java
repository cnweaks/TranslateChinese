package english.cnweak.translate.handle;
import english.cnweak.translate.util.TranslateUtils;

//子线程
public class TranHander extends Thread{
    String TRAN_TEXT , TRAN_TEXTED;
    String translation;
    public void setTrantion(String s){
      this.translation = s;  
        
    }
    public void setCong(String T , String D){
        this.TRAN_TEXT = T;
        this.TRAN_TEXTED = D;
    }
        public void run(){
                try{
                        TRAN_TEXTED =  new TranslateUtils().BingTranslate(TRAN_TEXT);
                    }catch (Exception e){}
            }
    }     
