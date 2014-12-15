Bachelorproef: bewegingen herkennen met een smartphone
========================================

Arne De Brabandere en Menno Keustermans

Begeleiders: Wannes Meert en Leander Schietgat

## Probleemstelling

> Welke machine learning techniek is het nauwkeurigst om een sequentie van verschillende
> activiteiten correct te classificeren?

*Opmerking: sequenties van activiteiten = verschillende activiteiten na elkaar, eventueel met "ruis" ertussen*

### Deel 1: afzonderlijke activiteiten

In het eerste deel maken we een model om de verschillende activiteiten afzonderlijk te kunnen
herkennen. De activiteiten zijn:

* Wandelen
* Lopen
* Fietsen
* Trap op
* Trap af
* Lift naar boven
* Lift naar beneden
* Tanden poetsen
* Niets doen (staan, zitten, liggen)

### Deel 2: sequenties van activiteiten

Nadat we een goed model hebben voor de afzonderlijke activiteiten, zullen we dit gebruiken om
sequenties van activiteiten te classificeren.

## Planning

* Week van 8/12
    * Data opmeten
    * Nieuwe metingen knippen

* Week van 15/12
    * Modellen maken voor afzonderlijke activiteiten
       * Classifiers
       * HMM's?

## Verslag

* Week van 8/12
    * ActivityRecognition uitgebreid om op verschillende manieren features te berekenen en verschillende methodes (met variërende waardes voor parameters) uit te voeren.
    * Deze verschillende methodes geëvalueerd met de huidige metingen als training set. Dit hebben we gedaan met crossvalidatie voor 10 en 20 folds. We hebben telkens de accuracy en de confusion matrix laten berekenen.

* Week van 1/12
    * Presentatie: probleemstelling
    * Data opgemeten
    * Data geknipt 
    * Tweede versie ActivityRecognition: programma om data te knippen
    * Enkele classifiers uitgetest dmv cross-validatie mbv Weka

* Week van 24/11
    * Voorbereiding presentatie: probleemstelling
    * Data opgemeten
    * Eerste versie ActivityRecognition: programma om data te knippen

* Week van 17/11
    * Presentatie: literatuurstudie
    * Afspraak met begeleiders
    * Data opgemeten

* Week van 10/11
    * Voorbereiding presentatie: literatuurstudie
    * Eerste test met weka voor kleine trainingset:
       * Beslissingsbomen
       * Naive Bayes
       * KNN
       * Beslissingstabellen
    * Afspraak met begeleiders over eerste versie van presentatie: literatuurstudie

* Week van 3/11
    * Papers herlezen
    * Voorbereiding presentatie: literatuurstudie

* Week van 27/10
    * Papers gelezen    
    * Voorbereiding presentatie: literatuurstudie
    * Afspraak met begeleiders over eerste versie van presentatie: literatuurstudie

* Week van 20/10
    * Papers gelezen
    * Voorbereiding presentatie: literatuurstudie

* Week van 13/10
    * Uittesten van applicatie om gegevens op te meten
    * Programma geschreven om accelerometer data grafisch weer te geven
    * Papers gelezen

* Week van 6/10
    * Eerste afspraak met begeleiders
 
