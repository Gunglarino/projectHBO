package controller;

import model.PrIS;
import model.persoon.Docent;
import model.persoon.Escalatie;
import model.persoon.SLB;
import model.persoon.Student;
import model.presentie.Presentie;
import model.vak.Cursus;
import server.Conversation;
import server.Handler;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class StudentenController implements Handler {
	private PrIS informatieSysteem;

	/**
	 * De StudentenController klasse moet alle student-gerelateerde aanvragen
	 * afhandelen. Methode handle() kijkt welke URI is opgevraagd en laat
	 * dan de juiste methode het werk doen. Je kunt voor elke nieuwe URI
	 * een nieuwe methode schrijven.
	 *
	 * @param infoSys - het toegangspunt tot het domeinmodel
	 */
	public StudentenController(PrIS infoSys) {
		informatieSysteem = infoSys;
	}

	public void handle(Conversation conversation) {
		if (conversation.getRequestedURI().startsWith("/studenten/ophalen")) {
			ophalen(conversation);
		} else if (conversation.getRequestedURI().startsWith("/student/escaleren")) {
			escaleren(conversation);
		} else if (conversation.getRequestedURI().startsWith("/slb/escalaties/ophalen")) {
			escalatiesOphalen(conversation);
		} else if (conversation.getRequestedURI().startsWith("/slb/escalatie/afhandelen")) {
			escalatieAfhandelen(conversation);
		}
	}

	/**
	 * Deze methode haalt eerst de opgestuurde JSON-data op. Daarna worden
	 * de benodigde gegevens uit het domeinmodel gehaald. Deze gegevens worden
	 * dan weer omgezet naar JSON en teruggestuurd naar de Polymer-GUI!
	 *
	 * @param conversation - alle informatie over het request
	 */
	private void ophalen(Conversation conversation) {
		JsonObject jsonObjectIn = (JsonObject) conversation.getRequestBodyAsJSON();

//		String klasCode = jsonObjectIn.containsKey("classCode") ? jsonObjectIn.getString("classCode") : null;
		String docentGebruikersnaam = jsonObjectIn.containsKey("username") ? jsonObjectIn.getString("username") : null;
		String cursusCode = jsonObjectIn.containsKey("cursusCode") ? jsonObjectIn.getString("cursusCode") : null;

		ArrayList<Student> studenten = new ArrayList<>();

//		if (klasCode != null) {
//			if (!klasCode.equals("all")) {
//				// klas van de klascode opzoeken
//				Klas klas = informatieSysteem.getKlas(klasCode);
//
//				// studenten ophalen
//				studenten.addAll(klas.getStudenten());
//			} else {
//				informatieSysteem.getKlassen().forEach(k -> studenten.addAll(k.getStudenten()));
//			}
//		}

		if (cursusCode != null && docentGebruikersnaam != null) {
			informatieSysteem.getLessen(informatieSysteem.getDocent(docentGebruikersnaam)).stream()
				.filter(l -> l.getCursus().getCursusCode().equals(cursusCode))
				.forEach(l -> l.getGroepen()
					.forEach(g -> g.getStudenten()
						.forEach(s -> {
							if (!studenten.contains(s))
								studenten.add(s);
						})));
		}

		// Uiteindelijk gaat er een array...
		JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();

		// met daarin voor elke medestudent een JSON-object...
		for (Student cursist : studenten) {

			AtomicInteger present = new AtomicInteger();
			AtomicInteger absent = new AtomicInteger();

			informatieSysteem.getLessen().stream()
				.filter(l ->
					l.getGroepen().stream()
						.anyMatch(k -> k.bevatStudent(cursist)) &&
						l.getCursus().getCursusCode().equals(cursusCode))
				.forEach(l -> {
					Presentie presentie = l.getPresentie(cursist);

					if (presentie == null)
						return;

					if (presentie.isPresent()) {
						present.getAndIncrement();
					} else {
						absent.getAndIncrement();
					}
				});

			// maak het JsonObject voor een student
			JsonObjectBuilder jsonStudentBuilder = Json.createObjectBuilder();
			jsonStudentBuilder
				.add("id", cursist.getStudentNummer())
				.add("firstName", cursist.getVoornaam())
				.add("lastName", cursist.getVolledigeAchternaam())
				.add("username", cursist.getGebruikersnaam())
				.add("present", present.get())
				.add("absent", absent.get())
				.add("number", cursist.getStudentNummer());

			jsonArrayBuilder.add(jsonStudentBuilder);
		}

		conversation.sendJSONMessage(jsonArrayBuilder.build().toString());
	}

	/**
	 * Deze methode haalt eerst de opgestuurde JSON-data op. Daarna worden
	 * de benodigde gegevens uit het domeinmodel gehaald. Deze gegevens worden
	 * dan weer omgezet naar JSON en teruggestuurd naar de Polymer-GUI!
	 *
	 * @param conversation - alle informatie over het request
	 */
	private void escaleren(Conversation conversation) {
		JsonObject jsonObjectIn = (JsonObject) conversation.getRequestBodyAsJSON();

		String docentGebruikersnaam = jsonObjectIn.getString("teacherUsername");
		String studentGebruikersnaam = jsonObjectIn.getString("studentUsername");
		String cursusCode = jsonObjectIn.getString("cursusCode");

		Student student = informatieSysteem.getStudent(studentGebruikersnaam);
		Docent docent = informatieSysteem.getDocent(docentGebruikersnaam);
		SLB slb = informatieSysteem.getSLB(student);
		Cursus cursus = informatieSysteem.getCursus(cursusCode);

		if (student == null || slb == null || cursus == null) {
			conversation.sendJSONMessage(Json.createObjectBuilder().add("error", 2).build().toString());
			return;
		}

		Escalatie escalatie = new Escalatie(student, docent, cursus);

		boolean success = slb.addEscalatie(escalatie);

		conversation.sendJSONMessage(Json.createObjectBuilder().add("error", success ? 0 : 1).build().toString());
	}

	/**
	 * Deze methode haalt eerst de opgestuurde JSON-data op. Daarna worden
	 * de benodigde gegevens uit het domeinmodel gehaald. Deze gegevens worden
	 * dan weer omgezet naar JSON en teruggestuurd naar de Polymer-GUI!
	 *
	 * @param conversation - alle informatie over het request
	 */
	private void escalatiesOphalen(Conversation conversation) {
		JsonObject jsonObjectIn = (JsonObject) conversation.getRequestBodyAsJSON();

		String gebruikersnaam = jsonObjectIn.getString("username");
		SLB slb = informatieSysteem.getSLB(gebruikersnaam);

		if (slb == null) {
			return;
		}

		JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();

		for (Escalatie escalatie : slb.getEscalaties()) {
			JsonObjectBuilder jsonEscaltieBuilder = Json.createObjectBuilder();

			AtomicInteger present = new AtomicInteger();
			AtomicInteger absent = new AtomicInteger();

			informatieSysteem.getLessen().stream()
				.filter(l ->
					l.getGroepen().stream()
						.anyMatch(k -> k.bevatStudent(escalatie.getStudent())) &&
						l.getCursus().getCursusCode().equals(escalatie.getCursus().getCursusCode()))
				.forEach(l -> {
					Presentie presentie = l.getPresentie(escalatie.getStudent());

					if (presentie == null)
						return;

					if (presentie.isPresent()) {
						present.getAndIncrement();
					} else {
						absent.getAndIncrement();
					}
				});

			jsonEscaltieBuilder
				.add("studentNumber", escalatie.getStudent().getStudentNummer())
				.add("studentName", escalatie.getStudent().getVolledigeNaam())
				.add("studentUsername", escalatie.getStudent().getGebruikersnaam())
				.add("teacherName", escalatie.getDocent().getVolledigeNaam())
				.add("teacherUsername", escalatie.getDocent().getGebruikersnaam())
				.add("cursusCode", escalatie.getCursus().getCursusCode())
				.add("cursusName", escalatie.getCursus().getNaam())
				.add("present", present.get())
				.add("absent", absent.get());

			jsonArrayBuilder.add(jsonEscaltieBuilder);
		}

		conversation.sendJSONMessage(jsonArrayBuilder.build().toString());
	}

	private void escalatieAfhandelen(Conversation conversation) {
		JsonObject jsonObjectIn = (JsonObject) conversation.getRequestBodyAsJSON();

		String docentGebruikersnaam = jsonObjectIn.getString("teacherUsername");
		String studentGebruikersnaam = jsonObjectIn.getString("studentUsername");
		String cursusCode = jsonObjectIn.getString("cursusCode");

		Student student = informatieSysteem.getStudent(studentGebruikersnaam);
		Docent docent = informatieSysteem.getDocent(docentGebruikersnaam);
		SLB slb = informatieSysteem.getSLB(student);
		Cursus cursus = informatieSysteem.getCursus(cursusCode);

		if (student == null || slb == null || cursus == null) {
			return;
		}

		slb.removeEscalatie(student, docent, cursus);

		conversation.sendJSONMessage(Json.createObjectBuilder().add("error", 0).build().toString());
	}
}
