# Data Validation and Reporting Tool

## Overview
The **Data Validation and Reporting Tool** is a Java program that checks and cleans data from input files. It helps find missing values, wrong formats, or inconsistent entries, and creates a short report that summarizes the issues found.
This project shows how to use **Java file handling, data parsing, and validation logic** in a clear, simple, and object-oriented way.

---

## Features
- Validates input data for missing or invalid entries  
- Generates a summary report with counts and details  
- Supports basic custom validation rules  
- Displays clear console messages for progress and errors  
- Organized, readable Java structure  

---

## Technical Details
- **Language:** Java 17  
- **Key Concepts:**  
  - File I/O (`BufferedReader`, `FileReader`)  
  - Exception handling (`try-catch`)  
  - Use of Lists and Maps  
  - Modular and maintainable code design  

---

## How to Run
1. Extract or clone the project folder  
2. Open it in an IDE (like IntelliJ, VS Code, or Eclipse) or use the terminal  
3. Make sure your data file (e.g., a `.csv` file) is in the same level as the `DataValidationTool.java` file.  
   *(In this repo, a sample data file named `survey_data.csv` is provided — you can replace it with your own if you want.)*  
4. Compile the code:  
   ```bash
   javac DataValidationTool.java
   ```
5. Run the program:  
   ```bash
   java DataValidationTool
   ```

---

## Example Output
```
=== Data Validation Tool ===

=== Validation Summary ===
Total rows processed: 18
Valid rows: 11
Invalid rows: 7

Errors by column:
 - Age: 0
 - Income: 3
 - Email: 3

Validation summary saved as 'validation_summary.csv'.

Charts saved as 'validity_chart.png' and 'error_chart.png'.
=== Program Finished ===
```

---

## Notes
This project was created to show solid understanding of **Java programming** and **practical file validation**. It is written with readability and modularity in mind so that it can be extended easily for more advanced validation tasks in the future. 
