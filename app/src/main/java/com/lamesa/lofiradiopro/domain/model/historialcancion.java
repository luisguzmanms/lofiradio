package com.lamesa.lofiradiopro.domain.model;
/**
 * Created by Luis Mesa on 08/07/2019.
 */

public class historialcancion {
	private String Titulo;
	private String Fecha;
	private String LinkYT;


	public historialcancion(String titulo, String fecha, String linkYT) {
		this.Titulo = titulo;
		this.Fecha = fecha;
		this.LinkYT = linkYT;

	}

	public String getTitle() {
		return Titulo;
	}

	public void setTitulo(String titulo) {
		this.Titulo = titulo;
	}


	public String getFecha() {
		return Fecha;
	}

	public void setFecha(String fecha) {
		this.Fecha = fecha;
	}

	public String getLinkYT() {
		return LinkYT;
	}

	public void setLinkYT(String linkYT) {
		this.LinkYT = linkYT;
	}


}
