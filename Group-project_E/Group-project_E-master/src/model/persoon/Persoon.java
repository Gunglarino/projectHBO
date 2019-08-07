package model.persoon;

public abstract class Persoon {

	private String voornaam, tussenvoegsel, achternaam, wachtwoord, gebruikersnaam;

	public Persoon(String voornaam, String tussenvoegsel, String achternaam, String wachtwoord, String gebruikersnaam) {
		this.voornaam = voornaam;
		this.tussenvoegsel = tussenvoegsel;
		this.achternaam = achternaam;
		this.wachtwoord = wachtwoord;
		this.gebruikersnaam = gebruikersnaam;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		boolean gelijk = false;
		if (obj instanceof Persoon) {
			Persoon p = (Persoon) obj;
			gelijk = this.voornaam.equals(p.voornaam) && this.tussenvoegsel.equals(p.tussenvoegsel)
					&& this.achternaam.equals(p.achternaam) && this.wachtwoord.equals(p.wachtwoord)
					&& this.gebruikersnaam.equals(p.gebruikersnaam);
		}
		return gelijk;
	}

	@Override
	public String toString() {
		return "Persoon{" +
			"voornaam='" + voornaam + '\'' +
			", tussenvoegsel='" + tussenvoegsel + '\'' +
			", achternaam='" + achternaam + '\'' +
			", gebruikersnaam='" + gebruikersnaam + '\'' +
			'}';
	}

	public String getVoornaam() {
		return this.voornaam;
	}

	protected String getTussenvoegsel() {
		return tussenvoegsel;
	}

	protected String getAchternaam() {
		return this.achternaam;
	}

	protected String getWachtwoord() {
		return this.wachtwoord;
	}

	public String getGebruikersnaam() {
		return this.gebruikersnaam;
	}

	public String getVolledigeAchternaam() {
		String volledigeAchternaam = "";
		if (this.tussenvoegsel != null && !this.tussenvoegsel.equals("") && this.tussenvoegsel.length() > 0) {
			volledigeAchternaam += this.tussenvoegsel + " ";
		}
		volledigeAchternaam += this.getAchternaam();
		return volledigeAchternaam;
	}

	public String getVolledigeNaam() {
		return this.voornaam + " " + getVolledigeAchternaam();
	}

	public boolean komtWachtwoordOvereen(String wachtwoord) {
		boolean status = false;
		if (this.getWachtwoord().equals(wachtwoord)) {
			status = true;
		}
		return status;
	}
}
