package pdf.rotate;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.util.Matrix;

public class PDFRotation extends JFrame implements ActionListener {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JButton browseButton;
    private JButton rotateButton;
    private JTextField filePathField;
    private JLabel statusLabel;

    private File selectedFile;
    private PDDocument document;

    public PDFRotation() {
        setTitle("PDF Rotation Example");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 200);
        setLocationRelativeTo(null);
        setLayout(new FlowLayout());

        filePathField = new JTextField(20);
        browseButton = new JButton("Browse");
        rotateButton = new JButton("Rotate");
        statusLabel = new JLabel();

        browseButton.addActionListener(this);
        rotateButton.addActionListener(this);

        add(filePathField);
        add(browseButton);
        add(rotateButton);
        add(statusLabel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new PDFRotation().setVisible(true);
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == browseButton) {
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                selectedFile = fileChooser.getSelectedFile();
                filePathField.setText(selectedFile.getAbsolutePath());
            }
        } else if (e.getSource() == rotateButton) {
            if (selectedFile != null && selectedFile.exists()) {
                try {
                    document = PDDocument.load(selectedFile);
                    int rotationAngle = askRotationAngle();
                    if (rotationAngle == 0) {
                        statusLabel.setText("Invalid rotation angle. Please enter either 1 (right) or 2 (left).");
                    } else {
                        rotatePDFPages(rotationAngle);
                        statusLabel.setText("PDF pages rotated successfully.");
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                    statusLabel.setText("Error loading PDF file.");
                }
            } else {
                statusLabel.setText("Please select a valid PDF file.");
            }
        }
    }

    private int askRotationAngle() {
        String input = JOptionPane.showInputDialog(null, "Enter rotation direction:\n1. Right\n2. Left");
        if (input != null && (input.equals("1") || input.equals("2"))) {
            return Integer.parseInt(input);
        }
        return 0;
    }

    private void rotatePDFPages(int rotationAngle) throws IOException {
        for (PDPage page : document.getPages()) {
            PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, true);
            PDRectangle mediaBox = page.getMediaBox();
            PDRectangle newMediaBox = new PDRectangle(mediaBox.getHeight(), mediaBox.getWidth());
            contentStream.transform(new Matrix(newMediaBox.getWidth(), 0, 0, newMediaBox.getHeight(), 0, 0));
            contentStream.close();

            int currentRotation = page.getRotation();
            int newRotation;
            if (rotationAngle == 1) {
                newRotation = (currentRotation + 90) % 360;
            } else {
                newRotation = (currentRotation - 90) % 360;
            }
            page.setRotation(newRotation);
        }
        document.save(selectedFile);
        document.close();
    }
}

