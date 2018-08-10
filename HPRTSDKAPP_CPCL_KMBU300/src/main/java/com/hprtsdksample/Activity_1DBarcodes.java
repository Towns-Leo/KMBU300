package com.hprtsdksample;

import com.hprtsdksample.cpcl.R;

import KMAndroidSDK.KMPrinterHelper;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class Activity_1DBarcodes  extends Activity 
{	
	private Context thisCon=null;
	private Spinner spnBarcodeType=null;
	private ArrayAdapter arrBarcodeType;
	private Spinner spnBarcodeWidth=null;
	private ArrayAdapter arrBarcodeWidth;
	private Spinner spnBarcode_readable=null;
	private Spinner spnBarcode_rotation=null;
	private Spinner spnBarcode_narrow=null;
	private ArrayAdapter arrBarcodeHRILayout;
	private ArrayAdapter arrBarcoderotation;
	private ArrayAdapter arrBarcodenarrow;
	private EditText txtBarcodeData=null;
	private EditText txtBarcodeHeight=null;
	private EditText txtBarcode_x=null;
	private EditText txtBarcode_y=null;
	
	private int justification=0;
	private int BarcodeType=0;
	private int BarcodeWidth=2;
	private boolean Barcodereadable=true;
	private String Barcoderotation="";
	private int Barcodenarrow=0;
	private String Barcodetype;
	private PublicAction pAct;
		
	@SuppressWarnings("unchecked")
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);	   
		this.requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_1dbarcodes);			
		thisCon=this.getApplicationContext();
		pAct = new PublicAction(thisCon);	
		spnBarcodeType = (Spinner) findViewById(R.id.spnBarcodeType);			
		//arrBarcodeType = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item);
		String[] barcode=new String[]{"UPC-A","UPC-E","EAN","Code39","Code93","Code128","Codabar","MSI","Postnet","FIM"};
		arrBarcodeType = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,barcode);
		arrBarcodeType=ArrayAdapter.createFromResource(this, R.array.activity_1dbarcodes_barcode_type, android.R.layout.simple_spinner_item);
		arrBarcodeType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);		
		spnBarcodeType.setAdapter(arrBarcodeType);
		spnBarcodeType.setOnItemSelectedListener(new OnItemSelectedBarcodeType());
		
		spnBarcodeWidth = (Spinner) findViewById(R.id.spnBarcodeWidth);			
		arrBarcodeWidth = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item);				
		arrBarcodeWidth=ArrayAdapter.createFromResource(this, R.array.activity_1dbarcodes_width, android.R.layout.simple_spinner_item);
		arrBarcodeWidth.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);		
		spnBarcodeWidth.setAdapter(arrBarcodeWidth);
		spnBarcodeWidth.setOnItemSelectedListener(new OnItemSelectedBarcodeWidth());
		
		spnBarcode_readable = (Spinner) findViewById(R.id.spnBarcode_readable);			
		arrBarcodeHRILayout = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item);				
		arrBarcodeHRILayout=ArrayAdapter.createFromResource(this, R.array.activity_1dbarcodes_hri_position, android.R.layout.simple_spinner_item);
		arrBarcodeHRILayout.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);		
		spnBarcode_readable.setAdapter(arrBarcodeHRILayout);	
		spnBarcode_readable.setOnItemSelectedListener(new OnItemSelectedBarcodeHRILayout());
		
		spnBarcode_rotation = (Spinner) findViewById(R.id.spnBarcode_rotation);			
		arrBarcoderotation = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item);				
		arrBarcoderotation=ArrayAdapter.createFromResource(this, R.array.activity_1dbarcodes_rotation, android.R.layout.simple_spinner_item);
		arrBarcoderotation.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);		
		spnBarcode_rotation.setAdapter(arrBarcoderotation);	
		spnBarcode_rotation.setOnItemSelectedListener(new OnItemSelectedBarcoderotation());
		
		spnBarcode_narrow = (Spinner) findViewById(R.id.spnBarcode_narrow);			
		arrBarcodenarrow = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item);				
		arrBarcodenarrow=ArrayAdapter.createFromResource(this, R.array.activity_1dbarcodes_width, android.R.layout.simple_spinner_item);
		arrBarcodenarrow.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);		
		spnBarcode_narrow.setAdapter(arrBarcodenarrow);	
		spnBarcode_narrow.setOnItemSelectedListener(new OnItemSelectedBarcodenarrow());
		
		txtBarcodeData=(EditText)this.findViewById(R.id.txtBarcodeData);
		txtBarcodeHeight=(EditText)this.findViewById(R.id.txtBarcodeHeight);
		txtBarcode_x=(EditText)this.findViewById(R.id.txtBarcode_x);
		txtBarcode_y=(EditText)this.findViewById(R.id.txtBarcode_y);
		
	}
	
	private class OnItemSelectedBarcodeType implements OnItemSelectedListener
	{				
		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,long arg3) 
		{						
			switch (arg2) {
			case 0:
				Barcodetype=KMPrinterHelper.code128;
				break;
			case 1:
				Barcodetype=KMPrinterHelper.UPCA;
				break;
			case 2:
				Barcodetype=KMPrinterHelper.UPCE;
				break;
			case 3:
				Barcodetype=KMPrinterHelper.EAN13;
				break;
			case 4:
				Barcodetype=KMPrinterHelper.code39;
				break;
			case 5:
				Barcodetype=KMPrinterHelper.code93;
				break;
			case 6:
				Barcodetype=KMPrinterHelper.CODABAR;
				break;
			case 7:
				Barcodetype=KMPrinterHelper.MSI;
				break;
			case 8:
				Barcodetype=KMPrinterHelper.POSTNET;
				break;
			case 9:
				Barcodetype=KMPrinterHelper.FIM;
				break;

			default:
				break;
			}
		}
		@Override
		public void onNothingSelected(AdapterView<?> arg0) 
		{
			// TODO Auto-generated method stub			
		}
	}
	
	private class OnItemSelectedBarcodeWidth implements OnItemSelectedListener
	{				
		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,long arg3) 
		{			
			BarcodeWidth=arg2+1;
		}
		@Override
		public void onNothingSelected(AdapterView<?> arg0) 
		{
			// TODO Auto-generated method stub			
		}
	}
	
	private class OnItemSelectedBarcodeHRILayout implements OnItemSelectedListener
	{				
		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,long arg3) 
		{		
			switch (arg2) {
			case 0:
				Barcodereadable=true;
				break;
			case 1:
				Barcodereadable=false;
				break;

			default:
				break;
			}
		}
		@Override
		public void onNothingSelected(AdapterView<?> arg0) 
		{
			// TODO Auto-generated method stub			
		}
	}
	private class OnItemSelectedBarcoderotation implements OnItemSelectedListener
	{				
		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,long arg3) 
		{			
			switch (arg2) {
			case 0:
				Barcoderotation=KMPrinterHelper.BARCODE;
				break;
			case 1:
				Barcoderotation=KMPrinterHelper.VBARCODE;
				break;

			default:
				break;
			}
		}
		@Override
		public void onNothingSelected(AdapterView<?> arg0) 
		{
			// TODO Auto-generated method stub			
		}
	}
	private class OnItemSelectedBarcodenarrow implements OnItemSelectedListener
	{				
		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,long arg3) 
		{			
			Barcodenarrow=arg2+1;
		}
		@Override
		public void onNothingSelected(AdapterView<?> arg0) 
		{
			// TODO Auto-generated method stub			
		}
	}

	
	public void onClickPrint(View view) 
	{
    	if (!checkClick.isClickEvent()) return;
    	
    	try
    	{
	    	if(txtBarcodeData.getText().toString().trim().length()==0)
	    	{
	    		Toast.makeText(thisCon, getString(R.string.activity_1dbarcodes_no_data), Toast.LENGTH_SHORT).show();
	    		return;
	    	}
	    	KMPrinterHelper.printAreaSize("0", "200","200","500","1");
	    	KMPrinterHelper.Barcode(Barcoderotation, Barcodetype,""+BarcodeWidth,""+Barcodenarrow,txtBarcodeHeight.getText().toString(), txtBarcode_x.getText().toString(),txtBarcode_y.getText().toString(),Barcodereadable,"7","0","5",txtBarcodeData.getText().toString().trim());
//	    	HPRTPrinterHelper.Barcode("BARCODE", "128","1","1","50","0","0",true,"7","0","5","1234567890");
//	    	HPRTPrinterHelper.Form();
	    	if ("1".equals(Activity_Main.paper)) {
	    		KMPrinterHelper.Form();
			}
	    	KMPrinterHelper.Print();
    	}
		catch (Exception e) 
		{			
			Log.d("HPRTSDKSample", (new StringBuilder("Activity_1DBarcodes --> onClickPrint ")).append(e.getMessage()).toString());
		}
    }
}