import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class Task implements Callable<TreeMap> {

    private String name;
    private Path file;

    public Task(String s, Path p){
        name = s;
        file = p;
    }

    @Override
    public TreeMap call() {
        Scanner scanner = null;
        String text = null;
        try {
            scanner = new Scanner(new File(String.valueOf(file)));
            text = scanner.useDelimiter("\\A").next();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        finally {
            scanner.close();
        }

        return getWords(text);
    }

    private TreeMap getWords(String str){
        Pattern pattern = Pattern.compile("\\w+");
        Matcher matcher = pattern.matcher(str);
        List<String> words = new ArrayList();
        int max = 0;
        while(matcher.find()){
            String s = matcher.group().toLowerCase();
            words.add(s);
            if(max < s.length()){
                max = s.length();
            }
        }
        Map<String, Integer> frequencyMap = words.stream().collect(Collectors.toMap(s -> s, s -> 1, Integer::sum));
        TreeMap orderedList = new TreeMap(frequencyMap);
        return orderedList;
    }

}

public class WordCounter {
    public static final Path FOLDER_OF_TEXT_FILES = Paths.get("D:\\Projects\\class work\\CSE216\\Homework-5\\src\\files");
    public static final Path WORD_COUNT_TABLE_FILE = Paths.get("D:\\Projects\\class " +
            "work\\CSE216\\Homework-5\\src\\output\\output.txt");
    public static final int NUMBER_OF_THREADS = 1;
    public static TreeMap<String, ArrayList<Integer>> table = new TreeMap<>();

    public static void main(String[] args) throws Exception {
        int maxStringLength = 0;
        File output = new File(String.valueOf(WORD_COUNT_TABLE_FILE));
        FileWriter fw = new FileWriter(output);
        ArrayList<Path> files = new ArrayList(10);
        List<Future<TreeMap>> resultList = new ArrayList<>();
        TreeMap<String, Integer> completeMap = new TreeMap();

        if(output.exists()){
            output.delete();
        }
        output.createNewFile();

        ExecutorService pool = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

        try (Stream<Path> paths = Files.walk(FOLDER_OF_TEXT_FILES)) {
            paths.filter(Files::isRegularFile).forEach(files::add);
        }

        Task[] tasks = new Task[files.size()];

        for(int i = 0; i < tasks.length; i++){
            tasks[i] = new Task("task " + i, files.get(i));
            Future<TreeMap> result = pool.submit(tasks[i]);
            resultList.add(result);
        }
        for(int i = 0; i < resultList.size(); i++){
            for(Object entry : resultList.get(i).get().keySet()){
                if(!completeMap.containsKey(entry)){
                    completeMap.put((String)entry, (Integer)resultList.get(i).get().get(entry));
                }else{
                    completeMap.put((String)entry,
                            (Integer)resultList.get(i).get().get(entry) + (Integer)completeMap.get(entry));
                }
                if(maxStringLength < ((String) entry).length()){
                    maxStringLength = ((String) entry).length();
                }
            }
        }


        Collections.sort(files);

        for(String s : completeMap.keySet()){
            table.put(s, new ArrayList<>(files.size()));
            for(int x : table.get(s)){
                x = 0;
            }
        }

        for(int i = 0; i < resultList.size(); i++) {
            for (Object entry : completeMap.keySet()) {
                if (!resultList.get(i).get().containsKey(entry)) {
                    table.get(entry).add(0);
                } else {
                    table.get(entry).add((Integer)resultList.get(i).get().get(entry));
                }
            }
        }

        try{
            for(int i = 0; i < maxStringLength + 1; i++){
                fw.write(" ");
            }

            for(int i = 0; i < files.size(); i++){
                fw.write(String.valueOf(files.get(i).getFileName()));
                if(String.valueOf(files.get(i).getFileName()).length() < maxStringLength){
                    for(int j = String.valueOf(files.get(i).getFileName()).length(); j < maxStringLength; j++){
                        fw.append(' ');
                    }
                }else{
                    fw.write(" ");
                }
            }
            fw.write("total");
            fw.write("\n");

            for(String s : completeMap.keySet()){
                fw.write(s);
                createSpacing(fw, s, maxStringLength);
                fw.flush();
                for(int i = 0; i < table.get(s).size(); i++){
                    fw.write(table.get(s).get(i).toString());
                    createSpacing(fw, String.valueOf(table.get(s).get(i)), maxStringLength - 1);
                    fw.flush();
                }
                fw.write(completeMap.get(s).toString());
                createSpacing(fw, String.valueOf(completeMap.get(s)), maxStringLength - 1);
                fw.flush();
                fw.write("\n");
            }
        }catch (Exception e){

        }finally{
            pool.shutdown();
            fw.flush();
            fw.close();
        }
    }

    public static void createSpacing(FileWriter fw, String s, int maxStringLength) throws IOException {
        if(s.length() <= maxStringLength){
            for(int j = s.length(); j < maxStringLength + 1; j++){
                fw.append(' ');
            }
        }
    }
}
