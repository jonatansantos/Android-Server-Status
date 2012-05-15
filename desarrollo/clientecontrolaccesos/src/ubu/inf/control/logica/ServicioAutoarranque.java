package ubu.inf.control.logica;

import java.util.ArrayList;

import ubu.inf.control.accesodatos.FachadaEmail;
import ubu.inf.control.accesodatos.FachadaServidores;
import ubu.inf.control.modelo.Servidor;
import ubu.inf.control.modelo.SingletonEmail;
import ubu.inf.control.modelo.SingletonServicios;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Servicio que se inicia al encender el tel�fono y comprueba si es necesario
 * lanzar los otros dos servicios que se encargan de comprobar si hay
 * notificaciones.
 * 
 * @author David Herrero
 * @author Jonatan Santos
 * 
 * @version 1.0
 * @see Service
 * @see ServicioEmail
 * @see ServicioSSH
 */
public class ServicioAutoarranque extends Service {

	private boolean ssh = false;
	private boolean email = false;
	private ArrayList<Servidor> datos;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();

		Log.i("control", "arrancado servicio autoarranque");
		datos = new ArrayList<Servidor>();
		// obtenemos las preferencias
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(this);
		ssh = pref.getBoolean("autoessh", false);
		email = pref.getBoolean("autoemail", false);
	}

	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);

		if (ssh) {
			datos = FachadaServidores.getInstance(this).loadServidores();
			for (int i = 0; i < datos.size(); ++i) {
				if (datos.get(i).isInicio())
					SingletonServicios.getConexion().getHosts()
							.add(datos.get(i));
			}

			Intent i = new Intent(this, ServicioSSH.class);
			startService(i);
		}
		if (email) {
			datos = FachadaEmail.getInstance(this).loadServidores();
			for (int i = 0; i < datos.size(); ++i) {
				if (datos.get(i).isInicio())
					SingletonEmail.getConexion().getHosts().add(datos.get(i));
			}
			Intent i = new Intent(this, ServicioEmail.class);
			startService(i);
		}
		// finalizamos este servicio ya que no nos interesa
		this.stopSelf();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}