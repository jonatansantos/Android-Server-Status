package ubu.inf.gps.logica;

import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import ubu.inf.gps.accesodatos.FachadaCoordenadas;
import ubu.inf.gps.modelo.GMailSender;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;
import android.util.Log;

/**
 * Servicio que se encarga de enviar un email con las últimas posiciones de GPS guardadas en 
 * la base de datos cada x minutos.
 * 
 * @author David Herrero
 * @author Jonatan Santos
 * 
 * @version 1.0
 * @see Service
 */
public class ServicioEnvioEmail extends Service{
	/**
	 * id del dispositivo.
	 */
	String id_dispositivo;
	/**
	 * Dirección a la que enviar el email.
	 */
	String destino;
	/**
	 * Dirección desde la que enviar el email.
	 */
	String origen;
	/**
	 * Contraseña de la cuenta de origen.
	 */
	String pass;
	/**
	 * Minutos entre un email y el siguiente.
	 */
	int minutos;
	
	/**
	 * Sender para  enviar emails.
	 */
	private GMailSender sender ;
	
	Timer timer = new Timer();
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void onCreate() {
		
		id_dispositivo= Secure.getString(getBaseContext().getContentResolver(), Secure.ANDROID_ID);
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(this);
	    destino = pref.getString("avisoemaildestino", "destino@gmail.com");
	    origen = pref.getString("avisoemailorigen", "origen@gmail.com");
	    pass = pref.getString("avisoemailcontrasena", "1111");
		String aux1 = pref.getString("avisoemailtiempo", "30");
		minutos = Integer.parseInt(aux1);
		Log.i("gps", "enviamos email a "+destino+" cada "+minutos+ " minutos");
		try{
	    sender = new GMailSender(origen, pass);
		ejecutar();
		}catch(Exception e){
			Log.e("gps", "error al obtener el GMailSender");
		}
		
		//FachadaCoordenadas.getInstance(this).insertCoordenadas(555.1, 555.4, 55);
		
		
	}
	
	private void ejecutar(){
		timer.scheduleAtFixedRate(new TimerTask() {
			
			@Override
			public void run() {
				ArrayList<Integer> ID =new ArrayList<Integer>();
				ArrayList<Double> longitud =new ArrayList<Double>();
				ArrayList<Double> latitud =new ArrayList<Double>();
				ArrayList<Long> fecha =new ArrayList<Long>();
				Date aux ;
				String mensaje = "";
				mensaje+="id:"+id_dispositivo;	
				mensaje+='\n';
				FachadaCoordenadas.getInstance(ServicioEnvioEmail.this).loadCoordenadas(ID, longitud, latitud, fecha);
				for(int i =0 ;i< ID.size();++i){
				
				
				aux=new Date(fecha.get(i));
				
				
				mensaje+=" lat: "+latitud.get(i);	
				mensaje+=" long: "+longitud.get(i);	
				mensaje+=" fecha: "+aux.toGMTString();	
				mensaje+='\n';
				}
				//enviar el email
				try {
					Log.i("gps", "enviamos email");
					sender.sendMail("Posición GPS", mensaje, origen, destino);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Log.e("gps","No había conexión para enviar el email");
					e.printStackTrace();
				}
			}
			
		}, 0, minutos*60*1000);
	}
	
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (timer != null) {
			timer.cancel();
		}
	}
	

}
