package ejemplo.trabajador;

import ejemplo.profesion.Profesion;

public class Trabajador {
	private Profesion trabajo;
	
	public Trabajador(Profesion profesion){
		this.trabajo = profesion;
	}
	
	public String trabaja() {
		return this.trabajo.funcion();
	}
}
