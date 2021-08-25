import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * Handles all the visuals
 */

public class WeatherOptionsView extends JFrame {

    public static final int X = 1000;
    public static final int Y = 300;
    private CityWeatherConnector con;
    private JPanel buttonPanel = new JPanel();
    private GridBagConstraints gridBagConstraints = new GridBagConstraints();

    public WeatherOptionsView(CityWeatherConnector con) {
        this.con = con;
        initComponents();
    }

    private void initComponents() {
        if (con !=null) {
            this.setLayout(new FlowLayout(FlowLayout.CENTER));
            this.setTitle("Information about "+ con.getCity());
            this.setPreferredSize(new Dimension(X, Y));
            this.pack();
            Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
            this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2);
            addInformationButton("General weather information", "The weather in "+ con.getCity()+" is "+ con.getWeatherDescription()+
                    "."+ '\n'+ "The temperature is "+ con.getTempCelsius()+'\u00B0'+"C, but feels like " + con.getFeelsLike()+'\u00B0'+"C.");
            //addInformationButton("Current weather", "The weather in "+ con.getCity()+" is currently "+ con.getWeatherDescription()+".");
            addInformationButton("Current Temperature", "The temperature in "+ con.getCity()+ " is "+ con.getTempKelvin()+"K, which equals "+
                    con.getTempCelsius()+ '\u00B0' +"C.");
            addInformationButton("Current Humidity","The humidity in "+ con.getCity()+ " is "+ con.getHumidity()+"%.");
            addInformationButton("Current Temperature, and how it feels","The temperature in "+ con.getCity()+" is " + con.getTempCelsius()+
                    '\u00B0' +"C, but feels like "+ con.getFeelsLike() + '\u00B0'+"C.");
            addInformationButton("Current air pressure","The air pressure in "+con.getCity()+" is "+con.getPressure() + "hPa.");
            addInformationButton("Information about location",con.getCity()+" is located in "+con.getCountry()+"."+'\n'+
                    "It has latitude "+con.getLat()+ " and longitude "+con.getLon() + ".");
            addInformationButton("Suntime","In "+con.getCity()+", the sun rises at "+con.getSunriseLocal()+", and sets at "+ con.getSunsetLocal()+ " local time.");
            addInformationButton("Air Quality","The air quality in "+con.getCity()+" scores a "+con.getAirQuality()+" out of 5, which is "+con.getAirQualityDescription()+".");
            addInformationButton("Air composition", "Concentration of different components of the air in "+con.getCity()+" (all in "+"μg/m"+'\u00B3'+"):"+
                    '\n'+"Carbon monoxide: "+'\t'+con.getAirComponentAmount("co")+'\n'+"" +
                    "Nitrogen monoxide: "+ con.getAirComponentAmount("no")+'\n'+"Nitrogen dioxide: "+ con.getAirComponentAmount("no2")+'\n'+
                    "Ozone: "+ con.getAirComponentAmount("o3")+'\n'+"Sulphur dioxide: "+ con.getAirComponentAmount("so2")+'\n'+
                    "Fine particles matter: "+ con.getAirComponentAmount("pm2_5")+'\n'+"Coarse particulate matter: "+ con.getAirComponentAmount("pm10")+'\n'+
                    "Ammonia: "+ con.getAirComponentAmount("nh3")+'\n');

            int cols = (int) Math.round(Math.sqrt(buttonPanel.getComponentCount()));
            GridLayout layout = new GridLayout(cols+1,cols+1);

            layout.setHgap(20);
            layout.setVgap(20);

            buttonPanel.setLayout(layout);
            this.add(buttonPanel);

            addChangeButton();
            this.setVisible(true);
            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        }
    }

    /**
     * creates a button that shows some information.
     * @param label the text on the button
     * @param information the information to show
     */

    void addInformationButton(String label, String information){
        JButton cb = new JButton();
        cb.setText(label);
        cb.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null,information);
            }
        });
        buttonPanel.add(cb,buttonPanel.getComponentCount());
    }

    /**
     * adds a button that closes the current window and asks for a different city
     */

    void addChangeButton() {
        JButton cb = new JButton();
        cb.setText("Get information about a different city");
        cb.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                chooseCity();
            }
        });
        buttonPanel.add(cb,buttonPanel.getComponentCount());
    }

    /**
     * opens an input dialog window, in which the user can input the name of a city. Creates a connector to the given city.
     */
    public static void chooseCity() {
        CityWeatherConnector connector = null;
        while (true) {
            String input = JOptionPane.showInputDialog("Write the name of a city to find out some information about it!");
            if (input==null){
                break;
            }
            try {
                connector = new CityWeatherConnector(input);
            } catch (IOException ioException) {
                JOptionPane.showMessageDialog(null, "Incorrect input. Please input an actual city.");
                continue;
            }
            break;
        }
        new WeatherOptionsView(connector);
    }
}
