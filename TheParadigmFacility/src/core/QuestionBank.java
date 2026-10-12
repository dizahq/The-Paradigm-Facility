package core;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class QuestionBank {
    private Question[] questions = new Question[10];
    private int size = 0;
    public void storeQuestions(String questionFile){
        try(BufferedReader br = new BufferedReader(new FileReader("SampleQ"))){
            String line;
            while(br.readLine() != null){
                line = br.readLine();
                String lines[] = line.split(",");
                questions[size] = new Question(lines[0], lines[1], lines[2], lines[3], lines[4], 0, 0);
                size++;
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public Question[] getQuestions() {
        return questions;
    }
}
