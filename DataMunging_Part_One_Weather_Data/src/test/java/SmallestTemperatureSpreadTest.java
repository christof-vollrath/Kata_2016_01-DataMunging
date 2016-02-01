import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertTrue;

public class SmallestTemperatureSpreadTest {

    private File file;

    @Before
    public void before() {
        ClassLoader classLoader = SmallestTemperatureSpreadTest.class.getClassLoader();
        file = new File(classLoader.getResource("weather.dat").getFile());
    }

    @Test
    public void parse_line() {
        String line = "   8  75    54    65          50.0       0.00 FH      160  4.2 150  10  2.6  93 41 1026.3";
        WeatherData weatherData = parseLine(line);
        assertThat(weatherData.getDy(), equalTo(8));
        assertThat(weatherData.getMxT(), equalTo(75.0));
        assertThat(weatherData.getMnT(), equalTo(54.0));

        line = "   9  86    32*   59       6  61.5       0.00         240  7.6 220  12  6.0  78 46 1018.6\n";
        weatherData = parseLine(line);
        assertThat(weatherData, nullValue());

        line = "  mo  82.9  60.5  71.7    16  58.8       0.00              6.9          5.3\n";
        weatherData = parseLine(line);
        assertThat(weatherData, nullValue());

        line = "\n";
        weatherData = parseLine(line);
        assertThat(weatherData, nullValue());
    }

    @Test
    public void file_exists() {
        assertThat(file, notNullValue());
    }

    @Test
    public void create_file_stream() throws IOException {
        Stream<String> lineStream = createFileStream(file);
        List<String> lineList = lineStream.collect(Collectors.toList());
        assertThat(lineList.get(0), equalTo("      Dy MxT   MnT   AvT   HDDay  AvDP 1HrP TPcpn WxType PDir AvSp Dir MxS SkyC MxR MnR AvSLP"));
        assertThat(lineList.get(32), equalTo("  mo  82.9  60.5  71.7    16  58.8       0.00              6.9          5.3"));
        assertThat(lineList.size(), equalTo(33));
    }

    @Test
    public void create_WeatherData_stream() throws IOException {
        Stream<String> lineStream = createFileStream(file);
        Stream<WeatherData> weatherDataStream = createWeatherDataStream(lineStream);
        List<WeatherData> weatherDataList = weatherDataStream.collect(Collectors.toList());
        assertThat(weatherDataList.size(), equalTo(28));
        assertThat(weatherDataList.get(0).getDy(), equalTo(1));
        assertThat(weatherDataList.get(0).getMxT(), equalTo(88.0));
        assertThat(weatherDataList.get(0).getMnT(), equalTo(59.0));
    }

    @Test
    public void smallest_spread_from_WeatherData_stream() {
        Stream<WeatherData> weatherDataStream = Stream.of(new WeatherData(1, 20.0, 10.0), new WeatherData(2, 25.0, 12.0), new WeatherData(3, 15.0, 12.0));
        Optional<WeatherData> smallestSpreadOptional = findSmallesSpread(weatherDataStream);
        assertTrue(smallestSpreadOptional.isPresent());
        WeatherData smallestSpread = smallestSpreadOptional.get();
        assertThat(smallestSpread.getDy(), equalTo(3));
    }

    @Test
    public void smallest_spread_from_file() throws IOException {
        Stream<String> lineStream = createFileStream(file);
        Stream<WeatherData> weatherDataStream = createWeatherDataStream(lineStream);
        Optional<WeatherData> smallestSpreadOptional = findSmallesSpread(weatherDataStream);
        System.out.println(smallestSpreadOptional.get().getDy());
    }

    private Optional<WeatherData> findSmallesSpread(Stream<WeatherData> weatherDataStream) {
        return weatherDataStream.min((weatherData1, weatherData2) -> Double.compare(weatherData1.getSpread(), weatherData2.getSpread()) );
    }

    private Stream<WeatherData> createWeatherDataStream(Stream<String> lineStream) {
        return lineStream.map(SmallestTemperatureSpreadTest::parseLine)
                .filter(e -> e != null);
    }

    private Stream<String> createFileStream(File file) throws IOException {
        return Files.lines(Paths.get(file.getAbsolutePath()));
    }

    private static WeatherData parseLine(String line) {
        String[] splits = line.split("\\s+");
        if (splits.length < 4) return null;
        try {
            return new WeatherData(Integer.parseInt(splits[1]), Double.parseDouble(splits[2]), Double.parseDouble(splits[3]));
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
