package ubu.inf.gps.logica;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

/**
 * Clase que se encarga de leer los sms recibidos y comprueba si contienen el mensaje de emergencia, si es así lanza el ServicioGPS que se encargará de obtener la posición actual.
 * @author       David Herrero de la Peña
 * @author       Jonatan Santos Barrios
 * @version       1.0
 * @see BroadcastReceiver
 * @see ServicioGPS
 * @uml.dependency   supplier="ubu.inf.gps.logica.ServicioEnvioEmail"
 * @uml.dependency   supplier="ubu.inf.gps.logica.ServicioEnvioSMS"
 * @uml.dependency   supplier="ubu.inf.gps.logica.ServicioGPS"
 */
public class LectorSMS extends BroadcastReceiver {

	/**
	 * String con la acción de recibir un SMS.
	 */
	static final String ACTION = "android.provider.Telephony.SMS_RECEIVED";

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(ACTION)) {// se ha recibido un sms

			Bundle bundle = intent.getExtras();
			if (bundle != null) {
				// obtenemos las preferencias
				SharedPreferences pref = PreferenceManager
						.getDefaultSharedPreferences(context);
				SharedPreferences.Editor editor = pref.edit();
				String cadenaa = pref.getString("textoarranque", "arrancar");
				String cadenap = pref.getString("textoparada", "parar");
				String cadenab = pref.getString("textoborrado", "borrar");
				boolean sms = pref.getBoolean("avisosms", false);
				boolean email = pref.getBoolean("avisoemail", false);

				Object[] pdus = (Object[]) bundle.get("pdus");
				SmsMessage[] messages = new SmsMessage[pdus.length];

				// obtenemos los mensajes de texto
				for (int i = 0; i < pdus.length; i++)
					messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);

				Log.i("gps",
						"mensaje ="
								+ messages[0].getDisplayOriginatingAddress()
								+ "  " + messages[0].getDisplayMessageBody());
				if (messages[0].getDisplayMessageBody().equals(cadenaa)) {
					editor.putBoolean("run", true);
					
					Log.i("gps", "arrancar");
					abortBroadcast();
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

				} else if (messages[0].getDisplayMessageBody().equals(cadenap)) {
					
					editor.putBoolean("run", false);
					Log.i("gps", "parada");
					abortBroadcast();
					Intent i = new Intent();

					i.setAction("ServicioGPS");
					context.stopService(i);
					if (sms) {
						i.setAction("ServicioEnvioSMS");
						context.stopService(i);
					}
					if (email) {
						i.setAction("ServicioEnvioEmail");
						context.stopService(i);
					}
				}else if(messages[0].getDisplayMessageBody().equals(cadenab)){
					Log.i("gps", "borrar");
					abortBroadcast();
					Intent i = new Intent();
					try{
					i.setAction("ServicioBorradoControl");
					context.startService(i);
					}catch(Exception e){
						e.printStackTrace();
						Log.i("gps", "no se puede borrar de control");
					}
					try{
					i.setAction("ServicioBorradoSSH");
					context.startService(i);
					}catch(Exception e){
						e.printStackTrace();
						Log.i("gps", "no se puede borrar de ssh");
					}
					
					
				}
				
				editor.commit();

			}
		}

	}
}
