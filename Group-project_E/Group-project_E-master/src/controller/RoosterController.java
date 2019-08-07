package controller;

import model.PrIS;
import model.persoon.Docent;
import model.persoon.Student;
import model.vak.Les;
import server.Conversation;
import server.Handler;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

public class RoosterController implements Handler {

	private PrIS infoSys;

	/**
	 * @param infoSys - het toegangspunt tot het domeinmodel
	 */
	public RoosterController(PrIS infoSys) {
		this.infoSys = infoSys;
	}

	public void handle(Conversation conversation) {
		ophalenRooster(conversation);
	}

	private void ophalenRooster(Conversation conversation) {
		JsonObject jsonObjectIn = (JsonObject) conversation.getRequestBodyAsJSON();
		String gebruikersnaam = jsonObjectIn.getString("username");
		String rol = jsonObjectIn.getString("rol");

		JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();

		if (rol.equals("student")) {
			Student student = infoSys.getStudent(gebruikersnaam);

			for (Les les : infoSys.getLessen(student)) {
				JsonObjectBuilder jsonLesBuilder = Json.createObjectBuilder();
				jsonLesBuilder
					.add("cursusNaam", les.getCursus().getNaam())
					.add("cursusCode", les.getCursus().getCursusCode())
					.add("startDatum", les.getStartDatum().toString())
					.add("startTijd", les.getStartTijd().toString())
					.add("eindDatum", les.getEindDatum().toString())
					.add("eindTijd", les.getEindTijd().toString())
					.add("werkvorm", les.getWerkvorm())
					.add("lokaal", les.getLokaal())
					.add("docentNaam", les.getDocenten().get(0).getVolledigeNaam())
					.add("docentEmail", les.getDocenten().get(0).getGebruikersnaam())
					.add("klas", les.getGroepen().get(0).getKlasCode());

				jsonArrayBuilder.add(jsonLesBuilder);
			}
		} else if (rol.equals("docent")) {
			Docent docent = infoSys.getDocent(gebruikersnaam);

			for (Les les : infoSys.getLessen(docent)) {
				StringBuilder docentenNaam = new StringBuilder();
				StringBuilder docentenEmail = new StringBuilder();

				for(Docent teacher : les.getDocenten()) {
					docentenNaam.append(teacher.getVolledigeNaam()).append(",");
				}
				for(Docent teacher : les.getDocenten()) {
					docentenEmail.append(teacher.getGebruikersnaam()).append(",");
				}

				JsonObjectBuilder jsonLesBuilder = Json.createObjectBuilder();
				jsonLesBuilder
					.add("cursusNaam", les.getCursus().getNaam())
					.add("cursusCode", les.getCursus().getCursusCode())
					.add("startDatum", les.getStartDatum().toString())
					.add("startTijd", les.getStartTijd().toString())
					.add("eindDatum", les.getEindDatum().toString())
					.add("eindTijd", les.getEindTijd().toString())
					.add("werkvorm", les.getWerkvorm())
					.add("lokaal", les.getLokaal())
					.add("docentNaam", docentenNaam.toString())
					.add("docentEmail", docentenEmail.toString())
					.add("klas", les.getGroepen().get(0).getKlasCode());
				jsonArrayBuilder.add(jsonLesBuilder);
			}
		}

		conversation.sendJSONMessage(jsonArrayBuilder.build().toString());
	}

}
