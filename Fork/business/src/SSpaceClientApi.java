import java.io.File;
import java.io.IOException;
import java.util.*;

import edu.ucla.sspace.common.SemanticSpace;
import edu.ucla.sspace.common.SemanticSpaceIO;
import edu.ucla.sspace.ri.RandomIndexing;
import edu.ucla.sspace.text.Document;
import edu.ucla.sspace.util.NearestNeighborFinder;
import edu.ucla.sspace.util.SimpleNearestNeighborFinder;
import edu.ucla.sspace.util.SortedMultiMap;


public class SSpaceClientApi {

    SemanticSpace sSpace = new RandomIndexing(System.getProperties());
    final int NUMBER_OF_NEIGHBOURS = 5;

    public void readSpace(File fileName) throws IOException {
       sSpace =  SemanticSpaceIO.load(fileName);
    }

    public void performRandomIndexing(Iterator<Document> documents, File outputDirectory) throws IOException,
            InterruptedException {
        performAnalysis(documents, sSpace);
        File outputFile = new File(outputDirectory + ".sSpace");
        saveSSpace(sSpace, outputFile);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void saveSSpace(SemanticSpace sSpace, File outputFile) throws IOException {
        outputFile.getParentFile().mkdirs();
        outputFile.createNewFile();
        SemanticSpaceIO.save(sSpace, outputFile, SemanticSpaceIO.SSpaceFormat.TEXT);
    }

    private void performAnalysis(Iterator<Document> documents, SemanticSpace sSpace) throws IOException, InterruptedException {
        parseDocumentsMultiThreaded(sSpace,documents);
        sSpace.processSpace(System.getProperties());
    }

    @SuppressWarnings("ConstantConditions")
    private void parseDocumentsMultiThreaded(final SemanticSpace sSpace, final Iterator<Document> documents)
            throws IOException, InterruptedException {
        while (documents.hasNext()) {
            Document doc = documents.next();
            try {
                sSpace.processDocument(doc.reader());
            } catch (Throwable t) {
                t.printStackTrace();
        }
      }
    }

    public SortedMultiMap<Double,String> getNearestNeighbours(String queryTerm)
    {
        NearestNeighborFinder currentNnf = new SimpleNearestNeighborFinder(sSpace);
        return currentNnf.getMostSimilar(queryTerm, NUMBER_OF_NEIGHBOURS);
    }

    public List<String> getNearestNeighboursEqualProbability(String queryTerm)
    {
        List<String> neighbours = new ArrayList<String>();
        SortedMultiMap<Double,String> nearestNeighbours = getNearestNeighbours(queryTerm);
        if(nearestNeighbours != null &&  nearestNeighbours.size() > 0)
         for (Map.Entry<Double,String> e : nearestNeighbours.entrySet()) {
             neighbours.add(e.getValue());
         }
        return neighbours;
    }
}