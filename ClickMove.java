package clickMove;

import java.awt.Insets;

import javax.swing.JFrame;
import javax.swing.JMenuBar;

public class ClickMove {
	static JFrame frame;
	int height = 0;

	public ClickMove() {
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(new Panel());
		frame.pack();
		frame.setVisible(true);
		frame.setResizable(false);
		// frame.setLocationRelativeTo(null);
		frame.setTitle("ClickMove");
		frame.createBufferStrategy(2);

		JMenuBar menubar = frame.getJMenuBar();
		int mbh = (menubar != null ? menubar.getSize().height : 0);
		Insets insets = frame.getInsets();
		height += insets.top + mbh;
		Panel.setTopInset(height);
	}

	public static void main(String[] args) {
		new ClickMove();
	}
}
