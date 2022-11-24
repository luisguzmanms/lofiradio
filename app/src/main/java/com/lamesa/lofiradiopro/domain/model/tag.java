package com.lamesa.lofiradiopro.domain.model;

/**
 * Created by Luis Mesa on 08/07/2019.
 */

public class tag {
	private String title;
	private int gradient;

	public tag(String title, int gradient) {
		this.title = title;
		this.gradient = gradient;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getGradient() {
		return gradient;
	}

	public void setGradient(int gradient) {
		this.gradient = gradient;
	}
}
