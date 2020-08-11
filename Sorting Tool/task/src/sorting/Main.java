package sorting;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

class InputParameters {
    private String dataType = "word";
    private String sortingType = "natural";
    private String inputFile = "nofile";
    private String outputFile = "nofile";

    String getDataType() {
        return dataType;
    }

    private void setDataType(String dataType) {
        this.dataType = dataType;
    }

    String getSortingType() {
        return sortingType;
    }

    private void setSortingType(String sortingType) {
        this.sortingType = sortingType;
    }

    boolean isCommand(String command) {
        return command.charAt(0) == '-';
    }

    String getInputFile() {
        return inputFile;
    }

    private void setInputFile(String inputFile) {
        this.inputFile = inputFile;
    }

    String getOutputFile() {
        return outputFile;
    }

    private void setOutputFile(String outputFile) {
        this.outputFile = outputFile;
    }

    void initParams(InputParameters input, String[] args) {
        for (int i = 0; i < args.length; i += 2) {
            if (i + 1 >= args.length || isCommand(args[i + 1])) {
                if (isCommand(args[i])) {
                    System.out.println(args[i] + " is not defined!");
                    System.exit(0);
                }
            }

            switch (args[i].toLowerCase()) {
                case "-datatype":
                    this.setDataType(args[i + 1]);
                    break;
                case "-sortingtype":
                    this.setSortingType(args[i + 1]);
                    break;
                case "-inputfile":
                    this.setInputFile(args[i + 1]);
                    break;
                case "-outputfile":
                    this.setOutputFile(args[i + 1]);
                    break;
                default:
                    break;
            }
        }
    }
}

class DataSorter {
    private final Scanner in;
    private final String DATATYPE;
    private final String SORTTYPE;
    private final File OUTFILE;
    private int counter = 0;
    private Map<Object, Integer> sortedData = new TreeMap<>();

    DataSorter(InputParameters params) throws FileNotFoundException {
        DATATYPE = params.getDataType();
        SORTTYPE = params.getSortingType();
        in = params.getInputFile().equals("nofile") ?
                new Scanner(System.in) : new Scanner(new File(params.getInputFile()));
        OUTFILE = params.getOutputFile().equals("nofile") ? null : new File(params.getOutputFile());
    }

    void readData() {
        switch (DATATYPE) {
            case "line":
                readLines();
                break;
            case "long":
            case "integer":
                readNumbers();
                break;
            case "word":
                readWords();
                break;
        }
    }

    private void readLines() {
        while (in.hasNextLine()) {
            String line = in.nextLine();
            counter++;
            sortedData.put(line, sortedData.getOrDefault(line, 0) + 1);
        }
    }

    private void readNumbers() {
        while (in.hasNext()) {
            String num = in.next();

            if (num.matches("[-+]?\\d+")) {
                long n = Long.parseLong(num);
                counter++;
                sortedData.put(n, sortedData.getOrDefault(n, 0) + 1);
            } else {
                System.out.printf("\"%s\" isn't a %s. It's skipped%n", num, DATATYPE);
            }
        }
    }

    private void readWords() {
        while (in.hasNext()) {
            String word = in.next();
            counter++;
            sortedData.put(word, sortedData.getOrDefault(word, 0) + 1);
        }
    }

    void getResult() {
        in.close();
        StringBuilder result = new StringBuilder();
        result.append("Total ").append(DATATYPE).append("s: ").append(counter).append(".\n");

        if (SORTTYPE.equals("natural")) {
            if (!DATATYPE.equals("line")) {
                sortedData.forEach((key, value) -> {
                    while (value-- != 0) {
                        result.append(key).append(" ");
                    }
                });
            } else {
                sortedData.forEach((key, value) -> {
                    while (value-- != 0) {
                        result.append(key).append("\n");
                    }
                });
            }
        } else {
            int finalCounter = counter;

            sortedData.entrySet().stream().sorted(Map.Entry.comparingByValue()).
                    forEach(o -> {
                        String percents = String.format("%.1f%%%n", (double) o.getValue() / (double) finalCounter * 100);
                        result.append(o.getKey()).append(": ").
                                append(o.getValue()).append(" time(s), ").
                                append(percents);
                    });
        }

        if (OUTFILE != null) {
            try (FileWriter fw = new FileWriter(OUTFILE)) {
                fw.write(String.valueOf(result));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else {
            System.out.print(result);
        }
    }
}

public class Main {
    public static void main(final String[] args) throws FileNotFoundException {
        InputParameters input = new InputParameters();
        input.initParams(input, args);

        DataSorter dSorter = new DataSorter(input);

        dSorter.readData();
        dSorter.getResult();
    }
}
