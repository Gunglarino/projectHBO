package model.vak;

import model.klas.Klas;
import model.persoon.Docent;
import model.persoon.Student;
import model.presentie.Presentie;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Objects;

public class Les {

	private Cursus cursus;
	private LocalDate startDatum;
	private LocalTime startTijd;
	private LocalDate eindDatum;
	private LocalTime eindTijd;
	private String werkvorm;
	private ArrayList<Docent> docenten;
	private String lokaal;
	private ArrayList<Klas> groepen;
	private int grootte;
	private ArrayList<Presentie> presenties;

	public Les(Cursus cursus, LocalDate startDatum, LocalTime startTijd, LocalDate eindDatum, LocalTime eindTijd, String werkvorm, ArrayList<Docent> docenten, String lokaal, ArrayList<Klas> groepen, int grootte) {
		this.cursus = cursus;
		this.startDatum = startDatum;
		this.startTijd = startTijd;
		this.eindDatum = eindDatum;
		this.eindTijd = eindTijd;
		this.werkvorm = werkvorm;
		this.docenten = docenten;
		this.lokaal = lokaal;
		this.groepen = groepen;
		this.grootte = grootte;
		this.presenties = new ArrayList<>();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Les les = (Les) o;

		if (grootte != les.grootte) return false;
		if (!Objects.equals(cursus, les.cursus)) return false;
		if (!Objects.equals(startDatum, les.startDatum)) return false;
		if (!Objects.equals(startTijd, les.startTijd)) return false;
		if (!Objects.equals(eindDatum, les.eindDatum)) return false;
		if (!Objects.equals(eindTijd, les.eindTijd)) return false;
		if (!Objects.equals(werkvorm, les.werkvorm)) return false;
		if (!Objects.equals(docenten, les.docenten)) return false;
		if (!Objects.equals(lokaal, les.lokaal)) return false;
		if (!Objects.equals(presenties, les.presenties)) return false;
		return Objects.equals(groepen, les.groepen);
	}

	@Override
	public String toString() {
		return "Les{" +
			"cursus=" + cursus +
			", startDatum=" + startDatum +
			", startTijd=" + startTijd +
			", eindDatum=" + eindDatum +
			", eindTijd=" + eindTijd +
			", werkvorm='" + werkvorm + '\'' +
			", docenten=" + docenten +
			", lokaal='" + lokaal + '\'' +
			", groepen=" + groepen +
			", grootte=" + grootte +
			", presenties=" + presenties +
			'}';
	}

	public Cursus getCursus() {
		return cursus;
	}

	public LocalDate getStartDatum() {
		return startDatum;
	}

	public LocalTime getStartTijd() {
		return startTijd;
	}

	public LocalDate getEindDatum() {
		return eindDatum;
	}

	public LocalTime getEindTijd() {
		return eindTijd;
	}

	public String getWerkvorm() {
		return werkvorm;
	}

	public ArrayList<Docent> getDocenten() {
		return docenten;
	}

	public String getLokaal() {
		return lokaal;
	}

	public ArrayList<Klas> getGroepen() {
		return groepen;
	}

	public int getGrootte() {
		return grootte;
	}

	public ArrayList<Presentie> getPresenties() {
		return presenties;
	}

	public Presentie getPresentie(Student student) {
		return presenties.stream()
			.filter(p -> p.getStudent().equals(student))
			.findFirst()
			.orElse(null);
	}

	public void setPresenties(ArrayList<Presentie> presenties) {
		this.presenties = presenties;
	}
}
