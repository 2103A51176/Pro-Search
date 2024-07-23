package com.example.pro_search;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 100;
    private Button selectBtn, searchBtn;
    private LinearLayout selectedFilesContainer, searchResultsContainer;
    private EditText searchEditText;
    private Set<Uri> selectedPdfUris = new HashSet<>();
    private Set<Uri> selectedWordUris = new HashSet<>();
    private Set<Uri> selectedExcelUris = new HashSet<>();
    private final ActivityResultLauncher<Intent> filePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();
                    if (data.getClipData() != null) {
                        int count = data.getClipData().getItemCount();
                        for (int i = 0; i < count; i++) {
                            Uri fileUri = data.getClipData().getItemAt(i).getUri();
                            String fileType = getContentResolver().getType(fileUri);
                            if (fileType != null && fileType.equals("application/pdf")) {
                                addSelectedPdf(fileUri);
                            } else if (fileType != null && (fileType.equals("application/msword") || fileType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))) {
                                addSelectedWord(fileUri);
                            } else if (fileType != null && (fileType.equals("application/vnd.ms-excel") || fileType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))) {
                                addSelectedExcel(fileUri);
                            }
                        }
                        selectBtn.setText("Add More Files");
                    } else if (data.getData() != null) {
                        Uri fileUri = data.getData();
                        String fileType = getContentResolver().getType(fileUri);
                        if (fileType != null && fileType.equals("application/pdf")) {
                            addSelectedPdf(fileUri);
                        } else if (fileType != null && (fileType.equals("application/msword") || fileType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))) {
                            addSelectedWord(fileUri);
                        } else if (fileType != null && (fileType.equals("application/vnd.ms-excel") || fileType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))) {
                            addSelectedExcel(fileUri);
                        }
                        selectBtn.setText("Add More Files");
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        selectedFilesContainer = findViewById(R.id.selected_files_container);
        searchResultsContainer = findViewById(R.id.search_results_container);
        selectBtn = findViewById(R.id.idBtnSelect);

        searchBtn = findViewById(R.id.idBtnSearch);
        searchEditText = findViewById(R.id.idSearchText);

        selectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
                } else {
                    selectFiles();
                }
            }
        });



        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchInFiles();
            }
        });
    }

    private void selectFiles() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        String[] mimeTypes = {"application/pdf", "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document","application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"};
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        filePickerLauncher.launch(intent);
    }

    private void addSelectedPdf(Uri fileUri) {
        if (!selectedPdfUris.contains(fileUri)) {
            selectedPdfUris.add(fileUri);
            String fileName = getFileName(fileUri);
            addFileView(fileName, fileUri);
        }
        else{
            Toast.makeText(MainActivity.this, "This PDF is already Selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void addSelectedWord(Uri fileUri) {
        if (!selectedWordUris.contains(fileUri)) {
            selectedWordUris.add(fileUri);
            String fileName = getFileName(fileUri);
            addFileView(fileName, fileUri);
        }
        else{
            Toast.makeText(MainActivity.this, "This Word is already Selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void addSelectedExcel(Uri fileUri) {
        if (!selectedExcelUris.contains(fileUri)) {
            selectedExcelUris.add(fileUri);
            String fileName = getFileName(fileUri);
            addFileView(fileName, fileUri);
        }
        else{
            Toast.makeText(MainActivity.this, "This Excel is already Selected", Toast.LENGTH_SHORT).show();
        }
    }
    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(Math.abs(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)));
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private void addFileView(String fileName, Uri fileUri) {
        View fileView = getLayoutInflater().inflate(R.layout.selected_file_item, null);
        TextView fileNameTextView = fileView.findViewById(R.id.file_name_text);
        Button removeButton = fileView.findViewById(R.id.remove_button);

        fileNameTextView.setText(fileName);

        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedPdfUris.contains(fileUri)) {
                    selectedPdfUris.remove(fileUri);
                } else if (selectedWordUris.contains(fileUri)) {
                    selectedWordUris.remove(fileUri);
                } else if (selectedExcelUris.contains(fileUri)) {
                    selectedExcelUris.remove(fileUri);
                }
                selectedFilesContainer.removeView(fileView);
            }
        });

        selectedFilesContainer.addView(fileView);
    }



    private void searchInFiles() {
        String query = searchEditText.getText().toString();
        searchResultsContainer.removeAllViews();

        if (query.isEmpty()) {
            Toast.makeText(MainActivity.this, "Enter Text to Search", Toast.LENGTH_SHORT).show();
            return;
        }
        if( selectedPdfUris.isEmpty()  && selectedExcelUris.isEmpty() && selectedWordUris.isEmpty()){
            Toast.makeText(MainActivity.this, "Please Select any File", Toast.LENGTH_SHORT).show();
            return;
        }
        for (Uri pdfUri : selectedPdfUris) {
            try {
                String fileName = getFileName(pdfUri);
                PdfReader reader = new PdfReader(getContentResolver().openInputStream(pdfUri));
                int n = reader.getNumberOfPages();
                boolean found = false;

                for (int i = 0; i < n; i++) {
                    String pageText = PdfTextExtractor.getTextFromPage(reader, i + 1);
                    if (pageText.contains(query)) {
                        found = true;
                        String[] sentences = pageText.split("\\. ");
                        for (String sentence : sentences) {
                            if (sentence.contains(query)) {
                                addSearchResult(fileName, sentence.trim() + ".");
                            }
                        }
                    }
                }
                reader.close();

                if (!found) {
                    addSearchResult(fileName, "No results found.");
                }

            } catch (IOException e) {
                e.printStackTrace();
                addSearchResult(getFileName(pdfUri), "Error reading PDF.");
            }
        }

        for (Uri wordUri : selectedWordUris) {
            try {
                String fileName = getFileName(wordUri);
                String content = "";

                if (fileName.endsWith(".doc")) {
                    content = extractDocText(wordUri);
                } else if (fileName.endsWith(".docx")) {
                    content = extractDocxText(wordUri);
                }

                if (content.contains(query)) {
                    String[] paragraphs = content.split("\\. ");
                    for (String paragraph : paragraphs) {
                        if (paragraph.contains(query)) {
                            addSearchResult(fileName, paragraph.trim() + ".");
                        }
                    }
                } else {
                    addSearchResult(fileName, "No results found.");
                }

            } catch (IOException e) {
                e.printStackTrace();
                addSearchResult(getFileName(wordUri), "Error reading Word document.");
            }
        }

        for (Uri excelUri : selectedExcelUris) {
            try {
                String fileName = getFileName(excelUri);
                String content = parseExcel(excelUri);

                if (content.contains(query)) {
                    String[] rows = content.split("\\n");
                    for (String row : rows) {
                        if (row.contains(query)) {
                            addSearchResult(fileName, row.trim());
                        }
                    }
                } else {
                    addSearchResult(fileName, "No results found.");
                }

            } catch (IOException e) {
                e.printStackTrace();
                addSearchResult(getFileName(excelUri), "Error reading Excel document.");
            }
        }
    }

    private String extractDocText(Uri uri) throws IOException {
        HWPFDocument document = new HWPFDocument(getContentResolver().openInputStream(uri));
        WordExtractor extractor = new WordExtractor(document);
        return String.join(". ", extractor.getParagraphText());
    }

    private String extractDocxText(Uri uri) throws IOException {
        XWPFDocument document = new XWPFDocument(getContentResolver().openInputStream(uri));
        XWPFWordExtractor extractor = new XWPFWordExtractor(document);
        return extractor.getText();
    }

    private String parseExcel(Uri fileUri) throws IOException {
        InputStream inputStream = getContentResolver().openInputStream(fileUri);
        Workbook workbook = new XSSFWorkbook(inputStream);
        StringBuilder content = new StringBuilder();
        for (Sheet sheet : workbook) {
            for (Row row : sheet) {
                for (Cell cell : row) {
                    content.append(cell.toString()).append(" ");
                }
                content.append("\n");
            }
        }
        workbook.close();
        return content.toString();
    }
    private void addSearchResult(String fileName, String resultText) {
        View resultView = getLayoutInflater().inflate(R.layout.search_result_item, null);
        TextView fileNameTextView = resultView.findViewById(R.id.result_file_name_text);
        TextView resultTextView = resultView.findViewById(R.id.result_text);

        fileNameTextView.setText(fileName);
        resultTextView.setText(resultText);

        searchResultsContainer.addView(resultView);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                selectFiles();
            }
        }
    }
}
