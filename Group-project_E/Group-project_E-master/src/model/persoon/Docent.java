package model.persoon;

public class Docent extends Persoon {

	private int docentNummer;

	public Docent(String voornaam, String tussenvoegsel, String achternaam, String wachtwoord, String gebruikersnaam,
				  int docentNummer) {
		super(voornaam, tussenvoegsel, achternaam, wachtwoord, gebruikersnaam);
		this.docentNummer = docentNummer;
	}

	@Override
	public String toString() {
		return "Docent{" +
			"persoon=" + super.toString() +
			", docentNummer=" + docentNummer +
			'}';
	}

	public int getDocentNummer() {
		return docentNummer;
	}

	public void setDocentNummer(int docentNummer) {
		this.docentNummer = docentNummer;
	}

}
