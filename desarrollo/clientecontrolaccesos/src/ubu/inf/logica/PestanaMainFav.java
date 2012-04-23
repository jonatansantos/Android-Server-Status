package ubu.inf.logica;

import java.util.ArrayList;
import java.util.Calendar;

import ubu.inf.R;

import ubu.inf.accesodatos.FachadaServidores;
import ubu.inf.modelo.ServIntent;
import ubu.inf.modelo.Servidor;
import ubu.inf.modelo.SingletonServicios;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ToggleButton;

/**
 * Clase que implementa la funcionalidad de la pestana de favoritos.
 * 
 * @author David Herrero
 * @author Jonatan Santos
 * 
 *@version 1.0
 *
 */
public class PestanaMainFav extends Activity {
	private static final int REQUEST_FORMULARIO = 0;
	private static final int REQUEST_CONTEXT = 1;
	private int idaux;
	private ArrayList<Servidor> datos;
	private ArrayList<ServIntent> activados;
	private ToggleButton run;
	private ListView list;
	private ImageButton add;
	private TextView cantidad;
	private FachadaServidores fachada;

	private ArrayAdapterServidor adapter;
	
	private Calendar calendario;
	private AlarmManager alarma;
	private PendingIntent pen;

	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		 setContentView(R.layout.pestanamainfav);
		 inicializa();
	}
	
	private void inicializa() {
		activados = new ArrayList<ServIntent>();
		datos = new ArrayList<Servidor>();
		cantidad = (TextView) findViewById(R.id.tv_pestanamainfav_contador);
		list = (ListView) findViewById(R.id.lv_main_fav_servidores);
		add = (ImageButton) findViewById(R.id.ib_main_fav_add);
		run = (ToggleButton) findViewById(R.id.tb_pestanamainfav_run);
		run.setOnCheckedChangeListener(new ListenerRun());
		add.setOnClickListener(new ListenerAdd());

		list.setOnItemClickListener(new ListenerListView());
		registerForContextMenu(list);

		fachada = FachadaServidores.getInstance(this);
		
		
		datos = fachada.loadServidores();
		adapter = new ArrayAdapterServidor(this, datos);
		list.setAdapter(adapter);
		
		
	}
	
	private class ListenerRun implements OnCheckedChangeListener{

		@Override
		public void onCheckedChanged(CompoundButton boton, boolean estado) {
			if(estado){

				alarma = (AlarmManager) getSystemService(ALARM_SERVICE);
				
				 calendario = Calendar.getInstance();
				 
				   calendario.setTimeInMillis(System.currentTimeMillis());
				  
				   calendario.add(Calendar.SECOND, 10);//cada diez segundos
				  
				   Intent myIntent = new Intent(PestanaMainFav.this,Servicio.class);
				
					
					 pen = PendingIntent.getService(PestanaMainFav.this, 0, myIntent, 0);
				
				
					alarma.setRepeating(AlarmManager.RTC_WAKEUP, calendario.getTimeInMillis(), 30000, pen);
					Toast.makeText(PestanaMainFav.this, "Alarma iniciada", Toast.LENGTH_SHORT).show();
					cantidad.setText("servicios :"+SingletonServicios.getConexion().getHosts().size());
				//TODO
				//compruebaActivados();
			}else{
				SingletonServicios.getConexion().getHosts().clear();
				alarma.cancel(pen);
				pen=null;
				Toast.makeText(PestanaMainFav.this, "Alarma detenida", Toast.LENGTH_SHORT).show();
			}
			
		}
		
	}
	private void limpiar() {
		fachada.borraTabla();
		datos.clear();
		adapter.notifyDataSetChanged();

	}
	private void editar(AdapterContextMenuInfo info) {
		Intent i = new Intent(PestanaMainFav.this, Formulario.class);
		
		i.putExtra("host", datos.get(info.position).getIp());
		i.putExtra("desc", datos.get(info.position).getDescripcion());
		i.putExtra("inicio", datos.get(info.position).isInicio());
		i.putExtra("color", datos.get(info.position).getColor());
		idaux= datos.get(info.position).getId();
		PestanaMainFav.this.startActivityForResult(i, REQUEST_CONTEXT);

	}

	private void borrar(AdapterContextMenuInfo info) {
		
		fachada.deleteServidor(datos, datos.get(info.position).getId());
		adapter.notifyDataSetChanged();
	}
	
	private void compruebaActivados(){
		//TODO
		Servidor s=null;
		for(int i =0;i<datos.size();++i){
			if(datos.get(i).isInicio()){
				
				s=datos.get(i);
				//introducimos el nuevo host
				SingletonServicios.getConexion().getHosts().add(s.getIp());
//			Intent myIntent = new Intent(this,Servicio.class);
//			//le indicamos a donde se tiene que conectar
//			myIntent.putExtra("host",s.getIp() );
//			PendingIntent p = PendingIntent.getService(this, s.getId(), myIntent, 0);
//		
//			activados.add(new ServIntent(s,p));
//			alarma.setRepeating(AlarmManager.RTC_WAKEUP, calendario.getTimeInMillis(), 30000, p);
//				activados.add(new ServIntent(s, p));
				Log.i("control", "11111111");
			}
		}
		
		cantidad.setText("servicios activos : "+activados.size());
	}
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		MenuInflater inflater = getMenuInflater();

		// AdapterView.AdapterContextMenuInfo info
		// =(AdapterView.AdapterContextMenuInfo)menuInfo;

		menu.setHeaderTitle("Opciones");

		Log.i("control", "antes de inflar");
		inflater.inflate(R.menu.menu_fav, menu);
		Log.i("control", "despues");

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
	
	private void iniciaServicio(Servidor s){
		
		//Toast.makeText(this, "iniciando servicio "+s.getId(), Toast.LENGTH_SHORT).show();
		SingletonServicios.getConexion().getHosts().add(s.getIp());
		cantidad.setText("servicios :"+SingletonServicios.getConexion().getHosts().size());

		
		
	}
	
	private void paraServicio(Servidor s){
		
		//Toast.makeText(this, "deteniendo servicio "+s.getId(), Toast.LENGTH_SHORT).show();
		SingletonServicios.getConexion().getHosts().remove(s.getIp());
		cantidad.setText("servicios :"+SingletonServicios.getConexion().getHosts().size());
		
		
	}
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_FORMULARIO) {
			if (resultCode == Activity.RESULT_OK) {
				
				Bundle bundle = data.getExtras();
				Servidor serv = new Servidor(bundle.getString("host"),
						bundle.getString("desc"),
						bundle.getBoolean("inicio"),
						0,bundle.getInt("color"));
				fachada.insertServidor(datos, serv);
				adapter.notifyDataSetChanged();
			}
		} else {
			if (requestCode == REQUEST_CONTEXT) {
				if (resultCode == Activity.RESULT_OK) {
					
					Log.i("control", "estamos en editar");
					Bundle bundle = data.getExtras();
					Servidor serv = new Servidor(bundle.getString("host"),
							bundle.getString("desc"),
							bundle.getBoolean("inicio"),
							idaux,bundle.getInt("color"));
					//Log.i("control", "nuevo nombre =" + serv.getIp());

					fachada.editServidor(datos, serv);
					adapter.notifyDataSetChanged();
				}
			}
		}
	}
	
	private class ListenerAdd implements View.OnClickListener {

		

		public void onClick(View arg0) {
			Intent i = new Intent(PestanaMainFav.this, Formulario.class);
			PestanaMainFav.this.startActivityForResult(i, REQUEST_FORMULARIO);

		}

	}
	
	private class ListenerListView implements OnItemClickListener {

		public void onItemClick(AdapterView<?> a, View v, int position, long id) {

			//TODO

		}
	}

	private class ArrayAdapterServidor extends ArrayAdapter<Servidor> {
		private Activity context;
		private ArrayList<Servidor> datos;

		public ArrayAdapterServidor(Activity context, ArrayList<Servidor> array) {
			super(context, R.layout.list_servers, array);
	
			this.context = context;
			datos = array;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {

			LayoutInflater inflater = context.getLayoutInflater();
			View item = inflater.inflate(R.layout.list_servers, null);
			
			 TextView ID = (TextView) item.findViewById(R.id.tv_listservers_id);
			Integer id = datos.get(position).getId();
			ID.setText(id.toString());
			ID.setBackgroundColor(datos.get(position).getColor());
			
			TextView ip = (TextView) item.findViewById(R.id.tv_listservers_ip2);
			ip.setText(datos.get(position).getIp());
			TextView desc = (TextView) item.findViewById(R.id.tv_listservers_desc2);
			desc.setText(datos.get(position).getDescripcion());
			
			TextView estado = (TextView) item.findViewById(R.id.tv_listservers_estado);
			
			
			ToggleButton boton = (ToggleButton) item.findViewById(R.id.tb_listservers_servicio);
			boton.setBackgroundResource(R.drawable.tb_icon);
			
			
			//boton.setChecked(datos.get(position).isInicio());
			
			if(datos.get(position).isInicio()){
				estado.setTextColor(Color.GREEN);
				estado.setText("autoarranque SI");
			}else{
				estado.setTextColor(Color.RED);
				estado.setText("autoarranque NO");
			}
			
			ID.setBackgroundColor(datos.get(position).getColor());
			
			boton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton boton1, boolean activado) {
					if(activado){
						iniciaServicio(datos.get(position));
					}else{
						paraServicio(datos.get(position));
					}
					//Toast.makeText(PestanaMainFav.this, "boton "+ position, Toast.LENGTH_SHORT).show();
				}
			});
			
			return (item);
			
		}

	}
	
	
}
