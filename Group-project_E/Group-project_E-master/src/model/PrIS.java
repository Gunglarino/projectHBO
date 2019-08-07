package model;

import model.klas.Klas;
import model.persoon.Docent;
import model.persoon.Persoon;
import model.persoon.SLB;
import model.persoon.Student;
import model.vak.Cursus;
import model.vak.Les;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

public class PrIS {

	private ArrayList<Docent> docenten;
	private ArrayList<Student> studenten;
	private ArrayList<Klas> klassen;
	private ArrayList<Cursus> cursussen;
	private ArrayList<Les> lessen;

	/**
	 * De constructor maakt een set met standaard-data aan. Deze data moet nog
	 * uitgebreidt worden met rooster gegevens die uit een bestand worden ingelezen,
	 * maar dat is geen onderdeel van deze demo-applicatie!
	 * <p>
	 * De klasse PrIS (PresentieInformatieSysteem) heeft nu een meervoudige
	 * associatie met de klassen Docent, Student, Vakken en Klassen Uiteraard kan
	 * dit nog veel verder uitgebreid en aangepast worden!
	 * <p>
	 * De klasse fungeert min of meer als ingangspunt voor het domeinmodel. Op dit
	 * moment zijn de volgende methoden aanroepbaar:
	 * <p>
	 * String login(String gebruikersnaam, String wachtwoord) Docent
	 * getDocent(String gebruikersnaam) Student getStudent(String gebruikersnaam)
	 * ArrayList<Student> getStudentenVanKlas(String klasCode)
	 * <p>
	 * Methode login geeft de rol van de gebruiker die probeert in te loggen, dat
	 * kan 'student', 'docent' of 'undefined' zijn! Die informatie kan gebruikt
	 * worden om in de Polymer-GUI te bepalen wat het volgende scherm is dat getoond
	 * moet worden.
	 */
	public PrIS() {
		docenten = new ArrayList<>();
		studenten = new ArrayList<>();
		klassen = new ArrayList<>();
		cursussen = new ArrayList<>();
		lessen = new ArrayList<>();

		vulDocenten(docenten);
		vulKlassen(klassen);
		vulStudenten(studenten, klassen);
		vulSLB(docenten);
		vulVakken(cursussen, lessen);

	} // Einde Pris constructor

	// deze method is static onderdeel van PrIS omdat hij als hulp methode
	// in veel controllers gebruikt wordt
	// een standaardDatumString heeft formaat YYYY-MM-DD
	public static Calendar standaardDatumStringToCal(String stadaardDatumString) {
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			calendar.setTime(sdf.parse(stadaardDatumString));
		} catch (ParseException e) {
			e.printStackTrace();
			calendar = null;
		}
		return calendar;
	}

	// deze method is static onderdeel van PrIS omdat hij als hulp methode
	// in veel controllers gebruikt wordt
	// een standaardDatumString heeft formaat YYYY-MM-DD
	public static String calToStandaardDatumString(Calendar calendar) {
		int jaar = calendar.get(Calendar.YEAR);
		int maand = calendar.get(Calendar.MONTH) + 1;
		int dag = calendar.get(Calendar.DAY_OF_MONTH);

		String maandStr = Integer.toString(maand);
		if (maandStr.length() == 1) {
			maandStr = "0" + maandStr;
		}

		String dagStr = Integer.toString(dag);
		if (dagStr.length() == 1) {
			dagStr = "0" + dagStr;
		}

		return jaar + "-" + maandStr + "-" + dagStr;
	}

	public Persoon login(String gebruikersnaam, String wachtwoord) {
		for (Docent d : docenten) {
			if (d.getGebruikersnaam().equals(gebruikersnaam)) {
				if (d.komtWachtwoordOvereen(wachtwoord)) {
					return d;
				}
			}
		}

		for (Student s : studenten) {
			if (s.getGebruikersnaam().equals(gebruikersnaam)) {
				if (s.komtWachtwoordOvereen(wachtwoord)) {
					return s;
				}
			}
		}

		return null;
	}

	public Calendar eersteLesDatum() {
		if (lessen.size() == 0)
			return Calendar.getInstance();

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(Date.from(lessen.get(0).getStartDatum().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
		return calendar;
	}

	public Calendar laatsteLesDatum() {
		if (lessen.size() == 0)
			return Calendar.getInstance();

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(Date.from(lessen.get(lessen.size() - 1).getEindDatum().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
		return calendar;
	}

	private void vulDocenten(ArrayList<Docent> docenten) {
		String csvFile = "././CSV/docenten.csv";
		BufferedReader br = null;
		String line;
		String cvsSplitBy = ";";

		try {

			br = new BufferedReader(new FileReader(csvFile));
			while ((line = br.readLine()) != null) {
				// use comma as separator
				String[] elements = line.split(cvsSplitBy);
				String gebruikersnaam = elements[0].toLowerCase();
				String voornaam = elements[1];
				String tussenvoegsel = elements[2];
				String achternaam = elements[3];
				docenten.add(new Docent(voornaam, tussenvoegsel, achternaam, "geheim", gebruikersnaam, 1));
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// close the bufferedReader if opened.
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			// verify content of arraylist, if empty add Jos
			if (docenten.isEmpty())
				docenten.add(new Docent(
					"Jos",
					"van",
					"Reenen",
					"supergeheim",
					"jos.vanreenen@hu.nl",
					1));
		}
	}

	private void vulSLB(ArrayList<Docent> docenten) {
		String csvFile = "././CSV/slbers.csv";
		BufferedReader br = null;
		String line;
		String cvsSplitBy = "\";\"";

		try {

			br = new BufferedReader(new FileReader(csvFile));

			while ((line = br.readLine()) != null) {
				String[] rawElements = line.split(cvsSplitBy);

				List<String> elements = Arrays.stream(rawElements).map((e) -> e.replaceAll("\"", "")).collect(Collectors.toList());

				if (elements.get(0).startsWith("#")) {
					continue;
				}

				Docent docent = getDocent(elements.get(0));
				int index = docenten.indexOf(docent);

				if (docent == null) {
					continue;
				}

				ArrayList<Student> studenten = Arrays.stream(elements.get(1).split("; "))
					.map(this::getStudent)
					.collect(Collectors.toCollection(ArrayList::new));

				if (studenten.get(0) == null) {
					continue;
				}

				SLB slb = new SLB(docent, studenten);

				docenten.set(index, slb);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void vulKlassen(ArrayList<Klas> klassen) {
		// TICT-SIE-VIA is de klascode die ook in de rooster file voorkomt
		// V1A is de naam van de klas die ook als file naam voor de studenten van die
		// klas wordt gebruikt
		Klas k1 = new Klas("TICT-SIE-V1A", "V1A");
		Klas k2 = new Klas("TICT-SIE-V1B", "V1B");
		Klas k3 = new Klas("TICT-SIE-V1C", "V1C");
		Klas k4 = new Klas("TICT-SIE-V1D", "V1D");
		Klas k5 = new Klas("TICT-SIE-V1E", "V1E");

		klassen.add(k1);
		klassen.add(k2);
		klassen.add(k3);
		klassen.add(k4);
		klassen.add(k5);
	}

	private void vulStudenten(ArrayList<Student> studenten, ArrayList<Klas> klassen) {
		Student student;
		Student dummyStudent = new Student(
			"Stu",
			"de",
			"Student",
			"geheim",
			"test@student.hu.nl",
			0);

		for (Klas k : klassen) {
			// per klas
			String csvFile = "././CSV/" + k.getNaam() + ".csv";
			BufferedReader br = null;
			String line;
			String cvsSplitBy = ";";

			try {

				br = new BufferedReader(new FileReader(csvFile));

				while ((line = br.readLine()) != null) {
					// line = line.replace(",,", ", ,");
					// use comma as separator
					String[] elements = line.split(cvsSplitBy);
					String gebruikersnaam = (elements[3] + "." + elements[2] + elements[1] + "@student.hu.nl")
						.toLowerCase();

					// verwijder spaties tussen dubbele voornamen en tussen bv van der
					gebruikersnaam = gebruikersnaam.replace(" ", "");
					String studentNrString = elements[0];
					int studentNr = Integer.parseInt(studentNrString);

					// Volgorde 3-2-1 = voornaam, tussenvoegsel en achternaam
					student = new Student(elements[3], elements[2], elements[1], "geheim", gebruikersnaam, studentNr);
					studenten.add(student);
					k.voegStudentToe(student);
				}

			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (br != null) {
					try {
						br.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				// mocht deze klas geen studenten bevatten omdat de csv niet heeft gewerkt:
				if (k.getStudenten().isEmpty()) {
					k.voegStudentToe(dummyStudent);
					System.out.println("Had to add Stu de Student to class: " + k.getKlasCode());
				}
			}

		}
		// mocht de lijst met studenten nu nog leeg zijn
		if (studenten.isEmpty())
			studenten.add(dummyStudent);
	}

	private void vulVakken(ArrayList<Cursus> cursussen, ArrayList<Les> lessen) {
		String csvFile = "././CSV/rooster.csv";
		BufferedReader br = null;
		String line;
		String cvsSplitBy = "\";\"";

		try {

			br = new BufferedReader(new FileReader(csvFile));

			Cursus cursus;
			Les les;

			while ((line = br.readLine()) != null) {
				String[] rawElements = line.split(cvsSplitBy);

				/*
				 elements[0]  Naam
				 elements[1]  Cursuscode
				 elements[2]  Startweek (ISO)
				 elements[3]  Startdag
				 elements[4]  Startdatum
				 elements[5]  Starttijd
				 elements[6]  Einddag
				 elements[7]  Einddatum
				 elements[8]  Eindtijd
				 elements[9]  Duur
				 elements[10] Werkvorm
				 elements[11] Docent(en)
				 elements[12] Lokaalnummer(s)
				 elements[13] Groep(en)
				 elements[14] Faculteit
				 elements[15] Grootte
				 elements[16] Opmerking
				 */

				List<String> elements = Arrays.stream(rawElements).map((e) -> e.replaceAll("\"", "")).collect(Collectors.toList());

				if (elements.get(0).startsWith("#")) {
					continue;
				}

				cursus = getCursus(elements.get(1));
				if (cursus == null) {
					cursus = new Cursus(elements.get(0).split("_")[0], elements.get(1));
					cursussen.add(cursus);
				}

				les = new Les(
					cursus,
					LocalDate.parse(elements.get(4)),                                                 // Begindatum
					LocalTime.parse(elements.get(5)),                                                 // Begintijd
					LocalDate.parse(elements.get(7)),                                                 // Einddatum
					LocalTime.parse(elements.get(8)),                                                 // Eindtijd
					elements.get(10),                                                                 // Werkvorm
					Arrays.stream(elements.get(11).split("; ")).map(this::getDocent).collect(Collectors.toCollection(ArrayList::new)),          // Docenten
					elements.get(12),                                                                 // Lokaal
					Arrays.stream(elements.get(13).split("; ")).map(this::getKlas).collect(Collectors.toCollection(ArrayList::new)),            // Klassen
					Integer.parseInt(elements.get(15))                                                // Lokaalgrootte
				);

				if (les.getDocenten().get(0) != null && les.getGroepen().get(0) != null)
					lessen.add(les);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public Docent getDocent(String gebruikersnaam) {
		return docenten.stream()
			.filter(d ->
				d.getGebruikersnaam().equalsIgnoreCase(gebruikersnaam))
			.findFirst()
			.orElse(null);
	}

	public Klas getKlasVanStudent(Student student) {
		return klassen.stream()
			.filter(k ->
				k.bevatStudent(student))
			.findFirst()
			.orElse(null);
	}

	public Student getStudent(String gebruikersnaam) {
		return studenten.stream()
			.filter(s ->
				s.getGebruikersnaam().equalsIgnoreCase(gebruikersnaam))
			.findFirst()
			.orElse(null);
	}

	public Student getStudent(int studentNummer) {
		return studenten.stream()
			.filter(s ->
				s.getStudentNummer() == studentNummer)
			.findFirst()
			.orElse(null);
	}

	public ArrayList<Les> getLessen(Student student) {
		if (getKlasVanStudent(student) == null) {
			return new ArrayList<>();
		}

		return lessen.stream()
			.filter(l ->
				l.getGroepen().stream()
					.anyMatch(g ->
						g.getKlasCode().equalsIgnoreCase(getKlasVanStudent(student).getKlasCode())))
			.collect(Collectors.toCollection(ArrayList::new));
	}

	public ArrayList<Les> getLessen(Docent docent) {
		return lessen.stream()
			.filter(l ->
				l.getDocenten().stream()
					.anyMatch(g ->
						g.getGebruikersnaam().equalsIgnoreCase(docent.getGebruikersnaam())))
			.collect(Collectors.toCollection(ArrayList::new));
	}

	public Cursus getCursus(String cursusCode) {
		return cursussen.stream()
			.filter(c -> c.getCursusCode().equals(cursusCode))
			.findFirst()
			.orElse(null);
	}

	public Klas getKlas(String klasCode) {
		return klassen.stream()
			.filter(k ->
				k.getKlasCode().equals(klasCode))
			.findFirst()
			.orElse(null);
	}

	public SLB getSLB(Student student) {
		return (SLB) docenten.stream()
			.filter(d -> (d instanceof SLB))
			.filter(d -> ((SLB) d).getStudentenBegeleiden().contains(student))
			.findFirst()
			.orElse(null);
	}

	public SLB getSLB(String gebruikersnaam) {
		return (SLB) docenten.stream()
			.filter(d -> (d instanceof SLB) && d.getGebruikersnaam().equalsIgnoreCase(gebruikersnaam))
			.findFirst()
			.orElse(null);
	}

	public ArrayList<Klas> getKlassen() {
		return klassen;
	}

	public ArrayList<Les> getLessen() {
		return lessen;
	}

	public ArrayList<Cursus> getCursussen() {
		return cursussen;
	}
}
