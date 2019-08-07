package controller;

import model.PrIS;
import model.persoon.Docent;
import model.persoon.Persoon;
import model.persoon.SLB;
import model.persoon.Student;
import server.Conversation;
import server.Handler;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

class LoginController implements Handler {
	private PrIS informatieSysteem;

	/**
	 * De LoginController klasse moet alle algemene aanvragen afhandelen. Methode
	 * handle() kijkt welke URI is opgevraagd en laat dan de juiste methode het werk
	 * doen. Je kunt voor elke nieuwe URI een nieuwe methode schrijven.
	 *
	 * @param infoSys - het toegangspunt tot het domeinmodel
	 */
	public LoginController(PrIS infoSys) {
		informatieSysteem = infoSys;
	}

	public void handle(Conversation conversation) {
		if (conversation.getRequestedURI().startsWith("/login")) {
			login(conversation);
		}
	}

	/**
	 * Deze methode haalt eerst de opgestuurde JSON-data op. Daarna worden de
	 * benodigde gegevens uit het domeinmodel gehaald. Deze gegevens worden dan weer
	 * omgezet naar JSON en teruggestuurd naar de Polymer-GUI!
	 *
	 * @param conversation - alle informatie over het request
	 */
	private void login(Conversation conversation) {
		JsonObject jsonObjIn = (JsonObject) conversation.getRequestBodyAsJSON();

		String gebruikersnaam = jsonObjIn.getString("username");
		String wachtwoord = jsonObjIn.getString("password");
		Persoon persoon = informatieSysteem.login(gebruikersnaam, wachtwoord);

		JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
		if (persoon == null) {
			jsonBuilder.add("rol", "undefined");
		}

		if (persoon instanceof Docent) {
			jsonBuilder.add("rol", "docent");
			jsonBuilder.add("isSLB", (persoon instanceof SLB));
		} else if (persoon instanceof Student) {
			jsonBuilder.add("rol", "student");
			jsonBuilder.add("isSLB", false);
		}

		conversation.sendJSONMessage(jsonBuilder.build().toString());
	}
}