package org.extractor.text;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.NodeTraversor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.extractor.util.FileUtil;

import org.extractor.text.visitor.PlainTextVisitor;
import org.extractor.text.visitor.TitleVisitor;
import org.extractor.text.visitor.VisitorFactory;

public class PlainTextExtracter {


	
	public static void main(String [] args) {

		try {

			ExtracterParam param = new ExtracterParam(FileUtil.readFile(args[0]));
			BufferedReader br = new BufferedReader(new FileReader(param.getFileList()));

			String aLineOfFile = "";
			while ((aLineOfFile = br.readLine()) != null) {
				PlainTextExtracter extracter = new PlainTextExtracter(param);
				String text = extracter.extractText(aLineOfFile);
				String textFileName = aLineOfFile.concat(".txt");
				System.out.println("save: "+textFileName);
				FileUtil.writeFile(textFileName, text);
			}
			br.close();
			
			System.out.println("All tasks are completed.");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private VisitorFactory visitorFactory = null;
	private ExtracterParam param;
	
	public PlainTextExtracter(ExtracterParam param) {
		this.param = param;
		this.visitorFactory= new VisitorFactory(FileUtil.readFile(param.getVisitorFactoryFilename()));
	}
	
	public String extractText(String filename) throws IOException {		
		
		String url = FileUtil.toUrl(param.getBaseFolder(), filename);
		System.out.println("url: "+url);
		String baseUri = FileUtil.toBaseUri(url);
		
		Document doc = Jsoup.parse(new File(filename), "GB2312", baseUri);
		
		String plainText = getPlainText(doc, url);
		plainText = removeScripts(plainText);
		String title = extractTitle(doc);
		String docDate = DocDateExtracter.extractDocumentDate(plainText, new File(filename));
		
		String header = "Url: " + url + "\r\n"+
		                "Title: " + title +"\r\n" +
				        "Date: " + docDate +"\r\n\r\n";

		return header+plainText;
	}
	
	private String removeScripts(String s) {
		String plainText = s;
		
		plainText = plainText.replaceAll("\\<SCRIPT.*\\>.*\\<\\/SCRIPT\\>", "");
		plainText = plainText.replaceAll("\\<NOSCRIPT.*\\>.*\\<\\/NOSCRIPT\\>", "");
		plainText = plainText.replaceAll("\\<A HREF.*\\>.*\\<\\/A\\>", "");
		
		return plainText;
	}

	private String extractTitle(Document doc) {
		TitleVisitor visitor = new TitleVisitor();
		NodeTraversor traversor = new NodeTraversor(visitor);
		
		traversor.traverse(doc);
		String title = visitor.toString().replaceAll("\r", "").replaceAll("\n", "");
		
		return title;
	}
	
	private String getPlainText(Element element, String url) {

		PlainTextVisitor visitor = visitorFactory.getPlainTextVisitor(element);
		NodeTraversor traversor = new NodeTraversor(visitor);
		traversor.traverse(element);

		return visitor.toString();
	}
}
