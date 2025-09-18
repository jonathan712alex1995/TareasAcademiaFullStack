package xideral.test;

import org.junit.jupiter.api.Test;

import ejemplo.profesiones.Carpintero;
import ejemplo.profesiones.Electricista;
import ejemplo.profesiones.Plomero;
import ejemplo.trabajador.Trabajador;

import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;

public class TrabajadorTest {
	
	Trabajador trabajador;
	
	@Test
	@DisplayName("Prueba trabajador carpintero")
	public void testTrabajadorCarpintero() {
		trabajador = new Trabajador(new Carpintero());
		String actual = trabajador.trabaja();
		assertEquals("carpintero fabrica y repara muebles.", actual.toLowerCase());
	}
	
	@Test
	@DisplayName("Prueba trabajador electricista")
	public void testTrabajadorElectricista() {
		trabajador = new Trabajador(new Electricista());
		String actual = trabajador.trabaja();
		assertEquals("electricista instala y repara instalaciones eléctricas.", actual.toLowerCase());
	}
	
	@Test
	@DisplayName("Prueba para trabajador plomero")
	public void testTrabajadorPlomero() {
		trabajador = new Trabajador(new Plomero());
		String actual = trabajador.trabaja();
		assertEquals("plomero instala y repara sistemas de tuberías." , actual.toLowerCase());
	}
	
	
	
}
