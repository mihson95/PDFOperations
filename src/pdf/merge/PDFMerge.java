package pdf.merge;

import org.apache.pdfbox.multipdf.PDFMergerUtility;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class PDFMerge extends JFrame implements ActionListener {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JButton browseButton1;
    private JButton browseButton2;
    private JButton mergeButton;
    private JTextField filePathField1;
    private JTextField filePathField2;
    private JLabel statusLabel;

    private File selectedFile1;
    private File selectedFile2;

    public PDFMerge() {
        setTitle("PDF Merge Example");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 200);
        setLocationRelativeTo(null);
        setLayout(new FlowLayout());

        filePathField1 = new JTextField(20);
        filePathField2 = new JTextField(20);
        browseButton1 = new JButton("Browse PDF 1");
        browseButton2 = new JButton("Browse PDF 2");
        mergeButton = new JButton("Merge");
        statusLabel = new JLabel();

        browseButton1.addActionListener(this);
        browseButton2.addActionListener(this);
        mergeButton.addActionListener(this);

        add(filePathField1);
        add(browseButton1);
        add(filePathField2);
        add(browseButton2);
        add(mergeButton);
        add(statusLabel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new PDFMerge().setVisible(true);
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == browseButton1) {
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                selectedFile1 = fileChooser.getSelectedFile();
                filePathField1.setText(selectedFile1.getAbsolutePath());
            }
        } else if (e.getSource() == browseButton2) {
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                selectedFile2 = fileChooser.getSelectedFile();
                filePathField2.setText(selectedFile2.getAbsolutePath());
            }
        } else if (e.getSource() == mergeButton) {
            if (selectedFile1 != null && selectedFile1.exists() &&
                    selectedFile2 != null && selectedFile2.exists()) {
                try {
                    mergePDFs(selectedFile1, selectedFile2);
                    statusLabel.setText("PDF files merged successfully.");
                } catch (IOException ex) {
                    ex.printStackTrace();
                    statusLabel.setText("Error merging PDF files.");
                }
            } else {
                statusLabel.setText("Please select two valid PDF files.");
            }
        }
    }

    private void mergePDFs(File file1, File file2) throws IOException {
        PDFMergerUtility mergerUtility = new PDFMergerUtility();
        mergerUtility.addSource(file1);
        mergerUtility.addSource(file2);
        String originalFileName1 = file1.getName().contains(".")?file1.getName().substring(0,file1.getName().lastIndexOf(".")):file1.getName();
        String originalFileName2 = file2.getName().contains(".")?file2.getName().substring(0,file2.getName().lastIndexOf(".")):file2.getName();
		String mergedFilePath = file1.getParent() + File.separator + originalFileName1 + "_" + originalFileName2
				+ "_merged.pdf";
        mergerUtility.setDestinationFileName(mergedFilePath);
        mergerUtility.mergeDocuments(null);
    }
}

