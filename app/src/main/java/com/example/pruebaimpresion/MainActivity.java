package com.example.pruebaimpresion;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.FileObserver;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.itextpdf.text.pdf.draw.VerticalPositionMark;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.lang.annotation.Documented;
import java.net.ContentHandler;

public class MainActivity extends AppCompatActivity {

    Button btn_pdf;

    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    if (ContextCompat.checkSelfPermission(
                            MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                            PackageManager.PERMISSION_GRANTED){
                        initPdfCreation();
                    }else{
                        checarPErmisos();
                    }

                } else {
                    Toast.makeText(this, "Se necesita el permiso", Toast.LENGTH_LONG);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_pdf = (Button) findViewById(R.id.btn_create_pdf);

        checarPErmisos();
    }

    void initPdfCreation(){
        btn_pdf.setOnClickListener(view -> createPDFFile(Common.getAppPath(MainActivity.this) + "test.pdf"));

        Dexter.withActivity(this)
                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {

                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                });
    }

    private void checarPErmisos(){
        if (ContextCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED) {
            checarPErmisosRead();
        }  else {
            requestPermissionLauncher.launch(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }

    private void checarPErmisosRead(){
        if (ContextCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED) {
            // You can use the API that requires the permission.
            initPdfCreation();
        }  else {
            requestPermissionLauncher.launch(
                    Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }

    private void createPDFFile(String path) {
        if(new File(path).exists())
            new File(path).delete();

        try{
            Document document = new Document();

            PdfWriter.getInstance(document, new FileOutputStream(path));

            document.open();

            document.setPageSize(PageSize.A4);
            document.addCreationDate();
            document.addAuthor("Martin");
            document.addCreator("Martin");

            BaseColor colorAccent = new BaseColor(0,153,204,255);

            float fontSize = 20.0f;
            float valueFontSize = 26.0f;

            Font titleFont = new Font(Font.FontFamily.HELVETICA, 36.0f);
            Font bodyFont = new Font(Font.FontFamily.HELVETICA, fontSize);

            addNewItem(document, "Titulo", Element.ALIGN_CENTER, titleFont);

            addNewItem(document, "No. orden", Element.ALIGN_LEFT, bodyFont);
            addNewItem(document, "#424242", Element.ALIGN_LEFT, bodyFont);

            addLineSeparator(document);

            addNewItem(document, "Fecha", Element.ALIGN_LEFT, bodyFont);
            addNewItem(document, "12/12/2021", Element.ALIGN_LEFT, bodyFont);

            addLineSeparator(document);

            addNewItem(document, "Cliente", Element.ALIGN_LEFT, bodyFont);
            addNewItem(document, "Fernando", Element.ALIGN_LEFT, bodyFont);

            addLineSeparator(document);

            addNewItemWithLeftAndRight(document, "Producto 1", "$3", bodyFont);

            addLineSeparator(document);

            addNewItemWithLeftAndRight(document, "Producto 2", "$3", bodyFont);

            addLineSeparator(document);
            addLineSeparator(document);

            addNewItemWithLeftAndRight(document, "Total", "$6", bodyFont);

            document.close();

            printPDF();

        } catch (FileNotFoundException | DocumentException e) {
            e.printStackTrace();
        }
    }

    void printPDF(){
        PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);

        try{
            PrintDocumentAdapter printDocumentAdapter = new PdfDocumentAdapter(MainActivity.this, Common.getAppPath(MainActivity.this) + "test.pdf");
            printManager.print("Documento", printDocumentAdapter, new PrintAttributes.Builder().build());
        }
        catch (Exception e){
            Log.e("DEV", "" + e.getMessage());
        }
    }

    private void addNewItemWithLeftAndRight(Document document, String left, String right, Font font) throws DocumentException {
        Chunk chunkLeft = new Chunk(left, font);
        Chunk chunkRight = new Chunk(right, font);

        Paragraph paragraph = new Paragraph(chunkLeft);
        paragraph.add(new Chunk(new VerticalPositionMark()));
        paragraph.add(chunkRight);

        document.add(paragraph);
    }

    void addLineSeparator(Document document) throws DocumentException {
        LineSeparator lineSeparator = new LineSeparator();
        lineSeparator.setLineColor(new BaseColor(0,0,0,68));
        addLineSpace(document);
        document.add(new Chunk(lineSeparator));
        addLineSpace(document);
    }

    void addLineSpace(Document document) throws DocumentException {
        document.add(new Paragraph(""));
    }

    void addNewItem(Document document, String text, int align, Font font) throws DocumentException {
        Chunk chunck = new Chunk(text, font);

        Paragraph paragraph = new Paragraph(chunck);
        paragraph.setAlignment(align);
        document.add(paragraph);
    }
}