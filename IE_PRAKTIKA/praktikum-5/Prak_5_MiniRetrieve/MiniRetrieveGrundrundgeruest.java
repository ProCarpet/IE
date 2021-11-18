import java.io.File;
import java.util.*;
import java.util.regex.Pattern;
//(w) 2007 Thomas Arni, InIT, ZHW
public class MiniRetrieveGrundrundgeruest {
	
	private static String queryDirectory = "queries";	//Verzeichnis mit allen Anfragen
	private static String documentDirectory = "documents"; //Verzeichnis mit allen Dokuementen
	
	private static double qNorm = 0;
	private static final int numberOfResults = 1000;
	private static int numberOfFiles = 0;
	
	private static InvertedIndex myInvertedIndex = new InvertedIndex();
	private static NonInvertedIndex myNonInvertedIndex = new NonInvertedIndex();
	private static QueryIndex myQueryIndex = new QueryIndex();

	private static HashMap<String,Double> accuHash = null;
	private static HashMap<String,Double> dNorm = new HashMap<String, Double>();
	private static HashMap<String,Double> idf = new HashMap<String, Double>();
	private static TreeMap<Integer,HashMap<String,Double>> myResultTreeMap = new TreeMap<Integer,HashMap<String,Double>>(new KeyComparator()); 
	
	public static void main(String[] args) {
		MiniRetrieveGrundrundgeruest myMiniRetrieve = new MiniRetrieveGrundrundgeruest();

		if(args.length == 0){
			myMiniRetrieve.createQueryHash(queryDirectory);
			myMiniRetrieve.createIndexes(documentDirectory);
			myMiniRetrieve.calculateIdfAndNorms();
		}
		else if (args.length ==2){
			myMiniRetrieve.createQueryHash(args[1]);
			myMiniRetrieve.createIndexes(args[0]);
			myMiniRetrieve.calculateIdfAndNorms();
		}
		myMiniRetrieve.processQueries();
		myMiniRetrieve.printResults();
	}//end main-method
	
	
	//	Erstellen des invertierten und nicht-invertierten Index
	private void createIndexes(String directory){
		////////////////////////////////////////
		/////////// Student code ///////////////
		////////////////////////////////////////
	}
	
	private void calculateIdfAndNorms(){
		////////////////////////////////////////
		/////////// Student code ///////////////
		////////////////////////////////////////
	}	
	
	//Verarbeite Anfragen
	private void processQueries(){
		Iterator itQueryIds = myQueryIndex.entrySet().iterator(); //Iterator ueber alle Anfragen
		while (itQueryIds.hasNext()) {
			accuHash = new HashMap<String,Double> (10000);
			qNorm = 0;
			
			Map.Entry queryIdMap = (Map.Entry)itQueryIds.next();
			HashMap currentQuery  = (HashMap) queryIdMap.getValue();
			int queryId = Integer.parseInt(queryIdMap.getKey().toString());
			
			////////////////////////////////////////
			/////////// Student code ///////////////
			////////////////////////////////////////			

			myResultTreeMap.put(queryId, accuHash);
		}
	}
	
	// Normalisiert Laenge der Vektoren
	private void normalizeVectors(NonInvertedIndex myNonInvertedIndex){
		Iterator itAccu = accuHash.entrySet().iterator();
		while (itAccu.hasNext()){
			Map.Entry accuMapEntry = (Map.Entry)itAccu.next();
			String filename = accuMapEntry.getKey().toString();
			////////////////////////////////////////
			/////////// Student code ///////////////
			////////////////////////////////////////
		}		
	}
	
	//Einlesen der Anfragen aus Verzeichnis
	private void createQueryHash(String directory){
		File myDirectory = new File(directory);	
		if(myDirectory.isDirectory() && myDirectory.exists()){
			File[] files =myDirectory.listFiles(); //File-Array mit allen Anfrage-Dateien im Verzeichnis
			for (int i=0;i<files.length;i++) { //alle Files durchlaufen und Inhalt indexieren
				String fileContent = Utilities.readFile(files[i].getAbsolutePath());
				String queryId = files[i].getName(); //entspricht Datei-Namen
				tokenizeQuery(queryId, fileContent);
			} // end for-loop
		} // end if
		else System.out.println("Verzeichniss nicht gefunden. Bitte Verzeichniss korrekt angeben!");
	} // end method createInvertedIndex
	
	//	Anfrage-Tokenisierung und in QueryHash ablegen
	public void tokenizeQuery(String queryId, String queryString){ 
        Pattern p = Pattern.compile("[\\W]+");
        String[] queryTokens = p.split(queryString);      
        for (int j=0; j<queryTokens.length; j++){ 			
			String currentToken = queryTokens[j].trim().toLowerCase(); //in Variable currentToken ist der aktuelle Term aus eniem Dokument
			if(myQueryIndex.containsKey(queryId)){ //Existiert aktuelle Query "queryId" schon
				if(myQueryIndex.get(queryId).containsKey(currentToken)){ //Existiert aktueller Token "currentToken" schon
					int counter = myQueryIndex.getTermFrequencyInOneDocument(queryId,currentToken);
					counter++;
					myQueryIndex.put(queryId,currentToken,counter); //Falls Query und Token schon existieren -> erhoehe Anzahl
				}
				else myQueryIndex.put(queryId,currentToken,1); //Falls Query "queryId" noch nicht existiert (Token aber schon) setze Anzahl auf 1
			}
			else myQueryIndex.put(queryId,currentToken,1);
		}		
	}
	
	private void printResults(){
        Iterator itaccu = myResultTreeMap.entrySet().iterator();
		while(itaccu.hasNext()){
			Map.Entry m = (Map.Entry) itaccu.next();
			HashMap accuHash  = (HashMap) m.getValue();			
		    Utilities.writeTrecResultOutput(m.getKey().toString(), accuHash, numberOfResults);
		}		
	}
}