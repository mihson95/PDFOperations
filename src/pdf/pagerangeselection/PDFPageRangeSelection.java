package pdf.pagerangeselection;

import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PDFPageRangeSelection extends JFrame implements ActionListener {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JButton browseButton;
    private JButton selectButton;
    private JTextField filePathField;
    private JTextField selectField;
    private JLabel statusLabel;

    private File selectedFile;
    private PDDocument document;

    public PDFPageRangeSelection() {
        setTitle("PDF Page Range Selection Example");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 200);
        setLocationRelativeTo(null);
        setLayout(new FlowLayout());

        filePathField = new JTextField(20);
        browseButton = new JButton("Browse");
        selectButton = new JButton("Select");
        selectField = new JTextField(10);
        statusLabel = new JLabel();

        browseButton.addActionListener(this);
        selectButton.addActionListener(this);

        add(filePathField);
        add(browseButton);
        add(new JLabel("Select Page Range:"));
        add(selectField);
        add(selectButton);
        add(statusLabel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new PDFPageRangeSelection().setVisible(true);
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
        } else if (e.getSource() == selectButton) {
            if (selectedFile != null && selectedFile.exists()) {
                try {
                    String[] pageRange = selectField.getText().split("-");
                    if (pageRange.length != 2) {
                        statusLabel.setText("Invalid page range. Please enter in the format 'start-end'.");
                    } else {
                        int startPage = Integer.parseInt(pageRange[0]);
                        int endPage = Integer.parseInt(pageRange[1]);
                        if (startPage <= 0 || endPage <= 0 || startPage > endPage) {
                            statusLabel.setText("Invalid page range. Please enter valid start and end pages.");
                        } else {
                            document = PDDocument.load(selectedFile);
                            int totalPages = document.getNumberOfPages();
                            if (startPage <= totalPages && endPage <= totalPages) {
                                selectPageRange(startPage, endPage);
                                statusLabel.setText("Selected pages saved successfully.");
                            } else {
                                statusLabel.setText("Invalid page range. The PDF file contains only " + totalPages + " pages.");
                            }
                        }
                    }
                } catch (NumberFormatException ex) {
                    statusLabel.setText("Invalid page range. Please enter numeric values.");
                } catch (IOException ex) {
                    ex.printStackTrace();
                    statusLabel.setText("Error loading PDF file.");
                }
            } else {
                statusLabel.setText("Please select a valid PDF file.");
            }
        }
    }

    private void selectPageRange(int startPage, int endPage) throws IOException {
        List<PDDocument> splitDocuments = new ArrayList<>();

        Splitter splitter = new Splitter();
        splitter.setStartPage(startPage);
        splitter.setEndPage(endPage);
        List<PDDocument> pages = splitter.split(document);

        for (PDDocument page : pages) {
            PDDocument newDocument = new PDDocument();
            newDocument.addPage(page.getPage(0));
            splitDocuments.add(newDocument);
        }
        String originalFileName = selectedFile.getName().contains(".")?selectedFile.getName().substring(0,selectedFile.getName().lastIndexOf(".")):selectedFile.getName();
		String outputFilePath = selectedFile.getParent() + File.separator + originalFileName + "Pages" + startPage
				+ "-" + endPage + ".pdf";
        mergeDocuments(splitDocuments, outputFilePath);

        for (PDDocument splitDocument : splitDocuments) {
            splitDocument.close();
        }
    }

    private void mergeDocuments(List<PDDocument> documents, String outputFilePath) throws IOException {
        PDDocument mergedDocument = new PDDocument();
        for (PDDocument doc : documents) {
            for (PDPage page : doc.getPages()) {
                mergedDocument.addPage(page);
            }
        }

        mergedDocument.save(outputFilePath);
        mergedDocument.close();
    }
}
