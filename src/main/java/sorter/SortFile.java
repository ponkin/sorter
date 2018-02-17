package sorter;

import java.io.*;
import java.util.*;
import java.nio.charset.*;
import java.nio.file.*;

/**
 *
 * External Sort files using merge sort.
 * You need to manually define preffered block size.
 * Usage:
 *   java SortFile <source.txt> <target.txt> <block_size>
 *
 **/
public class SortFile {

  public static void main(String[] args) throws IOException {
    if (args.length < 3) {
      System.out.println("Usage: java SortFile <file_to_sort> <result_file> <max_file_block_size>");
      System.out.println("For example sort file with 1Kb blocks: java SortFile file.txt result.txt 1024");
      System.exit(0);
    }
    File file = new File(args[0]);
    File target = new File(args[1]);
    int blockSize = Integer.parseInt(args[2]);
    SortFile sorter = new SortFile(blockSize);
    sorter.sort(file, target);
  }

  private static final Charset defaultCharset = Charset.defaultCharset();

  private final long maxBlockSizeInBytes;

  public SortFile(long maxBlockSizeInBytes) {
    this.maxBlockSizeInBytes = maxBlockSizeInBytes;
  }

  public void sort(File source, File target) throws IOException {
    File temp = Files.createTempDirectory("sorterTmp").toFile();
    List<File> chunks = split(source, temp);
    mergeFiles(chunks, target);
  }

  List<File> split(File in, File targetDir) throws IOException {
    List<File> files = new ArrayList<>();
    try (BufferedReader fbr = new BufferedReader(new InputStreamReader(
                                     new FileInputStream(in), defaultCharset))) {
      String line = "";
      List<String> lines = new ArrayList<>();
      while (line != null) {
        long currBlockSize = 0L;
        while ((currBlockSize < maxBlockSizeInBytes) && (line = fbr.readLine()) != null) {
          if (line.trim().length() > 0) { // skip empty line
            lines.add(line);
            currBlockSize += line.length() * 2; // character in java String is in UTF-16;
          }
        }
        File chunkFile = sortAndSave(lines, targetDir); 
        files.add(chunkFile);
        lines.clear();
      }
    }
    return files;
  }

  File sortAndSave(List<String> lines, File targetDir) throws IOException {
    File result = File.createTempFile("chunk", null, targetDir);
    result.deleteOnExit();
    Collections.sort(lines);
    try (BufferedWriter fbw = new BufferedWriter(new OutputStreamWriter(
                                   new FileOutputStream(result), defaultCharset))) {
      for( String line: lines) {
        fbw.write(line);
        fbw.newLine();
      }
    }
    return result;
  }

  void mergeFiles(List<File> files, File targetFile) throws IOException {
    PriorityQueue<CachedBR> queue = new PriorityQueue<>(50, 
        (CachedBR br1, CachedBR br2) -> br1.peek().compareTo(br2.peek())); //TODO: ignore case or not?
    for (File file: files) {
      CachedBR br = new CachedBR(file);
      if (br.isNotEmpty()) {
        queue.add(br);
      } else {
        br.close();
      }
    }
    try (BufferedWriter fbw = new BufferedWriter(new OutputStreamWriter(
                        new FileOutputStream(targetFile), defaultCharset))) {
      while (queue.size() > 0) {
        CachedBR br = queue.poll();
        String line = br.pop();
        fbw.write(line);
        fbw.newLine();
        if (br.isNotEmpty()){
          queue.add(br);
        } else {
          br.close();
        }
      }
    } finally {
      if (!queue.isEmpty()) { // if write to file failed
        for (CachedBR br: queue)
          br.close();
      }
    }
  }

  // we need peek() method in BufferedReader
  private class CachedBR {

    private final BufferedReader br;
    private String cache;

    CachedBR(File in) throws IOException {
      this.br = new BufferedReader(new InputStreamReader(
                  new FileInputStream(in), defaultCharset));
      reload();
    }

    boolean isNotEmpty() {
      return this.cache != null;
    }

    String peek() {
      return this.cache;
    }

    String pop() throws IOException {
      String result = peek().toString();
      reload();
      return result;
    }

    void close() throws IOException {
      br.close();
    }

    private void reload() throws IOException {
      this.cache = br.readLine();
    }
  }
}
