package ubu.inf.terminal.logica;


import ubu.inf.terminal.R;
import ubu.inf.terminal.modelo.SingletonConexion;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Actividad para conectarse a un servidor de manera manual.
 * @author       David Herreo de la Peña
 * @author       Jonatan Santos Barrios
 * @uml.dependency   supplier="ubu.inf.terminal.logica.SUserInfo"
 * @uml.dependency   supplier="ubu.inf.terminal.logica.Consola"
 */
public class PestanaMainManual extends Activity {
	private Button conectar;
	/**
	 * @uml.property  name="handler"
	 * @uml.associationEnd  
	 */
	private MyHandler handler = new MyHandler();
	private TextView ip;
	private TextView usuario;
	private TextView contrasena;
	private TextView puerto;
	private ProgressBar barra;
	private JSch jsch = null;
	private Session session = null;
	ProgressDialog dialog ;

	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pestanamainmanual);
		inicializa();
	}

	/**
	 * Función para iniciar todos los componentes de la actividad.
	 */
	private void inicializa() {
		conectar = (Button) findViewById(R.id.btConnect);

		ip = (TextView) findViewById(R.id.etHost);
		usuario = (TextView) findViewById(R.id.etUser);
		contrasena = (TextView) findViewById(R.id.etPass);
		puerto = (TextView) findViewById(R.id.etPort);
		barra = (ProgressBar) findViewById(R.id.progressBar1);
		conectar.setOnClickListener(new listenerConectar());

	}
	/**Listener del botón conectar.
	 * 
	 * @author David Herreo de la Peña
	 * @author Jonatan Santos Barrios
	 *
	 */
	private class listenerConectar implements View.OnClickListener {

		public void onClick(View arg0) {
			// TODO Auto-generated method stub

			if (SingletonConexion.getConexion().getJsch() == null
					|| SingletonConexion.getConexion().getSesion() == null) {
				conecta();
			} else {
				Intent intent = new Intent(PestanaMainManual.this,
						Consola.class);
				startActivity(intent);
			}

		}

	}
	/**Handler para manejar los mensajes del hilo asíncrono.
	 * 
	 * @author David Herreo de la Peña
	 * @author Jonatan Santos Barrios
	 *
	 */
	private class MyHandler extends Handler{
		@Override
		public void handleMessage(Message msg){
			dialog.dismiss();
			Toast.makeText(PestanaMainManual.this,
					"ERROR,compruebe los datos y el acceso a la red",
					Toast.LENGTH_LONG).show();
			barra.setProgress(0);
		}
	}
	
	/**
	 * Función para conectarse a un servidor mediantes ssh.
	 */
	private void conecta() {
		
		 dialog = ProgressDialog.show(this, "", "Conectando...", true);

			Thread hilo = new Thread(){
				@Override
				public void run(){
					try {
						int p = Integer.parseInt(puerto.getText().toString());

						jsch = new JSch();

						session = jsch.getSession(usuario.getText().toString(), ip
								.getText().toString(), p);
						barra.setProgress(67);
						UserInfo ui = new SUserInfo(contrasena.getText().toString(), null);

						session.setUserInfo(ui);

						session.setPassword(contrasena.getText().toString());

						session.connect(20000);
						dialog.dismiss();

						barra.setProgress(100);
						
						enviaIntent();

						// System.out.println("------ FIN");
					} catch (JSchException e) {
						dialog.dismiss();
						
						session = null;
						jsch = null;
						handler.sendEmptyMessage(0);
						
					}
					
				}
			};
			hilo.start();
			
	}

	/**
	 * Función para abrir la consola de comandos.
	 * @see Consola
	 */
	private void enviaIntent() {
		Intent intent = new Intent(PestanaMainManual.this, Consola.class);

		SingletonConexion.getConexion().setSesion(session);
		SingletonConexion.getConexion().setJsch(jsch);

		startActivity(intent);
	}
}