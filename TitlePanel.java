public class TitlePanel extends JPanel {
    private JLabel titleLabel;

    public TitlePanel(String title) {
        setLayout(new BorderLayout());
        titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.CENTER);
    }

    public void setTitle(String title) {
        titleLabel.setText(title);
    }
}