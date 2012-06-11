package ubu.inf.gps.logica;

import java.util.ArrayList;
import java.util.Date;

import ubu.inf.gps.R;
import ubu.inf.gps.R.layout;
import ubu.inf.gps.accesodatos.FachadaCoordenadas;
import ubu.inf.gps.modelo.GMailSender;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

/**
 * @author      david
 * @uml.dependency   supplier="ubu.inf.gps.logica.Preferencias"
 * @uml.dependency   supplier="ubu.inf.gps.logica.ServicioGPS"
 */
public class LocalizadorGPSActivity extends Activity {
	/**
	 * id del dispositivo.
	 */
	String id_dispositivo;
	/**
	 * Sender para enviar emails.
	 * @uml.property  name="sender"
	 * @uml.associationEnd  
	 */
	private GMailSender sender;
	/**
	 * array de id.
	 */
	ArrayList<Integer> ID;
	/**
	 * array de longitudes.
	 */
	ArrayList<Double> longitud;
	/**
	 * array de latitudes.
	 */
	ArrayList<Double> latitud;
	/**
	 * array de fechas.
	 */
	ArrayList<Long> fecha;

	SharedPreferences pref;

	private  TextView tlongitud;
	private  TextView tlatitud;
	private  TextView tfecha;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// //implementación de la contraseña

		// ///
		setContentView(R.layout.activity);
		inicializar();
	}

	/**
	 * Función que inica todos los componentes de la interfaz.
	 */
	private void inicializar() {
		ID = new ArrayList<Integer>();
		longitud = new ArrayList<Double>();
		latitud = new ArrayList<Double>();
		fecha = new ArrayList<Long>();
		id_dispositivo = Secure.getString(
				getBaseContext().getContentResolver(), Secure.ANDROID_ID);

		pref = PreferenceManager.getDefaultSharedPreferences(this);
		  tlongitud = (TextView) findViewById(R.id.tv_gps_long);
		  tlatitud = (TextView) findViewById(R.id.tv_gps_lat);
		  tfecha = (TextView) findViewById(R.id.tv_gps_fecha);

		ImageView act = (ImageView) findViewById(R.id.iv_gps_actualizar);
		act.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				FachadaCoordenadas.getInstance(LocalizadorGPSActivity.this)
						.loadCoordenadas(1, ID, longitud, latitud, fecha);
				if(ID.size()>0){
				tlongitud.setText(longitud.get(longitud.size()-1).toString());
				tlatitud.setText(latitud.get(latitud.size()-1).toString());
				tfecha.setText(new Date(fecha.get(fecha.size()-1)).toLocaleString());
				}else{
					Toast.makeText(LocalizadorGPSActivity.this,
							"Sin coordenadas en la BD", Toast.LENGTH_SHORT)
							.show();
				}
			}
		});

		ImageView bmapa = (ImageView) findViewById(R.id.iv_gps_mapa);
		bmapa.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				try {
					if (latitud.size() > 0) {
						String punto = "geo:" + latitud.get(latitud.size()-1) + ","
								+ longitud.get(longitud.size()-1);
						Intent mapa = new Intent(
								android.content.Intent.ACTION_VIEW, Uri
										.parse(punto));
						startActivity(mapa);
					}else{
						Toast.makeText(LocalizadorGPSActivity.this,
								"No hay coordenadas cargadas", Toast.LENGTH_SHORT)
								.show();
					}
				} catch (Exception e) {
					Toast.makeText(LocalizadorGPSActivity.this,
							"Coordenadas incorrectas", Toast.LENGTH_SHORT)
							.show();
					e.printStackTrace();
				}
			}
		});

		ToggleButton trun = (ToggleButton) findViewById(R.id.tb_gps_on);
		trun.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked && !isMyServiceRunning()) {// si lo hemos puesto a
															// on y no estaba el
															// servicio
					// iniciamos todo
					boolean sms = pref.getBoolean("avisosms", false);
					boolean email = pref.getBoolean("avisoemail", false);
					Intent i = new Intent();

					i.setAction("ServicioGPS");
					startService(i);
					if (sms) {
						i.setAction("ServicioEnvioSMS");
						startService(i);
					}
					if (email) {
						i.setAction("ServicioEnvioEmail");
						startService(i);
					}

				}
				if (!isChecked && isMyServiceRunning()) {// si lo ponemos a off
															// y estaba el
															// servicio
					// paramos
					Intent i = new Intent();
					i.setAction("ServicioGPS");
					stopService(i);
					if (isMyServiceRunningSMS()) {
						i.setAction("ServicioEnvioSMS");
						stopService(i);
					}
					if (isMyServiceRunningEmail()) {
						i.setAction("ServicioEnvioEmail");
						stopService(i);
					}
				}

			}
		});

		// comprobamos si ya estaba arrancado el servicio
		if (isMyServiceRunning()) {
			trun.setChecked(true);
		} else {
			trun.setChecked(false);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menugps, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.ajustes:
			preferencias();
			break;
		case R.id.clear:
			clear();
			break;
		case R.id.sms:
			sendSMS();
			break;
		case R.id.email:
			sendEmail();
			break;

		default:
			break;
		}

		return true;
	}

	/**
	 * Función para borrar la base de datos de coordenadas.
	 */
	private void clear() {
		FachadaCoordenadas.getInstance(this).borraTabla();

	}

	/**
	 * Función que llama a la ventana para elegir las preferencias de la
	 * aplicación.
	 */
	private void preferencias() {
		Intent i = new Intent(this, Preferencias.class);
		startActivity(i);
	}

	/**
	 * Función para enviar un sms.
	 * 
	 * @param phoneNumber
	 *            numero de destino.
	 * @param message
	 *            mensaje.
	 */
	private void sendSMS() {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(this);
		ArrayList<Integer> ID = new ArrayList<Integer>();
		ArrayList<Double> longitud = new ArrayList<Double>();
		ArrayList<Double> latitud = new ArrayList<Double>();
		ArrayList<Long> fecha = new ArrayList<Long>();
		String telefono = pref.getString("avisosmsnumero", "666666666");
		FachadaCoordenadas.getInstance(this).loadCoordenadas(1, ID, longitud,
				latitud, fecha);
		if (ID.size() > 0) {
			String mensaje = "";
			Date aux = new Date(fecha.get(0));
			mensaje += "id:" + id_dispositivo;

			mensaje += " lat: " + latitud.get(0);
			mensaje += " long: " + longitud.get(0);
			mensaje += " fecha: " + aux.toLocaleString();

			PendingIntent pi = PendingIntent.getActivity(this, 0, new Intent(
					this, ServicioEnvioSMS.class), 0);
			SmsManager sms = SmsManager.getDefault();
			sms.sendTextMessage(telefono, null, mensaje, pi, null);
		}
	}

	/**
	 * Función para enviar un email de forma directa, permite decidir la
	 * cantidad de coordenadas.
	 */
	private void sendEmail() {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(this);
		final ArrayList<Integer> ID = new ArrayList<Integer>();
		final ArrayList<Double> longitud = new ArrayList<Double>();
		final ArrayList<Double> latitud = new ArrayList<Double>();
		final ArrayList<Long> fecha = new ArrayList<Long>();

		final String origen = pref.getString("avisoemailorigen",
				"origen@gmail.com");
		String pass = pref.getString("avisoemailcontrasena", "1111");
		final String destino = pref.getString("avisoemaildestino",
				"destino@gmail.com");
		sender = new GMailSender(origen, pass);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final EditText et = new EditText(this);
		builder.setTitle("Cantidad de coordenadas");
		builder.setView(et);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				Date aux;
				String mensaje = "";
				int can = Integer.parseInt(et.getText().toString());
				FachadaCoordenadas.getInstance(LocalizadorGPSActivity.this)
						.loadCoordenadas(can, ID, longitud, latitud, fecha);
				mensaje += "id:" + id_dispositivo;
				mensaje += '\n';
				for (int i = 0; i < ID.size(); ++i) {

					aux = new Date(fecha.get(i));

					mensaje += " lat: " + latitud.get(i);
					mensaje += " long: " + longitud.get(i);
					mensaje += " fecha: " + aux.toLocaleString();
					mensaje += '\n';
				}
				// enviar el email
				try {
					Log.i("gps", "enviamos email");
					sender.sendMail("Posición GPS", mensaje, origen, destino);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});
		builder.create();
		builder.show();

	}

	/**
	 * Función para saber si el ServicioGPS está funcionando.
	 * 
	 * @return true si está funcionando, false si no lo está haciendo.
	 */
	private boolean isMyServiceRunning() {
		ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager
				.getRunningServices(Integer.MAX_VALUE)) {
			if ("ubu.inf.gps.logica.ServicioGPS".equals(service.service
					.getClassName())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Función para saber si el ServicioEmail está funcionando.
	 * 
	 * @return true si está funcionando, false si no lo está haciendo.
	 */
	private boolean isMyServiceRunningEmail() {
		ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager
				.getRunningServices(Integer.MAX_VALUE)) {
			if ("ubu.inf.gps.logica.ServicioEnvioEmail".equals(service.service
					.getClassName())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Función para saber si el ServicioSMS está funcionando.
	 * 
	 * @return true si está funcionando, false si no lo está haciendo.
	 */
	private boolean isMyServiceRunningSMS() {
		ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager
				.getRunningServices(Integer.MAX_VALUE)) {
			if ("ubu.inf.gps.logica.ServicioEnvioSMS".equals(service.service
					.getClassName())) {
				return true;
			}
		}
		return false;
	}
	
}