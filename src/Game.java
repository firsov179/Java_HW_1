import java.util.Objects;
import java.util.Scanner;

public class Game {
    private PersistentInt[][] map = new PersistentInt[8][8];
    private int round;
    private boolean options;
    private boolean finished;

    public Game() {
        options = false;
        finished = false;
        round = 0;
        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < 8; ++j) {
                map[i][j] = new PersistentInt();
            }
        }
        map[3][3].add(1, -1);
        map[4][4].add(1, -1);
        map[3][4].add(2, -1);
        map[4][3].add(2, -1);
        updateOptions();
    }

    public boolean isActive() {
        return !finished;
    }

    public int getRound() {
        return round;
    }

    public void cancel() {
        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < 8; ++j) {
                map[i][j].remove(round);
            }
        }
    }

    public double easyAIRound() {
        MyPair ans = new MyPair(-1, -1);
        double ansCount = -1;
        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < 8; ++j) {
                if (map[i][j].get() == 3) {
                    double curCount = updateColors(i, j);
                    cancel();
                    if (curCount > ansCount) {
                        ansCount = curCount;
                        ans = new MyPair(i, j);
                    }
                }
            }
        }
        updateColors(ans.getFirst(), ans.getSecond());
        round++;
        updateOptions();
        return ansCount;
    }

    public void hardAIRound() {
        if (round == 59) {
            easyAIRound();
            return;
        }
        MyPair ans = new MyPair(-1, -1);
        double ansCount = -100;
        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < 8; ++j) {
                if (map[i][j].get() == 3) {
                    double curCount = updateColors(i, j);
                    round++;
                    updateOptions();
                    curCount -= easyAIRound();
                    round--;
                    cancel();
                    round--;
                    cancel();
                    if (curCount > ansCount) {
                        ansCount = curCount;
                        ans = new MyPair(i, j);
                    }
                    updateOptions();
                }
            }
        }
        updateColors(ans.getFirst(), ans.getSecond());
        round++;
        updateOptions();
    }

    public void playerRound() {
        print();
        while (true) {
            MyPair input = read();
            if (map[input.getFirst()][input.getSecond()].get() == 3) {
                updateColors(input.getFirst(), input.getSecond());
                round++;
                break;
            }
            System.out.println("В эту ячейку нельзя сходить.");
        }
        updateOptions();
        print();
    }

    public MyPair printResult() {
        int couBlack = 0;
        int couWhite = 0;
        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < 8; ++j) {
                if (map[i][j].get() == 1) {
                    couBlack++;
                } else if (map[i][j].get() == 2) {
                    couWhite++;
                }
            }
        }
        System.out.println("Игра окончена!");
        System.out.printf("Победили %s! Со счетом %d : %d.\n", (couBlack > couWhite ? "Черные" : "Белые"),
                Math.max(couBlack, couWhite), Math.min(couBlack, couWhite));
        return new MyPair(couBlack, couWhite);
    }

    private void help() {
        System.out.println("Для отображения/скрытия вариантов следующего хода введите `\\options`");
        System.out.println("Для вывода игрового поля введите `\\map`");
        System.out.println("Для отмены последних 2 раундов введите `\\back`");
    }

    private MyPair read() {
        Scanner in = new Scanner(System.in);
        while (true) {
            System.out.println("Ваш ход. Введите число от 1 до 8 и латинскую букву от A до H через пробел.");
            System.out.println("Для вывода посказки введите `\\help`.");
            String inp = in.nextLine();
            if (Objects.equals(inp, "\\help")) {
                help();
            } else if (Objects.equals(inp, "\\back")) {
                if (round == 0) {
                    System.out.println("Еще не было ходов.");
                    continue;
                }
                cancel();
                round--;
                cancel();
                round--;
                cancel();
                print();
            } else if (Objects.equals(inp, "\\options")) {
                if (!options) {
                    options = true;
                    System.out.println("Теперь подсказки видны.");
                } else {
                    options = false;
                    System.out.println("Теперь подсказки не видны.");
                }
                print();
            } else if (Objects.equals(inp, "\\map")) {
                print();
            } else {
                byte[] input = inp.getBytes();
                if (inp.length() == 3 && input[0] < '9' && input[0] > '0'
                        && ((input[2] <= 'H' && input[2] >= 'A') || (input[2] <= 'h' && input[2] >= 'a'))) {
                    int i = 8 - input[0] + '0';
                    int j = (input[2] >= 'a' ? input[2] - 'a' : input[2] - 'A');
                    return new MyPair(i, j);
                }
                System.out.println("Некорректный ввод.");
            }
        }
    }

    private double updateColors(int i, int j) {
        double res = 0;
        if ((i == 0 || i == 7) && (j == 0 || j == 7)) {
            res = 0.8;
        } else if ((i == 0 || i == 7) || (j == 0 || j == 7)) {
            res = 0.4;
        }

        map[i][j].add(1 + round % 2, round);
        for (int i_delta = -1; i_delta < 2; ++i_delta) {
            for (int j_delta = -1; j_delta < 2; ++j_delta) {
                int x = i + i_delta;
                int y = j + j_delta;
                if (x >= 8 || x <= -1 || y >= 8 || y <= -1) {
                    continue;
                }
                while (map[x][y].get() == 1 + (round + 1) % 2) {
                    x += i_delta;
                    y += j_delta;
                    if (x == 8 || x == -1 || y == 8 || y == -1) {
                        break;
                    }
                }
                if (!((x == 8 || x == -1 || y == 8 || y == -1)) && map[x][y].get() == 1 + round % 2) {
                    while (x != i || y != j) {
                        x -= i_delta;
                        y -= j_delta;
                        map[x][y].add(1 + round % 2, round);
                        if ((x == 0 || x == 7) && (y == 0 || y == 7)) {
                            res += 4;
                        } else if ((x == 0 || x == 7) || (y == 0 || y == 7)) {
                            res += 2;
                        } else {
                            res += 1;
                        }
                    }
                }
            }
        }
        return res;
    }

    private char symbol(int n) {
        if (n == 1) {
            return 'B';
        }
        if (n == 2) {
            return 'W';
        }
        if (options && n == 3) {
            return '*';
        }
        return ' ';
    }

    private void print() {
        System.out.printf("              round %d            \n", round);
        System.out.println("+---+---+---+---+---+---+---+---+---+");
        for (int i = 0; i < 8; ++i) {
            System.out.print("| " + (8 - i) + " ");
            for (int j = 0; j < 8; ++j) {
                System.out.print("| " + symbol(map[i][j].get()) + " ");
            }
            System.out.println("|");
            System.out.println("+---+---+---+---+---+---+---+---+---+");
        }
        System.out.println("|   | A | B | C | D | E | F | G | H |");
        System.out.println("+---+---+---+---+---+---+---+---+---+");
    }

    private void updateOptions() {
        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < 8; ++j) {
                if (map[i][j].get() == 3) {
                    map[i][j].add(0, round);
                }
            }
        }
        finished = true;
        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < 8; ++j) {
                if (map[i][j].get() == 1 + round % 2) {
                    for (int i_delta = -1; i_delta < 2; ++i_delta) {
                        for (int j_delta = -1; j_delta < 2; ++j_delta) {
                            markPossibleField(i, j, i_delta, j_delta);
                        }
                    }
                }
            }
        }
    }

    private void markPossibleField(int i, int j, int i_delta, int j_delta) {
        i += i_delta;
        j += j_delta;
        if (i == 8 || i == -1 || j == 8 || j == -1) {
            return;
        }
        if (map[i][j].get() != 1 + (round + 1) % 2) {
            return;
        }
        while (map[i][j].get() == 1 + (round + 1) % 2) {
            i += i_delta;
            j += j_delta;
            if (i == 8 || i == -1 || j == 8 || j == -1) {
                return;
            }
        }
        if (map[i][j].get() == 0) {
            finished = false;
            map[i][j].add(3, round);
        }
    }
}
