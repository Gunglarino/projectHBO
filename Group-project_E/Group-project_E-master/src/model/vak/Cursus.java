package model.vak;

import java.util.Objects;

public class Cursus {

	private String naam;
	private String cursusCode;

	public Cursus(String naam, String cursusCode) {
		this.naam = naam;
		this.cursusCode = cursusCode;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Cursus cursus = (Cursus) o;

		if (!Objects.equals(naam, cursus.naam)) return false;
		return Objects.equals(cursusCode, cursus.cursusCode);
	}

	@Override
	public String toString() {
		return "Cursus{" +
			"naam='" + naam + '\'' +
			", cursusCode='" + cursusCode + '\'' +
			'}';
	}

	public String getNaam() {
		return naam;
	}

	public String getCursusCode() {
		return cursusCode;
	}
}
