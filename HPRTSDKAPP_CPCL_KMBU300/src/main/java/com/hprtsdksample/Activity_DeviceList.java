package com.hprtsdksample;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.hprtsdksample.cpcl.R;

import KMAndroidSDK.KMPrinterHelper;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class Activity_DeviceList extends Activity {

	public static final String TAG = "DeviceListActivity";
    public static final boolean D = true;
    public static String EXTRA_DEVICE_ADDRESS = "device_address";
    public BluetoothAdapter mBtAdapter;
    private BluetoothDevice mmDevice;
    private BluetoothSocket mmSocket;
    private InputStream mmInStream;
	private OutputStream mmOutStream;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    
    public List<String> pairedDeviceList=null;
    public List<String> newDeviceList=null;
    public ArrayAdapter<String> mPairedDevicesArrayAdapter;
    public ArrayAdapter<String> mNewDevicesArrayAdapter;
    public static String toothAddress=null;
    public static String toothName=null;
    private Context thisCon=null;
    private String strAddressList="";
    private Thread thread;
    Handler handler=new Handler(){
    	public void handleMessage(Message msg) {
    			progress.setVisibility(View.GONE);
				Intent intent = new Intent();                
	            intent.putExtra("is_connected", ((msg.what==0)?"OK":"NO")); 
	            intent.putExtra("BTAddress", toothAddress); 
	            setResult(KMPrinterHelper.ACTIVITY_CONNECT_BT, intent);  
	            finish(); 
    	};
    };
	private Message message;
    
	
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_devicelist); 
        setResult(Activity.RESULT_CANCELED);        
        progress = (ProgressBar) findViewById(R.id.progress);
        Button scanButton = (Button) findViewById(R.id.button_scan);
        scanButton.setOnClickListener(new OnClickListener() {
           public void onClick(View v) {
        	   strAddressList="";
                doDiscovery();
                v.setVisibility(View.GONE);
           }
        });
        thisCon=this.getApplicationContext();
        
        mPairedDevicesArrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,getPairedData());
        mNewDevicesArrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
        ListView pairedListView = (ListView) findViewById(R.id.paired_devices);
        ListView newDevicesListView = (ListView) findViewById(R.id.new_devices);
        pairedListView.setAdapter(mPairedDevicesArrayAdapter);
        newDevicesListView.setAdapter(mNewDevicesArrayAdapter);
        String ACTION_PAIRING_REQUEST = "android.bluetooth.device.action.PAIRING_REQUEST"; 
        IntentFilter intent = new IntentFilter(); 
        intent.addAction(BluetoothDevice.ACTION_FOUND);
        intent.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED); 
        intent.addAction(ACTION_PAIRING_REQUEST);
        intent.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED); 
        intent.addAction(BluetoothAdapter.ACTION_STATE_CHANGED); 
        intent.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver, intent); 
        try
        {
	        pairedListView.setOnItemClickListener(mDeviceClickListener);
	        newDevicesListView.setOnItemClickListener(mDeviceClickListener);
        }catch(Exception excpt)
        {
  	       Toast.makeText(this, thisCon.getString(R.string.activity_devicelist_get_device_err)+excpt,Toast.LENGTH_LONG).show();
        }
    }
    //ȡ���Ѿ���Ե�������Ϣ,�������ص�ListView��ȥ
    public List<String> getPairedData()
    {
        List<String> data = new ArrayList<String>();
        //Ĭ�ϵ�����������
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        // �õ���ǰ��һ���Ѿ���Ե������豸
        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
        ListView pairedListView = (ListView) findViewById(R.id.paired_devices);
        ListView newDevicesListView = (ListView) findViewById(R.id.new_devices);
		if (pairedDevices.size() > 0) 
		{
		    findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);        
		    for (BluetoothDevice device : pairedDevices) //����
		    {
		       data.add(device.getName() + "\n" + device.getAddress());
		    }
		} 
		else
		  {
		      String noDevices = getResources().getText(R.string.activity_devicelist_none_paired).toString();
		      data.add(noDevices);       
		  }
        return data;
    }
    
    @Override
	protected void onDestroy() 
    {
        super.onDestroy();
        // ȷ���Ƿ���Ҫ��ɨ��
        if (mBtAdapter != null)        
           mBtAdapter.cancelDiscovery();   
        if (thread!=null) {
    		Thread dummy = thread;
    		thread = null;
			dummy.interrupt();
		}
	}

    /**
     * ����װ�÷��ֵ�BluetoothAdapter
	*/
    public void doDiscovery() 
    {
        if (D) Log.d(TAG, "doDiscovery()");
        // �ڱ�����ע��ɨ��
        setProgressBarIndeterminateVisibility(true);
        setTitle(R.string.activity_devicelist_scanning);
        // ���ӱ�������豸
        findViewById(R.id.title_new_devices).setVisibility(View.VISIBLE);
        // ��������ɨ�裬�ر�ɨ��
        if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }
        //ɨ��        
        int intStartCount=0;
        while (!mBtAdapter.startDiscovery() && intStartCount<5)
        {
            Log.e("BlueTooth", "ɨ�賢��ʧ��");
            intStartCount++;
            try 
            {
                Thread.sleep(100);
            } 
            catch (InterruptedException e) 
            {
                e.printStackTrace();
            }
        }
    }

    // ���б���е������豸���������¼�
    public OnItemClickListener mDeviceClickListener = new OnItemClickListener() 
    {
       

		public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) 
        {
        	boolean hasConnected=false;
        	progress.setVisibility(View.VISIBLE);
        	try 
        	{ 
	        	if(mBtAdapter.isDiscovering())
	        	{
	        		mBtAdapter.cancelDiscovery();    
	        	}
	        	
	            //ȡ������mvc��ַ
	            String info = ((TextView) v).getText().toString();
	            toothAddress = info.substring(info.length() - 17);
	            if(!toothAddress.contains(":"))
	            { 
	            	return;
	            }
	           thread = new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						try {
							int portOpen = KMPrinterHelper.PortOpen("Bluetooth,"+toothAddress);
							System.out.println("IsOpened:"+KMPrinterHelper.IsOpened());
							message = new Message();
							message.what=portOpen;
							handler.sendMessage(message);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
	           thread.start();
//	            hasConnected= ConnectDevice();
//	            if (hasConnected)
//	            {	            	
//	            	DisConnect();
//	            }
	            
            }
            catch (Exception e)
            {  
            	progress.setVisibility(View.GONE);
            	 e.printStackTrace();
            } 
//        	finally
//        	{ 
//        		finish();
//        	}          	
        }
    };
    // ɨ�����ʱ�򣬸ı䰴ťtext
    public final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override 
        public void onReceive(Context context, Intent intent) {
        	
            String action = intent.getAction(); 
            BluetoothDevice device = null; 
            // �����豸ʱ��ȡ���豸��MAC��ַ 
            if (BluetoothDevice.ACTION_FOUND.equals(action)) { 
                device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE); 
                if (device.getBondState() == BluetoothDevice.BOND_NONE) 
                { 
                	if(device.getBluetoothClass().getMajorDeviceClass()==1536)
                	{
                		if(!strAddressList.contains(device.getAddress()))
                		{
                			Bundle b = intent.getExtras();    
                			String object =  String.valueOf(b.get("android.bluetooth.device.extra.RSSI"));
                			int valueOf = Integer.valueOf(object);
                			float power=(float) ((Math.abs(valueOf)-59)/(10*2.0));
                			float pow=(float) Math.pow(10,power);
	                		strAddressList+=device.getAddress()+",";
	                		mNewDevicesArrayAdapter.add(device.getName()+"  "+pow+"m" + "\n" + device.getAddress());
                		}
                	}
                } 
            }else if(BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)){ 
                device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE); 
                switch (device.getBondState()) { 
                case BluetoothDevice.BOND_BONDING: 
                    Log.d("BlueToothTestActivity", "�������......"); 
                    break; 
                case BluetoothDevice.BOND_BONDED: 
                    Log.d("BlueToothTestActivity", "������");                     
                    break; 
                case BluetoothDevice.BOND_NONE: 
                    Log.d("BlueToothTestActivity", "ȡ�����"); 
                default: 
                    break; 
                } 
            } 
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) 
            { 
	            setProgressBarIndeterminateVisibility(false);
	            setTitle(R.string.activity_devicelist_select_device);
	            if (mNewDevicesArrayAdapter.getCount() == 0) { }
            }           
        }
    };
	private ProgressBar progress;

    @Override
    protected void onStop() {
    	// TODO Auto-generated method stub
    	super.onStop();
    	if (thread!=null) {
    		Thread dummy = thread;
    		thread = null;
			dummy.interrupt();
		}
    }
}


