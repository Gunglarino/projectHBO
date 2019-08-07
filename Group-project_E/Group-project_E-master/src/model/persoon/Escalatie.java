package model.persoon;

import model.vak.Cursus;

public class Escalatie {

	private Student student;
	private Docent docent;
	private Cursus cursus;

	public Escalatie(Student student, Docent docent, Cursus cursus) {
		this.student = student;
		this.docent = docent;
		this.cursus = cursus;
	}

	@Override
	public String toString() {
		return "Escalatie{" +
			"student=" + student.getGebruikersnaam() +
			", docent=" + docent.getGebruikersnaam() +
			", cursus=" + cursus +
			'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Escalatie escalatie = (Escalatie) o;

		if (!student.equals(escalatie.student)) return false;
		if (!docent.equals(escalatie.docent)) return false;
		return cursus.equals(escalatie.cursus);
	}

	public Student getStudent() {
		return student;
	}

	public Docent getDocent() {
		return docent;
	}

	public Cursus getCursus() {
		return cursus;
	}
}
