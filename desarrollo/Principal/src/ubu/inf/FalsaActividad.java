package ubu.inf;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class FalsaActividad extends Activity {

	private ImageView google;
	private ImageView market;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.falsa);
		inicializa();
	}

	/**
	 * Función para iniciar todos los componentes de la aplicación.
	 */
	private void inicializa() {
		google = (ImageView) findViewById(R.id.iv_falsa_google);
		market = (ImageView) findViewById(R.id.iv_falsa_play);
		google.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent("android.intent.action.VIEW", Uri
						.parse("http://www.google.es"));
				startActivity(intent);

			}
		});
		market.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent("android.intent.action.VIEW", Uri
						.parse("market://search?q=AndroidServerStatus"));
				startActivity(intent);

			}
		});

		
	}

}
