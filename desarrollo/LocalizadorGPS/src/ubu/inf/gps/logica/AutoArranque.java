package ubu.inf.gps.logica;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Clase que hereda de BroadcastReceiver, se encarga de iniciar un servicio concreto cuando ocurre un evento declarado en el manifest, en este caso cuando el teléfono termina de encenderse.
 * @author               David Herrero de la Peña
 * @author               Jonatan Santos Barrios
 * @version               1.0
 * @see BroadcastReceiver
 * @see ServicioAutoarranque
 * @uml.dependency   supplier="ubu.inf.gps.logica.ServicioEnvioEmail"
 * @uml.dependency   supplier="ubu.inf.gps.logica.ServicioEnvioSMS"
 * @uml.dependency   supplier="ubu.inf.gps.logica.ServicioGPS"
 */
public class AutoArranque extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
	
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		boolean seguir = pref.getBoolean("seguirencencido", false);
		boolean run = pref.getBoolean("run", false);
		boolean sms = pref.getBoolean("avisosms", false);
		boolean email = pref.getBoolean("avisoemail", false);
		if (run && seguir) {// iniciamos los servicios
			Intent i = new Intent();

			i.setAction("ServicioGPS");
			context.startService(i);
			if (sms) {
				i.setAction("ServicioEnvioSMS");
				context.startService(i);
			}
			if (email) {
				i.setAction("ServicioEnvioEmail");
				context.startService(i);
			}
		}

	}

}
