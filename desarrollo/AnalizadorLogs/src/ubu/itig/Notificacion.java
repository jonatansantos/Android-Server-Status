package ubu.itig;

import java.util.Calendar;

public class Notificacion {
	
	private Calendar fecha;
	private String equipo;
	private String mensaje;
	
	public Notificacion(Calendar fecha, String equipo, String mensaje){
		this.fecha = fecha;
		this.equipo = equipo;
		this.mensaje = mensaje;
	}

	public Calendar getFecha() {
		return fecha;
	}

	public String getEquipo() {
		return equipo;
	}

	public String getMensaje() {
		return mensaje;
	}	
	
}
