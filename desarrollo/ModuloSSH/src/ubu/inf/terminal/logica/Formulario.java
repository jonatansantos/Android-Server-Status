package ubu.inf.terminal.logica;


import ubu.inf.terminal.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Actividad para crear un nuevo servidor, tiene la forma de un formulario estándar.
 * @author david
 *
 */
public class Formulario extends Activity {
	private EditText host;
	private EditText user;
	private EditText pass;
	private EditText port;
	private EditText desc;
	private Button OK;
	private Button cancel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.formulario);
		inicializa();
		//miramos si nos han enviado información
		if(getIntent().getExtras()!=null){
			Bundle b = getIntent().getExtras();
			host.setText(b.getString("host"));
			user.setText(b.getString("user"));
			pass.setText(b.getString("pass"));
			port.setText(b.getString("port"));
			desc.setText(b.getString("desc"));
			
		}
	}

	/**
	 * Función para iniciar todos los componentes de la actividad.
	 */
	private void inicializa() {
		host = (EditText) findViewById(R.id.et_formulario_Host);
		user = (EditText) findViewById(R.id.et_formulario_User);
		pass = (EditText) findViewById(R.id.et_formulario_Pass);
		port = (EditText) findViewById(R.id.et_formulario_Port);
		desc = (EditText) findViewById(R.id.et_formulario_Desc);
		OK = (Button) findViewById(R.id.bt_formulario_ok);
		cancel = (Button) findViewById(R.id.bt_formulario_cancel);
		OK.setOnClickListener(new listenerOK());
		cancel.setOnClickListener(new listenerCancelar());

	}

	/**
	 * Listener del boton OK.
	 * @author David Herrero de la Peña
	 * @author Jonatan Santos Barrios
	 *
	 */
	private class listenerOK implements View.OnClickListener {

		public void onClick(View arg0) {
			Intent resultData = new Intent();
            resultData.putExtra("host", host.getText().toString());
            resultData.putExtra("user", user.getText().toString());
            resultData.putExtra("pass", pass.getText().toString());
            resultData.putExtra("port", port.getText().toString());
            resultData.putExtra("desc", desc.getText().toString());
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
