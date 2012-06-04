package ubu.inf;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class Login extends Activity{
	private TextView pass;
	private Button ok;
	private SharedPreferences pref;
	private String passguardada;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		inicializa();
		
	}

	/**
	 * Funci�n para iniciar todos los componentes de la pantalla.
	 */
	private void inicializa(){
		//obtenemos la contrase�a guardada
		pref = PreferenceManager.getDefaultSharedPreferences(this);
		passguardada=pref.getString("pass", "");
		
		pass= (TextView) findViewById(R.id.et_login_pass);
		ok = (Button) findViewById(R.id.bt_login_ok);
		ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String aux = pass.getText().toString();
				if(aux.equals(passguardada)){//contrase�a correcta
					Intent i = new Intent(Login.this, Main.class);
					startActivity(i);
				}else{
					Intent i = new Intent(Login.this, FalsaActividad.class);
					startActivity(i);
				}
				
			}
		});
	}
}
