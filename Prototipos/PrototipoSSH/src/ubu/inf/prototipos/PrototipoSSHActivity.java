package ubu.inf.prototipos;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;




import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ViewSwitcher;

public class PrototipoSSHActivity extends Activity {
	 private static final String user = "david";
	   private static  final String host = "77.209.71.14";
	   private  static final Integer port = 22;
	   private static final String pass = "david";
	/** Called when the activity is first created. */
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		Class clase = null;
		try {
			clase = Class.forName("ubu.inf.prototipos.Ssh");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Intent intent = new Intent(PrototipoSSHActivity.this, clase);
		startActivity(intent);
//		EditText t = (EditText) findViewById(R.id.campo);
//		try{
//		   
//		 
//		    	
//		        //System.out.println("----- INICIO");
//		    t.setText("INICIO");
//		 
//		    
//		        JSch jsch = new JSch();
//		        
//		        t.setText(t.getText().toString()+" creamos sesion");
//		        
//		        Session session = jsch.getSession(user, host, port);
//		        
//		        t.setText(t.getText().toString()+" user info");
//		        
//		        UserInfo ui = new SUserInfo(pass, null);
//		        
//		        t.setText(t.getText().toString()+" añadimos a la sesion");
//		        
//		        session.setUserInfo(ui);
//		        
//		        session.setPassword(pass);
//		        
//		        t.setText(t.getText().toString()+" conectamos");
//		        
//		        session.connect();
//		        t.setText(t.getText().toString()+" abrimos canal");
//		        ChannelExec channelExec = (ChannelExec)session.openChannel("exec");
//		 
//		        InputStream in = channelExec.getInputStream();
//		 
//		        channelExec.setCommand("ls -l");
//		        channelExec.connect();
//		 
//		        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
//		        String linea = null;
//		        int index = 0;
//		 
//		        while ((linea = reader.readLine()) != null) {
//		          //  System.out.println(++index + " : " + linea);
//		        	t.setText(t.getText().toString() + index +" : "+ linea);
//		        	index++;
//		        }
//		 
//		        channelExec.disconnect();
//		        session.disconnect();
//		        t.setText(t.getText().toString()+"FIN");
//		        //System.out.println("------ FIN");
//		}catch (Exception e) {
//			// TODO: handle exception
//			t.setText(t.getText().toString()+"ERROR");
//		}

	
	
	

	}
	}
