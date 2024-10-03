package org.jepetto.webchat;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;

public class TextToPDFConverter {

	// public static boolean convertTextToPDF(File file) throws Exception{
	public static void main(String args[]) {
		TextToPDFConverter converter = new TextToPDFConverter();
		converter.convert("Qkslffk@91", "c://temp", "input.txt", "output.pdf");
	}

	/**
	 * 비밀번호를 적용하여 txt를 pdf로 변환
	 * 
	 * @param password      pdf에 적용할 비밀번호
	 * @param path          파일 작업이 이루어지는 디렉토리
	 * @param inputTxtFile  소스가 되는 txt 파일
	 * @param outputPdfFile 변환될 pdf 파일명
	 */
	public void convert(String password, String path, String inputTxt, String outputPdfFile) {

		String output_file = null;
		Document pdfDoc = null;

		try {
			// Document pdfDoc = new Document();
			pdfDoc = new Document();
			// String output_file = file.getParent() + "//" + "sample.pdf";

			output_file = path + File.separator + "_" + outputPdfFile;

			PdfWriter writer = PdfWriter.getInstance(pdfDoc, new FileOutputStream(output_file));
			pdfDoc.open();
			pdfDoc.setMarginMirroring(true);
			pdfDoc.setMargins(36, 72, 108, 180);
			pdfDoc.topMargin();

			BaseFont courier = BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1252, BaseFont.EMBEDDED);
			Font myfont = new Font(courier);

			// Font myfont = new Font();
			Font bold_font = new Font();

			bold_font.setStyle(Font.BOLD);
			bold_font.setSize(10);

			myfont.setStyle(Font.NORMAL);
			myfont.setSize(9);

			pdfDoc.add(new Paragraph("\n"));
			Paragraph para = new Paragraph(inputTxt + "\n", myfont);
			para.setAlignment(Element.ALIGN_JUSTIFIED);

			pdfDoc.add(para);

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			pdfDoc.close();
		}

		/*
		String output = path + File.separator + outputPdfFile;
		PdfReader pdfReader		= null;
		PdfStamper pdfStamper	= null;
		try {
			pdfReader	= new PdfReader(output_file);
			pdfStamper	= new PdfStamper(pdfReader, new FileOutputStream(output));
			pdfStamper.setEncryption(
				password.getBytes(),
				"HelloJTNet".getBytes(),
				PdfWriter.ALLOW_PRINTING,
				PdfWriter.ENCRYPTION_AES_128 | PdfWriter.DO_NOT_ENCRYPT_METADATA
			);
		} catch(IOException e) {
			e.printStackTrace();
		} catch(DocumentException e) {
			e.printStackTrace();
		} catch(Exception e) {
			e.printStackTrace();
		}finally {
			try {
				pdfStamper.close();
			} catch (DocumentException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			pdfReader.close();
		}
		//*/

	}

}