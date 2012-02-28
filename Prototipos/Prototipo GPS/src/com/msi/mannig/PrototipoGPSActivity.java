package com.msi.mannig;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class PrototipoGPSActivity extends Activity implements Runnable{
	
	 private ProgressDialog pd;
	    
		LocationManager miLocationManager;
		Location miLocation;
		MiLocationListener miLocationListener;
		
		Location localizacion = null;
		
		TextView latitud;
		TextView longitud;
		
	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.main);
	        
	        latitud = (TextView) findViewById(R.id.latitud);
	        longitud = (TextView) findViewById(R.id.longitud);
	        
			Button buscar = (Button) findViewById(R.id.buscar);
			buscar.setOnClickListener(new View.OnClickListener() {

	            public void onClick(View view) {
	                writeSignalGPS();
	            }
	          
	        });
			
			Button mapear = (Button) findViewById(R.id.mapa);
			mapear.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if (localizacion!=null) {
			    		String punto = "geo:" + localizacion.getLatitude() + "," + localizacion.getLongitude();
			    		Intent mapa = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(punto));
			    		startActivity(mapa);
			    	}
				}
			});
	        
	    }
	    
	    private void setCurrentLocation(Location loc) {
	    	localizacion = loc;
	    }
	    
	    
	    private void writeSignalGPS() {
	    	
	    	DialogInterface.OnCancelListener dialogCancel = new DialogInterface.OnCancelListener() {

	            public void onCancel(DialogInterface dialog) {
	                Toast.makeText(getBaseContext(), 
	                        getResources().getString(R.string.senal_gps_no_encontrada), 
	                        Toast.LENGTH_LONG).show();
	                handler.sendEmptyMessage(0);
	            }
	          
	        };
	    	
			pd = ProgressDialog.show(this, this.getResources().getString(R.string.buscando), 
					this.getResources().getString(R.string.buscar_senal_gps), true, true, dialogCancel);
			
			Thread thread = new Thread(this);
			thread.start();

	    }

		@Override
		public void run() {
			
			miLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
			
			if (miLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				
				Looper.prepare();
				
				miLocationListener = new MiLocationListener();
				
				miLocationManager.requestLocationUpdates(
		                LocationManager.GPS_PROVIDER, 0, 0, miLocationListener);
				Looper.loop(); 
				Looper.myLooper().quit(); 
				
			} else {
				
	            Toast.makeText(getBaseContext(), 
	                    getResources().getString(R.string.senal_gps_no_encontrada), 
	                    Toast.LENGTH_LONG).show();
	            
			}

		}
	    
		private Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				pd.dismiss();
				miLocationManager.removeUpdates(miLocationListener);
		    	if (localizacion!=null) {
		    		latitud.setText("Latitud: " + localizacion.getLatitude());
		    		longitud.setText("Longitud: " + localizacion.getLongitude());
		    	}
			}
		};
		
	    private class MiLocationListener implements LocationListener 
	    {
	        @Override
	        public void onLocationChanged(Location loc) {
	            if (loc != null) {
	                Toast.makeText(getBaseContext(), 
	                    getResources().getString(R.string.senal_gps_encontrada), 
	                    Toast.LENGTH_LONG).show();
	                setCurrentLocation(loc);
	                handler.sendEmptyMessage(0);
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