package ubu.itig;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class PestanaMainFav extends Activity {
	
//	 final String[] datos =
//     	    new String[]{"Elem1","Elem2","Elem3","Elem4","Elem5"};
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pestanamainfav);
        ArrayList<Servidor> datos;
        
        //creamos "servidores" y los a�adirmos al array
        datos = new ArrayList<Servidor>();
        Servidor serv;
        for(Integer i=0;i<20;++i){
        	serv = new Servidor(i, i.toString(), i.toString(), i.toString(), i.toString(), i.toString());
        	datos.add(serv);
        }
        
     ArrayAdapter<Servidor> adapter = new ArrayAdapterServidor(this, datos);
       
        	 
//        	ArrayAdapter<String> adapter =
//        	    new ArrayAdapter<String>(this,
//        	        android.R.layout.simple_list_item_1, datos);
      ListView list = (ListView) findViewById(R.id.lv_servidores);
        list.setAdapter(adapter);
    }



private class ArrayAdapterServidor extends ArrayAdapter<Servidor>{
	private Activity context;
	private ArrayList<Servidor> datos;

	public ArrayAdapterServidor(Activity context,ArrayList<Servidor> array) {
		super(context, R.layout.list_servers,array);
		// TODO Auto-generated constructor stub
		this.context=context;
		datos = array;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		LayoutInflater inflater = context.getLayoutInflater();
        View item = inflater.inflate(R.layout.list_servers, null);
        
        TextView ID = (TextView)item.findViewById(R.id.tv_id);
        Integer id = datos.get(position).getId();
        ID.setText(id.toString());
 
        TextView lblIP = (TextView)item.findViewById(R.id.tv_IP2);
        lblIP.setText("aaaa192.168.1."+datos.get(position).getIp());
 
        TextView lblusuario = (TextView)item.findViewById(R.id.tv_usuario2);
        lblusuario.setText("usuario aaaaa"+datos.get(position).getUsuario());
 
        TextView descripcion = (TextView)item.findViewById(R.id.tv_descripcion2);
        descripcion.setText(datos.get(position).getDescripcion());
        
        return(item);
		// TODO Auto-generated method stub
		//return super.getView(position, convertView, parent);
	}
	

}
}