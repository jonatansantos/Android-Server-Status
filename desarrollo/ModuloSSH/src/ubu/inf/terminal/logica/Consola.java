package ubu.inf.terminal.logica;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;


import ubu.inf.terminal.R;
import ubu.inf.terminal.accesodatos.FachadaComandos;
import ubu.inf.terminal.modelo.SingletonConexion;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Actividad que imita la apariencia de una terminal de comandos para poder comunicarse con el servidor via ssh.
 * @author           David Herrero de la Peña
 * @author           Jonatan Santos Barrios
 * @uml.dependency   supplier="ubu.inf.terminal.logica.ComandosEjecutados"
 * @uml.dependency   supplier="ubu.inf.terminal.logica.ComandosFavoritos"
 */
public class Consola extends Activity {

	/**
	 * Caja de texto donde introducir el comando.
	 */
	private EditText comando;
	/**
	 * Caja de texto donde se muestra la respuesta del servidor.
	 */
	private EditText result;
	/**
	 * Boton para ejecutar un comando.
	 */
	private ImageButton run;
	private Session session;
	private JSch jsch;
	/**
	 * Canal tipo shell para enviar y recibir datos al servidor.
	 */
	private ChannelShell channelShell;
	/**
	 * Array list con los últimos comandos ejecutados.
	 */
	private ArrayList<String> comandos;
	private EditText nombre;
	private TextView comandoguardar;
	private String auxnombre;
	/**
	 * Stream de salida hacia el servidor.
	 */
	private OutputStream toServer;
	/**
	 * Reader para leer la salida del servidor.
	 */
	private BufferedReader fromServer;

	/**
	 * Handler para manejar los mensajes del hilo asíncrono.
	 * @uml.property  name="handler"
	 * @uml.associationEnd  
	 */
	private MyHandler handler = new MyHandler();
	/**
	 * @uml.property  name="lector"
	 * @uml.associationEnd  
	 */
	private LectorBuffered lector;
	public static final int REQUEST_TEXT3 = 3;
	private static final int DIALOG_TEXT_ENTRY = 7;
	private static final int REQUEST_TEXT4 = 0;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.consola);
		inicializa();

	}

	@Override
	protected void onDestroy() {
		
		finalizaConexion();
		super.onDestroy();
	}

	/**
	 * Función para iniciar todos los componentes del a actividad.
	 */
	private void inicializa(){
		//obtenemos referencias a los elementos gráficos
		comando = (EditText) findViewById(R.id.et_consola_comando);
		//ponemos el listener
		comando.setOnKeyListener(new ListenerKey());
		result = (EditText) findViewById(R.id.et_consola_resultado);
		result.setOnKeyListener(new ListenerKey());
		run = (ImageButton) findViewById(R.id.ib_consola_run);
		//iniciamos variables
		comandos = new ArrayList<String>();
		session = SingletonConexion.getConexion().getSesion();
		jsch = SingletonConexion.getConexion().getJsch();
		run.setOnClickListener(new listenerComando());

		result.setEnabled(false);
		nombre = (EditText) findViewById(R.id.et_seleccion_nombre);
		comandoguardar = (TextView) findViewById(R.id.tv_seleccion_comando);
		//intentamos abrir el canal
		try {
			channelShell = (ChannelShell) session.openChannel("shell");
			toServer = channelShell.getOutputStream();
			fromServer = new BufferedReader(new InputStreamReader(
					channelShell.getInputStream(),"UTF-8"));

			result.setTextColor(Color.GREEN);
			channelShell.connect(3 * 1000);

			lector = new LectorBuffered();
			lector.start();
			

		} catch (JSchException e) {
			
			e.printStackTrace();
			Toast.makeText(this, "Error al obtener el canal de conexion.", Toast.LENGTH_SHORT).show();
			finalizaConexion();
		} catch (IOException e) {
			
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}

	}

	/**
	 * Función para crear una ventana de dialogo tipo POP-UP.
	 */
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_TEXT_ENTRY:
			LayoutInflater factory = LayoutInflater.from(this);
			final View selectorNombre = factory.inflate(
					R.layout.seleccionnombre, null);

			comandoguardar = (TextView) selectorNombre
					.findViewById(R.id.tv_seleccion_comando);
			comandoguardar.setText(comandos.get(comandos.size() - 1));
			comandoguardar.setTextColor(Color.RED);

			return new AlertDialog.Builder(Consola.this)
					.setIcon(R.drawable.ic_menu_add)
					.setTitle(R.string.nombre)
					.setView(selectorNombre)
					.setPositiveButton(R.string.ok,
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int wichButton) {
									Log.i("mssh", "boton ok");

									// se ha pulsado OK
									try {
										String cmd = comandos.get(comandos
												.size() - 1);
										ArrayList<String> array = new ArrayList<String>();
										array.add(cmd);

										nombre = (EditText) selectorNombre
												.findViewById(R.id.et_seleccion_nombre);
										auxnombre = nombre.getText().toString();
										Log.i("mssh", "texo=" + auxnombre);
										FachadaComandos.getInstance(
												Consola.this).insertComandos(
												auxnombre, 1, array);

									} catch (Exception e) {
										e.printStackTrace();
										Log.e("mssh",
												"error listener dialog OK");
									}

								}
							})
					.setNegativeButton(R.string.cancelar,
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int wichButton) {
									auxnombre = null;

								}
							}).create();

		}
		return null;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menucomandos, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.anadirVarios:
			añadirVarios();
			return true;
		case R.id.reconectar:
			reconectar();
			return true;
		case R.id.anadirUltimo:
			añadirUltimo();
			return true;
		case R.id.borrar:
			borrar();
			return true;
		case R.id.ejecutarFavorito:
			ejecutarFavorito();
			return true;
		case R.id.cerrar:
			finalizaConexion();
			
			return true;
		case R.id.terminar:
			try{
			toServer.write(3);
			toServer.flush();
			}catch(Exception e){
				e.printStackTrace();
			}
		default:
			return false;
		}
	}

	/**Función para terminar la conexión de forma segura, cierra los canales, los streams y 
	 * todo lo necesario para que no se produzcan fallos.
	 * 
	 */
	private void finalizaConexion(){
		if(lector!=null)
		lector.setTerminar(true);
		if(channelShell!=null)
		channelShell.disconnect();
		if(session!=null)
		session.disconnect();
		SingletonConexion.getConexion().setJsch(null);
		SingletonConexion.getConexion().setSesion(null);
		finish();
	}
	/**
	 * Función para ir a la pantalla de ejecutar favorito.
	 */
	private void ejecutarFavorito() {
		Intent i = new Intent(this, ComandosFavoritos.class);
		Consola.this.startActivityForResult(i, REQUEST_TEXT3);
		
	}

	/**
	 * Función que se llama cuando se retorna de una actividad llamada de forma especial,
	 * con startActivityForResult
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_TEXT3) {
			if (resultCode == Activity.RESULT_OK) {
				Bundle bundle = data.getExtras();
				ArrayList<String> respuesta = bundle
						.getStringArrayList("comandos");

				// aqui tendría que hacer el bucle para ejecutar los comandos
				// uno a uno
				for (int i = 0; i < respuesta.size(); ++i) {
					try {
						ejecutaComando(respuesta.get(i));
					} catch (JSchException e) {
						
						e.printStackTrace();
					} catch (IOException e) {
						
						e.printStackTrace();
					}
				}
			}
		}
		if (requestCode == REQUEST_TEXT4) {
			if (resultCode == Activity.RESULT_OK) {
				Bundle bundle = data.getExtras();
				ArrayList<String> respuesta = bundle
						.getStringArrayList("comandos");
				for (int i = 0; i < respuesta.size(); ++i) {
					try {
						ejecutaComando(respuesta.get(i));
					} catch (JSchException e) {
						
						e.printStackTrace();
					} catch (IOException e) {
						
						e.printStackTrace();
					}
				}
			}
		}
	}

	/**
	 * Limpia el texto de la pantalla.
	 */
	private void borrar() {
		result.setText("");

	}

	/**
	 * Función para añadir el último comando a favoritos.
	 */
	private void añadirUltimo() {
		if (comandos.size() >= 1) {

			// mostramos el dialogo
			showDialog(DIALOG_TEXT_ENTRY);

		} else {
			Toast.makeText(this, "no se ha ejecutado ningún comando",
					Toast.LENGTH_SHORT).show();
		}

	}

	/**
	 * Función para intentar reconectar y establecer de nuevo la conexión.
	 */
	private void reconectar() {
		try {
			if (!session.isConnected()) {
				Log.i("mssh", "intentando la reconexion");
				session.connect();
				Log.i("mssh", "reconexion correcta");
			} else {
				Toast.makeText(this, "Ya estás conectado", Toast.LENGTH_SHORT)
						.show();
			}
		} catch (JSchException e) {
			Log.i("mssh", "fallo al realizar la reconexion");
			Toast.makeText(this, "No se pudo reconectar", Toast.LENGTH_SHORT)
					.show();
			e.printStackTrace();
		}

	}

	/**
	 * Función para añadir varios comandos a la base de datos.
	 */
	private void añadirVarios() {
		if (comandos.size() >= 1) {
			Intent i = new Intent(this, ComandosEjecutados.class);
			i.putExtra("com", comandos);
			Consola.this.startActivityForResult(i, REQUEST_TEXT4);
		} else {
			Toast.makeText(this, "no se ha ejecutado ningún comando",
					Toast.LENGTH_SHORT).show();
		}

	}

	/**
	 * Clase que implementa el listener para cuando se hace click en el botón de ejecutar el comando.
	 * @author David Herrero de la Peña
	 * @author Jonatan Santos Barrios
	 *
	 *@see OnClickListener
	 */
	private class listenerComando implements View.OnClickListener {

		public void onClick(View arg0) {
			

			if (jsch != null && session != null) {

				try {

					ejecutaComando(comando.getText().toString());
					//borramos el editText
					comando.setText("");

				} catch (JSchException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
					Toast.makeText(Consola.this,
							"ERROR GRAVE, prueba a reconectar.",
							Toast.LENGTH_SHORT).show();
				}

			}
		}

	}
	/**
	 * Clase que implementa el listener para cuando se hace click en el boton de teclado ENTER.
	 * @author David Herrero de la Peña
	 * @author Jonatan Santos Barrios
	 *
	 *@see OnClickListener
	 */
	private class ListenerKey implements OnKeyListener {

		public boolean onKey(View v, int keyCode, KeyEvent event) {
			
			if (event.getAction() == KeyEvent.ACTION_DOWN) {
				if (keyCode == KeyEvent.KEYCODE_ENTER) {
					try {
						ejecutaComando(comando.getText().toString());
						comando.setText("");
						return true;
					} catch (JSchException e) {
						
						e.printStackTrace();
					} catch (IOException e) {
						
						e.printStackTrace();
					}
				}
			}
			return false;
		}

	}

	/**
	 * Función para ejecutar un comando, se añade el coamndo al Stream de salida.
	 * @param texto texto con el comando a ejecutar
	 * @throws JSchException
	 * @throws IOException
	 */
	private void ejecutaComando(String texto) throws JSchException, IOException {
		
		result.setTextColor(Color.GREEN);

		comandos.add(texto);
		toServer.write((texto + '\r' + '\n').getBytes());
		toServer.flush();


		

	}

	/**
	 * Clase que implementa a Thread, hilo asíncrono para leer la salida del servidor e ir enviando  mensajes al Handler para que los añada a la caja de texto.
	 * @author   david
	 */
	private class LectorBuffered extends Thread {
		private boolean terminar = false;
		
		/**
		 * @param  a
		 * @uml.property  name="terminar"
		 */
		public void setTerminar(boolean a){
			terminar = a;
		}
		@Override
		public void run() {
			while (!terminar) {
				try {

					String linea = fromServer.readLine();
					Log.i("mssh", "vaciando linea " + linea);
					Message msg = new Message();
					msg.obj = linea;
					handler.sendMessage(msg);
					

				} catch (IOException e) {
					e.printStackTrace();
					Log.e("mssh", "se ha terminado de leer el bufer");
					
				}
			}
		}
	}

	/**
	 * Handler que trata los mensajes que le manda el hilo asíncrono.
	 * Hace tratamiento de cadena para filtrar patrones y eliminar carácteres innecesarios.
	 * Además añade el texto a la caja de texto principal.
	 * @author david
	 *
	 */
	private class MyHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			String valor =(String)msg.obj;
			
			
			valor = valor.replaceAll("\\[..;..[A-Z]", "");//eliminamos colores
			valor = valor.replaceAll("\\[..;..[a-z]", "");
			valor = valor.replaceAll("\\[.m", "");
			valor = valor.replaceAll("\\[m", "");//eliminamos caracteres especiales		
			valor = valor.replaceAll("\\[K", "");
			valor = valor.replaceAll("\\[H", "");
			valor = valor.replaceAll("\\[J", "");
			valor = valor.replaceAll("", "");
			valor = valor.replaceAll("", "");	
			
			Log.i("mssh", "nos envian un mensaje , linea =" + valor);
			
			result.append(""+valor + '\n');
			

		}
	}
}
