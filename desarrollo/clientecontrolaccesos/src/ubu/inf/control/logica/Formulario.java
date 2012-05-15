package ubu.inf.control.logica;


import ubu.inf.control.R;
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
/**
 * Formulario para crear nuevos servidores o editar los que ya había.
 * 
 * @author David Herrero de la Peña
 * @author Jonatan Santos Barrios
 * 
 * @version 1.0
 * 
 *@see PestanaMainFav
 *@see PestanaMainEmail
 */
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

	/**
	 * Función para iniciar todos los componentes.
	 */
	private void inicializa() {
		//referencias a todos los componentes
		host=(EditText) findViewById(R.id.et_formulario_ip);
		desc=(EditText) findViewById(R.id.et_formulario_desc);
		OK = (Button) findViewById(R.id.bt_formulario_ok);
		cancel = (Button) findViewById(R.id.bt_formulario_cancel);		
		inicio = (CheckBox) findViewById(R.id.cb_formulario_inicio);
		color = (ImageView) findViewById(R.id.iv_formulario_coloractual);
		barracolor = (SeekBar) findViewById(R.id.seekBar1);
		//ponemos color , máximo y mímino a la barra de selección y listener.
		color.setBackgroundColor(actual);
		actual=-1;		
		barracolor.setMax(Color.BLACK * -1);
		barracolor.setOnSeekBarChangeListener(new ListenerBarra());
		//listener a los botones
		OK.setOnClickListener(new listenerOK());
		cancel.setOnClickListener(new listenerCancelar());
		
		
	}
	/**
	 * Listener de la barra para elegir color.
	 * @author David Herrero de la Peña
	 * @author Jonatan Santos Barrios
	 *
	 */
	private class ListenerBarra implements OnSeekBarChangeListener{

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			
			color.setBackgroundColor(progress * -1);
			actual=progress * -1;
			
		}

		

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			
			
		}



		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			
			
		}
		
	}
	/**
	 * Listener del botón OK.
	 * @author David Herrero de la Peña
	 * @author Jonatan Santos Barrios
	 *
	 */
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
	/**
	 * Listener del boton cancelar.
	 * @author David Herrero de la Peña
	 * @author Jonatan Santos Barrios
	 *
	 */
	private class listenerCancelar implements View.OnClickListener {

		public void onClick(View arg0) {
			Intent resultData = new Intent();
			setResult(Activity.RESULT_CANCELED, resultData);
            finish();
		}
	}

}
