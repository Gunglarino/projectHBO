package controller;

import model.PrIS;
import model.klas.Klas;
import model.persoon.Docent;
import model.persoon.Student;
import model.presentie.Presentie;
import model.vak.Cursus;
import model.vak.Les;
import server.Conversation;
import server.Handler;

import javax.json.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class PresentieController implements Handler {
	private PrIS informatieSysteem;

	/**
	 * De StudentController klasse moet alle present-gerelateerde aanvragen
	 * afhandelen. Methode handle() kijkt welke URI is opgevraagd en laat
	 * dan de juiste methode het werk doen. Je kunt voor elke nieuwe URI
	 * een nieuwe methode schrijven.
	 *
	 * @param infoSys - het toegangspunt tot het domeinmodel
	 */
	public PresentieController(PrIS infoSys) {
		informatieSysteem = infoSys;
	}

	public void handle(Conversation conversation) {
		if (conversation.getRequestedURI().startsWith("/presentie/ophalen")) {
			ophalen(conversation);
		} else if (conversation.getRequestedURI().startsWith("/presentie/opslaan")) {
			opslaan(conversation);
		} else if (conversation.getRequestedURI().startsWith("/presentie/individueel/ophalen")) {
			ophalenStudent(conversation);
		} else if (conversation.getRequestedURI().startsWith("/presentie/docent/ophalen")) {
			ophalenDocent(conversation);
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

		String cursusCode = jsonObjectIn.getString("cursusCode");
		String startDateStr = jsonObjectIn.getString("startDate");
		String startTimeStr = jsonObjectIn.getString("startTime");
		String endDateStr = jsonObjectIn.getString("endDate");
		String endTimeStr = jsonObjectIn.getString("endTime");
		String room = jsonObjectIn.getString("room");

		LocalDate startDate = LocalDate.parse(startDateStr);
		LocalTime startTime = LocalTime.parse(startTimeStr);
		LocalDate endDate = LocalDate.parse(endDateStr);
		LocalTime endTime = LocalTime.parse(endTimeStr);

		Les les = informatieSysteem.getLessen().stream().filter(l ->
			l.getCursus().getCursusCode().equals(cursusCode) &&
				l.getStartDatum().isEqual(startDate) &&
				l.getStartTijd().equals(startTime) &&
				l.getEindDatum().isEqual(endDate) &&
				l.getEindTijd().equals(endTime) &&
				l.getLokaal().equals(room))
			.findFirst().orElse(null);

		if (les == null)
			return;

		// studenten ophalen
		ArrayList<Student> studenten = new ArrayList<>();
		les.getGroepen().stream().map(Klas::getStudenten).forEach(studenten::addAll);

		// Uiteindelijk gaat er een array...
		JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();

		// met daarin voor elke medestudent een JSON-object...
		for (Student cursist : studenten) {

			// maak het JsonObject voor een student
			JsonObjectBuilder jsonStudentBuilder = Json.createObjectBuilder();

			Presentie presentie = les.getPresenties().stream()
				.filter(p -> p.getStudent().equals(cursist))
				.findFirst()
				.orElse(null);

			jsonStudentBuilder
				.add("id", cursist.getStudentNummer())
				.add("firstName", cursist.getVoornaam())
				.add("lastName", cursist.getVolledigeAchternaam())
				.add("number", cursist.getStudentNummer())
				.add("attended", presentie != null && presentie.isPresent());

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
	private void ophalenStudent(Conversation conversation) {
		JsonObject jsonObjectIn = (JsonObject) conversation.getRequestBodyAsJSON();

		String gebruikersnaam = jsonObjectIn.getString("username");
		String cursusCode = jsonObjectIn.containsKey("cursusCode") ? jsonObjectIn.getString("cursusCode") : null;
		Student student = informatieSysteem.getStudent(gebruikersnaam);

		AtomicInteger present = new AtomicInteger();
		AtomicInteger absent = new AtomicInteger();

		informatieSysteem.getLessen().stream()
			.filter(l ->
				l.getGroepen().stream()
					.anyMatch(k -> k.bevatStudent(student)) &&
					(cursusCode == null || l.getCursus().getCursusCode().equals(cursusCode)))
			.forEach(l -> {
				Presentie presentie = l.getPresentie(student);

				if (presentie == null)
					return;

				if (presentie.isPresent()) {
					present.getAndIncrement();
				} else {
					absent.getAndIncrement();
				}
			});

		JsonObjectBuilder jsonPresentieBuilder = Json.createObjectBuilder();

		if (cursusCode != null) {
			jsonPresentieBuilder.add("cursusCode", cursusCode);
		}

		jsonPresentieBuilder
			.add("present", present.get())
			.add("absent", absent.get())
			.add("total", present.get() + absent.get());

		conversation.sendJSONMessage(jsonPresentieBuilder.build().toString());
	}

	/**
	 * Deze methode haalt eerst de opgestuurde JSON-data op. Daarna worden
	 * de benodigde gegevens uit het domeinmodel gehaald. Deze gegevens worden
	 * dan weer omgezet naar JSON en teruggestuurd naar de Polymer-GUI!
	 *
	 * @param conversation - alle informatie over het request
	 */
	private void ophalenDocent(Conversation conversation) {
		JsonObject jsonObjectIn = (JsonObject) conversation.getRequestBodyAsJSON();

		String gebruikersnaam = jsonObjectIn.getString("username");
		Docent docent = informatieSysteem.getDocent(gebruikersnaam);

		JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();

		for (Cursus cursus : informatieSysteem.getCursussen()) {
			AtomicInteger present = new AtomicInteger();
			AtomicInteger absent = new AtomicInteger();
			AtomicBoolean givesClass = new AtomicBoolean(false);

			informatieSysteem.getLessen().stream()
				.filter(l ->
					l.getDocenten().stream()
						.anyMatch(d -> d.equals(docent)) &&
						l.getCursus().getCursusCode().equals(cursus.getCursusCode()))
				.forEach(l -> {
					if (!givesClass.get())
						givesClass.set(true);

					l.getPresenties().forEach(p -> {
						if (!givesClass.get())
							givesClass.set(true);

						if (p.isPresent()) {
							present.getAndIncrement();
						} else {
							absent.getAndIncrement();
						}
					});
				});

			if (!givesClass.get())
				continue;

			JsonObjectBuilder jsonPresentieBuilder = Json.createObjectBuilder();

			jsonPresentieBuilder
				.add("cursusCode", cursus.getCursusCode())
				.add("present", present.get())
				.add("absent", absent.get())
				.add("total", present.get() + absent.get());

			jsonArrayBuilder.add(jsonPresentieBuilder);
		}

		conversation.sendJSONMessage(jsonArrayBuilder.build().toString());
	}

	/**
	 * Deze methode haalt eerst de opgestuurde JSON-data op. Op basis van deze gegevens
	 * het domeinmodel gewijzigd. Een eventuele errorcode wordt tenslotte
	 * weer (als JSON) teruggestuurd naar de Polymer-GUI!
	 *
	 * @param conversation - alle informatie over het request
	 */
	private void opslaan(Conversation conversation) {
		JsonObject jsonObjectIn = (JsonObject) conversation.getRequestBodyAsJSON();

		String cursusCode = jsonObjectIn.getString("cursusCode");
		String startDateStr = jsonObjectIn.getString("startDate");
		String startTimeStr = jsonObjectIn.getString("startTime");
		String endDateStr = jsonObjectIn.getString("endDate");
		String endTimeStr = jsonObjectIn.getString("endTime");
		String room = jsonObjectIn.getString("room");
		JsonArray studenten = jsonObjectIn.getJsonArray("students");

		LocalDate startDate = LocalDate.parse(startDateStr);
		LocalTime startTime = LocalTime.parse(startTimeStr);
		LocalDate endDate = LocalDate.parse(endDateStr);
		LocalTime endTime = LocalTime.parse(endTimeStr);

		Les les = informatieSysteem.getLessen().stream().filter(l ->
			l.getCursus().getCursusCode().equals(cursusCode) &&
				l.getStartDatum().isEqual(startDate) &&
				l.getStartTijd().equals(startTime) &&
				l.getEindDatum().isEqual(endDate) &&
				l.getEindTijd().equals(endTime) &&
				l.getLokaal().equals(room))
			.findFirst().orElse(null);

		if (les == null)
			return;

		ArrayList<Presentie> presenties = new ArrayList<>();
		for (int i = 0; i < studenten.size(); i++) {
			JsonObject jsonStudent = studenten.getJsonObject(i);

			Student student = informatieSysteem.getStudent(jsonStudent.getInt("id"));
			Presentie presentie = new Presentie(student, jsonStudent.getBoolean("attended"));
			presenties.add(presentie);
		}

		les.setPresenties(presenties);
	}
}
