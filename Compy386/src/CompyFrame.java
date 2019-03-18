import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

@SuppressWarnings("serial")
public class CompyFrame extends JFrame {

	private JPanel contentPane;

	public static void main(String[] args) throws InterruptedException, IOException {
		JFrame.setDefaultLookAndFeelDecorated(true);
		CompyFrame frame = new CompyFrame(readFile());

		EventQueue.invokeLater(() -> {
			frame.setVisible(true);
		});

	}

	private int currentLine;

	public CompyFrame(List<String> lines) {

		setUndecorated(true);
		setBackground(new Color(0, 0, 0, 0));
		setLocationRelativeTo(null);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(450, 300);
		contentPane = new Compy386BorderedPanel(new BorderLayout());
		setContentPane(contentPane);

		JTextArea tf = new JTextArea();
		tf.setBackground(Color.black);
		tf.setForeground(Color.white);
		tf.setFont(new Font("Courier", Font.PLAIN, 14));
		tf.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		tf.putClientProperty("caretWidth", 3);
		tf.setCaretColor(Color.white);
		JScrollPane scrollPane = new JScrollPane(tf);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		contentPane.add(scrollPane, BorderLayout.CENTER);

		// animate adding text to the text area
		Timer timer = new Timer(true);
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				if (CompyFrame.this.isVisible() && currentLine < lines.size()) {
					String s = lines.get(currentLine++) + "\n";
					SwingUtilities.invokeLater(() -> {
						tf.append(s);
						tf.setCaretPosition(tf.getText().length());
						if (currentLine == lines.size()) {
							tf.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
						}
					});
				}
			}
		}, 500, 100);
	}

	private static List<String> readFile() throws IOException {
		try (BufferedReader br = new BufferedReader(
				new InputStreamReader(CompyFrame.class.getResourceAsStream("test.txt")))) {
			List<String> result = new ArrayList<String>();

			String s;
			while ((s = br.readLine()) != null) {
				result.add(s);
			}
			return result;
		}
	}

}
