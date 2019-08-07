package model.persoon;

public class Student extends Persoon {

	private int studentNummer;
	private String groepId;

	public Student(String voornaam, String tussenvoegsel, String achternaam, String wachtwoord, String gebruikersnaam, int studentNummer) {
		super(voornaam, tussenvoegsel, achternaam, wachtwoord, gebruikersnaam);
		this.studentNummer = studentNummer;
		this.setGroepId("");
	}

	@Override
	public boolean equals(Object obj) {
		if (super.equals(obj) && obj instanceof Student) {
			Student s = (Student) obj;
			return this.studentNummer == s.studentNummer;
		} else {
			return false;
		}
	}

	public String getGroepId() {
		return this.groepId;
	}

	public void setGroepId(String groepId) {
		this.groepId = groepId;
	}

	public int getStudentNummer() {
		return this.studentNummer;
	}

}
