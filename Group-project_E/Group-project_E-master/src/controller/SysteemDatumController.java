package controller;

import model.PrIS;
import server.Conversation;
import server.Handler;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import java.util.Calendar;

public class SysteemDatumController implements Handler {

	private PrIS infoSys;

	/**
	 * De SysteemDatumController klasse moet alle systeem (en test)-gerelateerde aanvragen
	 * afhandelen. Methode handle() kijkt welke URI is opgevraagd en laat
	 * dan de juiste methode het werk doen. Je kunt voor elke nieuwe URI
	 * een nieuwe methode schrijven.
	 *
	 * @param infoSys - het toegangspunt tot het domeinmodel
	 */
	public SysteemDatumController(PrIS infoSys) {
		this.infoSys = infoSys;
	}

	public void handle(Conversation conversation) {
		ophalenLesInfo(conversation);
	}

	private void ophalenLesInfo(Conversation conversation) {
		// De volgende statements moeten gewijzigd worden zodat daadwerkelijk de eerste en laatste lesdatum wordt bepaald
		Calendar eersteLesDatum = infoSys.eersteLesDatum();
		Calendar laatsteLesDatum = infoSys.laatsteLesDatum();

		JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
		// Deze volgorde mag niet worden gewijzigd i.v.m. JS. (Hier mag dus ook geen andere JSON voor komen.)
		jsonObjectBuilder
			.add("eerste_lesdatum", PrIS.calToStandaardDatumString(eersteLesDatum))
			.add("laatste_lesdatum", PrIS.calToStandaardDatumString(laatsteLesDatum));

		conversation.sendJSONMessage(jsonObjectBuilder.build().toString());
	}
}
