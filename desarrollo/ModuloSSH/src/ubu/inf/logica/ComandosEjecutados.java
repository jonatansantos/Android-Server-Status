package ubu.inf.logica;

import java.util.ArrayList;

import ubu.inf.R;
import ubu.inf.accesodatos.FachadaComandos;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;

public class ComandosEjecutados extends Activity{

	private Button guardar;
	private Button ejecutar;
	private Button all;
	private ListView lista;
	private String[] com;
	private boolean bandera = false;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.comandosejecutados);
		inicializa();
		
	}

	private void inicializa() {
		
		guardar = (Button) findViewById(R.id.bt_cmd_ejecutados_guardar);
		
		ejecutar = (Button) findViewById(R.id.bt_cmd_ejecutados_ejecutar);
		
		all = (Button) findViewById(R.id.bt_cmd_ejecutados_all);
		
		lista = (ListView) findViewById(R.id.lv_comandos_ejecutados);
		
		lista.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		if(getIntent().getExtras()!=null){
			ArrayList<String> aux = getIntent().getExtras().getStringArrayList("com");
			
			com = new String[aux.size()];
			//com = (String[]) aux.toArray();
			for(int i =0;i<aux.size();++i)
				com[i]=aux.get(i);
			
		}
		
		lista.setAdapter(new ArrayAdapter<String>(ComandosEjecutados.this, android.R.layout.simple_list_item_multiple_choice,com));
		lista.setItemsCanFocus(true);
		lista.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		
		guardar.setOnClickListener(new OnClickListener() {
			
			public void onClick(View arg0) {
				guardar();
				
			}
		});
		ejecutar.setOnClickListener(new OnClickListener() {
			
			public void onClick(View arg0) {
				ejecutar();
				
			}
		});
		all.setOnClickListener(new OnClickListener() {
			
			public void onClick(View arg0) {
				seleccionar();
				
			}
		});
		
				
	}
	private void guardar(){
		final ArrayList<String> comandos = new ArrayList<String>();
		int count = this.lista.getAdapter().getCount();
		for(int i = 0;i<count;++i){
			if(lista.isItemChecked(i)){
				comandos.add((String) lista.getItemAtPosition(i));
			}
		}
		//ahora creamos la venta de di�logo para meter el nombre
		LayoutInflater factory = LayoutInflater.from(ComandosEjecutados.this);
    	final View selector = factory.inflate(R.layout.seleccionscripmultiple, null);
    	
    	final EditText nombre = (EditText) selector.findViewById(R.id.et_seleccionscriptmultiple_nombre);
    	
    	EditText comando = (EditText) selector.findViewById(R.id.et_seleccionscriptmultiple);
    	comando.setEnabled(false);
    	
    	String total="";
    	for(int j=0;j<comandos.size();++j){
    		total+= comandos.get(j)+"  "+'\n';
    		
    	}
    	     	
    	comando.setText(total);
    	comando.setTextColor(Color.WHITE);
    	
    	AlertDialog.Builder constructor =new AlertDialog.Builder(ComandosEjecutados.this);
    	constructor.setTitle("Script");
    	constructor.setView(selector);
    	constructor.setPositiveButton("OK",new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface arg0, int arg1) {
				
				FachadaComandos.getInstance(ComandosEjecutados.this).insertComandos(nombre.getText().toString(), comandos.size(), comandos);
				finish();
				
			}
		});
		
		
		
		constructor.create();
		constructor.show();
		
		
	}
	private void ejecutar(){
		ArrayList<String> comandos2 = new ArrayList<String>();
		int count = this.lista.getAdapter().getCount();
		for(int i = 0;i<count;++i){
			if(lista.isItemChecked(i)){
				comandos2.add((String) lista.getItemAtPosition(i));
			}
		}
		
		Intent resultData = new Intent();
        resultData.putExtra("comandos", comandos2);
        setResult(Activity.RESULT_OK, resultData);
        finish();
		
		
	}
	private void seleccionar(){
		int count = this.lista.getAdapter().getCount();
		
		
		if(bandera){
			bandera=false;
			for(int i = 0;i<count;++i)
				lista.setItemChecked(i, true);
				
		}else{
			bandera=true;
			for(int i = 0;i<count;++i)
				lista.setItemChecked(i, false);
		}
			
	}
	
	

}