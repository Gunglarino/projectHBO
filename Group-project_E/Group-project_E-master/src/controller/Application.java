package controller;

import model.PrIS;
import server.JSONFileServer;

import java.io.File;

public class Application {

	/**
	 * Deze klasse is het startpunt voor de applicatie. Hierin maak je een server op
	 * een bepaalde poort (bijv. 8888). Daarna is er een PrIS-object gemaakt. Dit
	 * object fungeert als toegangspunt van het domeinmodel. Hiervandaan kan alle
	 * informatie bereikt worden.
	 * 
	 * Om het domeinmodel en de Polymer-GUI aan elkaar te koppelen zijn diverse
	 * controllers gemaakt. Er zijn meerdere controllers om het overzichtelijk te
	 * houden. Je mag zoveel controller-klassen maken als je nodig denkt te hebben.
	 * Elke controller krijgt een koppeling naar het PrIS-object om benodigde
	 * informatie op te halen.
	 * 
	 * Je moet wel elke URL die vanaf Polymer aangeroepen kan worden registreren!
	 * Dat is hieronder gedaan voor een drietal URLs. Je geeft daarbij ook aan welke
	 * controller de URL moet afhandelen.
	 * 
	 * Als je alle URLs hebt geregistreerd kun je de server starten en de applicatie
	 * in de browser opvragen! Zie ook de controller-klassen voor een voorbeeld!
	 * 
	 */
	public static void main(String[] args) {
		JSONFileServer server = new JSONFileServer(new File("webapp/app"), 8888);

		PrIS infoSysteem = new PrIS();

		SysteemDatumController systeemDatumController = new SysteemDatumController(infoSysteem);
		LoginController loginController = new LoginController(infoSysteem);
		MedestudentenController medestudentenController = new MedestudentenController(infoSysteem);
		RoosterController roosterController = new RoosterController(infoSysteem);
		PresentieController presentieController = new PresentieController(infoSysteem);
		StudentenController studentenController = new StudentenController(infoSysteem);

		server.registerHandler("/systeemdatum/lesinfo", systeemDatumController);

		server.registerHandler("/login", loginController);

		server.registerHandler("/student/medestudenten/ophalen", medestudentenController);
		server.registerHandler("/student/medestudenten/opslaan", medestudentenController);

		server.registerHandler("/student/rooster", roosterController);

		server.registerHandler("/presentie/ophalen", presentieController);
		server.registerHandler("/presentie/opslaan", presentieController);
		server.registerHandler("/presentie/individueel/ophalen", presentieController);
		server.registerHandler("/presentie/docent/ophalen", presentieController);

		server.registerHandler("/studenten/ophalen", studentenController);
		server.registerHandler("/student/escaleren", studentenController);

		server.registerHandler("/slb/escalaties/ophalen", studentenController);
		server.registerHandler("/slb/escalatie/afhandelen", studentenController);

		server.start();
	}
}