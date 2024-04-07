// Author : Mamotsoko Maphutha
// Date: 07/04/2024
// Project : Till Float Management

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) {
     
        String inputFile = "input.txt";
        String outputFile = "output.txt";

        int tillStartAmount = 500; //This is the initial amount that was in the till before we start with transactions

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {

            writer.write("Till Start, Transaction Total, Paid, Change Total, Change Breakdown\n");

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length != 2) {
                    System.out.println("Error: Line '" + line + "' does not contain proper format. Skipping.");
                    continue;
                }


                List<String> items = parseItems(parts[0]);
                List<Integer> paidAmounts = parsePaidAmounts(parts[1]);

                int totalTransactionCost = calculateTotalTransactionCost(items);

                int totalPaid = calculateTotalPaid(paidAmounts);

                int totalChange = totalPaid - totalTransactionCost;

                String changeBreakdown = calculateChangeBreakdown(totalChange);

                String transactionResult = String.format("R%d, R%d, R%d, R%d, %s\n", tillStartAmount,
                    totalTransactionCost, totalPaid, totalChange, changeBreakdown);
                writer.write(transactionResult);

                tillStartAmount += totalTransactionCost;
            }

            writer.write("R" + tillStartAmount);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<String> parseItems(String itemsString) {
        List<String> items = new ArrayList<>();
        Pattern pattern = Pattern.compile("(?<description>.+?)\\s*R(?<amount>\\d+)");
        Matcher matcher = pattern.matcher(itemsString);
        while (matcher.find()) {
            String description = matcher.group("description").trim();
            String amount = matcher.group("amount");
            items.add(description + ": R" + amount);
        }
        return items;
    }

private static List<Integer> parsePaidAmounts(String paidAmountString) {
    List<Integer> paidAmounts = new ArrayList<>();
    Pattern pattern = Pattern.compile("R(\\d+)");
    Matcher matcher = pattern.matcher(paidAmountString);
    while (matcher.find()) {
        String amount = matcher.group(1);
        paidAmounts.add(Integer.parseInt(amount));
    }
    return paidAmounts;
}

    private static int calculateTotalTransactionCost(List<String> items) {
        return items.stream()
                .mapToInt(item -> Integer.parseInt(item.split("R")[1]))
                .sum();
    }

    private static int calculateTotalPaid(List<Integer> paidAmounts) {
        return paidAmounts.stream().mapToInt(Integer::intValue).sum();
    }

    private static String calculateChangeBreakdown(int totalChange) {
        int[] denominations = {50, 20, 10, 5, 2, 1};
        StringBuilder changeBreakdown = new StringBuilder();
        for (int denomination : denominations) {
            while (totalChange >= denomination) {
                if (changeBreakdown.length() > 0) {
                    changeBreakdown.append("-");
                }
                changeBreakdown.append("R").append(denomination);
                totalChange -= denomination;
            }
        }
        return changeBreakdown.toString();
    }
}

