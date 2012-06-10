package ubu.inf.terminal.logica;

import java.util.ArrayList;




import ubu.inf.terminal.R;
import ubu.inf.terminal.accesodatos.FachadaServidores;
import ubu.inf.terminal.modelo.Servidor;
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
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Actividad que contiene los servidores favoritos guardados en la base de datos y que permite conectarse a ellos mediante un click.
 * @author         David Herreo de la Peña
 * @author         Jonatan Santos Barrios
 * @uml.dependency   supplier="ubu.inf.terminal.logica.SUserInfo"
 * @uml.dependency   supplier="ubu.inf.terminal.logica.Formulario"
 * @uml.dependency   supplier="ubu.inf.terminal.logica.Consola"
 */
public class PestanaMainFav extends Activity {
	/**
	 * Handler para manejar los mensajes del hilo asíncrono.
	 * @uml.property  name="handler"
	 * @uml.associationEnd  
	 */
	private MyHandler handler = new MyHandler();
	ProgressDialog dialog ;
	public static final int REQUEST_TEXT = 0;
	public static final int REQUEST_TEXT2 = 1;
	/**
	 * Array con los servidores para añadir a la lista.
	 */
	private ArrayList<Servidor> datos;
	/**
	 * @uml.property  name="fachada"
	 * @uml.associationEnd  
	 */
	private FachadaServidores fachada;
	/**
	 * Adaptador de la lista.
	 */
	private ArrayAdapter<Servidor> adapter;
	/**
	 * Lista en la que mostrar los elementos.
	 */
	private ListView list;
	private ImageButton add;
	private JSch jsch = null;
	private Session session = null;
	private int idedit = 0;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pestanamainfav);
		inicializa();

	}


	/**
	 * Función para iniciar los componentes de la actividad.
	 */
	private void inicializa() {
		datos = new ArrayList<Servidor>();
		list = (ListView) findViewById(R.id.lv_main_fav_servidores);
		add = (ImageButton) findViewById(R.id.ib_main_fav_add);
		add.setOnClickListener(new ListenerAdd());

		list.setOnItemClickListener(new ListenerListView());
		registerForContextMenu(list);

		fachada = FachadaServidores.getInstance(this);
		datos = fachada.loadServidores();
		adapter = new ArrayAdapterServidor(this, datos);
		list.setAdapter(adapter);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		Log.i("mssh", "MainFav, on destroy , cerramos la fachada");
		fachada.closeFachada();	
		super.onDestroy();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		MenuInflater inflater = getMenuInflater();

		// AdapterView.AdapterContextMenuInfo info
		// =(AdapterView.AdapterContextMenuInfo)menuInfo;

		menu.setHeaderTitle("Opciones");

		Log.i("mssh", "antes de inflar");
		inflater.inflate(R.menu.menu_fav, menu);
		Log.i("mssh", "despues");

	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {

		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();

		switch (item.getItemId()) {
		case R.id.CtxLstFavBorrar:
			borrar(info);

			return true;
		case R.id.CtxLstFavEdit:
			editar(info);

			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menufav, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		limpiar();
		return true;
	}

	/**
	 * Función para borrar todos los servidores de la base de datos y de la lista.
	 */
	private void limpiar() {
		fachada.borraTabla();
		datos.clear();
		adapter.notifyDataSetChanged();

	}

	/**
	 * Función para editar un elemento (Servidor) de la lista.
	 * @param info información del elemento pulsado.
	 */
	private void editar(AdapterContextMenuInfo info) {
		Intent i = new Intent(PestanaMainFav.this, Formulario.class);
		i.putExtra("host", datos.get(info.position).getIp().toString());
		i.putExtra("user", datos.get(info.position).getUsuario().toString());
		i.putExtra("pass", datos.get(info.position).getContraseña().toString());
		i.putExtra("port", datos.get(info.position).getPuerto().toString());
		i.putExtra("desc", datos.get(info.position).getDescripcion().toString());

		idedit = datos.get(info.position).getId();
		PestanaMainFav.this.startActivityForResult(i, REQUEST_TEXT2);

	}

	/**
	 * Función para borrar un servidor de la lista y de la base de datos.
	 * @param info información del elemento pulsado.
	 */
	private void borrar(AdapterContextMenuInfo info) {
		// TODO Auto-generated method stub
		fachada.deleteServidor(datos, datos.get(info.position).getId());
		adapter.notifyDataSetChanged();
	}

	/**Listener de la lista, para efectuar acciones cuando se pulsa uno de 
	 * sus elementos.
	 * 
	 * @author David Herreo de la Peña
	 * @author Jonatan Santos Barrios
	 *
	 */
	private class ListenerListView implements OnItemClickListener {

		public void onItemClick(AdapterView<?> a, View v, int position, long id) {

			Servidor serv = datos.get(position);
			
			if (SingletonConexion.getConexion().getJsch() == null
					|| SingletonConexion.getConexion().getSesion() == null) {

				conectar(serv);
			} else {
				if(SingletonConexion.getConexion().getSesion().getUserName().equals(datos.get(position).getUsuario())){//la sesion estaba abierta
					Intent i = new Intent(PestanaMainFav.this, Consola.class);
					startActivity(i);
				}else{
					session.disconnect();
					SingletonConexion.getConexion().setSesion(null);
					SingletonConexion.getConexion().setJsch(null);
					
					Toast.makeText(PestanaMainFav.this,
							"ya había una conexión abierta,se ha cerrado",
							Toast.LENGTH_LONG).show();	
				}
				
			}

		}

		/**
		 * Función para conectarse mediante ssh a un servidor.
		 * @param serv servidor al que conectarse.
		 */
		private void conectar(final Servidor serv) {
			 dialog = ProgressDialog.show(PestanaMainFav.this, "", "Conectando...", true);
			Thread hilo = new Thread(){
				@Override
				public void run(){
					try {
						int p = Integer.parseInt(serv.getPuerto());

						jsch = new JSch();
						Log.i("mssh", "conseguimos el jsch");
						session = jsch.getSession(serv.getUsuario(), serv.getIp(), p);
						Log.i("mssh", "creamos el objeto sesion");
						UserInfo ui = new SUserInfo(serv.getContraseña(), null);

						session.setUserInfo(ui);

						session.setPassword(serv.getContraseña());
						Log.i("mssh", "intentamos la conexión");
						session.connect(20000);
						dialog.dismiss();
						Log.i("mssh", "conexión realizada");

						enviaIntent();

					} catch (JSchException e) {
						handler.sendEmptyMessage(0);
					
					}
				}
				
			};
			hilo.start();
			

		
		}
	}
	/**Listener del boton añadir nuevo servidor.
	 * 
	 * @author David Herreo de la Peña
	 * @author Jonatan Santos Barrios
	 *
	 */
	private class ListenerAdd implements View.OnClickListener {

		public void onClick(View arg0) {
			Intent i = new Intent(PestanaMainFav.this, Formulario.class);
			PestanaMainFav.this.startActivityForResult(i, REQUEST_TEXT);

		}

	}

	/**
	 * Función que se llama tras efectuarse un startActivityForResult.
	 * 
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_TEXT) {
			if (resultCode == Activity.RESULT_OK) {
				Bundle bundle = data.getExtras();
				Servidor serv = new Servidor(0, bundle.getString("host"),
						bundle.getString("user"), bundle.getString("pass"),
						bundle.getString("port"), bundle.getString("desc"));
				fachada.insertServidor(datos, serv);
				adapter.notifyDataSetChanged();
			}
		} else {
			if (requestCode == REQUEST_TEXT2) {
				if (resultCode == Activity.RESULT_OK) {
					Bundle bundle = data.getExtras();
					Servidor serv = new Servidor(idedit,
							bundle.getString("host"), bundle.getString("user"),
							bundle.getString("pass"), bundle.getString("port"),
							bundle.getString("desc"));
					fachada.editServidor(datos, serv);
					adapter.notifyDataSetChanged();
				}
			}
		}
	}
	/**Adapter de la lista, se encarga de dibujar cada uno de los elementos de la lista.
	 * 
	 * @author David Herreo de la Peña
	 * @author Jonatan Santos Barrios
	 *
	 */
	private class ArrayAdapterServidor extends ArrayAdapter<Servidor> {
		private Activity context;
		private ArrayList<Servidor> datos;

		public ArrayAdapterServidor(Activity context, ArrayList<Servidor> array) {
			super(context, R.layout.list_servers, array);
			// TODO Auto-generated constructor stub
			this.context = context;
			datos = array;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			LayoutInflater inflater = context.getLayoutInflater();
			View item = inflater.inflate(R.layout.list_servers, null);

			final TextView ID = (TextView) item.findViewById(R.id.tv_id);
			Integer id = datos.get(position).getId();
			ID.setText(id.toString());

			TextView lblIP = (TextView) item.findViewById(R.id.tv_IP2);
			lblIP.setText(datos.get(position).getIp());

			TextView lblusuario = (TextView) item
					.findViewById(R.id.tv_usuario2);
			lblusuario.setText(datos.get(position).getUsuario());

			TextView descripcion = (TextView) item
					.findViewById(R.id.tv_descripcion2);
			descripcion.setText(datos.get(position).getDescripcion());

			return (item);
			// TODO Auto-generated method stub
			// return super.getView(position, convertView, parent);
		}

	}

	/**
	 * Función para llamar a la actividad Consola, se usa tras una conexión existosa con el servidor.
	 * @see Consola
	 */
	private void enviaIntent() {
		Intent intent = new Intent(PestanaMainFav.this, Consola.class);

		SingletonConexion.getConexion().setSesion(session);
		SingletonConexion.getConexion().setJsch(jsch);

		startActivity(intent);
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
			Toast.makeText(PestanaMainFav.this,
					"ERROR,compruebe los datos y el acceso a la red",
					Toast.LENGTH_LONG).show();
			
		}
	}
}