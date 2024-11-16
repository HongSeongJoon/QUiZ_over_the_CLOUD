
import java.io.*;
import java.net.*;

public class QuizClient {
    public static void main(String[] args) {
        String serverAddress = "localhost";
        int port = 1234;

        try (Socket socket = new Socket(serverAddress, port);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader console = new BufferedReader(new InputStreamReader(System.in))) {

            System.out.println("Connected to the quiz server.");

            String question;
            while ((question = in.readLine()) != null && !question.startsWith("Your final score")) {
                System.out.println("Question: " + question);
                System.out.print("Your answer: ");
                String answer = console.readLine();
                out.println(answer);
                System.out.println("Feedback: " + in.readLine());
            }

            System.out.println(question); // Final score
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
