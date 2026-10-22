package core;

import javax.swing.JButton;

public class AnswerButton extends JButton {
    private Answer answer;

    public Answer getAnswer() {
        return answer;
    }

    public void setAnswer(Answer answer) {
        this.answer = answer;
        setText(answer.getAnswer());
    }
}
