import java.io.IOException;
import java.util.Scanner;
import java.util.*;

public class Main {
    public static String calc(String input) throws Exception {
        IConverter converter = new Converter(input);
        converter.checkOperation();
        EOperations operation = converter.convertInputOper();
        converter.checkException();
        int[] operands = converter.convertInputVal();
        ICalcSystem calculator = new CalcSystem(operands, operation);
        return converter.convertResult(calculator.calculate());
    }
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.println("Введите строку запроса");
        try {
            System.out.println(calc(in.nextLine()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
    enum EOperations
    {
        Sum,
        Div,
        Sub,
        Mul,
        Default
    }
    interface IConverter
    {
        void checkException() throws Exception;
        String convertResult(int result);
        int[] convertInputVal() throws Exception;
        EOperations convertInputOper();
        void checkOperation() throws Exception;
    }
     class Converter implements IConverter
    {
        String input;
        int index;
        boolean isRoma;
        public Converter(String input)
        {
            this.input = input.replace(" ", "");
        }
        public EOperations convertInputOper()
        {
            EOperations operation = EOperations.Default;
            int i = 0;
            for (char c : input.toCharArray())
            {
                switch (c) // Определение операции
                {
                    case '+':
                        operation = EOperations.Sum;
                        index = i;
                        return operation;
                    case '-':
                        operation = EOperations.Sub;
                        index = i;
                        return operation;
                    case '*':
                        operation = EOperations.Mul;
                        index = i;
                        return operation;
                    case '/':
                        operation = EOperations.Div;
                        index = i;
                        return operation;
                }
                i++;
            }
            return operation;
        }
        public int[] convertInputVal() throws Exception
        {
            int[] result = new int[2];
            String a = input.substring(0,index);
            String b = input.substring(index+1);
            result[0] = Integer.parseInt(a);
            result[1] = Integer.parseInt(b);
            return result;
        }
        public String convertResult(int result)
        {
            if (isRoma){
                IRomaConverter converter = new RomaConverter();
                return converter.convertIntoRoma(result);
            }
            else {
                return String.valueOf(result);
            }

        }

        public void checkOperation() throws Exception
        {
            if (
                    (input.contains("+") && countMatches(input, '+') != 1) ^
                    (input.contains("-") && countMatches(input, '-') != 1) ^
                    (input.contains("*") && countMatches(input, '*') != 1) ^
                    (input.contains("/")&& countMatches(input, '/') != 1) ^
                    !(input.contains("+") || input.contains("-") || input.contains("/") || input.contains("*"))
            )
            {
                throw new IOException("Неверный формат операции в строке");
            }
        }
        public void checkException() throws Exception
        {
            if (input.isEmpty()){
                throw new IOException("Пустая строка");
            }

            String a = input.substring(0,index);
            String b = input.substring(index+1);

            IRomaConverter romaConverter = new RomaConverter();

            if (checkArabFormat(a) && checkArabFormat(b)){
                if (Integer.parseInt(b) == 0 && input.contains("/")){
                    throw new IOException("На ноль делить нельзя!");
                }
                if ((Integer.parseInt(a) > 10 || Integer.parseInt(a) < 1) ||
                        (Integer.parseInt(b) > 10 || Integer.parseInt(b) < 1)){
                    throw new IOException("Умеет читать числа только от 1 до 10 включительно");
                }
            }
            else
            if (checkRomaFormat(a) && checkRomaFormat(b)){
                isRoma = true;
                if (
                        (
                                (romaConverter.convertIntoArab(a) > romaConverter.convertIntoArab(b)) &&
                                (input.contains("/") || input.contains("-"))
                        )
                        ||
                        (
                                (input.contains("*") || input.contains("+"))
                        )
                   )
                {
                    if ((romaConverter.convertIntoArab(a) > 10 || romaConverter.convertIntoArab(a) < 1) ||
                            romaConverter.convertIntoArab(b) > 10 || romaConverter.convertIntoArab(b) < 1){
                        throw new IOException("Умеет читать числа только от 1 до 10 включительно");
                    }
                    else {
                        input = "";
                        input += romaConverter.convertIntoArab(a);
                        index = input.length();
                        input += "_";
                        input += romaConverter.convertIntoArab(b);
                    }
                }
                else{
                    throw new IOException("В римской системе нет неположительных чисел");
                }

            }
            else{
                throw new IOException("Числа неверного формата");
            }

        }
        boolean checkArabFormat(String value){
            try {
                Integer.parseInt(value);
            }
            catch (NumberFormatException e) {
                return false;
            }
            return true;
        }
        boolean checkRomaFormat(String value){
            return value.matches("^(M{0,3})(D?C{0,3}|C[DM])(L?X{0,3}|X[LC])(V?I{0,3}|I[VX])$");
        }
        int countMatches(String input, char symbol){
            int counter = 0;
            for (char c : input.toCharArray()) {
                if (c == symbol){
                    counter++;
                }
            }
            return counter;
        }
    }
    interface ICalcSystem
    {
        int calculate();
    }
    class CalcSystem implements ICalcSystem
    {
        int[] values;
        EOperations operation;
        public CalcSystem(int[] values, EOperations operation)
        {
            this.values = values;
            this.operation = operation;
        }
        public int calculate()
        {
            switch (operation)
            {
                case Sum:
                    return sum();
                case Div:
                    return div();
                case Sub:
                    return sub();
                case Mul:
                    return mul();
                default:
                    return 0;
            }
        }
        private int div()
        {
            return values[0] / values[1];
        }
        private int mul()
        {
            return values[0] * values[1];
        }
        private int sub()
        {
            return values[0] - values[1];
        }
        private int sum()
        {
            return values[0] + values[1];
        }
    }
interface IRomaConverter
{
    int convertIntoArab(String input);
    String convertIntoRoma(int input);
}
class RomaConverter implements IRomaConverter
{
    public int convertIntoArab(String input) {
        int arabNumber = 0;
        StringBuilder romanPattern = new StringBuilder();
        for (Integer key : units.descendingKeySet()) {
            while (input.startsWith(units.get(key))){
                arabNumber += key;
                input = input.substring(units.get(key).length());
            }
        }
        return arabNumber;
    }
    public String convertIntoRoma(int input) {
        if (input >= 4000 || input <= 0)
            return null;
        StringBuilder result = new StringBuilder();
        for(Integer key : units.descendingKeySet()) {
            while (input >= key) {
                input -= key;
                result.append(units.get(key));
            }
        }
        return result.toString();
    }
    static final NavigableMap<Integer, String> units;
    static {
        NavigableMap<Integer, String> initMap = new TreeMap<>();
        initMap.put(1000, "M");
        initMap.put(900, "CM");
        initMap.put(500, "D");
        initMap.put(400, "CD");
        initMap.put(100, "C");
        initMap.put(90, "XC");
        initMap.put(50, "L");
        initMap.put(40, "XL");
        initMap.put(10, "X");
        initMap.put(9, "IX");
        initMap.put(5, "V");
        initMap.put(4, "IV");
        initMap.put(1, "I");
        units = Collections.unmodifiableNavigableMap(initMap);
    }
}
