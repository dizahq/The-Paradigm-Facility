package core;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class QuestionBank {
    private final String QUESTION_FILE = "TheParadigmFacility/data/SampleQ.csv";
    private ArrayList<Question> questions = new ArrayList<>();
    public QuestionBank(){
        storeQuestions();
    }
    public void storeQuestions(){
        try(BufferedReader br = new BufferedReader(new FileReader(QUESTION_FILE))){
            String line;
            while((line = br.readLine()) != null){
                String[] lines = line.split(",");
                questions.add(new Question(lines[0], lines[1], lines[2], lines[3], lines[4], 0, 0));
            }
        }catch(IOException e){
            System.out.println("Error while reading file");
        }
    }

    public ArrayList<Question> getQuestions() {
        return questions;
    }
}
