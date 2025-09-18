package ejemplo.app;

import ejemplo.profesion.Profesion;
import ejemplo.profesiones.*;
import ejemplo.trabajador.Trabajador;

public class App {

	public static void main(String[] args) {
		
		Trabajador trabajador;
		
		Profesion emp1 = new Carpintero();
		trabajador = new Trabajador(emp1);
		System.out.println(trabajador.trabaja());
		
		Profesion emp2 = new Electricista();
		trabajador = new Trabajador(emp2);
		System.out.println(trabajador.trabaja());
		
		Profesion emp3 = new Plomero();
		trabajador= new Trabajador(emp3);
		System.out.println(trabajador.trabaja());
	}

}
