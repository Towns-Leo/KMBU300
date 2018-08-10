package com.hprtsdksample;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;

import com.hprtsdksample.cpcl.R;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import KMAndroidSDK.KMPrinterHelper;
import KMAndroidSDK.IPort;
import KMAndroidSDK.PublicFunction;

public class Activity_Main extends Activity 
{
	private Context thisCon=null;
	private BluetoothAdapter mBluetoothAdapter;
	private PublicFunction PFun=null;
	private PublicAction PAct=null;
	
	private Button btnWIFI=null;
	private Button btnBT=null;
	private Button btnUSB=null;
	
	private Spinner spnPrinterList=null;
	private TextView txtTips=null;
	private Button btnOpenCashDrawer=null;
	private Button btnSampleReceipt=null;	
	private Button btn1DBarcodes=null;
	private Button btnQRCode=null;
	private Button btnPDF417=null;
	private Button btnCut=null;
	private Button btnPageMode=null;
	private Button btnImageManage=null;
	private Button btnGetRemainingPower=null;
	
	private EditText edtTimes=null;
	
	private ArrayAdapter arrPrinterList; 
	private static KMPrinterHelper HPRTPrinter=new KMPrinterHelper();
	private String ConnectType="";
	private String PrinterName="";
	private String PortParam="";
	
	private UsbManager mUsbManager=null;	
	private UsbDevice device=null;
	private static final String ACTION_USB_PERMISSION = "com.HPRTSDKSample";
	private PendingIntent mPermissionIntent=null;
	private static IPort Printer=null;
	private Handler handler;
	private ProgressDialog dialog;
	public static String paper="0"; 
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		try
		{
			thisCon=this.getApplicationContext();
			
			btnWIFI = (Button) findViewById(R.id.btnWIFI);
			btnUSB = (Button) findViewById(R.id.btnUSB);
			btnBT = (Button) findViewById(R.id.btnBT);
			
			//edtTimes = (EditText) findViewById(R.id.edtTimes);
			
			spnPrinterList = (Spinner) findViewById(R.id.spn_printer_list);	
			txtTips = (TextView) findViewById(R.id.txtTips);
			btnSampleReceipt = (Button) findViewById(R.id.btnSampleReceipt);
			btnOpenCashDrawer = (Button) findViewById(R.id.btnOpenCashDrawer);
			btn1DBarcodes = (Button) findViewById(R.id.btn1DBarcodes);
			btnQRCode = (Button) findViewById(R.id.btnQRCode);
			btnPDF417 = (Button) findViewById(R.id.btnPDF417);
			btnCut = (Button) findViewById(R.id.btnCut);
			btnPageMode = (Button) findViewById(R.id.btnPageMode);
			btnImageManage = (Button) findViewById(R.id.btnImageManage);
			btnGetRemainingPower = (Button) findViewById(R.id.btnGetRemainingPower);
			btnGetStatus = (Button) findViewById(R.id.btnGetStatus);
					
			mPermissionIntent = PendingIntent.getBroadcast(thisCon, 0, new Intent(ACTION_USB_PERMISSION), 0);
	        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
			thisCon.registerReceiver(mUsbReceiver, filter);
			
			PFun=new PublicFunction(thisCon);
			PAct=new PublicAction(thisCon);
			InitSetting();
			InitCombox();
			this.spnPrinterList.setOnItemSelectedListener(new OnItemSelectedPrinter());
			//Enable Bluetooth
			EnableBluetooth();
			handler = new Handler(){
				@Override
				public void handleMessage(Message msg) {
					// TODO Auto-generated method stub
					super.handleMessage(msg);
					if (msg.what==1) {
						Toast.makeText(thisCon, "succeed", 0).show();
						dialog.cancel();
					}else {
						Toast.makeText(thisCon, "failure", 0).show();
						dialog.cancel();
					}
				}
			};
		}
		catch (Exception e) 
		{			
			Log.e("HPRTSDKSample", (new StringBuilder("Activity_Main --> onCreate ")).append(e.getMessage()).toString());
		}
	}
	
	private void InitSetting()
	{
		String SettingValue="";
		SettingValue=PFun.ReadSharedPreferencesData("Codepage");
		if(SettingValue.equals(""))		
			PFun.WriteSharedPreferencesData("Codepage", "0,PC437(USA:Standard Europe)");			
		
		SettingValue=PFun.ReadSharedPreferencesData("Cut");
		if(SettingValue.equals(""))		
			PFun.WriteSharedPreferencesData("Cut", "0");	//0:???,1:????,2:?????
			
		SettingValue=PFun.ReadSharedPreferencesData("Cashdrawer");
		if(SettingValue.equals(""))			
			PFun.WriteSharedPreferencesData("Cashdrawer", "0");
					
		SettingValue=PFun.ReadSharedPreferencesData("Buzzer");
		if(SettingValue.equals(""))			
			PFun.WriteSharedPreferencesData("Buzzer", "0");
					
		SettingValue=PFun.ReadSharedPreferencesData("Feeds");
		if(SettingValue.equals(""))			
			PFun.WriteSharedPreferencesData("Feeds", "0");	
		String paper = PFun.ReadSharedPreferencesData("papertype");
		if (!"".equals(paper)) {
			Activity_Main.paper=paper;
		}
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		String paper = PFun.ReadSharedPreferencesData("papertype");
		if (!"".equals(paper)) {
			Activity_Main.paper=paper;
		}
		String[] arrpaper = getResources().getStringArray(R.array.activity_main_papertype);
		if ("1".equals(Activity_Main.paper)) {
			btnOpenCashDrawer.setText(getResources().getString(R.string.activity_esc_function_btnopencashdrawer)+":"+arrpaper[1]);
		}else {
			btnOpenCashDrawer.setText(getResources().getString(R.string.activity_esc_function_btnopencashdrawer)+":"+arrpaper[0]);
		}
	}
	//add printer list
	private void InitCombox()
	{
		try
		{
			arrPrinterList = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item);
			String strSDKType=thisCon.getString(com.hprtsdksample.cpcl.R.string.sdk_type);
			if(strSDKType.equals("all"))
				arrPrinterList=ArrayAdapter.createFromResource(this, R.array.printer_list_cpcl, android.R.layout.simple_spinner_item);
			if(strSDKType.equals("hprt"))
				arrPrinterList=ArrayAdapter.createFromResource(this, R.array.printer_list_hprt, android.R.layout.simple_spinner_item);
			if(strSDKType.equals("mkt"))
				arrPrinterList=ArrayAdapter.createFromResource(this, R.array.printer_list_mkt, android.R.layout.simple_spinner_item);
			if(strSDKType.equals("mprint"))
				arrPrinterList=ArrayAdapter.createFromResource(this, R.array.printer_list_mprint, android.R.layout.simple_spinner_item);
			if(strSDKType.equals("sycrown"))
				arrPrinterList=ArrayAdapter.createFromResource(this, R.array.printer_list_sycrown, android.R.layout.simple_spinner_item);
			if(strSDKType.equals("mgpos"))
				arrPrinterList=ArrayAdapter.createFromResource(this, R.array.printer_list_mgpos, android.R.layout.simple_spinner_item);
			if(strSDKType.equals("ds"))
				arrPrinterList=ArrayAdapter.createFromResource(this, R.array.printer_list_ds, android.R.layout.simple_spinner_item);
			if(strSDKType.equals("cst"))
				arrPrinterList=ArrayAdapter.createFromResource(this, R.array.printer_list_cst, android.R.layout.simple_spinner_item);
			if(strSDKType.equals("other"))
				arrPrinterList=ArrayAdapter.createFromResource(this, R.array.printer_list_other, android.R.layout.simple_spinner_item);
			arrPrinterList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			PrinterName=arrPrinterList.getItem(0).toString();
			spnPrinterList.setAdapter(arrPrinterList);
		}
		catch (Exception e) 
		{			
			Log.e("HPRTSDKSample", (new StringBuilder("Activity_Main --> InitCombox ")).append(e.getMessage()).toString());
		}
	}
	
	private class OnItemSelectedPrinter implements OnItemSelectedListener
	{				
		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,long arg3) 
		{

			PrinterName=arrPrinterList.getItem(arg2).toString();
			HPRTPrinter=new KMPrinterHelper(thisCon,PrinterName);
			CapturePrinterFunction();
	//		GetPrinterProperty();
		}
		@Override
		public void onNothingSelected(AdapterView<?> arg0) 
		{
			// TODO Auto-generated method stub			
		}
	}
	
	//EnableBluetooth
	private boolean EnableBluetooth()
    {
        boolean bRet = false;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter != null)
        {
            if(mBluetoothAdapter.isEnabled())
                return true;
            mBluetoothAdapter.enable();
            try 
    		{
    			Thread.sleep(500);
    		} 
    		catch (InterruptedException e) 
    		{			
    			e.printStackTrace();
    		}
            if(!mBluetoothAdapter.isEnabled())
            {
                bRet = true;
                Log.d("PRTLIB", "BTO_EnableBluetooth --> Open OK");
            }
        } 
        else
        {
        	Log.d("HPRTSDKSample", (new StringBuilder("Activity_Main --> EnableBluetooth ").append("Bluetooth Adapter is null.")).toString());
        }
        return bRet;
    }
	
	//call back by scan bluetooth printer
	@Override  
  	protected void onActivityResult(int requestCode, int resultCode, final Intent data)  
  	{  
  		try
  		{  		
  			String strIsConnected;
	  		switch(resultCode)
	  		{
	  			case KMPrinterHelper.ACTIVITY_CONNECT_BT:		
	  				String strBTAddress="";
	  				strIsConnected=data.getExtras().getString("is_connected");
	  	        	if (strIsConnected.equals("NO"))
	  	        	{
	  	        		txtTips.setText(thisCon.getString(R.string.activity_main_scan_error));	  	        		
  	                	return;
	  	        	}
	  	        	else
	  	        	{	  	        		
	  						txtTips.setText(thisCon.getString(R.string.activity_main_connected));
	  					return;
	  	        	}		  	        	
	  			case KMPrinterHelper.ACTIVITY_CONNECT_WIFI:		
	  				String strIPAddress="";
	  				String strPort="";
	  				strIsConnected=data.getExtras().getString("is_connected");
	  	        	if (strIsConnected.equals("NO"))
	  	        	{
	  	        		txtTips.setText(thisCon.getString(R.string.activity_main_scan_error));	  	        		
  	                	return;
	  	        	}
	  	        	else
	  	        	{	  	        		
	  	        		strIPAddress=data.getExtras().getString("IPAddress");
	  	        		strPort=data.getExtras().getString("Port");
	  	        		if(strIPAddress==null || !strIPAddress.contains("."))	  					
	  						return;	  						  					
	  	        		HPRTPrinter=new KMPrinterHelper(thisCon,spnPrinterList.getSelectedItem().toString().trim());
	  					if(KMPrinterHelper.PortOpen("WiFi,"+strIPAddress+","+strPort)!=0)	  						  						
	  						txtTips.setText(thisCon.getString(R.string.activity_main_connecterr));	  	                	
	  					else
	  						txtTips.setText(thisCon.getString(R.string.activity_main_connected));
	  					return;
	  	        	}		  	        	
	  			case KMPrinterHelper.ACTIVITY_IMAGE_FILE:	  				
//	  		    	PAct.LanguageEncode();
	  				dialog = new ProgressDialog(Activity_Main.this);
					dialog.setMessage("Printing.....");
					dialog.setProgress(100);
					dialog.show();
		  				new Thread(){
		  					public void run() {
		  						try {
	  				String strImageFile=data.getExtras().getString("FilePath");
	  				Bitmap bmp=BitmapFactory.decodeFile(strImageFile);
	  				int height = bmp.getHeight();
	  				System.err.println("height:"+height);
	  				KMPrinterHelper.printAreaSize("0", "200", "200", ""+height, "1");
	  		    	int a=KMPrinterHelper.Expanded("0","0",strImageFile);
	  		    	if ("1".equals(Activity_Main.paper)) {
	  		    		KMPrinterHelper.Form();
	  				}
	  		    	KMPrinterHelper.Print();
	  		    	if (a>0) {
						handler.sendEmptyMessage(1);
					}else {
						handler.sendEmptyMessage(0);
					}
						}catch (Exception e) {
						handler.sendEmptyMessage(0);
					}
				}
			}.start();
	  				return;
	  			case KMPrinterHelper.ACTIVITY_PRNFILE:	  				
	  				String strPRNFile=data.getExtras().getString("FilePath");
	  				KMPrinterHelper.PrintBinaryFile(strPRNFile);  					  				
	  				
	  				/*String strPRNFile=data.getExtras().getString("FilePath");	  					  				
	  				byte[] bR=new byte[1];
	  				byte[] bW=new byte[3];
	  				bW[0]=0x10;bW[1]=0x04;bW[2]=0x02;
	  				for(int i=0;i<Integer.parseInt(edtTimes.getText().toString());i++)
	  				{
	  					KMPrinterHelper.PrintBinaryFile(strPRNFile);
	  					KMPrinterHelper.DirectIO(bW, null, 0);
	  					KMPrinterHelper.DirectIO(null, bR, 1);	  						
	  				}*/
	  				return;
  			}
  		}
  		catch(Exception e)
  		{
  			Log.e("HPRTSDKSample", (new StringBuilder("Activity_Main --> onActivityResult ")).append(e.getMessage()).toString());
  		}
        super.onActivityResult(requestCode, resultCode, data);  
  	} 
	
	@SuppressLint("NewApi")
	public void onClickConnect(View view) 
	{		
    	if (!checkClick.isClickEvent()) return;
    	
    	try
    	{
	    	if(HPRTPrinter!=null)
			{					
	    		KMPrinterHelper.PortClose();
			}
			
	    	if(view.getId()==R.id.btnBT)
	    	{	
	    		ConnectType="Bluetooth";
				Intent serverIntent = new Intent(thisCon,Activity_DeviceList.class);				
				startActivityForResult(serverIntent, KMPrinterHelper.ACTIVITY_CONNECT_BT);				
				return;
	    	}
	    	else if(view.getId()==R.id.btnWIFI)
	    	{	    		
	    		ConnectType="WiFi";
	    		Intent serverIntent = new Intent(thisCon,Activity_Wifi.class);
				serverIntent.putExtra("PN", PrinterName); 
				startActivityForResult(serverIntent, KMPrinterHelper.ACTIVITY_CONNECT_WIFI);				
				return;	
	    	}
	    	else if(view.getId()==R.id.btnUSB)
	    	{
	    		ConnectType="USB";							
				HPRTPrinter=new KMPrinterHelper(thisCon,arrPrinterList.getItem(spnPrinterList.getSelectedItemPosition()).toString());					
				//USB not need call "iniPort"				
				mUsbManager = (UsbManager) thisCon.getSystemService(Context.USB_SERVICE);
				Toast.makeText(thisCon, ":"+mUsbManager.toString(), 1).show();
		  		HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();  
		  		Toast.makeText(thisCon, "deviceList:"+deviceList.size(), 1).show();
		  		Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
		  		
		  		boolean HavePrinter=false;		  
		  		while(deviceIterator.hasNext())
		  		{
		  		    device = deviceIterator.next();
		  		    int count = device.getInterfaceCount();
		  		  Toast.makeText(thisCon, "count:"+count, 1).show();
		  		    for (int i = 0; i < count; i++) 
		  	        {
		  		    	UsbInterface intf = device.getInterface(i); 
		  		    	Toast.makeText(thisCon, ""+intf.getInterfaceClass(), 1).show();
		  	            if (intf.getInterfaceClass() == 7) 
		  	            {
		  	            	HavePrinter=true;
		  	            	mUsbManager.requestPermission(device, mPermissionIntent);		  	            	
		  	            }
		  	        }
		  		}
		  		if(!HavePrinter)
		  			txtTips.setText(thisCon.getString(R.string.activity_main_connect_usb_printer));	
	    	}
    	}
		catch (Exception e) 
		{			
			Log.e("HPRTSDKSample", (new StringBuilder("Activity_Main --> onClickConnect "+ConnectType)).append(e.getMessage()).toString());
		}
    }
		   			
	private BroadcastReceiver mUsbReceiver = new BroadcastReceiver() 
	{
	    public void onReceive(Context context, Intent intent) 
	    {
	    	try
	    	{
		        String action = intent.getAction();	       
		        //Toast.makeText(thisCon, "now:"+System.currentTimeMillis(), Toast.LENGTH_LONG).show();
		        //KMPrinterHelper.WriteLog("1.txt", "fds");
		        //???????USB?豸???
		        if (ACTION_USB_PERMISSION.equals(action))
		        {
			        synchronized (this) 
			        {		        	
			            device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
				        if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false))
				        {			 
				        	if(KMPrinterHelper.PortOpen(device)!=0)
							{					
				        		HPRTPrinter=null;
								txtTips.setText(thisCon.getString(R.string.activity_main_connecterr));												
			                	return;
							}
				        	else
				        		txtTips.setText(thisCon.getString(R.string.activity_main_connected));
				        		
				        }		
				        else
				        {			        	
				        	return;
				        }
			        }
			    }
		        //???????
		        if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) 
		        {
		            device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
		            if (device != null) 
		            {	                	            	
		            	KMPrinterHelper.PortClose();					
		            }
		        }	    
	    	} 
	    	catch (Exception e) 
	    	{
	    		Log.e("HPRTSDKSample", (new StringBuilder("Activity_Main --> mUsbReceiver ")).append(e.getMessage()).toString());
	    	}
		}
	};
	private Button btnGetStatus;
	
	public void onClickClose(View view) 
	{
    	if (!checkClick.isClickEvent()) return;
    	
    	try
    	{
	    	if(HPRTPrinter!=null)
			{					
	    		KMPrinterHelper.PortClose();
			}
			this.txtTips.setText(R.string.activity_main_tips);
			return;	
    	}
		catch (Exception e) 
		{			
			Log.e("HPRTSDKSample", (new StringBuilder("Activity_Main --> onClickClose ")).append(e.getMessage()).toString());
		}
    }
	
	public void onClickbtnSetting(View view) 
	{
    	if (!checkClick.isClickEvent()) return;
    	
    	try
    	{
    		Intent myIntent = new Intent(this, Activity_Setting.class);
    		startActivityForResult(myIntent, KMPrinterHelper.ACTIVITY_IMAGE_FILE);
        	startActivityFromChild(this, myIntent, 0);	
    	}
		catch (Exception e) 
		{			
			Log.e("HPRTSDKSample", (new StringBuilder("Activity_Main --> onClickClose ")).append(e.getMessage()).toString());
		}
    }
	
	public void onClickDo(View view) 
	{
		if (!checkClick.isClickEvent()) return;
		
		if(!KMPrinterHelper.IsOpened())
		{
			Toast.makeText(thisCon, thisCon.getText(R.string.activity_main_tips), Toast.LENGTH_SHORT).show();				
			return;
		}
		String paper = PFun.ReadSharedPreferencesData("papertype");
		if (!"".equals(paper)) {
			Activity_Main.paper=paper;
		}
		if (view.getId()==R.id.btnOpenCashDrawer) {
			paperAlertDialog();
		}   	    	
    	if(view.getId()==R.id.btnGetStatus)
    	{
    		Intent myIntent = new Intent(this, Activity_Status.class);
        	startActivityFromChild(this, myIntent, 0);
    	}
    	else if(view.getId()==R.id.btnSampleReceipt)
    	{
    		PrintSampleReceipt();
    	}
    	else if(view.getId()==R.id.btn1DBarcodes)
    	{
    		Intent myIntent = new Intent(this, Activity_1DBarcodes.class);    		
        	startActivityFromChild(this, myIntent, 0);
    	}
    	else if(view.getId()==R.id.btnTextFormat)
    	{
    		Intent myIntent = new Intent(this, Activity_TextFormat.class);
        	startActivityFromChild(this, myIntent, 0);
    	}
    	else if(view.getId()==R.id.btnPrintImageFile)
    	{
    		Intent myIntent = new Intent(this, Activity_PRNFile.class); 
        	myIntent.putExtra("Folder", android.os.Environment.getExternalStorageDirectory().getAbsolutePath());
        	myIntent.putExtra("FileFilter", "jpg,gif,png,");
        	startActivityForResult(myIntent, KMPrinterHelper.ACTIVITY_IMAGE_FILE);
    	}
    	else if(view.getId()==R.id.btnPrintPRNFile)
    	{
    		Intent myIntent = new Intent(this, Activity_PRNFile.class);    	
        	myIntent.putExtra("Folder", android.os.Environment.getExternalStorageDirectory().getAbsolutePath());
        	myIntent.putExtra("FileFilter", "prn,");
        	startActivityForResult(myIntent, KMPrinterHelper.ACTIVITY_PRNFILE);
    	}
    	else if(view.getId()==R.id.btnQRCode)
    	{
    		Intent myIntent = new Intent(this, Activity_QRCode.class);
        	startActivityFromChild(this, myIntent, 0);
    	}    	
    	else if(view.getId()==R.id.btnPrintTestPage)
    	{
    		try {
    			KMPrinterHelper.printAreaSize("0", "200", "200", "1400", "1");
    			KMPrinterHelper.Align(KMPrinterHelper.CENTER);
    			KMPrinterHelper.Text(KMPrinterHelper.TEXT, "4", "0", "0", "5", getResources().getString(R.string.activity_test_page));
    			KMPrinterHelper.Align(KMPrinterHelper.LEFT);
    			KMPrinterHelper.Text(KMPrinterHelper.TEXT, "4", "0", "0", "50", "code128");
    			KMPrinterHelper.Barcode(KMPrinterHelper.BARCODE, "128", "2", "1", "50", "0", "80", true, "7", "0", "5", "123456789");
    			KMPrinterHelper.Text(KMPrinterHelper.TEXT, "4", "0", "0", "180", "UPCA");
    			KMPrinterHelper.Barcode(KMPrinterHelper.BARCODE, KMPrinterHelper.UPCA, "2", "1", "50", "0", "210", true, "7", "0", "5", "123456789012");
    			KMPrinterHelper.Text(KMPrinterHelper.TEXT, "4", "0", "0", "310", "UPCE");
    			KMPrinterHelper.Barcode(KMPrinterHelper.BARCODE, KMPrinterHelper.code128, "2", "1", "50", "0", "340", true, "7", "0", "5", "0234565687");
    			KMPrinterHelper.Text(KMPrinterHelper.TEXT, "4", "0", "0", "440", "EAN8");
    			KMPrinterHelper.Barcode(KMPrinterHelper.BARCODE, KMPrinterHelper.EAN8, "2", "1", "50", "0", "470", true, "7", "0", "5", "12345678");
    			KMPrinterHelper.Text(KMPrinterHelper.TEXT, "4", "0", "0", "570", "CODE93");
    			KMPrinterHelper.Barcode(KMPrinterHelper.BARCODE, KMPrinterHelper.code93, "2", "1", "50", "0", "600", true, "7", "0", "5", "123456789");
    			KMPrinterHelper.Text(KMPrinterHelper.TEXT, "4", "0", "0", "700", "CODE39");
    			KMPrinterHelper.Barcode(KMPrinterHelper.BARCODE, KMPrinterHelper.code39, "2", "1", "50", "0", "730", true, "7", "0", "5", "123456789");
    			KMPrinterHelper.Text(KMPrinterHelper.TEXT, "8", "0", "0", "830",getResources().getString(R.string.activity_esc_function_btnqrcode));
    			KMPrinterHelper.PrintQR(KMPrinterHelper.BARCODE, "0", "870", "4", "5", "ABC123");
    			KMPrinterHelper.Text(KMPrinterHelper.TEXT, "8", "0", "0", "1000", getResources().getString(R.string.activity_test_line));
    			KMPrinterHelper.Line("0", "1030", "400", "1030", "1");
    			KMPrinterHelper.Text(KMPrinterHelper.TEXT, "8", "0", "0", "1050", getResources().getString(R.string.activity_test_box));
    			KMPrinterHelper.Box("0", "1080", "400", "1300", "1");
    			if ("1".equals(Activity_Main.paper)) {
    				KMPrinterHelper.Form();
    			}
  		    	KMPrinterHelper.Print();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Log.e("HPRTSDKSample", (new StringBuilder("Activity_Main --> onClickWIFI ")).append(e.getMessage()).toString());
			}
    	}
    }
	
	private void paperAlertDialog(){
		
		final String[] papertype = getResources().getStringArray(R.array.activity_main_papertype);
		Builder builder = new AlertDialog.Builder(Activity_Main.this);
		 builder.setIcon(R.drawable.ic_launcher).setTitle(getResources().getString(R.string.activity_esc_function_btnopencashdrawer))
         .setItems(papertype, new OnClickListener() {

             @Override
             public void onClick(DialogInterface dialog, int which) {
            	 switch (which) {
				case 1:
					try {
						//????
						KMPrinterHelper.papertype_CPCL(1);
						PFun.WriteSharedPreferencesData("papertype", "1");
						btnOpenCashDrawer.setText(getResources().getString(R.string.activity_esc_function_btnopencashdrawer)+":"+papertype[1]);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				case 0:
					try {
						//?????
						KMPrinterHelper.papertype_CPCL(0);
						PFun.WriteSharedPreferencesData("papertype", "0");
						btnOpenCashDrawer.setText(getResources().getString(R.string.activity_esc_function_btnopencashdrawer)+":"+papertype[0]);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				default:
					break;
				}
             }
         });
		 builder.create().show();
	}
	private void CapturePrinterFunction()
	{
		try
		{
			
			int[] propType=new int[1];
			byte[] Value=new byte[500];
			int[] DataLen=new int[1];
			String strValue="";
			boolean isCheck=false;
			if (PrinterName.equals("HM-T300")|PrinterName.equals("HM-A300")|PrinterName.equals("108B")|PrinterName.equals("R42")|PrinterName.equals("106B")) {
				btnCut.setVisibility(View.GONE);		
				btnOpenCashDrawer.setVisibility(View.VISIBLE);		
				btn1DBarcodes.setVisibility(View.VISIBLE);		
				btnQRCode.setVisibility(View.VISIBLE);		
				btnPageMode.setVisibility(View.GONE);		
				btnPDF417.setVisibility(View.GONE);		
				btnGetRemainingPower.setVisibility(View.GONE);		
				btnWIFI.setVisibility(View.VISIBLE);		
//				btnUSB.setVisibility(View.VISIBLE);		
				btnBT.setVisibility(View.VISIBLE);	
				btnSampleReceipt.setVisibility(View.VISIBLE);	
				btnGetStatus.setVisibility(View.VISIBLE);
			}/*else {
				int iRtn=KMPrinterHelper.CapturePrinterFunction(KMPrinterHelper.HPRT_MODEL_PROPERTY_KEY_CUT, propType, Value,DataLen);
				if(iRtn!=0)
					return;			
				PrinterProperty.Cut=(Value[0]==0?false:true);
				btnCut.setVisibility((PrinterProperty.Cut?View.VISIBLE:View.GONE));
				
				iRtn=KMPrinterHelper.CapturePrinterFunction(KMPrinterHelper.HPRT_MODEL_PROPERTY_KEY_DRAWER, propType, Value,DataLen);
				if(iRtn!=0)
					return;		
				PrinterProperty.Cashdrawer=(Value[0]==0?false:true);
				btnOpenCashDrawer.setVisibility((PrinterProperty.Cashdrawer?View.VISIBLE:View.GONE));
				
				iRtn=KMPrinterHelper.CapturePrinterFunction(KMPrinterHelper.HPRT_MODEL_PROPERTY_KEY_BARCODE, propType, Value,DataLen);
				if(iRtn!=0)
					return;						
				PrinterProperty.Barcode=new String(Value);
				isCheck=PrinterProperty.Barcode.replace("QRCODE", "").replace("PDF417", "").replace(",,", ",").replace(",,", ",").length()>0;
				btn1DBarcodes.setVisibility((isCheck?View.VISIBLE:View.GONE));								
				isCheck = PrinterProperty.Barcode.contains("QRCODE");
				btnQRCode.setVisibility((isCheck?View.VISIBLE:View.GONE));
				btnPDF417.setVisibility((PrinterProperty.Barcode.indexOf("PDF417") != -1?View.VISIBLE:View.GONE));
				
				iRtn=KMPrinterHelper.CapturePrinterFunction(KMPrinterHelper.HPRT_MODEL_PROPERTY_KEY_PAGEMODE, propType, Value,DataLen);
				if(iRtn!=0)
					return;		
				PrinterProperty.Pagemode=(Value[0]==0?false:true);
				btnPageMode.setVisibility((PrinterProperty.Pagemode?View.VISIBLE:View.GONE));
				
				iRtn=KMPrinterHelper.CapturePrinterFunction(KMPrinterHelper.HPRT_MODEL_PROPERTY_KEY_GET_REMAINING_POWER, propType, Value,DataLen);
				if(iRtn!=0)
					return;	
				PrinterProperty.GetRemainingPower=(Value[0]==0?false:true);
				btnGetRemainingPower.setVisibility((PrinterProperty.GetRemainingPower?View.VISIBLE:View.GONE));
				
				iRtn=KMPrinterHelper.CapturePrinterFunction(KMPrinterHelper.HPRT_MODEL_PROPERTY_CONNECT_TYPE, propType, Value,DataLen);
				if(iRtn!=0)
					return;	
				PrinterProperty.ConnectType=(Value[1]<<8)+Value[0];
				btnWIFI.setVisibility(((PrinterProperty.ConnectType&1)==0?View.GONE:View.VISIBLE));
//			btnUSB.setVisibility(((PrinterProperty.ConnectType&16)==0?View.GONE:View.VISIBLE));
				btnBT.setVisibility(((PrinterProperty.ConnectType&32)==0?View.GONE:View.VISIBLE));
				iRtn=KMPrinterHelper.CapturePrinterFunction(KMPrinterHelper.HPRT_MODEL_PROPERTY_KEY_PRINT_RECEIPT, propType, Value,DataLen);
				if(iRtn!=0)
					return;			
				PrinterProperty.SampleReceipt=(Value[0]==0?false:true);
				btnSampleReceipt.setVisibility((PrinterProperty.SampleReceipt?View.VISIBLE:View.GONE));							
				
			}*/
		}
		catch(Exception e)
		{
			Log.e("HPRTSDKSample", (new StringBuilder("Activity_Main --> CapturePrinterFunction ")).append(e.getMessage()).toString());
		}
	}
	
/*	private void GetPrinterProperty()
	{
		try
		{
			int[] propType=new int[1];
			byte[] Value=new byte[500];
			int[] DataLen=new int[1];
			String strValue="";			
			int iRtn=0;
			
			iRtn=KMPrinterHelper.CapturePrinterFunction(KMPrinterHelper.HPRT_MODEL_PROPERTY_KEY_STATUS_MODEL, propType, Value,DataLen);
			if(iRtn!=0)
				return;			
			PrinterProperty.StatusMode=Value[0];
			
			if(PrinterProperty.Cut)
			{
				iRtn=KMPrinterHelper.CapturePrinterFunction(KMPrinterHelper.HPRT_MODEL_PROPERTY_KEY_CUT_SPACING, propType, Value,DataLen);
				if(iRtn!=0)
					return;			
				PrinterProperty.CutSpacing=Value[0];				
			}
			else
			{
				iRtn=KMPrinterHelper.CapturePrinterFunction(KMPrinterHelper.HPRT_MODEL_PROPERTY_KEY_TEAR_SPACING, propType, Value,DataLen);
				if(iRtn!=0)
					return;		
				PrinterProperty.TearSpacing=Value[0];				
			}	
			
			if(PrinterProperty.Pagemode)
			{
				iRtn=KMPrinterHelper.CapturePrinterFunction(KMPrinterHelper.HPRT_MODEL_PROPERTY_KEY_PAGEMODE_AREA, propType, Value,DataLen);
				if(iRtn!=0)
					return;			
				PrinterProperty.PagemodeArea=new String(Value).trim();				
			}
			Value=new byte[500];
			iRtn=KMPrinterHelper.CapturePrinterFunction(KMPrinterHelper.HPRT_MODEL_PROPERTY_KEY_WIDTH, propType, Value,DataLen);
			if(iRtn!=0)
				return;			
			PrinterProperty.PrintableWidth=(int)(Value[0] & 0xFF | ((Value[1] & 0xFF) <<8));
		}
		catch(Exception e)
		{
			Log.e("HPRTSDKSample", (new StringBuilder("Activity_Main --> CapturePrinterFunction ")).append(e.getMessage()).toString());
		}*/
//	}
	
	private void PrintSampleReceipt()
	{
		try
		{
//			KMPrinterHelper.printAreaSize("0", "200", "200", "400", "1");
//			KMPrinterHelper.AutLine("10", "10", 200, 8, "??????????????????????????????????????????????????????????????????????????????");
		    KMPrinterHelper.printAreaSize("0", "200","200","700","1");
			String[] ReceiptLines = getResources().getStringArray(R.array.activity_main_sample_2inch_receipt);
			KMPrinterHelper.LanguageEncode="GBK";
			KMPrinterHelper.Align(KMPrinterHelper.CENTER);
			for(int i=0;i<ReceiptLines.length;i++){
				KMPrinterHelper.Text(KMPrinterHelper.TEXT, "8", "0", "0", ""+(i*30), ReceiptLines[i]);
			}
			if ("1".equals(Activity_Main.paper)) {
				KMPrinterHelper.Form();
			}
			KMPrinterHelper.Print();
		}
		catch(Exception e)
		{
			Log.e("HPRTSDKSample", (new StringBuilder("Activity_Main --> PrintSampleReceipt ")).append(e.getMessage()).toString());
		}
	}
	
	/*public static class PrinterProperty
	{
		public static String Barcode="";
		public static boolean Cut=false;
		public static int CutSpacing=0;
		public static int TearSpacing=0;
		public static int ConnectType=0;
		public static boolean Cashdrawer=false;
		public static boolean Buzzer=false;
		public static boolean Pagemode=false;
		public static String PagemodeArea="";
		public static boolean GetRemainingPower=false;
		public static boolean SampleReceipt=true;
		public static int StatusMode=0;
	}*/
}
