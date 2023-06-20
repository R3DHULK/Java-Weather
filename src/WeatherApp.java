import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherApp extends JFrame {
    private JLabel locationLabel;
    private JTextField locationField;
    private JLabel temperatureLabel;
    private JTextField temperatureField;
    private JButton getWeatherButton;

    public WeatherApp() {
        initializeUI();
        setupListeners();
    }

    private void initializeUI() {
        setTitle("Weather App");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(3, 2, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        locationLabel = new JLabel("Location:");
        locationField = new JTextField();
        temperatureLabel = new JLabel("Temperature:");
        temperatureField = new JTextField();
        temperatureField.setEditable(false);
        getWeatherButton = new JButton("Get Weather");

        mainPanel.add(locationLabel);
        mainPanel.add(locationField);
        mainPanel.add(temperatureLabel);
        mainPanel.add(temperatureField);
        mainPanel.add(getWeatherButton);

        add(mainPanel);

        pack();
    }

    private void setupListeners() {
        getWeatherButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String location = locationField.getText();
                try {
                    String temperature = getTemperature(location);
                    temperatureField.setText(temperature);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(
                            WeatherApp.this,
                            "Error retrieving temperature: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    private String getTemperature(String location) throws IOException {
        String url = "http://api.openweathermap.org/data/2.5/weather?q=" +
                location +
                "&appid=aba6ff9d6de967d5eac6fd79114693cc";

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");

        if (connection.getResponseCode() == 200) {
            InputStream responseBody = connection.getInputStream();
            String response = new String(responseBody.readAllBytes());
            responseBody.close();

            // Extract temperature from response
            int start = response.indexOf("\"temp\":") + 7;
            int end = response.indexOf(",", start);
            double temperature = Double.parseDouble(response.substring(start, end));

            // Convert temperature from Kelvin to Celsius
            double temperatureCelsius = temperature - 273.15;

            // Extract weather condition from response
            start = response.indexOf("\"main\":\"") + 8;
            end = response.indexOf("\"", start);
            String weatherCondition = response.substring(start, end);

            // Update the weather condition label
            if (weatherCondition.equalsIgnoreCase("Clouds")) {
                return String.format("%.2f °C (Cloudy)", temperatureCelsius);
            } else if (weatherCondition.equalsIgnoreCase("Rain")) {
                return String.format("%.2f °C (Rainy)", temperatureCelsius);
            } else if (weatherCondition.equalsIgnoreCase("Clear")) {
                return String.format("%.2f °C (Sunny)", temperatureCelsius);
            } else {
                return String.format("%.2f °C (%s)", temperatureCelsius, weatherCondition);
            }
        } else {
            throw new IOException("HTTP error code: " + connection.getResponseCode());
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new WeatherApp().setVisible(true);
            }
        });
    }
}
