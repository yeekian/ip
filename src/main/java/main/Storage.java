package main;

import task.Deadline;
import task.Event;
import task.Todo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.Scanner;

/**
 * The Storage class handles loading and saving task data to and from a file.
 */
public class Storage {

    protected String filePath;

    public Storage(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Writes the given text to a specified file.
     * @param textToAdd The text to add to the file.
     * @throws IOException If an I/O error occurs while writing to the file.
     */
    public void writeToFile(String textToAdd) throws IOException {
        FileWriter fw = new FileWriter(filePath);
        fw.write(textToAdd);
        fw.close();
    }

    /**
     * Sets up the file by creating a new file if it does not exist.
     * Also creates the parent directory if it doesn't exist.
     * @throws IOException If an I/O error occurs while setting up the file.
     */
    public void writerSetUp() throws IOException {
        File listFile = new File(filePath);
        if (!listFile.exists()) {
            File directory = listFile.getParentFile();
            if (!directory.exists()) {
                directory.mkdirs(); // Create directory if it doesn't exist
            }
            listFile.createNewFile(); // Create file if it doesn't exist
        }
    }

    /**
     * Loads task data from a file and adds it to the provided user list.
     * @param userList The list where the tasks will be added.
     */
    public void loadDataFromFile(TaskList userList) {
        File file = new File(filePath);
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                parseAndAddItem(line, userList);
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("\tFile not found: " + filePath);
        }
        Ui.printHorizontalLine();
    }

    /**
     * Parses a line from the file and adds the corresponding task to the user list.
     * @param line The line to parse.
     * @param userList The list where the task will be added.
     */
    private static void parseAndAddItem(String line, TaskList userList) {
        String[] parts = line.split(" \\| ");

        try {
            String taskType = parts[0]; // T, D, E
            boolean isDone = parts[1].equals("X");
            String taskDescription = parts[2];

            // Check for valid task types and number of fields
            switch (taskType) {
            case "T":
                if (parts.length != 3) {
                    System.out.println("\tInvalid format for Todo task: " + line);
                    return; // Skip this line
                }
                Todo todoTask = new Todo(taskDescription);
                if (isDone) {
                    todoTask.markAsDone();
                }
                userList.itemArrayList.add(todoTask);
                break;

            case "D":
                if (parts.length != 4) {
                    System.out.println("\tSkipping line as invalid format for Deadline task: " + line);
                    return; // Skip this line
                }
                try {
                    LocalDateTime deadlineDate = List.getDeadlineDateAsLocalDateTimeFromFile(parts[3]);
                    Deadline deadlineTask = new Deadline(taskDescription, deadlineDate);
                    if (isDone) {
                        deadlineTask.markAsDone();
                    }
                    userList.itemArrayList.add(deadlineTask);
                } catch (DateTimeException e) {
                    System.out.println("\tSkipping line as invalid date format in Deadline task: " + parts[3]);
                    System.out.println("\t\tInvalid date format, expected: \n\t\t\tyyyy-mm-dd HH:mm \n\t\t\tyyyy-MM-dd \n\t\t\tdd/MM/yyyy HH:mm \n\t\t\tdd/MM/yyyy");
                }
                break;

            case "E":
                if (parts.length != 5) {
                    System.out.println("\tSkipping line as invalid format for Event task: " + line);
                    return; // Skip this line
                }
                String eventStart = parts[3]; // Start date/time
                String eventEnd = parts[4];   // End date/time
                Event eventTask = new Event(taskDescription, eventStart, eventEnd);
                if (isDone) {
                    eventTask.markAsDone();
                }
                userList.itemArrayList.add(eventTask);
                break;

            default:
                System.out.println("\tSkipping line as invalid task type in file: " + line);
                break;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("\tSkipping line as error detected in line: " + line);
        }
    }


    /**
     * Saves the current task list to the specified file.
     * @param userList The list containing tasks to save.
     */
    public void saveListToFile(TaskList userList) {
        try {
            writeToFile(userList.getFormattedTasks()); // getFormattedTasks returns a formatted String
        } catch (IOException e) {
            System.out.println("\tAn error occurred while saving the list.");
        }
    }
}

