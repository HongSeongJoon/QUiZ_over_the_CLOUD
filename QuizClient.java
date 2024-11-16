package quiz;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class Quizserver {
    private static String serverIp = "localhost"; // 기본값: localhost
    private static int port = 6789; // 기본값: 6789

    // 퀴즈 질문과 정답 리스트 정의
    private static final List<String> questions = Arrays.asList(
        "What is the capital of France?", // 질문 1
        "What is 2 + 2?", // 질문 2
        "What is the largest planet in the solar system?" // 질문 3
    );
    private static final List<String> answers = Arrays.asList(
        "Paris", // 정답 1
        "4", // 정답 2
        "Jupiter" // 정답 3
    );

    public static void main(String[] args) {
        // server_info.dat 파일에서 서버 IP와 포트를 읽기
        try {
            loadServerConfig("server_info.dat");
        } catch (IOException e) {
            System.out.println("Configuration file not found. Using default IP and port.");
        }

        // 최대 10개의 클라이언트를 동시에 처리하기 위한 스레드 풀 생성
        ExecutorService threadPool = Executors.newFixedThreadPool(10);

        // 서버 소켓 생성 및 실행
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Multi-client quiz server is running on " + serverIp + " port " + port);

            // 클라이언트 연결을 계속 수락
            while (true) {
                // 클라이언트가 서버에 연결 요청 시 소켓 생성
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected!");

                // 클라이언트 요청을 별도의 스레드에서 처리
                threadPool.execute(() -> handleClient(clientSocket));
            }
        } catch (IOException e) {
            // 서버 실행 중 예외 처리
            e.printStackTrace();
        } finally {
            // 스레드 풀 종료
            threadPool.shutdown();
        }
    }

    // 서버 설정 파일에서 IP와 포트를 읽는 메서드
    private static void loadServerConfig(String fileName) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            serverIp = reader.readLine(); // 첫 번째 줄: 서버 IP
            port = Integer.parseInt(reader.readLine()); // 두 번째 줄: 포트 번호
            System.out.println("Loaded configuration: " + serverIp + ":" + port);
        }
    }

    // 클라이언트 요청을 처리하는 메서드
    private static void handleClient(Socket clientSocket) {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); // 클라이언트로부터 메시지 수신
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true) // 클라이언트로 메시지 송신
        ) {
            int score = 0; // 클라이언트 점수 초기화

            // 모든 질문에 대해 반복
            for (int i = 0; i < questions.size(); i++) {
                // 질문 전송
                out.println(questions.get(i));

                // 클라이언트 응답 수신
                String response = in.readLine();

                // 정답 여부 확인 및 피드백 전송
                if (response != null && response.equalsIgnoreCase(answers.get(i))) {
                    out.println("Correct!"); // 정답 피드백
                    score++; // 점수 증가
                } else {
                    out.println("Incorrect."); // 오답 피드백
                }
            }

            // 최종 점수 전송
            out.println("Your final score is: " + score + "/" + questions.size());
        } catch (IOException e) {
            // 클라이언트 처리 중 예외 발생 시 로그 출력
            System.out.println("Error handling client: " + e.getMessage());
        } finally {
            // 클라이언트 소켓 종료
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Client disconnected."); // 연결 종료 메시지 출력
        }
    }
}
