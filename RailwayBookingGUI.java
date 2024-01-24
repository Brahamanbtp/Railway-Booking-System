import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class RailwayBookingGUI {
    private RailwayBookingSystem bookingSystem;
    private JTextField passengerNameField;
    private JTextField trainNumberField;
    private JTextField seatNumberField;
    private JComboBox<String> coachClassComboBox;
    private JTextArea outputTextArea;

    public RailwayBookingGUI() {
        bookingSystem = new RailwayBookingSystem();
        initializeGUI();
    }
    private void initializeGUI() {
        JFrame frame = new JFrame("Railway Booking System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(4, 2));
        passengerNameField = new JTextField();
        trainNumberField = new JTextField();
        seatNumberField = new JTextField();
        String[] coachClasses = {"First Class", "Second Class"};
        coachClassComboBox = new JComboBox<>(coachClasses);

        inputPanel.add(new JLabel("Passenger Name:"));
        inputPanel.add(passengerNameField);
        inputPanel.add(new JLabel("Train Number:"));
        inputPanel.add(trainNumberField);
        inputPanel.add(new JLabel("Seat Number:"));
        inputPanel.add(seatNumberField);
        inputPanel.add(new JLabel("Coach Class:"));
        inputPanel.add(coachClassComboBox);

        JButton bookButton = new JButton("Book Ticket");
        bookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String passengerName = passengerNameField.getText();
                    int trainNumber = Integer.parseInt(trainNumberField.getText());
                    int seatNumber = Integer.parseInt(seatNumberField.getText());
                    int coachClass = coachClassComboBox.getSelectedIndex() + 1;
                    bookingSystem.book(passengerName, trainNumber, seatNumber, coachClass);
                    updateOutput("Ticket booked successfully.");
                } catch (BookingException ex) {
                    updateOutput("Error: " + ex.getMessage());
                }
            }
        });

        JButton cancelButton = new JButton("Cancel Ticket");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int ticketNumber = Integer.parseInt(JOptionPane.showInputDialog("Enter ticket number to cancel:"));
                    bookingSystem.cancel(ticketNumber);
                    updateOutput("Ticket canceled successfully.");
                } catch (BookingException ex) {
                    updateOutput("Error: " + ex.getMessage());
                }
            }
        });

        JButton displayTrainsButton = new JButton("Display Trains");
        displayTrainsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                bookingSystem.displayTrains();
            }
        });

        JButton displayBookedTicketsButton = new JButton("Display Booked Tickets");
        displayBookedTicketsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                bookingSystem.displayBookedTickets();
            }
        });

        JButton displaySeatAvailabilityButton = new JButton("Display Seat Availability");
        displaySeatAvailabilityButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int trainNumber = Integer.parseInt(JOptionPane.showInputDialog("Enter train number:"));
                    int coachClass = Integer.parseInt(JOptionPane.showInputDialog("Enter coach class (1 for First Class, 2 for Second Class):"));
                    bookingSystem.displaySeatAvailability(trainNumber, coachClass);
                } catch (NumberFormatException ex) {
                    updateOutput("Invalid input. Please enter valid numbers.");
                }
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(bookButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(displayTrainsButton);
        buttonPanel.add(displayBookedTicketsButton);
        buttonPanel.add(displaySeatAvailabilityButton);

        outputTextArea = new JTextArea();
        outputTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputTextArea);

        frame.add(inputPanel, BorderLayout.NORTH);
        frame.add(buttonPanel, BorderLayout.CENTER);
        frame.add(scrollPane, BorderLayout.SOUTH);

        frame.pack();
        frame.setVisible(true);
    }

    private void updateOutput(String message) {
        outputTextArea.append(message + "\n");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new RailwayBookingGUI();
            }
        });
    }
}
