
import java.io.*;
import java.net.*;
import java.util.*;

public class QuizServer {
    private static final int PORT = 1234;
    private static final List<String> questions = Arrays.asList(
        "What is the capital of France?",
        "What is 2 + 2?",
        "What is the largest planet in the solar system?"
    );
    private static final List<String> answers = Arrays.asList(
        "Paris",
        "4",
        "Jupiter"
    );

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is running on port " + PORT);

            while (true) {
                try (Socket clientSocket = serverSocket.accept()) {
                    System.out.println("Client connected.");
                    handleClient(clientSocket);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket clientSocket) throws IOException {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            int score = 0;
            for (int i = 0; i < questions.size(); i++) {
                out.println(questions.get(i));
                String response = in.readLine();

                if (response != null && response.equalsIgnoreCase(answers.get(i))) {
                    out.println("Correct!");
                    score++;
                } else {
                    out.println("Incorrect.");
                }
            }
            out.println("Your final score is: " + score + "/" + questions.size());
        }
    }
}
