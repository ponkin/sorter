package sorter;

import org.junit.*;

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class SortFileTest {

  private static final Charset defaultCharset = Charset.defaultCharset();

  @Test
  public void testMergeFiles() throws Exception {
    List<File> testFiles = new ArrayList<>();
    testFiles.add(new File("src/test/resources/test1.txt"));
    testFiles.add(new File("src/test/resources/test2.txt"));
    testFiles.add(new File("src/test/resources/test3.txt"));

    SortFile sorter = new SortFile(100);
    File result = File.createTempFile("sorter", null);
    result.deleteOnExit();
    sorter.mergeFiles(testFiles, result);
    String content = "";
    try (BufferedReader fbr = new BufferedReader(new InputStreamReader(
                                     new FileInputStream(result), defaultCharset))) {
      String line = "";
      while ((line = fbr.readLine()) != null) {
        content += line;
      }
    }
    assertEquals("ABCDEFG", content);
  }

  @Test
  public void testSortAndSave() throws Exception {
    File temp = Files.createTempDirectory("temp").toFile();

    SortFile sorter = new SortFile(100);

    List<String> lines = new ArrayList<>();
    lines.add("Z");
    lines.add("Y");
    lines.add("F");
    lines.add("A");

    File resultFile = sorter.sortAndSave(lines, temp); 
    try (BufferedReader fbr = new BufferedReader(new InputStreamReader(
                                     new FileInputStream(resultFile), defaultCharset))) {
      assertEquals("A", fbr.readLine());
      assertEquals("F", fbr.readLine());
      assertEquals("Y", fbr.readLine());
      assertEquals("Z", fbr.readLine());
     }

  }

  @Test
  public void testSplit() throws Exception {
    File temp = Files.createTempDirectory("temp").toFile();
    SortFile sorter = new SortFile(500);
    List<File> files = sorter.split(new File("src/test/resources/test.txt"), temp);
    assertEquals(7, files.size());
  }
}
