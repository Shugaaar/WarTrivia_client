WARTRIVIA 
Studente: Massimiliano Coltelli , 676424

SERVIZIO WEB:
Il servizio agisce da intermediario tra il Client e l'API pubblica https://opentdb.com/. All'inizializzazione, preleva un set di domande trivia a risposta multipla e le storicizza nella tabella Questions del database MySQL locale, la quale funge da cache persistente per garantire coerenza tra i turni di gioco.
Ogni Match (partita) viene generato associando due utenti e selezionando casualmente un set di domande dalla cache. La logica di gioco è a stati (FIRST_R, SECOND_R, COMPLETED) per gestire la natura asincrona delle sfide.
RICHIESTE POSSIBILI AL SERVIZIO (API ENDPOINTS):
/inizializza : Effettua il download delle domande da OpenTDB popolando la cache e inizializza il sistema, cancella le partite esistenti.
/register : Registra un nuovo utente nel database (con verifica duplicati).
/login : Verifica le credenziali e restituisce l'ID utente per la sessione.
Gestione Amicizie:
/getfriends : Restituisce la lista di oggetti Friendship relative all'utente, indicando lo stato (accettato/pendente).
/addfriend : Crea una nuova relazione di amicizia (stato false = pendente) tra due utenti.
/acceptfriend : Aggiorna lo stato dell'amicizia a true (accettata).
/delfriend : Rimuove il record relativo all'amicizia dal database.
Gestione Partite (Gameplay):
/getmatches : Restituisce la lista di tutte le partite in cui l'utente è coinvolto (sia come sfidante che come sfidato), includendo stato e punteggi.
/challenge : Crea un nuovo Match in stato FIRST_R, associa le domande dalla cache e imposta lo sfidante.
/acceptchallenge : Permette all'utente sfidato di accettare la partita, la quale si troverà in SECOND_R, per giocare il proprio turno sulle stesse domande.
/answer : Riceve la risposta dell'utente e l'ID della partita. Il server verifica la correttezza, aggiorna il punteggio del giocatore corrente e controlla se la partita è conclusa o se deve passare in uno stato successivo (passaggio a stato COMPLETED o SECOND_R).
TECNOLOGIE E IMPLEMENTAZIONE:
Backend: È stato utilizzato Spring Boot per la struttura del servizio REST.
JPA (Java Persistence API): Utilizzato per la mappatura ORM delle entità (User, Match, Question, Friendship) sul database MySQL.
JPQL: Le query complesse (es. recupero partite o verifica amicizie esistenti) sono state scritte utilizzando JPQL per astrarre la logica dal database specifico.
Frontend (Client): Sviluppato in JavaFX.
Asincronia: L'interazione con il servizio avviene tramite Task in thread separati per non bloccare la UI.
Gestione Tempo: L'interfaccia di gioco utilizza una Timeline per gestire il countdown delle risposte e le transizioni di stato (colorazione bottoni e pause).


CLIENT DESKTOP (JAVA FX):
Il client è un'applicazione desktop realizzata con il framework JavaFX.
La GUI principale contiene tre liste:
-  Games: elenca le partite nel loro stato, mostra il punteggio e permette a uno sfidato di giocare il suo turno tramite un pulsante “play”.
Friends: elenca gli amici dell’utente, le richieste pendenti e quelle ricevute, inoltre per le amicizie confermate mostra numero di vittorie e sconfitte contro quell’amico. Tramite un ContextMenu è possibile accettare, rimuovere o sfidare l’utente. La listView è organizzata tramite una CellsFactory.
All User: elenca tutti gli utenti registrati al servizio. Tramite un ContextMenu è possibile mandare una richiesta di amicizia all’utente selezionato. La listView è organizzata tramite una CellsFactory.

Cliccando sul proprio username con il tasto destro è possibile effettuare il logout.

CARATTERISTICHE IMPLEMENTATIVE:
Gestione della Concorrenza (Multithreading): Per garantire la reattività dell'interfaccia utente (evitando il "freezing" della finestra), tutte le operazioni di rete (chiamate HTTP verso il server) sono state incapsulate all'interno della classe javafx.concurrent.Task. L'esecuzione avviene su thread in background, mentre l'aggiornamento della UI al termine dell'operazione è gestito tramite le callback setOnSucceeded e setOnFailed nel JavaFX Application Thread.
Logica Temporale e Animazioni:
Timeline: Il timer di gioco (countdown di 15 secondi per domanda) è implementato tramite una Timeline con ciclo indefinito, che aggiorna la label del tempo ogni secondo.
PauseTransition: Utilizzata per introdurre un ritardo non bloccante (3 secondi) tra la risposta dell'utente e il caricamento della domanda successiva, permettendo la visualizzazione del feedback (risposta corretta/errata).
Gestione dello Stato (Sessione): È stato implementato il pattern Singleton nella classe UserSession. Questo permette di mantenere un'unica istanza globale contenente i dati dell'utente loggato e le informazioni della partita corrente, rendendoli accessibili tra le diverse scene (Login, Home, Game).
Interfaccia e Styling:
FXML: La struttura gerarchica delle viste è definita in file .fxml separati.
 CSS: Lo stile grafico (colori, bordi arrotondati, ombreggiature delle card, feedback visivo dei bottoni) è esternalizzato in file .css, permettendo una facile manutenzione del look & feel.
Comunicazione Dati: Utilizzo della libreria Gson per la serializzazione e deserializzazione degli oggetti (Match, Question, User) scambiati in formato JSON con il servizio REST.


UTILIZZO DELL’ AI:
L’AI generativa è stata usata per:
- la creazione del file Theme.css che fornisce gli stili ai quali vari elementi della GUI sono stati associati (Il tema è in seguito stato anche rivisitato personalmente).
debugging
La documentazione e la scoperta di nuove dipendenze e/o costrutti utili allo scopo (per esempio le FactoryCells,Timeline,PauseTransition
