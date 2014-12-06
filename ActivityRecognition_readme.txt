-----------------------
ActivityRecognition.jar
-----------------------

* Commando's *

	log [log-bestand]
		Kies een log-bestand waaraan je wil werken
		(Dan moet je dus niet meer telkens de bestandsnaam van het log-bestand meegeven.)
							
	plot [log-bestand]
		Plot een opgegeven log-bestand in een PDF-bestand
		
	plot
		Plot het huidige log-bestand (zoals opgegeven met het commando: log [log-bestand])
	
	cut [log-bestand] [start] [einde]
		Knip een opgegeven log-bestand van [start] tot [einde]
		Start- en eindtijd in seconden!
	
	cut [start] [einde]
		Knip het huidige log-bestand
		
	getlabel [log-bestand]
		Toon het label van een opgegeven log-bestand
		
	getlabel
		Toon het label van het huidige log-bestand
		
	setlabel [log-bestand] [label]
		Verander het label van een opgegeven log-bestand naar [label]
		
	setlabel [label]
		Verander het label van het huidige log-bestand naar [label]
		
* Belangrijk *

	MotionFingerprint.jar moet in dezelfde map staan als ActivityRecognition.jar