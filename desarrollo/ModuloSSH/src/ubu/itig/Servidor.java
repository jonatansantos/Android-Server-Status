package ubu.itig;

public class Servidor {
	private int id;
	private String ip;
	private String usuario;
	private String contrase�a;
	private String puerto;
	private String descripcion;
	
	

	public Servidor(int id, String ip, String usuario, String contrase�a,
			String puerto, String descripcion) {
		super();
		this.id = id;
		this.ip = ip;
		this.usuario = usuario;
		this.contrase�a = contrase�a;
		this.puerto = puerto;
		this.descripcion = descripcion;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getUsuario() {
		return usuario;
	}
	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}
	public String getContrase�a() {
		return contrase�a;
	}
	public void setContrase�a(String contrase�a) {
		this.contrase�a = contrase�a;
	}
	public String getPuerto() {
		return puerto;
	}
	public void setPuerto(String puerto) {
		this.puerto = puerto;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	

}