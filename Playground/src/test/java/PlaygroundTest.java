import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.IsEqual.equalTo;

public class PlaygroundTest {

    @Test
    public void from_list_to_stream_to_list_again() {
        List<String> inputList = Arrays.asList("Alles", "in", "Butter");
        assertThat(inputList, contains("Alles", "in", "Butter"));

        Stream<String> inputStream = inputList.stream();
        List<String> resultList = inputStream.collect(Collectors.toList());
        assertThat(resultList, contains("Alles", "in", "Butter"));
    }

    @Test
    public void read_resource_to_stream() throws IOException {
        ClassLoader classLoader = PlaygroundTest.class.getClassLoader();
        File file = new File(classLoader.getResource("lines.txt").getFile());
        System.out.println("Reading " + file.getAbsolutePath());
        assertThat(file, notNullValue());
        Stream<String> inputStream = Files.lines(Paths.get(file.getAbsolutePath()));
        List<String> resultList = inputStream.collect(Collectors.toList());
        assertThat(resultList, contains("line1", "line2", "line3", "line4", "line5"));
    }

    @Test
    public void map_strings_to_numbers() {
        List<String> inputList = Arrays.asList("1", "2", "3");
        Stream<String> inputStream = inputList.stream();
        List<Integer> resultList = inputStream
                .map(Integer::parseInt)
                .collect(Collectors.toList());
        assertThat(resultList, contains(1, 2, 3));
    }

    @Test
    public void map_strings_to_numbers_ignoring_format_errors() {
        List<String> inputList = Arrays.asList("1", "2", "no int", "3");
        Stream<String> inputStream = inputList.stream();
        List<Integer> resultList = inputStream
                .map(PlaygroundTest::parseInt)
                .filter(e -> e != null)
                .collect(Collectors.toList());
        assertThat(resultList, contains(1, 2, 3));
    }

    @Test
    public void map_strings_to_numbers_ignoring_format_errors_returning_min() {
        List<String> inputList = Arrays.asList("1", "2", "no int", "3");
        Stream<String> inputStream = inputList.stream();
        Optional<Integer> result = inputStream
                .map(PlaygroundTest::parseInt)
                .filter(e -> e != null)
                .min(Comparator.<Integer>naturalOrder());
        assertThat(result.get(), equalTo(1));
    }

    @Test
    public void split_string() {
        String input = "   9  86    32*   59       6  61.5       0.00         240  7.6 220  12  6.0  78 46 1018.6";
        String[] splits = input.split("\\s+");
        assertThat(splits[1], equalTo("9"));
        assertThat(splits[3], equalTo("32*"));
        assertThat(splits[15], equalTo("1018.6"));
    }

    public static Integer parseInt(String input) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
