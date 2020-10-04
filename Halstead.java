package sample;

import javafx.util.Pair;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Halstead {

    private ArrayList<String> allFileLines;
    private String typescriptProgram;


    public Halstead(File file) throws IOException {
        allFileLines = (ArrayList<String>) Files.readAllLines(file.toPath());

        StringBuilder sb = new StringBuilder();
        for (String s : allFileLines) {
            sb.append(s);
            sb.append("\n");
        }

        typescriptProgram = sb.toString();

        final String id = "[A-z$_&&[^0-9]][A-z$_0-9]+";
        //Множество операндов
        operands = new HashMap<>();
        //Множество операторов
        HashMap<String, Integer> operators = new LinkedHashMap<>();
        //Множество ключевых слов
        HashSet<String> keyWords = new HashSet<>();
        //Biba - это строка с кодом программы целиком

        //Здесь мы избавляемся от всех комментариев в коде программы
        typescriptProgram = typescriptProgram.replaceAll("(?s:/\\*.*?\\*/)|//.*", "");

        //Здесь мы ищем строковые литералы
        Matcher m = Pattern.compile("\"(?:\\\\\"|[^\"])*?\"").matcher(typescriptProgram);
        while (m.find()) {
            //Каждый строковы    литерал ммы добавляем в множество операндов
            //operands.put(m.group());
            String buf = m.group();
            if (!operands.containsKey(buf)) {
                operands.put(buf, 1);
            } else {
                operands.put(buf, operands.get(buf) + 1);
            }
        }


        //Теперь в исходном коде программы мы удаляем все строковые литералы
        typescriptProgram = typescriptProgram.replaceAll("\"(?:\\\\\"|[^\"])*?\"", "");

        //Из файла считываем все основые операторы и ключ слова
        Files.lines(Paths.get("keyWords.txt")).forEach(line -> keyWords.add(line));
        Files.lines(Paths.get("operators.txt")).forEach(line -> operators.put(line, 0));


        //Сюда будем класть "обрезанные методы"
        HashMap<String, Integer> methods = new HashMap<>();


        //Добавление пробелов перед и после скобок, замена кучи пробелов одним
        typescriptProgram = typescriptProgram.replace("(", "( ").replace(")", " )").replaceAll(" +", " ");

        //Удаление классов(типов)
        String[] lines = typescriptProgram.split("\\n");
        for (String line : lines) {
            String[] words = line.split(" +");
            for (int i = 0; i < words.length - 1; i++) {
                if (words[i].matches(id) && words[i + 1].matches(id) && !(keyWords.contains(words[i]) || operators.containsKey(words[i]) || keyWords.contains(words[i + 1]))||words[i].contains("let")) {
                    typescriptProgram = typescriptProgram.replace(words[i] + " " + words[i + 1], words[i + 1]);
                }

            }
        }

        //Удаление классов(объявление)
        typescriptProgram = typescriptProgram.replaceAll("class\\s+" + id + "\\s*[{]", " ");


        //Подсчет и удаление методов и управляющих операторов(все что со скобочками)
        sb = new StringBuilder(typescriptProgram);
        Pattern p = Pattern.compile(id + "\\s*" + "[(]");
        m = p.matcher(sb);
        while (m.find()) {
            //Получаем имя метода/оператора
            String match = m.group().split("[\\s(]+")[0];
            if (methods.containsKey(match)) {
                int i = methods.get(match);
                methods.put(match, i + 1);
            } else {
                methods.put(match, 1);
            }
            //Удаление
            sb.delete(m.start(), m.end());
            m = p.matcher(sb);
        }

        HashSet<String> operatorsWithBraces = new HashSet<>();
        Files.lines(Paths.get("operatorsWithBraces.txt")).forEach(line -> operatorsWithBraces.add(line));
        for (String op : operatorsWithBraces) {
            p = Pattern.compile(op + "\\s*[{]");
            m = p.matcher(sb);
            while (m.find()) {
                int temp = 1;
                if (operators.containsKey(op)) {
                    temp += operators.get(op);
                }
                operators.put(op, temp);
                sb.delete(m.start(), m.end() - 1);
                m = p.matcher(sb);
            }
        }

        typescriptProgram = sb.toString();

        //Удаление ключ слов(почти)
        for (String keyWord : keyWords) {
            typescriptProgram = typescriptProgram.replaceAll("[\\s\n]+" + keyWord + "\\s+", " ");
        }

        sb = new StringBuilder(typescriptProgram);

//Подсчет и удаление оставшихся операторов
        for (String op : operators.keySet()) {
            int start = 0;
            while (start != -1) {
                start = sb.indexOf(op, start);
                if (start != -1) {
                    operators.put(op, operators.get(op) + 1);
                    sb.replace(start, start + op.length(), " ");
                    start += op.length();
                }
            }
        }

        typescriptProgram = sb.toString().trim();
        //Подсчет оставшихся операндов
        String[] ops = typescriptProgram.split("\\s+");
        for (String operand : ops) {
            if (operands.containsKey(operand)) {
                operands.put(operand, operands.get(operand) + 1);
            } else {
                operands.put(operand, 1);
            }
        }


//        System.out.println(operands);
//        System.out.println(methods);
//        System.out.println(operators);

        int doCount = 0;
        int whileCount = 0;
        int opCount = 0;

        //Подсчет словаря операторов
        for (Map.Entry<String, Integer> it : operators.entrySet()) {
            if (it.getValue() != 0 && !(it.getKey().equals("}")
                    || it.getKey().equals("]")
                    || it.getKey().equals(")")
                    || it.getKey().equals(":")
                    || it.getKey().equals("else")
                    || it.getKey().equals("case"))) {
                dictOperatorCount++;
                opCount += it.getValue();
            }
        }

        dictOperatorCount += methods.size();

        if (operators.containsKey("do"))
            doCount = operators.get("do");
        if (methods.containsKey("while"))
            whileCount = methods.get("while");

        if (doCount == whileCount && doCount != 0)
            dictOperatorCount--;

        dictOperandCount = operands.size();

        //Подсчет количества операторов
        for (Map.Entry<String, Integer> it : methods.entrySet()) {
            opCount += it.getValue();
        }

        opCount -= doCount;
        operatorCount = opCount;

        for (Map.Entry<String, Integer> it : operands.entrySet()) {
            operandCount += it.getValue();
            resSB.append(it.getKey() + " - " + it.getValue() + "\n");
        }

//        System.out.println(dictOperatorCount + " !!!!!!!!!!!!!!!!");
//        System.out.println(dictOperandCount + " !!!!!!!!!!!!!!!!");
//        System.out.println(operatorCount + " !!!!!!!!!!!!!!!!");
//        System.out.println(operandCount + " !!!!!!!!!!!!!!!!!");

        dictProgram = dictOperandCount + dictOperatorCount;
        programLength = operandCount + operatorCount;
        volumeLength = programLength * Math.log10(dictProgram) * 3.31;

//        System.out.println(dictProgram);
//        System.out.println(programLength);
//        System.out.println(volumeLength);

//        System.out.println(methods);
//        System.out.println(operators);

        operatorsArrayList = new ArrayList<>();
        for (Map.Entry<String, Integer> it : operators.entrySet()) {
            if (it.getKey().equals("else") ||
                    it.getKey().equals("case ") ||
                    it.getKey().equals(")") ||
                    it.getKey().equals("}") ||
                    it.getKey().equals("]") ||
                    it.getKey().equals(":")) continue;
            if (it.getValue() != 0) {
                if (it.getKey().equals("[")) {
                    operatorsArrayList.add(new PairEntry("[..]", it.getValue()));
                } else if (it.getKey().equals("?[")) {
                    operatorsArrayList.add(new PairEntry("?[..]", it.getValue()));
                } else if (it.getKey().equals("do")) {
                    operatorsArrayList.add(new PairEntry("do{..}while(..);", it.getValue()));
                } else if (it.getKey().equals("(")) {
                    operatorsArrayList.add(new PairEntry("(..)", it.getValue()));
                } else if (it.getKey().equals("{")) {
                    operatorsArrayList.add(new PairEntry("{..}", it.getValue()));
                } else if (it.getKey().equals("?")) {
                    operatorsArrayList.add(new PairEntry("...?...:...", it.getValue()));
                } else {
                    operatorsArrayList.add(new PairEntry(it.getKey(), it.getValue()));
                }
            }
        }

        for (Map.Entry<String, Integer> it : methods.entrySet()) {
            if (it.getValue() != 0) {
                if (it.getKey().equals("while")) continue;
                if (it.getKey().equals("else")) continue;
                ;

                if (it.getKey().equals("if")) {
                    operatorsArrayList.add(new PairEntry("if(..){..}else{..}", it.getValue()));
                } else if (it.getKey().equals("switch")) {
                    operatorsArrayList.add(new PairEntry("switch(..){ case... }", it.getValue()));
                } else {
                    operatorsArrayList.add(new PairEntry(it.getKey() + "(..)", it.getValue()));
                }
            }
        }

        if (whileCount - doCount > 0) {
            operatorsArrayList.add(new PairEntry("while(..){..}", whileCount - doCount));
        }

    }

    public int dictOperatorCount = 0;
    public int dictOperandCount = 0;
    public int operatorCount = 0;
    public int operandCount = 0;
    public int dictProgram = 0;
    public int programLength = 0;
    public double volumeLength = 0;
    public StringBuilder resSB = new StringBuilder();


    public HashMap<String, Integer> operands;
    public ArrayList<PairEntry> operatorsArrayList;
}
