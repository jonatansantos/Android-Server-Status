package ubu.inf.logica;


import ubu.inf.R;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

public class Formulario extends Activity {
	
	private EditText host;
	private CheckBox inicio;
	private SeekBar barracolor;
	private ImageView color;
	private int actual;
	private EditText desc;
	private Button OK;
	private Button cancel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.formulario);
		inicializa();
		//miramos si nos han enviado información
		if(this.getIntent().getExtras()!=null){
			Bundle aux = getIntent().getExtras();
			host.setText(aux.getString("host"));
			desc.setText(aux.getString("desc"));
			inicio.setChecked(aux.getBoolean("inicio"));
			actual = aux.getInt("color");
			color.setBackgroundColor(actual);
		}
		
	}

	private void inicializa() {
		host=(EditText) findViewById(R.id.et_formulario_ip);
		desc=(EditText) findViewById(R.id.et_formulario_desc);
		OK = (Button) findViewById(R.id.bt_formulario_ok);
		OK.setOnClickListener(new listenerOK());
		cancel = (Button) findViewById(R.id.bt_formulario_cancel);
		cancel.setOnClickListener(new listenerCancelar());
		inicio = (CheckBox) findViewById(R.id.cb_formulario_inicio);
		actual=-1;
		barracolor = (SeekBar) findViewById(R.id.seekBar1);
		barracolor.setMax(Color.BLACK * -1);
		barracolor.setOnSeekBarChangeListener(new ListenerBarra());
		
		color = (ImageView) findViewById(R.id.iv_formulario_coloractual);
		color.setBackgroundColor(actual);
	}
	private class ListenerBarra implements OnSeekBarChangeListener{

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			//host.setText("progreso : "+progress);
			color.setBackgroundColor(progress * -1);
			actual=progress * -1;
			
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			
			//Toast.makeText(Formulario.this, "presion ON", Toast.LENGTH_SHORT).show();
			
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			
			//Toast.makeText(Formulario.this, "presion OFF", Toast.LENGTH_SHORT).show();
		}
		
	}
	
	private class listenerOK implements View.OnClickListener {

		public void onClick(View arg0) {
			Intent resultData = new Intent();
			resultData.putExtra("host", host.getText().toString());
			resultData.putExtra("desc", desc.getText().toString());
			resultData.putExtra("inicio", inicio.isChecked());
			resultData.putExtra("color", actual);
          
			setResult(Activity.RESULT_OK, resultData);
            finish();
        } 
		
	}

	private class listenerCancelar implements View.OnClickListener {

		public void onClick(View arg0) {
			Intent resultData = new Intent();
			setResult(Activity.RESULT_CANCELED, resultData);
            finish();
		}
	}

}
