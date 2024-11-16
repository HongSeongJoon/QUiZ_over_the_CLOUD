package quiz;

import java.io.*;
import java.net.*;

public class QuizClient {
    public static void main(String[] args) {
        String serverAddress = "localhost"; // 기본값: localhost
        int port = 6789; // 기본값: 6789

        // server_info.dat 파일에서 서버 IP와 포트를 읽기
        try {
            String[] config = loadServerConfig("server_info.dat");
            serverAddress = config[0]; // 파일에서 IP 읽기
            port = Integer.parseInt(config[1]); // 파일에서 포트 읽기
            System.out.println("Loaded server configuration: " + serverAddress + ":" + port);
        } catch (IOException e) {
            System.out.println("Configuration file not found. Using default settings.");
        }

        try (
            Socket socket = new Socket(serverAddress, port); // 서버 연결
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())); // 서버로부터 메시지 수신
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true); // 서버로 메시지 송신
            BufferedReader console = new BufferedReader(new InputStreamReader(System.in)) // 사용자 입력 수신
        ) {
            System.out.println("Connected to the quiz server.");

            String question;
            // 퀴즈 세션 진행 루프
            while ((question = in.readLine()) != null && !question.startsWith("Your final score")) {
                System.out.println("Question: " + question); // 서버로부터 받은 질문 출력
                System.out.print("Your answer: ");
                String answer = console.readLine(); // 사용자 입력 대기
                out.println(answer); // 서버로 답변 전송
                System.out.println("Feedback: " + in.readLine()); // 서버로부터 받은 피드백 출력
            }

            // 최종 점수 출력
            System.out.println(question); // "Your final score" 메시지 출력

            // 종료 대기
            System.out.println("\nPress Enter to exit...");
            console.readLine(); // 사용자가 Enter를 누를 때까지 대기

        } catch (IOException e) {
            // 예외 발생 시 오류 메시지 출력
            System.out.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 서버 설정 파일 읽기 메서드
    private static String[] loadServerConfig(String fileName) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String ip = reader.readLine(); // 첫 번째 줄: 서버 IP
            String port = reader.readLine(); // 두 번째 줄: 포트 번호
            return new String[] { ip, port };
        }
    }
}
