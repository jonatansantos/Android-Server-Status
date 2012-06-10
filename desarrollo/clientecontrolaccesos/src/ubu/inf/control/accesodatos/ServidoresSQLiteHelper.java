package ubu.inf.control.accesodatos;

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
public class ServidoresSQLiteHelper extends SQLiteOpenHelper {
	/**
	 * Cadena para crear la tabla servidores.
	 */
	private String sqlCreate = "CREATE TABLE servidores (id INTEGER PRIMARY KEY,host TEXT,inicio INTEGER,color INTEGER,descripcion TEXT,puerto INTEGER)";
	/**
	 * Cadena para crear la tabla email.
	 */
	private String sqlCreate2 = "CREATE TABLE email (id INTEGER PRIMARY KEY,host TEXT,inicio INTEGER,color INTEGER,descripcion TEXT,puerto INTEGER)";
	/**
	 * Constructor para crear el Helper.
	 * @param context contexto desde el que se llama.
	 * @param name nombre de la base de datos.
	 * @param factory factory para crear la base de datos.
	 * @param version versión de la base de datos que se desea crear.
	 */
	public ServidoresSQLiteHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
	
		// TODO Auto-generated method stub
		db.execSQL(sqlCreate);
		db.execSQL(sqlCreate2);
		
	}


	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		 db.execSQL("DROP TABLE IF EXISTS servidores");
		 db.execSQL("DROP TABLE IF EXISTS email");
		 
	        //Se crea la nueva versión de la tabla
	        db.execSQL(sqlCreate);
	        db.execSQL(sqlCreate2);
		
	}

}
