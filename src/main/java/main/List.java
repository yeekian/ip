package main;

import exception.EmptyDateFieldException;
import exception.EmptyDescriptionException;
import task.Deadline;
import task.Event;
import task.Task;
import task.Todo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.time.DateTimeException;

/**
 * Manages the list of tasks. This class provides functionality to
 * add tasks (events, todos, deadlines) and mark or delete tasks
 * in the task list.
 */
public class List {

    /**
     * Adds an event task to the provided task list.
     * The event contains a description, start date, and end date.
     *
     * @param itemArrayList The list of tasks to add the event to.
     * @param ui
     * @param line          The user input containing the event description, start date, and end date.
     * @throws EmptyDescriptionException If the event description is missing.
     * @throws EmptyDateFieldException   If the event start or end date is missing.
     */
    public static void addEvent(ArrayList<Task> itemArrayList, Ui ui, String line) {
        try {
            String eventDescription = Parser.extractEventDescription(line);
            String eventStartDate = Parser.extractEventStartDate(line);
            String eventEndDate = Parser.extractEventEndDate(line);
            Event newEvent = new Event(eventDescription, eventStartDate, eventEndDate);
            itemArrayList.add(newEvent);
            ui.printAddedMessage(itemArrayList, newEvent);
        } catch (EmptyDescriptionException e) {
            ui.printTaskDescriptionEmptyMessage();
        } catch (EmptyDateFieldException e) {
            ui.printDateFieldEmptyMessage();
        }
    }

    /**
     * Adds a todo task to the provided task list.
     * The todo contains only a description.
     *
     * @param itemArrayList The list of tasks to add the todo to.
     * @param ui
     * @param line          The user input containing the todo description.
     * @throws EmptyDescriptionException If the todo description is missing.
     */
    public static void addTodo(ArrayList<Task> itemArrayList, Ui ui, String line) {
        try {
            String todoDescription = Parser.extractTodoDescription(line);
            Todo newTodo = new Todo(todoDescription);
            itemArrayList.add(newTodo);
            ui.printAddedMessage(itemArrayList, newTodo);
        } catch (EmptyDescriptionException e) {
            ui.printTaskDescriptionEmptyMessage();
        }
    }

    /**
     * Adds a deadline task to the provided task list.
     * The deadline contains a description and a due date.
     *
     * @param itemArrayList The list of tasks to add the deadline to.
     * @param ui
     * @param line          The user input containing the deadline description and date.
     * @throws EmptyDescriptionException If the deadline description is missing.
     * @throws EmptyDateFieldException   If the deadline date is missing.
     */
    public static void addDeadline(ArrayList<Task> itemArrayList, Ui ui, String line) {
        try {
            String deadlineDescription = Parser.extractDeadlineDescription(line);
            LocalDateTime deadlineDate = Parser.extractDeadlineDate(line);
            Deadline newDeadline = new Deadline(deadlineDescription, deadlineDate);
            itemArrayList.add(newDeadline);
            ui.printAddedMessage(itemArrayList, newDeadline);
        } catch (EmptyDescriptionException e) {
            ui.printTaskDescriptionEmptyMessage();
        } catch (EmptyDateFieldException e) {
            ui.printDateFieldEmptyMessage();
        } catch (DateTimeException e) {
            ui.printInvalidDateFormatMessage();
        }
    }

    /**
     * Marks a task in the task list as done.
     *
     * @param itemArrayList The list of tasks.
     * @param itemNum The task number to be marked as done.
     */
    public static void markListItemAsDone(ArrayList<Task> itemArrayList, int itemNum) {
        itemArrayList.get(itemNum - 1).markAsDone();
    }

    /**
     * Marks a task in the task list as not done.
     *
     * @param itemArrayList The list of tasks.
     * @param itemNum The task number to be marked as not done.
     */
    public static void markListItemAsUnDone(ArrayList<Task> itemArrayList, int itemNum) {
        itemArrayList.get(itemNum - 1).markAsUnDone();
    }

    /**
     * Deletes a task from the task list.
     *
     * @param itemArrayList The list of tasks.
     * @param itemNum The task number to be deleted.
     */
    public static void deleteListItem(ArrayList<Task> itemArrayList, int itemNum) {
        itemArrayList.remove(itemNum - 1);
    }

    public static LocalDateTime convertDeadlineDateAsLocalDateTime(String deadlineDate) {
        // Define multiple format patterns to support different date and date-time formats
        DateTimeFormatter[] formatters = {
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),  // With time
                DateTimeFormatter.ofPattern("yyyy-MM-dd"),        // Date only
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"),  // With time, different format
                DateTimeFormatter.ofPattern("dd/MM/yyyy"),        // Date only, different format
        };

        // Try to parse as LocalDateTime (with time)
        for (DateTimeFormatter formatter : formatters) {
            try {
                return LocalDateTime.parse(deadlineDate, formatter);
            } catch (DateTimeException ignored) {
                // Ignore and try the next formatter
            }
        }

        // Try to parse as LocalDate (without time) and convert to LocalDateTime
        for (DateTimeFormatter formatter : formatters) {
            try {
                LocalDate date = LocalDate.parse(deadlineDate, formatter);
                return date.atTime(23, 59, 59);  // Return LocalDateTime with time set to 23:59
            } catch (DateTimeException ignored) {
                // Ignore and try the next formatter
            }
        }

        // If parsing fails for all formats, throw an exception
        throw new DateTimeException("Invalid date format: " + deadlineDate);
    }


    public static LocalDateTime getDeadlineDateAsLocalDateTimeFromFile(String deadlineDate) {
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("MMM dd yyyy, h:mm a");
        return LocalDateTime.parse(deadlineDate, inputFormatter);
    }

    public static String convertDeadlineDateAsString(LocalDateTime dateTime) {
        DateTimeFormatter outputformatter = DateTimeFormatter.ofPattern("MMM dd yyyy, h:mm a");
        String formattedDateTime = dateTime.format(outputformatter);

        return formattedDateTime;
    }
}

