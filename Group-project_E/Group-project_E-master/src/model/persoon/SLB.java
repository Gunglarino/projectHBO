package model.persoon;

import model.vak.Cursus;

import java.util.ArrayList;

public class SLB extends Docent {

	private ArrayList<Student> studentenBegeleiden;
	private ArrayList<Escalatie> escalaties;

	public SLB(Docent docent) {
		super(docent.getVoornaam(), docent.getTussenvoegsel(), docent.getAchternaam(), docent.getWachtwoord(), docent.getGebruikersnaam(), docent.getDocentNummer());

		this.studentenBegeleiden = new ArrayList<>();
		this.escalaties = new ArrayList<>();
	}

	public SLB(Docent docent, ArrayList<Student> studentenBegeleiden) {
		this(docent);

		this.studentenBegeleiden = studentenBegeleiden;
	}

	@Override
	public String toString() {
		return "SLB{" +
			"docent=" + super.toString() +
			", studentenBegeleiden=" + studentenBegeleiden +
			", escalaties=" + escalaties +
			'}';
	}

	public boolean addEscalatie(Escalatie escalatie) {
		if (!escalaties.contains(escalatie)) {
			escalaties.add(escalatie);
			return true;
		}

		return false;
	}

	public void removeEscalatie(Student student, Docent docent, Cursus cursus) {
		escalaties
			.removeIf(e -> e.getStudent().equals(student) && e.getDocent().equals(docent) && e.getCursus().equals(cursus));
	}

	public ArrayList<Student> getStudentenBegeleiden() {
		return studentenBegeleiden;
	}

	public ArrayList<Escalatie> getEscalaties() {
		return escalaties;
	}

	public void setStudentenBegeleiden(ArrayList<Student> studentenBegeleiden) {
		this.studentenBegeleiden = studentenBegeleiden;
	}

}
