/*
 * DataValidationTool.java
 * -----------------------
 * A simple Java program that reads a CSV file, validates the data,
 * and generates a text-based summary report along with pie/bar charts.
 * For STATCAN Application
 *
 * Blessy Rampogu
 * Date: October 2025
 *
 * Features:
 *  - Reads a CSV file
 *  - Checks for missing and invalid values
 *  - Prints summary to console
 *  - Generates pie & bar charts as PNG images
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import java.io.PrintWriter;



public class DataValidationTool {

    // File path to dataset
    private static final String INPUT_FILE = "survey_data.csv";

    // Validation statistics
    private static int totalRows = 0;
    private static int validRows = 0;
    private static int invalidRows = 0;
    private static Map<String, Integer> columnErrorCount = new LinkedHashMap<>();

    public static void main(String[] args) {
        System.out.println("=== Data Validation Tool ===");

        // Step 1: Read the file
        List<String[]> data = readCSV(INPUT_FILE);
        if (data == null || data.isEmpty()) {
            System.out.println("No data found. Exiting program.");
            return;
        }

        // Step 2: Validate each row
        validateData(data);

        // Step 3: Print summary
        printSummary();
        writeSummaryCSV("validation_summary.csv");


        // Step 4: Generate simple charts
        createPieChart(validRows, invalidRows, "validity_chart.png");
        createBarChart(columnErrorCount, "error_chart.png");

        System.out.println("\nCharts saved as 'validity_chart.png' and 'error_chart.png'.");
        System.out.println("=== Program Finished ===");
    }

    /**
     * Reads a CSV file line by line.
     * @param fileName path to CSV file
     * @return list of rows (each row = array of Strings)
     */
    private static List<String[]> readCSV(String fileName) {
        List<String[]> rows = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Skip empty lines
                if (line.trim().isEmpty()) continue;
                String[] values = line.split(",");
                rows.add(values);
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
        return rows;
    }

    /**
     * Validates the dataset for missing and invalid values.
     * Simple rules:
     *  - Age must be between 0 and 120
     *  - Income must be > 0
     *  - Email must contain '@'
     *  - This is also customizable depending on the data you are working with you can change feilds (email, age, etc)
     */
    private static void validateData(List<String[]> data) {
        String[] header = data.get(0);
        columnErrorCount.put("Age", 0);
        columnErrorCount.put("Income", 0);
        columnErrorCount.put("Email", 0);

        for (int i = 1; i < data.size(); i++) { // skip header
            String[] row = data.get(i);
            totalRows++;

            boolean rowValid = true;

            try {
                String ageStr = row[1].trim();
                String incomeStr = row[2].trim();
                String email = row[4].trim();

                // Check for missing or invalid age
                if (ageStr.isEmpty() || !isNumber(ageStr)) {
                    rowValid = false;
                    increment("Age");
                } else {
                    int age = Integer.parseInt(ageStr);
                    if (age < 0 || age > 120) {
                        rowValid = false;
                        increment("Age");
                    }
                }

                // Check for missing or invalid income
                if (incomeStr.isEmpty() || !isNumber(incomeStr)) {
                    rowValid = false;
                    increment("Income");
                } else {
                    double income = Double.parseDouble(incomeStr);
                    if (income <= 0) {
                        rowValid = false;
                        increment("Income");
                    }
                }

                // Check for invalid email
                if (email.isEmpty() || !email.contains("@")) {
                    rowValid = false;
                    increment("Email");
                }

            } catch (Exception e) {
                rowValid = false;
            }

            if (rowValid) validRows++;
            else invalidRows++;
        }
    }

    /** Helper: checks if a string is numeric. */
    private static boolean isNumber(String s) {
        try {
            Double.parseDouble(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /** Helper: increments count of a column’s errors. */
    private static void increment(String columnName) {
        columnErrorCount.put(columnName, columnErrorCount.get(columnName) + 1);
    }

    /** Prints the validation summary to the console. */
    private static void printSummary() {
        System.out.println("\n=== Validation Summary ===");
        System.out.println("Total rows processed: " + totalRows);
        System.out.println("Valid rows: " + validRows);
        System.out.println("Invalid rows: " + invalidRows);
        System.out.println("\nErrors by column:");
        for (String key : columnErrorCount.keySet()) {
            System.out.println(" - " + key + ": " + columnErrorCount.get(key));
        }
    }

    /** Creates a basic pie chart using Java2D graphics. */
    private static void createPieChart(int valid, int invalid, String fileName) {
        int total = valid + invalid;
        if (total == 0) return;

        int width = 400, height = 300;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();

        // background
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);

        // slices
        double validAngle = 360.0 * valid / total;
        g.setColor(new Color(100, 200, 100)); // green
        g.fillArc(50, 50, 200, 200, 0, (int) validAngle);

        g.setColor(new Color(200, 100, 100)); // red
        g.fillArc(50, 50, 200, 200, (int) validAngle, 360 - (int) validAngle);

        // labels
        g.setColor(Color.BLACK);
        g.drawString("Valid: " + valid, 270, 120);
        g.drawString("Invalid: " + invalid, 270, 140);

        // title
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Valid vs Invalid Records", 100, 30);

        g.dispose();

        try {
            ImageIO.write(image, "png", new File(fileName));
        } catch (IOException e) {
            System.out.println("Error saving chart: " + e.getMessage());
        }
    }

    /** Creates a simple bar chart showing column errors. */
    private static void createBarChart(Map<String, Integer> dataMap, String fileName) {
        int width = 400, height = 300;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);

        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.drawString("Error Counts by Column", 100, 30);

        int x = 80;
        int maxHeight = 150;
        int barWidth = 50;
        int maxValue = Collections.max(dataMap.values()) + 1;

        for (String key : dataMap.keySet()) {
            int value = dataMap.get(key);
            int barHeight = (int) ((value / (double) maxValue) * maxHeight);
            g.setColor(new Color(100, 150, 240));
            g.fillRect(x, 250 - barHeight, barWidth, barHeight);
            g.setColor(Color.BLACK);
            g.drawRect(x, 250 - barHeight, barWidth, barHeight);
            g.drawString(key, x + 5, 270);
            x += 80;
        }

        g.dispose();

        try {
            ImageIO.write(image, "png", new File(fileName));
        } catch (IOException e) {
            System.out.println("Error saving chart: " + e.getMessage());
        }
    }

    /**
     * Writes validation summary results to a CSV file.
     */
    private static void writeSummaryCSV(String fileName) {
        try (PrintWriter writer = new PrintWriter(new File(fileName))) {
            writer.println("Metric,Count,Percentage");

            double total = totalRows == 0 ? 1 : totalRows;
            writer.printf("Total Rows Processed,%d,100%%%n", totalRows);
            writer.printf("Valid Rows,%d,%.1f%%%n", validRows, (validRows / total) * 100);
            writer.printf("Invalid Rows,%d,%.1f%%%n", invalidRows, (invalidRows / total) * 100);

            for (Map.Entry<String, Integer> entry : columnErrorCount.entrySet()) {
                double percent = (entry.getValue() / total) * 100;
                writer.printf("%s Errors,%d,%.1f%%%n", entry.getKey(), entry.getValue(), percent);
            }

            System.out.println("\nValidation summary saved as '" + fileName + "'.");
        } catch (IOException e) {
            System.out.println("Error writing summary CSV: " + e.getMessage());
        }
    }

}
