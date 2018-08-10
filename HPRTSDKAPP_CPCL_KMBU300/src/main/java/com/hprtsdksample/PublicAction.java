package com.hprtsdksample;

import java.util.HashMap;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import KMAndroidSDK.KMPrinterHelper;
import KMAndroidSDK.PublicFunction;

public class PublicAction
{	
	private Context context=null;	
	public PublicAction()
	{
		
	}
	//*�򿪻�ر�WiFi���봫�����ҳ���Context
	public PublicAction(Context con)
	{
		context = con;
	}
//	public String LanguageEncode()
//	{
//		try
//		{
//			PublicFunction PFun=new PublicFunction(context);
//			String sLanguage=PFun.ReadSharedPreferencesData("Codepage").split(",")[1].toString();
//			String sLEncode="gb2312";
//			int intLanguageNum=0;
//			
//			sLEncode=PFun.getLanguageEncode(sLanguage);		
//			intLanguageNum= PFun.getCodePageIndex(sLanguage);	
//			
//			KMPrinterHelper.SetCharacterSet((byte)intLanguageNum);
//			KMPrinterHelper.LanguageEncode=sLEncode;
//		
//			return sLEncode;
//		}
//		catch(Exception e)
//		{			
//			Log.e("HPRTSDKSample", (new StringBuilder("PublicAction --> AfterPrintAction ")).append(e.getMessage()).toString());
//			return "";
//		}
//	}
}