package ubu.inf.gps.accesodatos;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
/**
 * Clase que extiende de SQLiteOpenHelper y que se encarga de crear la base de datos con las tablas necesarias.
 * 
 * @author David Herrero de la Peña
 * @author Jonatan Santos Barrios
 * 
 * @version 1.0
 * @see SQLiteOpenHelper
 * 
 */
public class CoordenadasSQLiteHelper extends SQLiteOpenHelper {
	/**
	 * Cadena para crear la tabla coordenadas.
	 */
	private String sqlCreate1 = "CREATE TABLE coordenadas (idCoordenada INTEGER PRIMARY KEY,longitud REAL,latitud REAL,fecha INTEGER)";
	
	/**
	 * Constructor para crear el Helper.
	 * @param context contexto desde el que se llama.
	 * @param name nombre de la base de datos.
	 * @param factory factory para crear la base de datos.
	 * @param version versión de la base de datos que se desea crear.
	 */

	public CoordenadasSQLiteHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}


	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.i("mssh", "vamos a crear la tabla de comandos");
		// TODO Auto-generated method stub
		db.execSQL(sqlCreate1);
		

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS coordenadas");
		

		// Se crea la nueva versión de la tabla
		db.execSQL(sqlCreate1);
		
	}

}
