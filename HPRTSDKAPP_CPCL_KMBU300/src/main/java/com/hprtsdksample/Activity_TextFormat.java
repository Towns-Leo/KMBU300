package com.hprtsdksample;

import java.util.HashMap;

import com.hprtsdksample.cpcl.R;



import KMAndroidSDK.KMPrinterHelper;
import KMAndroidSDK.PublicFunction;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class Activity_TextFormat  extends Activity 
{	
	private Context thisCon=null;
	private PublicFunction PFun=null;
	private EditText txtText=null;
	private EditText txtformat_x=null;
	private EditText txtformat_y=null;
	private Spinner spnformat_font=null;
	private Spinner spnformat_rotation=null;
	private ArrayAdapter arrformat_font;
	private ArrayAdapter arrformatrotation;
	private Spinner spnformat_x_multiplication=null;
	private ArrayAdapter arrformat_x_multiplication;
	private Spinner spnformat_y_multiplication=null;
	private ArrayAdapter arrformat_y_multiplication;
	private int formatfont=16;
	private String x_multiplication="0";
	private String y_multiplication="0";
	private String qrcoderotation="";
	private CheckBox cb_textformat_bold;
	private CheckBox cb_textformat_inverse;
	private CheckBox cb_textformat_doublewidth;
	private CheckBox cb_textformat_doubleheight;
	private int textType=1;
	private EditText ed_textformat_papeheight;
	private int papeheight=0;
	
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);	   
		this.requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_text_format);			
		thisCon=this.getApplicationContext();
		
		txtText = (EditText) findViewById(R.id.txtText);
		txtformat_x = (EditText) findViewById(R.id.txtformat_x);
		txtformat_y = (EditText) findViewById(R.id.txtformat_y);
		
		spnformat_font = (Spinner) findViewById(R.id.spnformat_font);	
		String[] sList = "16,24,32".split(",");
		arrformat_font = new ArrayAdapter<String>(Activity_TextFormat.this,android.R.layout.simple_spinner_item, sList);
		arrformat_font.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);		
		spnformat_font.setAdapter(arrformat_font);
		spnformat_font.setOnItemSelectedListener(new OnItemSelectedformatfont());
		
		spnformat_rotation = (Spinner) findViewById(R.id.spnformat_rotation);			
		arrformatrotation = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item);				
		arrformatrotation=ArrayAdapter.createFromResource(this, R.array.activity_1dbarcodes_hri_rotation, android.R.layout.simple_spinner_item);
		arrformatrotation.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);		
		spnformat_rotation.setAdapter(arrformatrotation);	
		spnformat_rotation.setOnItemSelectedListener(new OnItemSelectedformatrotation());
		cb_textformat_bold = (CheckBox) findViewById(R.id.cb_textformat_bold);
		cb_textformat_inverse = (CheckBox) findViewById(R.id.cb_textformat_inverse);
		cb_textformat_doublewidth = (CheckBox) findViewById(R.id.cb_textformat_doublewidth);
		cb_textformat_doubleheight = (CheckBox) findViewById(R.id.cb_textformat_doubleheight);
		ed_textformat_papeheight = (EditText) findViewById(R.id.ed_textformat_papeheight);
	}
	
	private class OnItemSelectedformatrotation implements OnItemSelectedListener
	{				
		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,long arg3) 
		{			
			switch (arg2) {
			case 0:
				qrcoderotation=KMPrinterHelper.TEXT;
				break;
			case 1:
				qrcoderotation=KMPrinterHelper.TEXT270;
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
	private class OnItemSelectedformatfont implements OnItemSelectedListener
	{				
		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,long arg3) 
		{			
			formatfont=Integer.valueOf(spnformat_font.getSelectedItem().toString());
			
		}
		@Override
		public void onNothingSelected(AdapterView<?> arg0) 
		{
			// TODO Auto-generated method stub			
		}
	}
	private class OnItemSelectedformat_x_multiplication implements OnItemSelectedListener
	{				
		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,long arg3) 
		{			
			x_multiplication=spnformat_x_multiplication.getSelectedItem().toString();
		}
		@Override
		public void onNothingSelected(AdapterView<?> arg0) 
		{
			// TODO Auto-generated method stub			
		}
	}
	private class OnItemSelectedformat_y_multiplication implements OnItemSelectedListener
	{				
		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,long arg3) 
		{			
			y_multiplication=spnformat_y_multiplication.getSelectedItem().toString();
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
    		String sText=txtText.getText().toString();
	    	if(sText.length()==0)
	    	{
	    		Toast.makeText(thisCon, getString(R.string.activity_1dbarcodes_no_data), Toast.LENGTH_SHORT).show();
	    		return;
	    	}
	    	if ("".equals(ed_textformat_papeheight.getText().toString())) {
	    		Toast.makeText(thisCon, getString(R.string.activity_text_format_no_pape), Toast.LENGTH_SHORT).show();
	    		return;
			}
	    	papeheight=Integer.valueOf(ed_textformat_papeheight.getText().toString());
	    	if (papeheight<0||papeheight==0) {
	    		Toast.makeText(thisCon, getString(R.string.activity_text_format_no_pape), Toast.LENGTH_SHORT).show();
	    		return;
			}
	    	textType=0;
	    	if (cb_textformat_bold.isChecked()) {
				textType=textType|1;
			}
	    	if (cb_textformat_inverse.isChecked()) {
	    		textType=textType|2;
	    	}
	    	if (cb_textformat_doublewidth.isChecked()) {
	    		textType=textType|4;
	    	}
	    	if (cb_textformat_doubleheight.isChecked()) {
	    		textType=textType|8;
	    	}
	    	String[] split = sText.split("\n");
	    	PublicAction PAct=new PublicAction(thisCon);
	    	KMPrinterHelper.printAreaSize("0", "200","200",""+papeheight*8,"1");
	    	for (int i = 0; i < split.length; i++) {
//	    		KMPrinterHelper.Text(qrcoderotation,formatfont, x_multiplication, txtformat_x.getText().toString(),""+(Integer.valueOf(txtformat_y.getText().toString())+i*30),split[i]);
	    		if (qrcoderotation.equals(KMPrinterHelper.TEXT)) {
	    			KMPrinterHelper.PrintTextCPCL(qrcoderotation, formatfont, txtformat_x.getText().toString(), ""+(Integer.valueOf(txtformat_y.getText().toString())+(i*formatfont)*2), split[i], textType,false,100);
				}else {
					KMPrinterHelper.PrintTextCPCL(qrcoderotation, formatfont, ""+(Integer.valueOf(txtformat_x.getText().toString())+i*formatfont*2), txtformat_y.getText().toString(), split[i], textType,false,100);
				}
			}
//	    	KMPrinterHelper.Form();
//	    	KMPrinterHelper.PrintTextCPCL(KMPrinterHelper.TEXT, 32, "10", "40", sText, 15);
//	    	KMPrinterHelper.PrintTextCPCL(KMPrinterHelper.TEXT270, 32, "90", "100", sText, 15);
//	    	KMPrinterHelper.PrintTextCPCL(KMPrinterHelper.TEXT270, 24, "130", "100", sText, 15);
	    	if ("1".equals(Activity_Main.paper)) {
				KMPrinterHelper.Form();
			}
	    	KMPrinterHelper.Print();
    	}
		catch (Exception e) 
		{			
			Log.d("HPRTSDKSample", (new StringBuilder("Activity_TextFormat --> onClickPrint ")).append(e.getMessage()).toString());
		}
    }
}
