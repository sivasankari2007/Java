import java.io.*;
import java.util.*;
import java.util.concurrent.*;

class Question {
    int id;
    String question, optionA, optionB, optionC, optionD, correctAnswer;

    public Question(int id, String q, String a, String b, String c, String d, String ans) {
        this.id = id;
        this.question = q;
        this.optionA = a;
        this.optionB = b;
        this.optionC = c;
        this.optionD = d;
        this.correctAnswer = ans;
    }
}

class TimerThread extends Thread {
    private int timeLimit;
    private boolean timeUp = false;

    public TimerThread(int seconds) {
        this.timeLimit = seconds;
    }

    public void run() {
        try {
            for (int i = timeLimit; i > 0; i--) {
                Thread.sleep(1000);
            }
            timeUp = true;
        } catch (InterruptedException e) {
            // Stopped early
        }
    }

    public boolean isTimeUp() {
        return timeUp;
    }
}

public class OnlineQuizWithLoginAndChoice {
    private static List<Question> questions = new ArrayList<>();
    private static int score = 0;
    private static String currentUser = "";

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("===== 🎓 ONLINE QUIZ SYSTEM =====");

        if (!loginMenu(sc)) {
            System.out.println("👋 Exiting...");
            return;
        }

        loadQuestions("questions.txt");
        if (questions.isEmpty()) {
            System.out.println("⚠️ No questions found! Please check questions.txt");
            return;
        }

        startQuiz(sc);
        saveResult();
    }

    // ---------------- LOGIN MENU ----------------
    private static boolean loginMenu(Scanner sc) {
        while (true) {
            System.out.println("\n1️⃣ Login");
            System.out.println("2️⃣ Register");
            System.out.println("3️⃣ Exit");
            System.out.print("Choose an option: ");
            String choice = sc.nextLine();

            switch (choice) {
                case "1":
                    if (login(sc)) return true;
                    break;
                case "2":
                    register(sc);
                    break;
                case "3":
                    return false;
                default:
                    System.out.println("Invalid option!");
            }
        }
    }

    private static boolean login(Scanner sc) {
        System.out.print("Enter username: ");
        String user = sc.nextLine();
        System.out.print("Enter password: ");
        String pass = sc.nextLine();

        try (BufferedReader br = new BufferedReader(new FileReader("users.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] creds = line.split(",");
                if (creds.length == 2 && creds[0].equals(user) && creds[1].equals(pass)) {
                    System.out.println("✅ Login successful! Welcome " + user + "!");
                    currentUser = user;
                    return true;
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading users file.");
        }
        System.out.println("❌ Invalid username or password!");
        return false;
    }

    private static void register(Scanner sc) {
        System.out.print("Choose username: ");
        String user = sc.nextLine();
        System.out.print("Choose password: ");
        String pass = sc.nextLine();

        try (FileWriter fw = new FileWriter("users.txt", true)) {
            fw.write(user + "," + pass + "\n");
            System.out.println("✅ Registration successful! You can now log in.");
        } catch (IOException e) {
            System.out.println("Error saving user.");
        }
    }

    // ---------------- QUIZ LOGIC ----------------
    private static void loadQuestions(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] q = line.split(",");
                if (q.length == 7) {
                    questions.add(new Question(
                            Integer.parseInt(q[0]), q[1], q[2], q[3], q[4], q[5], q[6]));
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading questions: " + e.getMessage());
        }
    }

    private static void startQuiz(Scanner sc) {
        System.out.println("\n===== 🧠 QUIZ STARTED =====");
        System.out.println("You have 10 seconds for each question.");

        // Ask how many questions user wants
        System.out.print("How many questions would you like to answer (max " + questions.size() + ")? ");
        int count;
        try {
            count = Integer.parseInt(sc.nextLine());
            if (count > questions.size()) count = questions.size();
        } catch (Exception e) {
            count = questions.size();
        }

        // Randomize questions
        Collections.shuffle(questions);
        List<Question> selected = questions.subList(0, count);

        System.out.println("You will be asked " + count + " questions.\n");

        int qNo = 1;
        for (Question q : selected) {
            System.out.println("\nQ" + qNo++ + ": " + q.question);
            System.out.println("A. " + q.optionA);
            System.out.println("B. " + q.optionB);
            System.out.println("C. " + q.optionC);
            System.out.println("D. " + q.optionD);
            System.out.println("Type your answer (A/B/C/D) or type 'exit' to stop:");

            TimerThread timer = new TimerThread(10);
            timer.start();

            ExecutorService ex = Executors.newSingleThreadExecutor();
            Future<String> input = ex.submit(() -> sc.nextLine().trim().toUpperCase());

            String ans = "";
            try {
                ans = input.get(10, TimeUnit.SECONDS);
                timer.interrupt();
            } catch (TimeoutException e) {
                System.out.println("⏰ Time up!");
            } catch (Exception e) {
                System.out.println("Error reading input!");
            } finally {
                ex.shutdownNow();
            }

            if (ans.equalsIgnoreCase("EXIT")) {
                System.out.println("👋 You exited the quiz early!");
                break;
            } else if (ans.equals(q.correctAnswer)) {
                System.out.println("✅ Correct!");
                score++;
            } else if (!ans.isEmpty()) {
                System.out.println("❌ Wrong! Correct answer: " + q.correctAnswer);
            }
        }

        System.out.println("\n===== ✅ QUIZ COMPLETED =====");
        System.out.println("Your Score: " + score + "/" + count);
    }

    private static void saveResult() {
        try (FileWriter fw = new FileWriter("results.txt", true)) {
            fw.write("User: " + currentUser + " | Score: " + score + "/" + questions.size() + " | Date: " + new Date() + "\n");
            System.out.println("Result saved successfully ✅");
        } catch (IOException e) {
            System.out.println("Error saving result: " + e.getMessage());
        }
    }
}

