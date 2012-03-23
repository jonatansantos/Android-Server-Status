package ubu.itig;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

public class PestanaMainManual extends Activity {
	private Button conectar;

	private TextView ip;
	private TextView usuario;
	private TextView contrasena;
	private TextView puerto;
	private ProgressBar barra;
	private JSch jsch=null;
	private Session session=null;
	
	
	
	

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pestanamainmanual);
		inicializa();
	}

	private void inicializa() {
		conectar = (Button) findViewById(R.id.btConnect);

		ip = (TextView) findViewById(R.id.etHost);
		usuario = (TextView) findViewById(R.id.etUser);
		contrasena = (TextView) findViewById(R.id.etPass);
		puerto = (TextView) findViewById(R.id.etPort);
		barra = (ProgressBar) findViewById(R.id.progressBar1);
		conectar.setOnClickListener(new listenerConectar());
		
		
	}

	private class listenerConectar implements View.OnClickListener {

		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			
			if (jsch == null && session == null) {
				try {
					int p = Integer.parseInt(puerto.getText().toString());
					
					jsch = new JSch();
					barra.setProgress(10);
					session = jsch.getSession(usuario.getText().toString(), ip
							.getText().toString(), p);
					barra.setProgress(40);
					UserInfo ui = new SUserInfo(contrasena.getText().toString(), null);
					barra.setProgress(60);
					session.setUserInfo(ui);
					barra.setProgress(65);
					session.setPassword(contrasena.getText().toString());
					barra.setProgress(85);
					session.connect();
					barra.setProgress(100);
					

					
					// System.out.println("------ FIN");
				} catch (Exception e) {
					// TODO: handle exception

				}
			}else{
				enviaIntent();
			}
			
		}

	}
	public void enviaIntent(){
		Intent intent = new Intent(PestanaMainManual.this,Consola.class);
		
		Conexion.getConexion().setSesion(session);
		Conexion.getConexion().setJsch(jsch);
		
		startActivity(intent);
	}
}