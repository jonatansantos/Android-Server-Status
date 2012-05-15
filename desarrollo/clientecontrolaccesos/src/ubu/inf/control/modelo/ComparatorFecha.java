package ubu.inf.control.modelo;

import java.util.Comparator;
/**Clase que comprar entre dos Notificaciones por la fecha.
 * 
 * @author David Herrero de la Pe�a
 * @author Jonatan Santos Barrios
 * @version 1.0
 * 
 * @see Comparator
 * @see Notificacion
 *
 */
public class ComparatorFecha  implements Comparator<Notificacion> {

	@Override
	public int compare(Notificacion lhs, Notificacion rhs) {
		// TODO Auto-generated method stub
		return lhs.getFecha().compareTo(rhs.getFecha());
	}


}