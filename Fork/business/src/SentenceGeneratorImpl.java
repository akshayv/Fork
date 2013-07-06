import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class SentenceGeneratorImpl implements SentenceGenerator {

    final private String SPACE = " ";

    private Log logger = LogFactory.getLog(FileAnalyserImpl.class);
    private SSpaceClientApi sSpaceClientApi;
    private DataCollector dataCollector;
    private String collatedTrainingData = "";


    public SentenceGeneratorImpl(SSpaceClientApi sSpaceClientApi, DataCollector dataCollector) {
        this.sSpaceClientApi = sSpaceClientApi;
        this.dataCollector = dataCollector;
    }

    public void setCollatedData(File directory) throws IOException {
        collatedTrainingData = SPACE + dataCollector.getCollatedData(directory).toLowerCase();
    }

    @Override
    public String generateSentence(String query) throws IOException {
        String sentence = SPACE;
        List<String> currentWords = new ArrayList<String>();
        final String DELIMITER = "</S>";
        String generatedWord = "";
        sentence = setInitialWord(query, sentence, currentWords);

        while((generatedWord == null) || !generatedWord.equals(DELIMITER)) {
            generatedWord = generateNextWord(currentWords);
            if(generatedWord == null) {
                currentWords = currentWords.subList(1, currentWords.size());
                continue;
            }
            sentence = addWordToSentence(generatedWord, sentence);
            System.out.println(sentence);
            setCurrentWords(generatedWord, currentWords);

        }
        return sentence;
    }

    private String addWordToSentence(String generatedWord, String sentence) {
        return sentence + generatedWord + SPACE;
    }

    private String setInitialWord(String word, String sentence, List<String> currentWords) {
        currentWords.add(word);
        return addWordToSentence(word, sentence);
    }

    private String generateNextWord(List<String> currentWords)
    {
        Map<String, Double> neighbourStatistics = new LinkedHashMap<String, Double>();

        String queryTerms = SPACE;

        for (String currentWord : currentWords) {
            queryTerms = SPACE + currentWord + queryTerms;
            neighbourStatistics = getAllRightNeighboursWithCount(neighbourStatistics, queryTerms);
            if(neighbourStatistics.isEmpty()) {
                return null;
            }
            for (String word : neighbourStatistics.keySet()) {
              neighbourStatistics.put(word, neighbourStatistics.get(word) / 2);
            }
        }

        neighbourStatistics = sortNeighboursBasedOnFreq(neighbourStatistics);

        String generatedWord = generateAndSelectAlternatives(selectRandomNeighbour(neighbourStatistics));
        logger.info("Generated next word was :"+generatedWord);
        return generatedWord;
    }

    private Map<String, Double> sortNeighboursBasedOnFreq(Map<String, Double> neighbourStatistics) {

        //noinspection unchecked
        List list = new LinkedList(neighbourStatistics.entrySet());

        // sort list based on comparator
        Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
                //noinspection unchecked
                return ((Comparable) ((Map.Entry) (o2)).getValue())
                        .compareTo(((Map.Entry) (o1)).getValue());
            }
        });

        // put sorted list into map again
        //LinkedHashMap make sure order in which keys were inserted
        Map bufferMap = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            //noinspection unchecked
            bufferMap.put(entry.getKey(), entry.getValue());
        }
        //noinspection unchecked
        return bufferMap;

    }

    private void setCurrentWords(String generatedWord, List<String> currentWords) {
        if(currentWords.size() > 2)
            currentWords.remove(currentWords.size() - 1);
        currentWords.add(0, generatedWord);
    }

    private String generateAndSelectAlternatives(String queryTerm) {
        logger.info("Generating alternatives for :" + queryTerm);
        List<String> alternatives = getAlternatives(queryTerm);
        int maxPossibilities = 5;
        if(alternatives.size() < 5)
            maxPossibilities = alternatives.size();
        return alternatives.get(new Random().nextInt(maxPossibilities));
    }

    private String selectRandomNeighbour(Map<String, Double> neighbourStatistics) {
        int maxPossibilities = 5;
        if(neighbourStatistics.size() < 5)
            maxPossibilities = neighbourStatistics.size();
        int number = new Random().nextInt(maxPossibilities);
        return (neighbourStatistics.keySet().toArray())[number].toString();
    }


    private List<String> getAlternatives(String word)
    {
       return sSpaceClientApi.getNearestNeighboursEqualProbability(word);
    }

    private Map<String, Double> getAllRightNeighboursWithCount(Map<String, Double> neighbourStatistics, String term)
    {
        String analysedText = collatedTrainingData;
        while(analysedText.contains(term)) {
        analysedText = analysedText.substring(analysedText.indexOf(term) + term.length());
        int nextWordEndIndex = analysedText.indexOf(SPACE);
        String nextWord = analysedText.substring(0, nextWordEndIndex);
        if(!neighbourStatistics.keySet().contains(nextWord))
            neighbourStatistics.put(nextWord, 1.0);
        else
            neighbourStatistics.put(nextWord, neighbourStatistics.get(nextWord) + 1);
        }
        return neighbourStatistics;
    }

//    private Map<String, Double> getAllLeftNeighboursWithCount(Map<String, Double> neighbourStatistics, String term)
//    {
//        String analysedText = collatedTrainingData;
//        while(analysedText.contains(term)) {
//            analysedText = analysedText.substring(analysedText.indexOf(term) + term.length() + 1);
//            int nextWordEndIndex = analysedText.indexOf(SPACE);
//            String nextWord = analysedText.substring(0, nextWordEndIndex);
//            if(!neighbourStatistics.keySet().contains(nextWord))
//                neighbourStatistics.put(nextWord, 1.0);
//            else
//                neighbourStatistics.put(nextWord, neighbourStatistics.get(nextWord) + 1);
//        }
//        return neighbourStatistics;
//    }

}
