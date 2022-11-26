import java.util.Objects;
import java.util.Scanner;

public class Main {

    public static int pveGame(int player, boolean hard) {
        Game s = new Game();
        while (s.isActive()) {
             if (s.getRound() % 2 == player) {
                 s.playerRound();
             } else if (hard) {
                 s.hardAIRound();
             } else {
                 s.easyAIRound();
             }
        }
        MyPair res = s.printResult();
        if (player == 0) {
            return res.getFirst();
        }
        return res.getSecond();
    }

    static void pvpGame() {
        Game s = new Game();
        while (s.isActive()) {
            s.playerRound();
        }
        s.printResult();
    }


    public static void main(String[] args) {
        int pvpMax = -1;
        String pvpName = "";
        Scanner in = new Scanner(System.in);
        while (true) {
            System.out.println("Вы хотите играть против компьютера или игрока?(PVE/PVP) По умолчанию PVE.");
            String comand = in.nextLine();
            if (!Objects.equals(comand, "PVP")) {
                System.out.println("Вы хотите играть за черных?(YES/NO) По умолчанию YES.");
                String player = in.nextLine();
                System.out.println("Вы сложного противника?(YES/NO) По умолчанию NO.");
                String hard = in.nextLine();
                int res = pveGame(!Objects.equals(player, "NO")? 0 : 1, Objects.equals(hard, "YES"));
                if (res > pvpMax) {
                    pvpName = in.nextLine();
                    pvpMax = res;
                } else {
                    System.out.printf("У пользователя %s был ркзультат лучше: %d.\n", pvpName, res);
                }
            } else {
                pvpGame();
                System.out.println("В текущем режиме лидер не определяется).");
            }

        }
    }
}