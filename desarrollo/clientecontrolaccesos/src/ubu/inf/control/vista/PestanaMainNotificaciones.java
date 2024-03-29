package ubu.inf.control.vista;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Stack;
import java.util.TreeSet;
import java.util.Vector;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import ubu.inf.control.R;
import ubu.inf.control.modelo.ComparatorFecha;
import ubu.inf.control.modelo.ComparatorID;
import ubu.inf.control.modelo.ComparatorTipo;
import ubu.inf.control.modelo.ComparatorUrgencia;
import ubu.inf.control.modelo.Notificacion;
import ubu.inf.control.modelo.Servidor;
import ubu.inf.control.modelo.SingletonEmail;
import ubu.inf.control.modelo.SingletonSSH;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Clase que implementa la funcionalidad de la pestana de notificaciones.
 * @author     David Herrero
 * @author     Jonatan Santos
 * @version     1.0
 * @uml.dependency   supplier="ubu.inf.control.vista.Preferencias"
 * @uml.dependency   supplier="ubu.inf.control.vista.Filtro"
 */
public class PestanaMainNotificaciones extends Activity {
	/**
	 * Clase que implementa el ArrayAdapter para las lista de notificaciones.
	 * 
	 * @author David Herrero de la Pe�a
	 * @author Jonatan Santos Barrio
	 * 
	 */
	private class ArrayAdapterNot extends ArrayAdapter<Notificacion> {
		private Activity context;
		private ArrayList<Notificacion> datos;

		public ArrayAdapterNot(Activity context, ArrayList<Notificacion> array) {
			super(context, R.layout.list_servers, array);

			this.context = context;
			datos = array;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			LayoutInflater inflater = context.getLayoutInflater();
			View item = inflater.inflate(R.layout.list_notificaciones, null);

			TextView ip = (TextView) item.findViewById(R.id.tv_listnot_ip);
			TextView id = (TextView) item.findViewById(R.id.tv_listnot_id);
			TextView fecha = (TextView) item
					.findViewById(R.id.tv_listnot_fecha);
			TextView hora = (TextView) item.findViewById(R.id.tv_listnot_hora);
			TextView urgencia = (TextView) item
					.findViewById(R.id.tv_listnot_urgencia);
			ImageView tipo = (ImageView) item.findViewById(R.id.imageView1);
			// ponemos la IP
			ip.setText(datos.get(position).getServ().getIp() + ":"
					+ datos.get(position).getServ().getPuerto());
			// ponemos el ID
			id.setText("" + datos.get(position).getServ().getId());
			// ponemos el color identificativo
			id.setBackgroundColor(datos.get(position).getServ().getColor());
			// ponemos el icono del ssh o del email
			if (datos.get(position).getTipo() == 0) {
				tipo.setBackgroundResource(R.drawable.ic_ssh);
			} else {
				tipo.setBackgroundResource(R.drawable.ic_email);
			}
			// construimos la cadena con la fecha
			String fechaaux = datos.get(position).getFecha().toLocaleString();

			String stringfecha = fechaaux.substring(0, 11);
			fecha.setText(stringfecha);
			// construimos la cadena con la hora

			String stringhora = fechaaux.substring(11);
			hora.setText(stringhora);
			// ponemos la urgencia
			switch (datos.get(position).getUrgencia()) {
			case 0:
				urgencia.setText("Grave");
				urgencia.setTextColor(Color.RED);
				break;
			case 1:
				urgencia.setText("Medio");
				urgencia.setTextColor(Color.YELLOW);
				break;
			case 2:
				urgencia.setText("Info");
				urgencia.setTextColor(Color.GREEN);
				break;

			default:
				break;
			}

			return (item);

		}
	}
	/**
	 * Clase que implementa el Listener del ListView.
	 * 
	 * @author David Herrero de la Pe�a
	 * @author Jonatan Santos Barrio
	 * 
	 */
	private class ListenerListView implements OnItemClickListener {

		public void onItemClick(AdapterView<?> a, View v, int position, long id) {
			AlertDialog.Builder builder = new AlertDialog.Builder(
					PestanaMainNotificaciones.this);

			builder.setTitle("Informacion");
			builder.setMessage(datos.get(position).getTexto());
			builder.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
					});
			builder.create();
			builder.show();

		}
	}
	/**
	 * Handler para manejar los mensajes del hilo as�ncrono.
	 * 
	 * @author David Herreo de la Pe�a
	 * @author Jonatan Santos Barrios
	 * 
	 */
	private class MyHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {

			adapter3.notifyDataSetChanged();
		}
	}

	/**
	 * Handler para manejar los mensajes del hilo as�ncrono cuando se borran
	 * notificaciones.
	 * 
	 * @author David Herreo de la Pe�a
	 * @author Jonatan Santos Barrios
	 * 
	 */
	private class MyHandlerBorrar extends Handler {
		@Override
		public void handleMessage(Message msg) {

			Notificacion auxiliar = (Notificacion) msg.obj;
			datos.remove(auxiliar);
			borrados.add(auxiliar);// borramos del array y lo
			// metemos en el otro array
			adapter3.notifyDataSetChanged();
		}
	}

	/**
	 * Handler para manejar los mensajes del hilo as�ncrono cuando se borran
	 * notificaciones.
	 * 
	 * @author David Herreo de la Pe�a
	 * @author Jonatan Santos Barrios
	 * 
	 */
	private class MyHandlerBorrarError extends Handler {
		@Override
		public void handleMessage(Message msg) {

			Toast.makeText(PestanaMainNotificaciones.this,
					"Error al borrar notificaciones", Toast.LENGTH_SHORT)
					.show();
		}
	}
	private boolean filtroenviado = false;

	private AdapterContextMenuInfo informacion;
	ProgressDialog dialog;
	/**
	 * Handler para manejar los eventos que crean los hilos as�ncronos.
	 */
	private Handler handler = new MyHandler();
	/**
	 * Handler para manejar los eventos que crean los hilos as�ncronos.
	 */
	private Handler handlerBorrar = new MyHandlerBorrar();
	/**
	 * Handler para manejar los eventos que crean los hilos as�ncronos.
	 */
	private Handler handlerBorrarError = new MyHandlerBorrarError();
	/**
	 * Barra de progreso de la descarga de notificaciones.
	 */
	private ProgressDialog barra;
	private static final String SOAPACTIONBORRAR = "http://notificador.serverstatus.itig.ubu/borrarNotificacion";
	private static final String METHODBORRAR = "borrarNotificacion";
	private static final String SOAPACTIONTODAS = "http://notificador.serverstatus.itig.ubu/obtenerNotificacionesAntiguas";
	private static final String METHODTODAS = "obtenerNotificacionesAntiguas";
	private static final String SOAPACTION = "http://notificador.serverstatus.itig.ubu/obtenerNotificacionesNuevas";

	private static final String METHOD = "obtenerNotificacionesNuevas";
	private static final String NAMESPACE = "http://notificador.serverstatus.itig.ubu";
	/**
	 * URL donde se encuentra el web service.
	 */
	private String URL = "";
	/**
	 * id del dispositivo
	 */

	String id_dispositivo;
	private static final int RESULT_FILTRO = 1;

	private ArrayList<Notificacion> borrados;
	/**
	 * Array con todas las notificaciones a mostrar.
	 */
	private ArrayList<Notificacion> datos;

	/**
	 * Pila donde se guardan arrays de notificaciones cuando se usan filtros.
	 */
	private Stack<ArrayList<Notificacion>> pila;
	/**
	 * Lista donde se encuentran todos los elementos.
	 */
	private ListView lista;

	/**
	 * Adapter para llenar la lista con los elementos.
	 * @uml.property   name="adapter3"
	 * @uml.associationEnd   
	 */
	private ArrayAdapterNot adapter3;

	private Spinner ordenTipo;

	private Spinner ordenAsc;

	private int criterio = 0;

	private int orden = 0;

	/**
	 * Funci�n que actualiza las notificaciones por si hay alguna nueva. Solo se
	 * puede hacer si no hay ning�n filtro puesto.
	 */
	private void actualizar() {

		if (pila.isEmpty()) {

			borrados.clear();
			// calculamos la cantidad de servidores
			int max = SingletonEmail.getConexion().getHosts().size()
					+ SingletonSSH.getConexion().getHosts().size();
			// creamos la barra de progreso y la mostramos
			barra.setMax(max);
			barra.show();
			Thread hilo = new Thread() {
				@Override
				public void run() {

					// obtenemos todos los servidores de los que hay que obtener
					// notificaciones
					ArrayList<Servidor> email = SingletonEmail.getConexion()
							.getHosts();
					ArrayList<Servidor> ssh = SingletonSSH.getConexion()
							.getHosts();

					Log.i("control", "hemos vaciado el array");
					// //////////////
					int i;
					for (i = 0; i < email.size(); ++i) {
						obtenerNotificaiones(email.get(i), 1);
						barra.setProgress(i + 1);
					}
					for (int j = 0; j < ssh.size(); ++j) {
						obtenerNotificaiones(ssh.get(j), 0);
						barra.setProgress(i + j + 1);
					}
					// //////////////
					Log.i("control", "notificamos que los datos han cambiado");
					// adapter3 = new
					// ArrayAdapterNot(PestanaMainNotificaciones.this, datos);
					// lista.setAdapter(adapter3);
					barra.dismiss();
					handler.sendEmptyMessage(0);

				}
			};
			hilo.start();

		} else {
			Toast.makeText(this,
					"No se puede actualizar con filtros aplicados",
					Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * Funci�n para borrar una notificacion de la base de datos del servidor.
	 * 
	 * @param not
	 *            Notificacion a borrar.
	 * @return true si ha podido borrar.
	 */
	private boolean borrarNotificacion(Notificacion not) {

		// obtenemos el SoapObject
		SoapObject request = new SoapObject(NAMESPACE, METHODBORRAR);

		// objeto de propiedades
		PropertyInfo FahrenheitProp = new PropertyInfo();
		// nombre
		FahrenheitProp.setName("idDispositivo");
		// valor que
		FahrenheitProp.setValue(id_dispositivo);
		// tipo de valor
		FahrenheitProp.setType(String.class);
		request.addProperty(FahrenheitProp);

		PropertyInfo FahrenheitProp1 = new PropertyInfo();
		FahrenheitProp1.setName("idNotificacion");
		// valor que
		FahrenheitProp1.setValue(not.getId());
		// tipo de valor
		FahrenheitProp1.setType(Integer.class);

		// a�adimos las propiedades a la pregunta
		request.addProperty(FahrenheitProp1);
		// creamos el objeto http para conectarnos con el webservice
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);

		envelope.dotNet = false;

		envelope.setOutputSoapObject(request);

		URL = "http://" + not.getServ().getIp() + ":"
				+ not.getServ().getPuerto()
				+ "/axis2/services/Notificador.NotificadorHttpSoap12Endpoint/";
		// aqui pondr�amos la Ip del servidor
		HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

		try

		{

			androidHttpTransport.call(SOAPACTIONBORRAR, envelope);
			Log.i("control", "hacemos la llamada");
			// utilizamos SoapPrimitive porque esperamos un valor simple
			Log.i("control", "intentamos obtener la respuesta");
			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
			Log.i("control",
					"hemos obtenido la respuesta " + response.toString());
			Integer resultado = Integer.parseInt(response.toString());
			if (resultado == 0) {// ha fallado al borrar en la base de datos
				return false;
			} else {
				return true;
			}

		} catch (Exception e) {

			e.printStackTrace();
			return false;

		}

	}

	private void borrarTodasNotificaciones() {
		barra.setMax(datos.size());
		barra.setTitle("Borrando...");
		barra.setMessage("Borrando notificaciones ...");
		barra.show();
		Thread hilo = new Thread() {
			@Override
			public void run() {
				int i = 1;
				ArrayList<Notificacion> borradosaqui = new ArrayList<Notificacion>();
				boolean borrado;
				// intentamos borrar de la base de datos externa
				int candatos = datos.size();
				for (Notificacion n : datos) {
					borrado = borrarNotificacion(n);
					if (borrado) {
						borradosaqui.add(n);
					}
					barra.setProgress(i);
					++i;
				}
				i = 0;
				int canborrados = borradosaqui.size();
				// para todos los que hemos conseguido borrar los eliminamos de
				// la lista
				for (Notificacion n2 : borradosaqui) {
					Message msg = new Message();
					msg.obj = n2;
					handlerBorrar.sendMessage(msg);
				}

				borradosaqui.clear();
				barra.setProgress(0);
				barra.dismiss();
				if (canborrados != candatos) {
					handlerBorrarError.sendEmptyMessage(0);
				}

			}
		};
		hilo.start();

	}

	/**
	 * Funci�n que descarga las notificaciones guardadas en los servidores. Solo
	 * se puede hacer si no hay ning�n filtro puesto.
	 */
	private void descargarGuardados() {

		if (pila.isEmpty()) {
			datos.clear();// vaciamos el array list con notificaciones

			// calculamos la cantidad de servidores
			int max = SingletonEmail.getConexion().getHosts().size()
					+ SingletonSSH.getConexion().getHosts().size();
			// creamos la barra de progreso y la mostramos
			barra.setMax(max);
			barra.show();
			Thread hilo = new Thread() {
				@Override
				public void run() {

					// obtenemos todos los servidores de los que hay que obtener
					// notificaciones
					ArrayList<Servidor> email = SingletonEmail.getConexion()
							.getHosts();
					ArrayList<Servidor> ssh = SingletonSSH.getConexion()
							.getHosts();

					Log.i("control", "hemos vaciado el array");
					// //////////////
					int i;
					for (i = 0; i < email.size(); ++i) {
						obtenerTodasNotificaiones(email.get(i), 1);
						barra.setProgress(i + 1);
					}
					for (int j = 0; j < ssh.size(); ++j) {
						obtenerTodasNotificaiones(ssh.get(j), 0);
						barra.setProgress(i + j + 1);
					}
					// //////////////
					Log.i("control", "notificamos que los datos han cambiado");

					barra.dismiss();
					handler.sendEmptyMessage(0);

				}
			};
			hilo.start();

		} else {
			Toast.makeText(
					this,
					"No se pueden descargar nuevas notificaciones si hay filtros aplicados,eliminelos.",
					Toast.LENGTH_LONG).show();
		}
		// un nuevo thread para descargar todas las notificaciones, y al final
		// cambiar el adapter.
	}

	/**
	 * Funci�n para filtrar los datos, los datos anteriores se guardan en una
	 * pila para poder volver hacia atr�s.
	 * 
	 * @param d
	 *            Intent con los datos que ha enviado Filtro.java
	 * @see Filtrov1
	 */
	private void filtrar(Intent d) {
		Log.i("control", "aplicamos el filtro");

		Intent data = d;
		pila.push(datos);
		ArrayList<Notificacion> aux = new ArrayList<Notificacion>(datos);
		Bundle bundle = data.getExtras();

		int id = bundle.getInt("id");// filtro de ID
		if (id != -1) {
			Log.i("control", "filtro id");
			for (int i = 0; i < aux.size(); ++i) {
				if (aux.get(i).getServ().getId() != id) {

					aux.remove(i);
					--i;
				}
			}
		}
		int tipo = bundle.getInt("tipo");// filtro de tipo , 0 ninguna
		--tipo;
		if (tipo != -1) {

			for (int i = 0; i < aux.size(); ++i) {
				if (aux.get(i).getTipo() != tipo) {
					Log.i("control", "filtro tipo");
					aux.remove(i);
					--i;

				}

			}
		}
		Date dia = new Date(bundle.getLong("desde"));// filtro dia desde
		if (dia.getTime() != -1L) {// si hay fecha desde
			Log.i("control", "filtro desde");
			for (int i = 0; i < aux.size(); ++i) {
				if (aux.get(i).getFecha().before(dia)) {
					aux.remove(i);
					--i;
				}
			}
		}
		dia = new Date(bundle.getLong("hasta"));// filtro dia hasta

		if (dia.getTime() != -1L) {// si hay fecha hasta
			Log.i("control", "filtro hasta");
			for (int i = 0; i < aux.size(); ++i) {
				if (aux.get(i).getFecha().after(dia)) {
					aux.remove(i);
					--i;
				}
			}
		}
		int urgencia = bundle.getInt("urgencia");// filtro de urgencia
		--urgencia;
		if (urgencia != -1) {
			Log.i("control", "filtro urgencia");
			for (int i = 0; i < aux.size(); ++i) {
				if (aux.get(i).getUrgencia() != urgencia) {
					aux.remove(i);
					--i;
				}
			}
		}

		datos = aux;

		adapter3 = null;
		adapter3 = new ArrayAdapterNot(this, datos);
		lista.setAdapter(adapter3);
	}

	/**
	 * Funci�n para obtener la referencia a todos los componentes y a�adir los
	 * datos iniciales.
	 */
	private void inicializa() {
		// creamos la barra de progreso
		barra = new ProgressDialog(this);
		barra.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		barra.setMessage("Descargando notificaciones...");
		barra.setCancelable(false);
		// creamos el array y la pila.
		datos = new ArrayList<Notificacion>();
		 //metemos datos de prueba
//		 Servidor sas = new Servidor("10.10.10.10", "ninguna", false, 1,
//		 12345,
//		 22);
//		 Notificacion aux = new Notificacion("1", new Date(), 1, 1, sas, 1);
//		 datos.add(aux);
//		Date d = new Date();
//		d.setDate(2);
//		 aux = new Notificacion("2", d, 2, 2, sas, 2);
//		 datos.add(aux);
//		 aux = new Notificacion("3", new Date(), 1, 2, sas, 3);
//		 datos.add(aux);
//		 aux = new Notificacion("4", new Date(), 2, 1, sas, 4);
//		 datos.add(aux);
//		 aux = new Notificacion("5", new Date(), 1, 1, sas, 5);
//		 datos.add(aux);
		 
		pila = new Stack<ArrayList<Notificacion>>();
		borrados = new ArrayList<Notificacion>();

		// metiendo los datos en los spiner
		ordenTipo = (Spinner) findViewById(R.id.sp_pestananot_criterio);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.tipo, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		ordenTipo.setAdapter(adapter);
		ordenAsc = (Spinner) findViewById(R.id.sp_pestananot_orden);
		ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(
				this, R.array.asc, android.R.layout.simple_spinner_item);
		adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		ordenAsc.setAdapter(adapter2);

		// boton ok
		Button ok = (Button) findViewById(R.id.bt_pestanamainnot_ok);
		// lista
		lista = (ListView) findViewById(R.id.lv_pestanamainnot_lista);
		registerForContextMenu(lista);
		lista.setOnItemClickListener(new ListenerListView());
		adapter3 = new ArrayAdapterNot(this, datos);
		lista.setAdapter(adapter3);
		// a�adimos los Listener
		ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				ordena();
				adapter3.notifyDataSetChanged();

			}
		});
		ordenTipo.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parentView,
					View selectedItem, int position, long id) {

				//
				criterio = position;

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				//

			}
		});
		ordenAsc.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parentView,
					View selectedItem, int position, long id) {

				orden = position;

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});

	}

	/**
	 * Funci�n que realiza la conexi�n con el servidor para obtener las nuevas
	 * notificaciones y las guarda en el array de datos.
	 * 
	 * @param s
	 *            Servidor al que conectarse
	 * @param tipo
	 *            tipo de notificaci�n , 0 ssh , 1 email
	 */
	private void obtenerNotificaiones(Servidor s, int tipo) {
		Log.i("control", "Obtenemos las notificaciones de " + s.getIp()
				+ "tipo " + tipo);
		SoapObject notificacion = null;
		Notificacion aux = null;
		// obtenemos el SoapObject
		SoapObject request = new SoapObject(NAMESPACE, METHOD);

		// objeto de propiedades
		PropertyInfo FahrenheitProp = new PropertyInfo();
		// nombre
		FahrenheitProp.setName("idDispositivo");
		// valor que
		FahrenheitProp.setValue(id_dispositivo);
		// tipo de valor
		FahrenheitProp.setType(String.class);
		request.addProperty(FahrenheitProp);

		PropertyInfo FahrenheitProp1 = new PropertyInfo();
		FahrenheitProp1.setName("tipoMensaje");
		// valor que
		FahrenheitProp1.setValue(tipo);
		// tipo de valor
		FahrenheitProp1.setType(Integer.class);

		// a�adimos las propiedades a la pregunta
		request.addProperty(FahrenheitProp1);
		// creamos el objeto http para conectarnos con el webservice
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);

		envelope.dotNet = false;

		envelope.setOutputSoapObject(request);

		URL = "http://" + s.getIp() + ":" + s.getPuerto()
				+ "/axis2/services/Notificador.NotificadorHttpSoap12Endpoint/";
		// aqui pondr�amos la Ip del servidor
		HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

		try

		{
			// hacer la primera petici�n para ver is hay notificaciones(con tipo
			// y
			// dispositivo), si hay enviar la notificaci�n,
			// en el intent enviar cantidad y el servidor.
			androidHttpTransport.call(SOAPACTION, envelope);
			Log.i("control", "hacemos la llamada");
			// utilizamos SoapPrimitive porque esperamos un valor simple
			Log.i("control", "intentamos obtener la respuesta");
			Vector response = (Vector) envelope.getResponse();
			Log.i("control", "hemos obtenido la respuesta");
			if (response != null) {
				for (int i = 0; i < response.size(); ++i) {
					Log.i("control", "creamos las notificaciones");
					notificacion = (SoapObject) response.get(i);

					SoapPrimitive aux2;
					aux2 = (SoapPrimitive) notificacion.getProperty(0);
					Integer id = Integer.parseInt(aux2.toString());
					if (id == -1) {// yo na hay mas notificaciones
						break;
					}

					Log.i("control", notificacion.toString());
					SoapPrimitive mensaje = (SoapPrimitive) notificacion
							.getProperty(4);
					aux2 = (SoapPrimitive) notificacion.getProperty(1);
					Long fecha = Long.parseLong(aux2.toString());

					aux2 = (SoapPrimitive) notificacion.getProperty(2);
					Integer tipos = Integer.parseInt(aux2.toString());
					aux2 = (SoapPrimitive) notificacion.getProperty(3);
					Integer urgencia = Integer.parseInt(aux2.toString());

					aux = new Notificacion(mensaje.toString(), new Date(fecha),
							tipos, urgencia, s, id);

					datos.add(aux);
				}// for
			}// if

		} catch (Exception e) {

			e.printStackTrace();

		}
	}

	/**
	 * Funci�n que realiza la conexi�n con el servidor para obtener las
	 * notificaciones guardadas.
	 * 
	 * @param s
	 *            Servidor al que conectarse
	 * @param tipo
	 *            tipo de notificaci�n , 0 ssh , 1 email
	 */
	private void obtenerTodasNotificaiones(Servidor s, int tipo) {
		Log.i("control", "Obtenemos las notificaciones de " + s.getIp()
				+ "tipo " + tipo);
		SoapObject notificacion = null;
		Notificacion aux = null;
		// obtenemos el SoapObject
		SoapObject request = new SoapObject(NAMESPACE, METHODTODAS);

		// objeto de propiedades
		PropertyInfo FahrenheitProp = new PropertyInfo();
		// nombre
		FahrenheitProp.setName("idDispositivo");
		// valor que
		FahrenheitProp.setValue(id_dispositivo);
		// tipo de valor
		FahrenheitProp.setType(String.class);
		request.addProperty(FahrenheitProp);

		PropertyInfo FahrenheitProp1 = new PropertyInfo();
		FahrenheitProp1.setName("tipoMensaje");
		// valor que
		FahrenheitProp1.setValue(tipo);
		// tipo de valor
		FahrenheitProp1.setType(Integer.class);

		// a�adimos las propiedades a la pregunta
		request.addProperty(FahrenheitProp1);
		// creamos el objeto http para conectarnos con el webservice
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);

		envelope.dotNet = false;

		envelope.setOutputSoapObject(request);

		URL = "http://" + s.getIp() + ":" + s.getPuerto()
				+ "/axis2/services/Notificador.NotificadorHttpSoap12Endpoint/";
		// aqui pondr�amos la Ip del servidor
		HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

		try

		{
			// hacer la primera petici�n para ver is hay notificaciones(con tipo
			// y
			// dispositivo), si hay enviar la notificaci�n,
			// en el intent enviar cantidad y el servidor.
			androidHttpTransport.call(SOAPACTIONTODAS, envelope);
			Log.i("control", "hacemos la llamada");
			// utilizamos SoapPrimitive porque esperamos un valor simple
			Log.i("control", "intentamos obtener la respuesta");
			Vector response = (Vector) envelope.getResponse();
			Log.i("control", "hemos obtenido la respuesta");

			for (int i = 0; i < response.size(); ++i) {
				Log.i("control", "creamos las notificaciones");
				notificacion = (SoapObject) response.get(i);

				SoapPrimitive aux2;
				aux2 = (SoapPrimitive) notificacion.getProperty(0);
				Integer id = Integer.parseInt(aux2.toString());
				if (id == -1) {// yo na hay mas notificaciones
					break;
				}

				Log.i("control", notificacion.toString());
				SoapPrimitive mensaje = (SoapPrimitive) notificacion
						.getProperty(4);
				aux2 = (SoapPrimitive) notificacion.getProperty(1);
				Long fecha = Long.parseLong(aux2.toString());
				Log.e("control", "long : " + fecha);
				aux2 = (SoapPrimitive) notificacion.getProperty(2);
				Integer tipos = Integer.parseInt(aux2.toString());
				aux2 = (SoapPrimitive) notificacion.getProperty(3);
				Integer urgencia = Integer.parseInt(aux2.toString());

				aux = new Notificacion(mensaje.toString(), new Date(fecha),
						tipos, urgencia, s, id);

				datos.add(aux);
			}

		} catch (Exception e) {

			e.printStackTrace();

		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case RESULT_FILTRO:
			if (resultCode == RESULT_OK) {
				Log.i("control", "result");
				filtrar(data);

			}
			break;

		default:
			break;
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {

		informacion = (AdapterContextMenuInfo) item.getMenuInfo();

		switch (item.getItemId()) {
		case R.id.CtxNotBorrar:
			final ProgressDialog pd = ProgressDialog.show(this, "Borrar",
					"Borrando ...", true, false);
			new Thread(new Runnable() {

				@Override
				public void run() {
					Notificacion n2 = datos.get(informacion.position);
					if (borrarNotificacion(n2)) {// si hemos podido borrar
						Message msg = new Message();
						msg.obj = n2;
						handlerBorrar.sendMessage(msg);
					} else {
						handlerBorrarError.sendEmptyMessage(0);
					}
					pd.dismiss();
				}
			}).start();

			return true;

		default:
			return super.onContextItemSelected(item);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.pestanamainnot);
		id_dispositivo = Secure.getString(
				getBaseContext().getContentResolver(), Secure.ANDROID_ID);
		inicializa();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		MenuInflater inflater = getMenuInflater();

		menu.setHeaderTitle("Opciones");

		inflater.inflate(R.menu.menu_not, menu);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menunot, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.filtro:
			// showDialog(DIALOG_FILTRO);
			Log.i("control", "vamos a filtrar");
			filtroenviado=true;
			Intent i = new Intent(this, Filtro.class);
			startActivityForResult(i, RESULT_FILTRO);
			break;
		case R.id.re_filtro:
			// showDialog(DIALOG_FILTRO);
			quitarFiltro();
			break;
		case R.id.ajustes_not:
			// showDialog(DIALOG_FILTRO);
			preferencias();
			break;
		case R.id.actualizar_not:
			// showDialog(DIALOG_FILTRO);
			actualizar();
			break;
		case R.id.descargar_not:
			// showDialog(DIALOG_FILTRO);
			descargarGuardados();
			break;
		case R.id.borrar_not:
			// showDialog(DIALOG_FILTRO);
			borrarTodasNotificaciones();
			break;

		default:
			break;
		}
		return true;
	}

	@Override
	protected void onStart() {

		// aqui se podr�a hacer la carga de las notificaciones, ya que si se
		// hace en
		// onResume se har�a demasiadas veces
		if (getIntent().getExtras() != null) {
			if(!filtroenviado){
				filtroenviado=false;
				Log.i("control", "han llamado a la pesta�a desde una notificacion ");
				actualizar();

			}else{
				Log.i("control", "han llamado a la pesta�a desde filtro ");
				filtroenviado=false;
			}
			
			
		}
		Log.i("control", "onStart pestana not");

		super.onStart();
	}

	/**
	 * Funci�n para ordenar las Notificaciones mediantes distintos criterios.
	 */
	private void ordena() {
		switch (criterio) {
		case 1:// ID
			if (orden == 0) {// asc
				Collections.sort(datos, new ComparatorID());
			} else {
				Collections.sort(datos,
						Collections.reverseOrder(new ComparatorID()));
			}
			break;
		case 2:// TIPO
			if (orden == 0) {// asc
				Collections.sort(datos, new ComparatorTipo());

			} else {
				Collections.sort(datos,
						Collections.reverseOrder(new ComparatorTipo()));
			}
			break;
		case 3:// FECHA
			if (orden == 0) {// asc
				Collections.sort(datos, new ComparatorFecha());
			} else {
				Collections.sort(datos,
						Collections.reverseOrder(new ComparatorFecha()));
			}
			break;
		case 4:// URGENCIA
			if (orden == 0) {// asc
				Collections.sort(datos, new ComparatorUrgencia());
			} else {
				Collections.sort(datos,
						Collections.reverseOrder(new ComparatorUrgencia()));
			}
			break;

		default:
			break;
		}
		adapter3.notifyDataSetChanged();
	}

	/**
	 * Funci�n que llama a la ventana para elegir las preferencias de la
	 * aplicaci�n.
	 */
	private void preferencias() {
		Intent i = new Intent(this, Preferencias.class);
		startActivity(i);
	}

	/**
	 * Funci�n para quitar el filtro y colocar los datos en un estado anterior.
	 */
	private void quitarFiltro() {
		if (!pila.isEmpty()) {
			datos = pila.pop();
			for (Notificacion n : borrados) {// por cada elemento en borrados lo
												// eliminamos del nuevo array
				datos.remove(n);
			}
			adapter3 = null;
			adapter3 = new ArrayAdapterNot(this, datos);
			lista.setAdapter(adapter3);
			Toast.makeText(this, "Quedan " + pila.size() + " filtros",
					Toast.LENGTH_SHORT).show();
			if (pila.isEmpty())
				borrados.clear();

		} else {// se ha vaciado la lista, vaciamos borrados

			Toast.makeText(this, "No hay filtros aplicados", Toast.LENGTH_SHORT)
					.show();
		}
	}

}
