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
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class Consola extends Activity {

	private EditText comando;
	private EditText result;
	private ImageButton run;
	private Session session;
	private JSch jsch;
	private ChannelShell channelShell;
	private ArrayList<String> comandos;
	private EditText nombre;
	private TextView comandoguardar;
	private String auxnombre;
	private OutputStream toServer;
	private BufferedReader fromServer;

	private MyHandler handler = new MyHandler();
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

	public void inicializa(){
		comando = (EditText) findViewById(R.id.et_consola_comando);
		result = (EditText) findViewById(R.id.et_consola_resultado);
		result.setOnKeyListener(new ListenerKey());
		run = (ImageButton) findViewById(R.id.ib_consola_run);
		comandos = new ArrayList<String>();
		session = SingletonConexion.getConexion().getSesion();
		jsch = SingletonConexion.getConexion().getJsch();
		run.setOnClickListener(new listenerComando());

		result.setEnabled(false);
		nombre = (EditText) findViewById(R.id.et_seleccion_nombre);
		comandoguardar = (TextView) findViewById(R.id.tv_seleccion_comando);
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
			a�adirVarios();
			return true;
		case R.id.reconectar:
			reconectar();
			return true;
		case R.id.anadirUltimo:
			a�adirUltimo();
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
	private void ejecutarFavorito() {
		Intent i = new Intent(this, ComandosFavoritos.class);
		Consola.this.startActivityForResult(i, REQUEST_TEXT3);
		
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_TEXT3) {
			if (resultCode == Activity.RESULT_OK) {
				Bundle bundle = data.getExtras();
				ArrayList<String> respuesta = bundle
						.getStringArrayList("comandos");

				// aqui tendr�a que hacer el bucle para ejecutar los comandos
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

	private void borrar() {
		result.setText("");

	}

	private void a�adirUltimo() {
		if (comandos.size() >= 1) {

			// mostramos el dialogo
			showDialog(DIALOG_TEXT_ENTRY);

		} else {
			Toast.makeText(this, "no se ha ejecutado ning�n comando",
					Toast.LENGTH_SHORT).show();
		}

	}

	private void reconectar() {
		try {
			if (!session.isConnected()) {
				Log.i("mssh", "intentando la reconexion");
				session.connect();
				Log.i("mssh", "reconexion correcta");
			} else {
				Toast.makeText(this, "Ya est�s conectado", Toast.LENGTH_SHORT)
						.show();
			}
		} catch (JSchException e) {
			Log.i("mssh", "fallo al realizar la reconexion");
			Toast.makeText(this, "No se pudo reconectar", Toast.LENGTH_SHORT)
					.show();
			e.printStackTrace();
		}

	}

	private void a�adirVarios() {
		if (comandos.size() >= 1) {
			Intent i = new Intent(this, ComandosEjecutados.class);
			i.putExtra("com", comandos);
			Consola.this.startActivityForResult(i, REQUEST_TEXT4);
		} else {
			Toast.makeText(this, "no se ha ejecutado ning�n comando",
					Toast.LENGTH_SHORT).show();
		}

	}

	private class listenerComando implements View.OnClickListener {

		public void onClick(View arg0) {
			

			if (jsch != null && session != null) {

				try {

					ejecutaComando(comando.getText().toString());
					//

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

	private class ListenerKey implements OnKeyListener {

		public boolean onKey(View v, int keyCode, KeyEvent event) {
			
			if (event.getAction() == KeyEvent.ACTION_DOWN) {
				if (keyCode == KeyEvent.KEYCODE_ENTER) {
					try {
						ejecutaComando(comando.getText().toString());
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

	private void ejecutaComando(String texto) throws JSchException, IOException {
		
		result.setTextColor(Color.GREEN);

		comandos.add(texto);
		toServer.write((texto + '\r' + '\n').getBytes());
		toServer.flush();


		

	}

	private class LectorBuffered extends Thread {
		private boolean terminar = false;
		
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

	private class MyHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			String valor =(String)msg.obj;
			//TODO est� muy mal hecho
			
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
