package model.klas;

import model.persoon.Student;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Klas {

	private String klasCode;
	private String naam;
	private ArrayList<Student> studenten = new ArrayList<>();

	public Klas(String klasCode, String naam) {
		this.klasCode = klasCode;
		this.naam = naam;
	}

	public String getKlasCode() {
		return klasCode;
	}

	public String getNaam() {
		return naam;
	}

	public List<Student> getStudenten() {
		return Collections.unmodifiableList(studenten);
	}

	public boolean bevatStudent(Student student) {
		return this.getStudenten().contains(student);
	}

	public void voegStudentToe(Student student) {
		if (!this.getStudenten().contains(student)) {
			this.studenten.add(student);
		}
	}

}
