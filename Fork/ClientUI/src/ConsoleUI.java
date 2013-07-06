
import java.util.Scanner;

public class ConsoleUI implements ClientUI {

    ClientApi clientApi = new ClientApiImpl();

    String fileDirectory;
    public void displayMenu() throws Exception {

        clientApi.loadSSpaceFile();

        //noinspection InfiniteLoopStatement
        while(true) {
            System.out.println("Welcome to project fork");
            System.out.println("We require your data and query term");
            System.out.println("1. Analyse your data");
            System.out.println("2. Test the project");
            Scanner in = new Scanner(System.in);
            Integer choice = Integer.parseInt(in.next());
            switch(choice) {
                case 1:
                    String userDirectory = getDirectoryName();
                    clientApi.readCollatedData(userDirectory);
                    break;
                case 2:
                    String sentence = clientApi.generateSentence(getQueryTerm());
                    displaySentence(sentence);
                    break;
            }
        }
    }

    private void displaySentence(String sentence) {
        System.out.println("The generated sentence was: "+sentence);
    }

    private String getQueryTerm() {
        System.out.println("Enter query term");
        return getInput();
    }

    private String getDirectoryName() {
        System.out.println("Please enter the directory");
        return getInput();
    }

    private String getInput() {
        Scanner in = new Scanner(System.in);
        return in.nextLine();
    }

}
