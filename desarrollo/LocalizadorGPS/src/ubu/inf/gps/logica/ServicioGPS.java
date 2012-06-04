package ubu.inf.gps.logica;

import java.util.Date;

import ubu.inf.gps.accesodatos.FachadaCoordenadas;

import android.app.Service;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
/**Servicio que se encarga de obtener las posiciones del GPS y guardarlas en una base de datos interna.
 * Además puede borrar todos los datos de los servidores y notificaciones de las otras aplicaciones.
 * 
 * @author David Herrero de la Peña
 * @author Jonatan Santos Barrios
 * 
 * @version 1.0
 * 
 * @see Service
 */
public class ServicioGPS extends Service{
	boolean conectado = false;
	Thread hilo;
	LocationManager miLocationManager;
	Location miLocation;
	MiLocationListener miLocationListener;
	
	Location localizacion = null;

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.i("gps","onCreate ServicioGPS");
		miLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		
		if (miLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {//si hay gps
			Log.i("gps","gps conectado, nos suscribimos");
			conectado=true;
			miLocationListener = new MiLocationListener();
			//TODO demasiado poco tiempo (asa,tiempo minimo en milis,distancia minima en metros,)
			miLocationManager.requestLocationUpdates(
	                LocationManager.GPS_PROVIDER, 2*60*1000, 0, miLocationListener);			
		}else if(miLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
			
			Log.i("gps","3G conectado, nos suscribimos");
			conectado=true;
			miLocationListener = new MiLocationListener();
			//TODO demasiado poco tiempo (asa,tiempo minimo en milis,distancia minima en metros,)
			miLocationManager.requestLocationUpdates(
	                LocationManager.NETWORK_PROVIDER,  2*60*1000, 0, miLocationListener);
			
		}else{
			Log.e("gps", "servicioGPS,gps no activado, se cierra el servicio");
			this.stopSelf();
		}
	}
	
	@Override
	public void onDestroy(){
		
		if(conectado){
			Log.i("gps","servicioGps,eliminamos la suscripcion");
		miLocationManager.removeUpdates(miLocationListener);
		conectado=false;
		}
		super.onDestroy();
	}

	
	 private void setCurrentLocation(Location loc) {
		    Date fecha = new Date();
		   
	    	localizacion = loc;
	    	Log.i("gps","cambio de posicion long= "+loc.getLongitude()+" lati= "+loc.getLatitude()+" fecha:" + fecha.toLocaleString());
	    	FachadaCoordenadas.getInstance(this).insertCoordenadas(loc.getLongitude(), loc.getLatitude(), fecha.getTime());
	    }
	
	private class MiLocationListener implements LocationListener 
    {
        @Override
        public void onLocationChanged(Location loc) {
            if (loc != null) {
                
                setCurrentLocation(loc);
               
            }
        }

        @Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onStatusChanged(String provider, int status, 
            Bundle extras) {
            // TODO Auto-generated method stub
        }
    }
	
}
