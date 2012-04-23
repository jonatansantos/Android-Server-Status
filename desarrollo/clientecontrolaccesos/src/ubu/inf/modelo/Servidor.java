package ubu.inf.modelo;

import android.graphics.Color;

public class Servidor {
	
	private String ip;
	private String descripcion;
	private boolean inicio;
	private int id;
	private int color;
	public Servidor(String ip, String descripcion, boolean inicio, int id,
			int color) {
		super();
		this.ip = ip;
		this.descripcion = descripcion;
		this.inicio = inicio;
		this.id = id;
		this.color = color;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public boolean isInicio() {
		return inicio;
	}
	public void setInicio(boolean inicio) {
		this.inicio = inicio;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getColor() {
		return color;
	}
	public void setColor(int color) {
		this.color = color;
	}
	
	

}
