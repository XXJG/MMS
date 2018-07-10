package Winmms.ACT;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;
 
public class SmsReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent) 
    {
        Bundle bundle = intent.getExtras();        
        SmsMessage[] msgs = null;
        String str = "";            
        if (bundle != null)
        {
            //Ω” ’∂Ã–≈
            Object[] pdus = (Object[]) bundle.get("pdus");
            msgs = new SmsMessage[pdus.length];            
            for (int i=0; i<msgs.length; i++)
            {
                msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);                
                str += "SMS from:" + msgs[i].getOriginatingAddress();                     
                str += "\n";
                str += msgs[i].getMessageBody().toString();
                str += "\n";        
            }
            //---display the new SMS message---
            Toast.makeText(context, str + ">" + str.length(), Toast.LENGTH_SHORT).show();
        }                         
    }
}

