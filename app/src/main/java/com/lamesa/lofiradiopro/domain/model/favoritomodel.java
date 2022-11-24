package com.lamesa.lofiradiopro.domain.model;

/**
 * Created by Luis Mesa on 08/07/2019.
 */

public class favoritomodel {


	private String IdFavorito;
	private String LinkYT;
	private String NombreCancionSonando;


	public favoritomodel() {
	}


	public favoritomodel(String IdFavorito, String LinkYT, String NombreCancionSonando) {

		this.IdFavorito = IdFavorito;
		this.LinkYT = LinkYT;
		this.NombreCancionSonando = NombreCancionSonando;


	}


	public String getIdFavorito() {
		return IdFavorito;
	}

	public void setIdFavorito(String IdFavorito) {
		IdFavorito = IdFavorito;
	}


	public String getLinkYT() {
		return LinkYT;
	}

	public void setLinkYT(String LinkYT) {
		LinkYT = LinkYT;
	}


	public String getNombreCancionSonando() {
		return NombreCancionSonando;
	}

	public void setNombreCancionSonando(String NombreCancionSonando) {
		NombreCancionSonando = NombreCancionSonando;
	}


}




