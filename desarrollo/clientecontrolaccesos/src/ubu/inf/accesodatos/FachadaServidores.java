package ubu.inf.accesodatos;

import java.util.ArrayList;

import ubu.inf.modelo.Servidor;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.util.Log;


public class FachadaServidores {
	Context context;
	ServidoresSQLiteHelper Helperservidores ;
	SQLiteDatabase DBservidores;
	private static FachadaServidores myFachada;
	
	
	private FachadaServidores(Context context){
		this.context=context;
		Log.i("mssh", "ya tenemos la fachada, ahora a por el helper");
		Helperservidores = new ServidoresSQLiteHelper(context,"DBservidores", null, 1);
		Log.i("mssh", "helper creado correctamente");
	
	}
	
	public static FachadaServidores getInstance(Context context){
		
		Log.i("mssh", "entramos en getInstance");
		if(myFachada==null){
			Log.i("mssh","my fachada vale null");
			myFachada = new FachadaServidores(context);
		}
		return myFachada;
	}
	
	public void closeFachada(){
		Log.i("mssh", "closeFachada,cerramos el helper");
		Helperservidores.close();
		myFachada=null;
	}
	
	public ArrayList<Servidor> loadServidores(){
		Log.i("mssh", "loadServidores,creamos el array");
		ArrayList<Servidor> lista = new ArrayList<Servidor>();
		Log.i("mssh", "pedimos al helper la base de datos para leer");
		DBservidores = Helperservidores.getReadableDatabase();
		
		Cursor c = DBservidores.rawQuery("SELECT * FROM servidores", null);
		if(c.moveToFirst()){
			Log.i("mssh", "loadServidores,hay datos");
			do {
				 String ip = c.getString(1);
				 
				 String descripcion=c.getString(4);
				 boolean inicio;
				 if(c.getInt(2)!=0){
					 inicio=true;
				 }else{
					 inicio=false;
				 }
				 
				 int id=c.getInt(0);
				 int color= c.getInt(3);
				 
				 Servidor serv = new Servidor(ip, descripcion, inicio, id, color);
				 lista.add(serv);
		         
		     } while(c.moveToNext());
			
		}else{
			Log.i("mssh", "loadServidores,no hay datos en la base de datos");
		}
		
		Log.i("mssh", "cerramos la base de datos");
		DBservidores.close();
		return lista;
	}
	
	public void insertServidor(ArrayList<Servidor> ant, Servidor serv){
		int id=0;
		
		String ip=serv.getIp();
		String desc=serv.getDescripcion();
		int color=serv.getColor();
		boolean inicio=serv.isInicio();
		int aux;
		if(inicio){
			aux=1;
		}else{
			aux=0;
		}
		 Log.i("mssh", "fachada,insert,conseguimos la bd en forma W");
		DBservidores = Helperservidores.getWritableDatabase();
		if(DBservidores!=null){
			 Log.i("mssh", "hemos conseguido la base de datos");
			 try{
			 DBservidores.execSQL("INSERT INTO servidores(id,host,inicio,color,descripcion) "+
			 "VALUES (NULL,'"+ip+"',"+aux+","+color+",'"+desc+"')");
			 Cursor c = DBservidores.rawQuery("SELECT last_insert_rowid();", null);
			 if(c.moveToFirst()){
				   id = c.getInt(0);
			}
			 serv.setId(id);
			 ant.add(serv);
			 }catch (SQLException e) {
				 Log.e("mssh", "error al introducir,clave duplicada? id = "+id );
			}
			 Log.i("mssh", "derramos la base de datos");
			 DBservidores.close();
			 
			
		}else{
		 Log.i("mssh", "no hemos conseguido la base, retornamos null");
		}
		
		
	}
	
	public void deleteServidor(ArrayList<Servidor> ant,int id){
		 Log.i("mssh", "vamos a borrar");
		String sql = "DELETE FROM servidores WHERE id="+id;
		DBservidores = Helperservidores.getWritableDatabase();
		if(DBservidores!=null){
			 Log.i("mssh", "hemos conseguido la base de datos");
			 try{
			 DBservidores.execSQL(sql);
			
			 for(int i=0;i<ant.size();++i){
				 if(ant.get(i).getId()==id){
					 ant.remove(i);
					 break;
				 }
			 }
			 
			 }catch (SQLException e) {
				 Log.e("mssh", "error al borrar,clave incorrecta? id = "+id );
			}
			 Log.i("mssh", "cerramos la base de datos");
			 DBservidores.close();			
		}else{
		 Log.i("mssh", "no hemos conseguido la base, retornamos null");
		}
	}

	public void editServidor(ArrayList<Servidor> ant, Servidor serv) {
		int id = serv.getId();
		String ip=serv.getIp();
		String desc=serv.getDescripcion();
		int color=serv.getColor();
		boolean inicio=serv.isInicio();
		int aux;
		if(inicio){
			aux=1;
		}else{
			aux=0;
		}
		
		String sql = "UPDATE servidores SET host='"+ip+"' ,color="+color+" ,inicio="+aux+" ,descripcion='"+desc+"' WHERE id="+id+";";
		
		DBservidores = Helperservidores.getWritableDatabase();
		if(DBservidores!=null){
			 Log.i("mssh", "hemos conseguido la base de datos");
			 try{
			 DBservidores.execSQL(sql);
			
			 for(int i=0;i<ant.size();++i){
				 if(ant.get(i).getId()==id){
					
					 ant.get(i).setDescripcion(desc);
					 ant.get(i).setIp(ip);
					 ant.get(i).setColor(color);
					 ant.get(i).setInicio(inicio);
					 break;
				 }
			 }
			 
			 }catch (SQLException e) {
				 Log.e("control", "error al update,clave incorrecta? id = "+id );
			}
			 Log.i("control", "cerramos la base de datos");
			 DBservidores.close();			
		}else{
		 Log.i("control", "no hemos conseguido la base, retornamos null");
		}
		
	}
	public void borraTabla(){
		String sql = "DROP TABLE IF EXISTS servidores";
		String sqlCreate = "CREATE TABLE servidores (id INTEGER PRIMARY KEY,host TEXT,inicio INTEGER,color INTEGER,descripcion TEXT)";
		DBservidores = Helperservidores.getWritableDatabase();
		if(DBservidores!=null){
			 Log.i("mssh", "hemos conseguido la base de datos");
			 try{
			 DBservidores.execSQL(sql);
			 Log.i("mssh", "hemos borrado, ahora a crearla" );
			 DBservidores.execSQL(sqlCreate);
			 Log.i("mssh", "se ha creado de nuevo la tabla" );
						 
			 }catch (SQLException e) {
				 Log.e("mssh", "error al borrar la tabla" );
			}
			 Log.i("mssh", "cerramos la base de datos");
			 DBservidores.close();			
		}else{
		 Log.i("mssh", "no hemos conseguido la base, retornamos null");
		}
		myFachada=null;
		Helperservidores = new ServidoresSQLiteHelper(context,"DBservidores", null, 1);
	}
}

