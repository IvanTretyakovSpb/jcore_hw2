import java.util.Random;
import java.util.Scanner;

public class Game {
    private static final Scanner SCANNER = new Scanner(System.in);
    private static final Random RANDOM = new Random();
    private static char[][] field;

    // Константы заменены на переменные для запроса у пользователя настроек игры
    private static int fieldSize;
    private static char dotHuman;
    private static char dotAi;
    private static char dotEmpty;
    // Дополнительная переменная для определения очередности ходов: кто первый начинает
    private static boolean isUserStart;

    public static void main(String[] args) {
        getGameSetting(); // запрос настроек игры у пользователя (размерность поля, символы для клеток, очередность)
        initialize();
        printField();
        while (true) {
            // Добавлена реализация очередности ходов противников - кто первый начинает
            if (isUserStart) {
                humanTurn();
                printField();
                if (gameCheck(dotHuman, "Вы победили!"))
                    break;
                aiTurn();
                printField();
                if (gameCheck(dotAi, "Победил компьютер!"))
                    break;
            } else {
                aiTurn();
                printField();
                if (gameCheck(dotAi, "Победил компьютер!"))
                    break;
                humanTurn();
                printField();
                if (gameCheck(dotHuman, "Вы победили!"))
                    break;
            }

        }
        System.out.println("---".repeat(20));
    }

    private static void getGameSetting() {
        System.out.println("---".repeat(20));
        System.out.println("Для начала игры \"Крестики-нолики\" укажите игровые настройки.");
        System.out.println("Размерность игрового поля: ");
        fieldSize = SCANNER.nextInt();
        System.out.println("Символ для обозначения ваших ходов: ");
        dotHuman = SCANNER.next().charAt(0);
        System.out.println("Символ для обозначения ходов компьютера: ");
        dotAi = SCANNER.next().charAt(0);
        System.out.println("Символ для обозначения пустых клеток: ");
        dotEmpty = SCANNER.next().charAt(0);
        System.out.println("Вы будете ходить первым (Yes/No)?");
        isUserStart = SCANNER.next().toLowerCase().trim().startsWith("yes");
        System.out.println("---".repeat(20));
    }

    private static void initialize() {
        field = new char[fieldSize][fieldSize];
        for (int x = 0; x < fieldSize; x++) {
            for (int y = 0; y < fieldSize; y++) {
                field[x][y] = dotEmpty;
            }
        }
    }

    // При выводе игрового поля убрал лишние символы между номерами строк и столбцов
    private static void printField() {
        System.out.print(" ");
        for (int i = 0; i < fieldSize * 2 + 1; i++) {
            System.out.print((i % 2 == 0) ? " " : i / 2 + 1);
        }
        System.out.println();

        for (int i = 0; i < fieldSize; i++) {
            System.out.print(i + 1 + "|");

            for (int j = 0; j < fieldSize; j++)
                System.out.print(field[i][j] + "|");

            System.out.println();
        }

        for (int i = 0; i < fieldSize * 2 + 2; i++) {
            System.out.print(" ");
        }
        System.out.println();
    }

    private static void humanTurn() {
        int x, y;
        do {
            // Добавлена адаптивность при указании диапазона возможных значений координат
            System.out.printf("Введите координаты хода Х и Y (от 1 до %d), разделенные пробелом: \n", fieldSize);
            x = SCANNER.nextInt() - 1;
            y = SCANNER.nextInt() - 1;
        } while (!isCellValid(x, y) || !isCellEmpty(x, y));
        field[x][y] = dotHuman;
    }

    private static boolean isCellEmpty(int x, int y) {
        return field[x][y] == dotEmpty;
    }

    private static boolean isCellValid(int x, int y) {
        return x >= 0 && x < fieldSize
                && y >= 0 && y < fieldSize;
    }

    private static void aiTurn() {
        int[] res = new int[2]; // Создаём одномерный массив для хранения координат хода компьютера
        do {
            aiThink(res); // Компьютер анализирует ситуацию и выбирает хороший ход
        } while (!isCellEmpty(res[0], res[1]));
        field[res[0]][res[1]] = dotAi;
    }

    // Метод для размышлений компьютера над своим ходом
    private static void aiThink(int[] result) {
        // компьютер ищет вариант, чтобы выиграть самому в один ход
        if (findSmartMove(result, dotAi, dotHuman)) {
            return;
        }
        // ищем вариант не дать выиграть противнику в один ход
        if (findSmartMove(result, dotHuman, dotAi)) {
            return;
        }
        // если не нашлось умных ходов, то делаем рандомный ход
        result[0] = RANDOM.nextInt(fieldSize);
        result[1] = RANDOM.nextInt(fieldSize);
    }

    /**
     * Проверяет наличие ситуации, когда до выигрыша остаётся только один ход
     * и возвращает координаты клетки для полного заполнения горизонтали, вертикали или диагонали поля.
     *
     * @param result      одномерный целочисленный массив для хранения координат поля
     * @param dotForWin   символ, обозначающий ход противника, возможность победить которого проверяется
     * @param dotOpponent символ хода оппонента
     * @return булево значение, информирующее о нахождении подходящего хода:
     * (true - если нашли умный ход, false - если не нашли)
     */
    private static boolean findSmartMove(int[] result, char dotForWin, char dotOpponent) {
        // Проверка возможности выиграть по горизонтали
        for (int i = 0; i < fieldSize; i++) {
            int checkedSum = 0;
            int indexJ = 0;
            for (int j = 0; j < fieldSize; j++) {
                if (field[i][j] == dotForWin) {
                    checkedSum++;
                } else {
                    indexJ = j;
                }
            }
            // Проверяем, что для победы остался только один ход и необходимая клетка не занята противником
            if (checkedSum == fieldSize - 1 && field[i][indexJ] != dotOpponent) {
                // Сохраняем в массиве координаты подходящего хода и извещаем, что нашли умный ход
                result[0] = i;
                result[1] = indexJ;
                return true;
            }
        }

        // Проверка по диагоналям
        int checkedSum1 = 0; // для главной диагонали проверочная сумма символов
        int checkedSum2 = 0; // для второстепенной диагонали
        // Вводим переменные для поиска координат умных ходов по каждой из диагоналей в циклах
        int indexJ1 = 0; // для главной диагонали координаты i, j совпадают
        int indexI2 = 0;
        int indexJ2 = 0;
        for (int i = 0, j = fieldSize - 1; i < fieldSize; i++, j--) {
            if (field[i][i] == dotForWin) {
                checkedSum1++;
            } else {
                indexJ1 = i;
            }
            if (field[i][j] == dotForWin) {
                checkedSum2++;
            } else {
                indexI2 = i;
                indexJ2 = j;
            }
        }
        if (checkedSum1 == fieldSize - 1 && field[indexJ1][indexJ1] != dotOpponent) {
            // Сохраняем в массиве координаты подходящего хода и извещаем, что нашли умный ход
            result[0] = indexJ1;
            result[1] = indexJ1;
            return true;
        }
        // Проверка для второстепенной диагонали на наличие умного хода
        if (checkedSum2 == fieldSize - 1 && field[indexI2][indexJ2] != dotOpponent) {
            result[0] = indexI2;
            result[1] = indexJ2;
            return true;
        }

        // Проверка по вертикалям
        for (int j = 0; j < fieldSize; j++) {
            int checkedSum = 0;
            int indexI = 0;
            for (int i = 0; i < fieldSize; i++) {
                if (field[i][j] == dotForWin) {
                    checkedSum++;
                } else {
                    indexI = i;
                }
            }
            // Проверяем, что для победы остался только один ход и необходимая клетка не занята противником
            if (checkedSum == fieldSize - 1 && field[j][indexI] != dotOpponent) {
                // Сохраняем в массиве координаты подходящего хода и извещаем, что нашли умный ход
                result[0] = indexI;
                result[1] = j;
                return true;
            }
        }
        return false;
    }

    ;

    private static boolean gameCheck(char symbol, String message) {
        if (checkWin(symbol)) {
            System.out.println(message);
            return true;
        }
        if (checkDraw()) {
            System.out.println("В этот раз ничья!");
            return true;
        }
        return false;
    }

    private static boolean checkDraw() {
        for (int x = 0; x < fieldSize; x++) {
            for (int y = 0; y < fieldSize; y++) {
                if (isCellEmpty(x, y)) return false;
            }
        }
        return true;
    }

    // Реализована проверка на выигрыш через циклы.
    // Данная проверка не зависит от размерности поля, которая задаётся в начале игры.
    private static boolean checkWin(char symbol) {
        // Проверка по горизонтали
        for (int i = 0; i < fieldSize; i++) {
            int checkedSum = 0;
            for (int j = 0; j < fieldSize; j++) {
                if (field[i][j] == symbol) {
                    checkedSum++;
                }
            }
            if (checkedSum == fieldSize) {
                return true;
            }
        }
        // Проверка по диагоналям
        int checkedSum1 = 0; // для главной диагонали проверочная сумма символов
        int checkedSum2 = 0; // для второстепенной диагонали
        for (int i = 0, j = fieldSize - 1; i < fieldSize; i++, j--) {
            if (field[i][i] == symbol) {
                checkedSum1++;
            }
            if (field[i][j] == symbol) {
                checkedSum2++;
            }
        }
        if (checkedSum1 == fieldSize || checkedSum2 == fieldSize) {
            return true;
        }

        // Проверка по вертикалям
        for (int j = 0; j < fieldSize; j++) {
            int checkedSum = 0;
            for (int i = 0; i < fieldSize; i++) {
                if (field[i][j] == symbol) {
                    checkedSum++;
                }
            }
            if (checkedSum == fieldSize) {
                return true;
            }
        }
        return false;
    }
}